package fedora.server.resourceIndex;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.util.Map;
import java.util.HashMap;

import fedora.server.storage.ConnectionPool;
import fedora.server.storage.translation.FOXMLDODeserializer;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.BasicDigitalObject;

import org.trippi.TriplestoreConnector;
import org.trippi.TriplestoreWriter;

import junit.framework.TestCase;

/**
 * @author eddie
 *  
 */
public class TestResourceIndexTimings extends TestCase {
    private static final String LOCAL_SERVER_PATH = "/tmp/fedoraTest";
    private static final String MODEL_NAME = "testResourceIndex";
    private static final String SERVER_NAME = "testFedora";
    private static final String DEMO_OBJECTS_ROOT_DIR = "src/test/fedora/server/resourceIndex/foxmlTestObjects";

    private static final String CP_DRIVER = "com.mysql.jdbc.Driver";
    private static final String CP_URL = "jdbc:mysql://localhost/fedora20?useUnicode=true&amp;characterEncoding=UTF-8&amp;autoReconnect=true";
    private static final String CP_USERNAME = "fedoraAdmin";
    private static final String CP_PASSWORD = "fedoraAdmin";
    private static final int CP_ICONN = 1;
    private static final int CP_MAXCONN = 1;
    private static final boolean CP_WAIT = true;

    private static final String TRIPPI_CONNECTOR_CLASSNAME = "org.trippi.impl.kowari.KowariConnector";

    private ResourceIndex m_ri;
    private ConnectionPool m_cPool;
    private TriplestoreConnector m_conn;
    private TriplestoreWriter m_writer;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestResourceIndexTimings.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        // the database connection pool
        m_cPool = new ConnectionPool(CP_DRIVER, CP_URL, CP_USERNAME, CP_PASSWORD,
                CP_ICONN, CP_MAXCONN, CP_WAIT);

        // trippi
        Map config = new HashMap();
        config.put("modelName", MODEL_NAME);
        config.put("poolMaxGrowth", "-1");
        config.put("path", LOCAL_SERVER_PATH);
        config.put("bufferFlushBatchSize", "20000");
        config.put("autoCreate", "true");
        config.put("bufferSafeCapacity", "40000");
        config.put("autoFlushDormantSeconds", "15");
        config.put("poolInitialSize", "3");
        config.put("serverName", SERVER_NAME);
        config.put("readOnly", "false");
        config.put("autoTextIndex", "false");
        config.put("autoFlushBufferSize", "20000");
        config.put("remote", "false");
        config.put("memoryBuffer", "true");

        m_conn = TriplestoreConnector.init(TRIPPI_CONNECTOR_CLASSNAME, config);
        m_writer = m_conn.getWriter();
        m_ri = new ResourceIndexImpl(1, m_conn, m_cPool, null);

        // needed by the deserializer
        System.setProperty("fedoraServerHost", "localhost");
        System.setProperty("fedoraServerPort", "8080");
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        if (m_writer != null) {
            m_writer.close();
            m_writer = null;
        }
        if (m_conn != null) {
            m_conn.close();
            m_conn = null;
        }
        if (m_cPool != null) {
            m_cPool.closeAllConnections();
            m_cPool = null;
        }
        m_ri = null;
        deleteDirectory(LOCAL_SERVER_PATH);
    }

    public void testAddAndDelete() throws Exception {
        DigitalObject bdef = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bdefs/demo_8.xml"));
        DigitalObject bmech = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bmechs/demo_9.xml"));
        DigitalObject dataobject = getDigitalObject(new File(
                DEMO_OBJECTS_ROOT_DIR + "/dataobjects/demo_10.xml"));

        long add_start = System.currentTimeMillis();
        m_ri.addDigitalObject(bdef);
        m_ri.addDigitalObject(bmech);
        for (int i = 0; i < 100; i++) {
            dataobject.setPid("test:" + i);
            m_ri.addDigitalObject(dataobject);
        }
        m_writer.flushBuffer();
        long add_stop = System.currentTimeMillis();

        //long mod_start = System.currentTimeMillis();
        m_ri.modifyDigitalObject(bdef);
        m_ri.modifyDigitalObject(bmech);
        long mod_start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            dataobject.setPid("test:" + i);
            m_ri.modifyDigitalObject(dataobject);
        }
        m_writer.flushBuffer();
        long mod_stop = System.currentTimeMillis();
        
        long del_start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            dataobject.setPid("test:" + i);
            m_ri.deleteDigitalObject(dataobject);
        }
        m_ri.deleteDigitalObject(bmech);
        m_ri.deleteDigitalObject(bdef);
        m_writer.flushBuffer();
        long del_stop = System.currentTimeMillis();
        
        System.out.println("Add time: " + (add_stop - add_start));
        System.out.println("Modify time: " + (mod_stop - mod_start));
        System.out.println("Delete time: " + (del_stop - del_start));
        
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