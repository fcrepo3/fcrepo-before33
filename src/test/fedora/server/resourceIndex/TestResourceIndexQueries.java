package fedora.server.resourceIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

import org.trippi.RDFFormat;
import org.trippi.TriplestoreConnector;

import fedora.server.Parameterized;
import fedora.server.TestLogging;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.translation.FOXMLDODeserializer;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;
import fedora.server.utilities.ConfigurationLoader;
import fedora.server.utilities.DDLConverter;
import fedora.server.utilities.SQLUtility;
import junit.framework.TestCase;

/**
 * @author Edwin Shin
 */
public class TestResourceIndexQueries extends TestCase {
    private static final String DEMO_OBJECTS_ROOT_DIR = "src/test/fedora/server/resourceIndex/foxmlTestObjects";
    
    private static String tsPath;
    private static String fedoraHome = System.getProperty("fedora.home");
    private ResourceIndex m_ri;
    private ConnectionPool m_cPool;
    private TriplestoreConnector m_conn;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestResourceIndexQueries.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        if (fedoraHome == null || fedoraHome.equals("")) {
            fedoraHome = "dist";
        }
        
        ConfigurationLoader cl = new ConfigurationLoader(fedoraHome, "test.fcfg"); 
        
        Map riMP = cl.getModuleParameters("fedora.server.resourceIndex.ResourceIndex");
        int level = Integer.parseInt((String)riMP.get("level"));
        String datastoreId = (String)riMP.get("datastoreId");
        
        Map cpMP = cl.getModuleParameters("fedora.server.storage.ConnectionPoolManager");
        String cPoolName = (String)cpMP.get("defaultPoolName");
        
        Parameterized cpConf = cl.getDatastoreConfig(cPoolName);
        Map cpP = cpConf.getParameters();
        String cpUserName = (String)cpP.get("dbUsername");
        String cpPassword = (String)cpP.get("dbPassword");
        String cpURL = (String)cpP.get("jdbcURL");
        String cpDriver = (String)cpP.get("jdbcDriverClass");
        String cpDDLConverter = (String)cpP.get("ddlConverter");
        int cpIConn = Integer.parseInt((String)cpP.get("minPoolSize"));
        int cpMaxConn = Integer.parseInt((String)cpP.get("maxPoolSize"));
        DDLConverter ddlConverter=null;
        if (cpDDLConverter != null) {
            ddlConverter=(DDLConverter) Class.forName(cpDDLConverter).newInstance();
        }
        
        m_cPool = new ConnectionPool(cpDriver, cpURL, cpUserName, 
                                     cpPassword, cpIConn, cpMaxConn, true, ddlConverter);
        
        String dbSpec="src/dbspec/server/fedora/server/storage/resources/DefaultDOManager.dbspec";
        InputStream specIn = new FileInputStream(dbSpec);
        if (specIn==null) {
            throw new IOException("Cannot find required "
                + "resource: " + dbSpec);
        }
        SQLUtility.createNonExistingTables(m_cPool, specIn, new TestLogging());
        
        Parameterized tsConf = cl.getDatastoreConfig(datastoreId);
        Map tsP = tsConf.getParameters();
        String connectorClassName = (String) tsP.get("connectorClassName");
        tsPath = (String)tsP.get("path");
        
        m_conn = TriplestoreConnector.init(connectorClassName, tsP);
        
        m_ri = new ResourceIndexImpl(level, m_conn, m_cPool, null);

        // needed by the deserializer
        System.setProperty("fedoraServerHost", "localhost");
        System.setProperty("fedoraServerPort", "8080");
        
        //
        DigitalObject ri1010 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1010.xml"));
        DigitalObject ri1011 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1011.xml"));
        DigitalObject ri1012 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1012.xml"));
        DigitalObject ri1013 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1013.xml"));
        DigitalObject ri1014 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1014.xml"));
        DigitalObject ri1015 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1015.xml"));
        DigitalObject ri1016 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1016.xml"));
        DigitalObject ri1017 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1017.xml"));
        
        m_ri.addDigitalObject(ri1010);
        m_ri.addDigitalObject(ri1011);
        m_ri.addDigitalObject(ri1012);
        m_ri.addDigitalObject(ri1013);
        m_ri.addDigitalObject(ri1014);
        m_ri.addDigitalObject(ri1015);
        m_ri.addDigitalObject(ri1016);
        m_ri.addDigitalObject(ri1017);
        m_ri.commit();
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
            m_cPool.closeAllConnections();
            m_cPool = null;
        }
        m_ri = null;
        deleteDirectory(tsPath);
    }
    
    public void testCount() throws Exception {
        assertEquals(36, m_ri.countTriples(null, null, null, 0));
        //m_ri.export(new FileOutputStream("/tmp/out.rdf"), RDFFormat.RDF_XML);
    }
    
    public void testQuery1() throws Exception {
        // TODO
    }
    
    public void testQuery2() throws Exception {
        // TODO
    }
    
    ///////////////////////////////////////////////////////////////////
    
    private DigitalObject getDigitalObject(File file) throws Exception {
        FileInputStream in;
        DigitalObject obj = new BasicDigitalObject();

        in = new FileInputStream(file);
        FOXMLDODeserializer deser = new FOXMLDODeserializer();
        deser.deserialize(in, obj, "UTF-8", 0);
        return obj;
    }

    private void deleteRITables() throws Exception {
        Connection conn = m_cPool.getConnection();
        Statement stmt = conn.createStatement();
        String[] drop = {"DROP TABLE riMethodMimeType", "DROP TABLE riMethodImpl", 
                         "DROP TABLE riMethodPermutation", "DROP TABLE riMethod"};
        for (int i = 0; i < drop.length; i++) {
            stmt.execute(drop[i]);
        }
    }
    
    private boolean deleteDirectory(String directory) {
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
}
