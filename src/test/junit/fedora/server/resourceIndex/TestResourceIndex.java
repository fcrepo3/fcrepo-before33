package fedora.server.resourceIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.trippi.RDFFormat;
import org.trippi.TriplestoreConnector;

import fedora.server.DummyLogging;
import fedora.server.config.DatastoreConfiguration;
import fedora.server.config.ModuleConfiguration;
import fedora.server.config.Parameter;
import fedora.server.config.ServerConfiguration;
import fedora.server.config.ServerConfigurationParser;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.translation.FOXMLDODeserializer;
import fedora.server.storage.translation.METSLikeDODeserializer;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;
import fedora.server.utilities.DDLConverter;
import fedora.server.utilities.SQLUtility;
import fedora.test.FedoraTestCase;
import fedora.utilities.ExecUtility;

/**
 * @author Edwin Shin
 */
public abstract class TestResourceIndex extends FedoraTestCase {
	static {
		Logger logger = Logger.getRootLogger();
		logger.setLevel(Level.WARN);
	}
    protected static final String DEMO_OBJECTS_ROOT_DIR = "src/test/junit/foxmlTestObjects";
    
    protected static String m_triplestorePath;
    protected ResourceIndex m_ri;
    protected ConnectionPool m_cPool;
    protected TriplestoreConnector m_conn;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestResourceIndexImpl.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
    	ServerConfiguration serverConfig = getServerConfiguration();
    	
    	ModuleConfiguration riConfig = serverConfig.getModuleConfiguration("fedora.server.resourceIndex.ResourceIndex");
        Parameter riLevel = riConfig.getParameter("level");
        int level = Integer.parseInt(riLevel.getValue());
        Parameter riDatastore = riConfig.getParameter("datastore");
        String datastore = riDatastore.getValue();
    	
    	ModuleConfiguration cpmConfig = serverConfig.getModuleConfiguration("fedora.server.storage.ConnectionPoolManager");
    	Parameter cpmPool = cpmConfig.getParameter("defaultPoolName");
    	String cPoolName = cpmPool.getValue();
    	
    	DatastoreConfiguration dsConfig = serverConfig.getDatastoreConfiguration(cPoolName);
        String cpUserName = dsConfig.getParameter("dbUsername").getValue();
        String cpPassword = dsConfig.getParameter("dbPassword").getValue();
        String cpURL = dsConfig.getParameter("jdbcURL").getValue();
        String cpDriver = dsConfig.getParameter("jdbcDriverClass").getValue();
        String cpDDLConverter = dsConfig.getParameter("ddlConverter").getValue();
        int cpMaxActive = Integer.parseInt(dsConfig.getParameter("maxActive").getValue());
        int cpMaxIdle = Integer.parseInt(dsConfig.getParameter("maxIdle").getValue());
        long cpMaxWait = Long.parseLong(dsConfig.getParameter("maxWait").getValue()); 
        int cpMinIdle = Integer.parseInt(dsConfig.getParameter("minIdle").getValue());
        long cpMinEvictableIdleTimeMillis = Long.parseLong(dsConfig.getParameter("minEvictableIdleTimeMillis").getValue());
        int cpNumTestsPerEvictionRun = Integer.parseInt(dsConfig.getParameter("numTestsPerEvictionRun").getValue());
        long cpTimeBetweenEvictionRunsMillis = Long.parseLong(dsConfig.getParameter("timeBetweenEvictionRunsMillis").getValue());
        boolean cpTestOnBorrow = Boolean.getBoolean(dsConfig.getParameter("testOnBorrow").getValue());
        boolean cpTestOnReturn = Boolean.getBoolean(dsConfig.getParameter("testOnReturn").getValue());
        boolean cpTestWhileIdle = Boolean.getBoolean(dsConfig.getParameter("testWhileIdle").getValue());
        byte cpWhenExhaustedAction = Byte.parseByte(dsConfig.getParameter("whenExhaustedAction").getValue());        
        DDLConverter ddlConverter=null;

        if (cpDDLConverter != null) {
            ddlConverter=(DDLConverter) Class.forName(cpDDLConverter).newInstance();
        }
        
        m_cPool = new ConnectionPool(cpDriver, cpURL, cpUserName, 
                cpPassword, ddlConverter, cpMaxActive, cpMaxIdle, 
                cpMaxWait, cpMinIdle, cpMinEvictableIdleTimeMillis, 
                cpNumTestsPerEvictionRun, cpTimeBetweenEvictionRunsMillis, 
                cpTestOnBorrow, cpTestOnReturn, cpTestWhileIdle, cpWhenExhaustedAction);
        
        String dbSpec="src/dbspec/server/fedora/server/storage/resources/DefaultDOManager.dbspec";
        InputStream specIn = new FileInputStream(dbSpec);
        if (specIn==null) {
            throw new IOException("Cannot find required "
                + "resource: " + dbSpec);
        }
        SQLUtility.createNonExistingTables(m_cPool, specIn, new DummyLogging());
        
        DatastoreConfiguration tsConfig = serverConfig.getDatastoreConfiguration(datastore);
        Map tsP = new HashMap();
        Iterator it = tsConfig.getParameters().iterator();
        while (it.hasNext()) {
        	Parameter param = (Parameter)it.next();
        	tsP.put(param.getName(), param.getValue(param.getIsFilePath()));
        }
        String connectorClassName = tsConfig.getParameter("connectorClassName").getValue();
        m_triplestorePath = tsConfig.getParameter("path").getValue(true);
        
        m_conn = TriplestoreConnector.init(connectorClassName, tsP);
        
        //
        // Get anything starting with alias: and put the following name
        // and its value in the alias map.
        //
        HashMap aliasMap = new HashMap();
        Iterator iter = riConfig.getParameters().iterator();
        while (iter.hasNext()) {
            Parameter param = (Parameter)iter.next();
            
            String[] parts = (param.getName()).split(":");
            if ((parts.length == 2) && (parts[0].equals("alias"))) {
                aliasMap.put(parts[1], param.getValue(param.getIsFilePath()));
            }
        }
        
        m_ri = new ResourceIndexImpl(level, m_conn, m_cPool, aliasMap, null);

        // needed by the deserializer
        System.setProperty("fedoraServerHost", "localhost");
        System.setProperty("fedoraServerPort", "8080");
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        if (m_conn != null) {
            m_conn.close();
            m_conn = null;
        }
        if (m_cPool != null) {
            deleteRITables();
            m_cPool = null;
        }
        m_ri = null;
        deleteDirectory(m_triplestorePath);
    }
    
    protected void addDigitalObject(File file) throws Exception {
        FileInputStream in;
        DigitalObject obj = new BasicDigitalObject();

        in = new FileInputStream(file);
        FOXMLDODeserializer deser = new FOXMLDODeserializer();
        deser.deserialize(in, obj, "UTF-8", 0);
        addDigitalObject(obj);
    }

    protected void addDigitalObject(DigitalObject digitalObject) throws Exception {
        m_ri.addDigitalObject(digitalObject);
    }
    
    protected void addDigitalObjects(File dir) throws Exception {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File file, String name) {
                return (!name.startsWith(".") && name.endsWith(".xml"));
            }
        };
        File[] files = dir.listFiles(filter);
        for (int i = 0; i < files.length; i++) {
            addDigitalObject(files[i]);
        }
    }
    
    protected boolean deleteDirectory(String directory) {
        boolean result = false;

        if (directory != null) {
            File file = new File(directory);
            if (file.exists() && file.isDirectory()) {
                // 1. delete content of directory:
                File[] files = file.listFiles();
                result = true; //init result flag
                int count = files.length;
                for (int i = 0; i < count; i++) { //for each file:
                    File f = files[i];
                    if (f.isFile()) {
                        result = result && f.delete();
                    } else if (f.isDirectory()) {
                        result = result && deleteDirectory(f.getAbsolutePath());
                    }
                }//next file

                file.delete(); //finally delete (empty) input directory
            }//else: input directory does not exist or is not a directory
        }//else: no input value

        return result;
    }//deleteDirectory()
    
    protected void deleteRITables() throws Exception {
        Connection conn = m_cPool.getConnection();
        Statement stmt = conn.createStatement();
        String[] drop = {"DROP TABLE riMethodMimeType", "DROP TABLE riMethodImpl", 
                         "DROP TABLE riMethodPermutation", "DROP TABLE riMethod", 
                         "DROP TABLE riMethodImplBinding"};
        for (int i = 0; i < drop.length; i++) {
            stmt.execute(drop[i]);
        }
    }

    protected DigitalObject getFoxmlObject(File file) throws Exception {
        FileInputStream in;
        DigitalObject obj = new BasicDigitalObject();

        in = new FileInputStream(file);
        FOXMLDODeserializer deser = new FOXMLDODeserializer();
        deser.deserialize(in, obj, "UTF-8", 0);
        return obj;
    }
    
    protected DigitalObject getMetsObject(File file) throws Exception {
        FileInputStream in;
        DigitalObject obj = new BasicDigitalObject();

        in = new FileInputStream(file);
        METSLikeDODeserializer deser = new METSLikeDODeserializer();
        deser.deserialize(in, obj, "UTF-8", 0);
        return obj;
    }
    
    protected void export(String path) throws Exception {
        //m_ri.export(new FileOutputStream(path), RDFFormat.RDF_XML);
        m_ri.export(new FileOutputStream(path), RDFFormat.N_TRIPLES);
    }
    
    public static ServerConfiguration getServerConfiguration() throws Exception {
    	FileInputStream fis;
    	try {
    		fis = new FileInputStream(FCFG);
    	} catch (FileNotFoundException e) {
    		ExecUtility.execCommandLineUtility(FEDORA_HOME + "/server/bin/fedora-setup ssl-authenticate-apim");
    		fis = new FileInputStream(FCFG);
    	}
        return new ServerConfigurationParser(fis).parse();
    }
}
