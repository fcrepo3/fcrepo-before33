package fedora.server.resourceIndex;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import fedora.server.Parameterized;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.translation.FOXMLDODeserializer;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.utilities.ConfigurationLoader;
import fedora.server.utilities.DDLConverter;
import fedora.server.utilities.SQLUtility;
import fedora.server.TestLogging;

import org.trippi.RDFFormat;
import org.trippi.TriplestoreConnector;

import junit.framework.TestCase;

/**
 * @author eddie
 *  
 */
public class TestResourceIndexImpl extends TestCase {
    
    private static final String DEMO_OBJECTS_ROOT_DIR = "src/test/fedora/server/resourceIndex/foxmlTestObjects";
    
    private static String tsPath;
    private static String fedoraHome = System.getProperty("fedora.home");
    private ResourceIndex m_ri;
    private ConnectionPool m_cPool;
    private TriplestoreConnector m_conn;
    
    private int tc = 0;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestResourceIndexImpl.class);
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
        String level = (String)riMP.get("level");
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
        
        ConnectionPool m_cPool = new ConnectionPool(cpDriver, cpURL, cpUserName, 
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
        
        m_ri = new ResourceIndexImpl(1, m_conn, m_cPool, null);

        // needed by the deserializer
        System.setProperty("fedoraServerHost", "localhost");
        System.setProperty("fedoraServerPort", "8080");
        
        //
        DigitalObject bdef = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bdefs/demo_8.xml"));
        DigitalObject bmech = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bmechs/demo_9.xml"));
        DigitalObject dataobject = getDigitalObject(new File(
                DEMO_OBJECTS_ROOT_DIR + "/dataobjects/demo_10.xml"));
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
            m_cPool.closeAllConnections();
            m_cPool = null;
        }
        m_ri = null;
        deleteDirectory(tsPath);
    }

    public void testAddDigitalObject() throws Exception {
        DigitalObject obj = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_10.xml"));
        m_ri.addDigitalObject(obj);
    }

    public void testAddDatastream() {
        
    }

    public void testAddDisseminator() {
    }

    public void testModifyDigitalObject() {
    }

    public void testModifyDatastream() {
    }

    public void testModifyDissemination() {
    }

    public void testDeleteDigitalObject() throws Exception {
        DigitalObject obj = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR +
 "/dataobjects/demo_10.xml"));
        m_ri.deleteDigitalObject(obj);
    }

    public void testDeleteDatastream() {
    }

    public void testDeleteDissemination() {
    }
        
    public void testAddAndDelete() throws Exception {
        DigitalObject bdef = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bdefs/demo_8.xml"));
        DigitalObject bmech = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bmechs/demo_9.xml"));
        DigitalObject dataobject = getDigitalObject(new File(
                DEMO_OBJECTS_ROOT_DIR + "/dataobjects/demo_10.xml"));
    
        m_ri.addDigitalObject(bdef);
        m_ri.commit();
        int a = m_ri.countTriples(null, null, null, 0);
        assertTrue(m_ri.countTriples(null, null, null, 0) > 0);
        
        m_ri.addDigitalObject(bmech);
        m_ri.commit();
        int b = m_ri.countTriples(null, null, null, 0);
        assertTrue(b > a);
        
        m_ri.addDigitalObject(dataobject);
        m_ri.commit();
        int c = m_ri.countTriples(null, null, null, 0);
        assertTrue(c > b);
        
        m_ri.export(new FileOutputStream("/tmp/out.rdf"), RDFFormat.RDF_XML);
        
        m_ri.deleteDigitalObject(dataobject);
        m_ri.commit();
        int d = m_ri.countTriples(null, null, null, 0);
        assertTrue(d == b);
        
        m_ri.deleteDigitalObject(bmech);
        m_ri.commit();
        
        int e = m_ri.countTriples(null, null, null, 0);
        assertTrue(e == a);
        
        m_ri.deleteDigitalObject(bdef);
        m_ri.commit();
        int f = m_ri.countTriples(null, null, null, 0);
        assertEquals(m_ri.countTriples(null, null, null, 0), 0);
    }

    private void addDigitalObjects(File dir) throws Exception {
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

    private void addDigitalObject(File file) throws Exception {
        FileInputStream in;
        DigitalObject obj = new BasicDigitalObject();

        in = new FileInputStream(file);
        FOXMLDODeserializer deser = new FOXMLDODeserializer();
        deser.deserialize(in, obj, "UTF-8", 0);
        addDigitalObject(obj);
    }

    private void addDigitalObject(DigitalObject digitalObject) throws Exception {
        m_ri.addDigitalObject(digitalObject);
    }

    private void addBDefs() throws Exception {
        File dir = new File(DEMO_OBJECTS_ROOT_DIR + "/bdefs");
        addDigitalObjects(dir);
    }

    private void addBMechs() throws Exception {
        File dir = new File(DEMO_OBJECTS_ROOT_DIR + "/bmechs");
        addDigitalObjects(dir);
    }

    private void addDataObjects() throws Exception {
        File dir = new File(DEMO_OBJECTS_ROOT_DIR + "/dataobjects");
        addDigitalObjects(dir);
    }

    private DigitalObject getDigitalObject(File file) throws Exception {
        FileInputStream in;
        DigitalObject obj = new BasicDigitalObject();

        in = new FileInputStream(file);
        FOXMLDODeserializer deser = new FOXMLDODeserializer();
        deser.deserialize(in, obj, "UTF-8", 0);
        return obj;
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