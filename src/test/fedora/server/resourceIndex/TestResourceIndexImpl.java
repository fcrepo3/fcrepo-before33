package fedora.server.resourceIndex;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;

import org.kowari.server.driver.SessionFactoryFinder;
import org.kowari.server.local.LocalSessionFactory;
import org.kowari.store.DatabaseSession;
import org.kowari.store.jena.GraphKowariMaker;
import org.kowari.store.jena.ModelKowariMaker;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.shared.ReificationStyle;

import fedora.server.storage.translation.DOTranslationUtility;
import fedora.server.storage.translation.DOTranslatorImpl;
import fedora.server.storage.translation.FOXMLDODeserializer;
import fedora.server.storage.translation.FOXMLDOSerializer;
import fedora.server.storage.types.AuditRecord;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.Disseminator;
import junit.framework.TestCase;

/**
 * @author eddie
 *
 */
public class TestResourceIndexImpl extends TestCase {
    private static final String LOCAL_SERVER_PATH = "/tmp/kowariTest";
    private static final String MODEL_NAME = "testResourceIndex";
    private static final String SERVER_HOST = "localhost";
    private static final String SERVER_NAME = "testFedora";
    private static final String DEMO_OBJECTS_ROOT_DIR = "src/test/fedora/server/resourceIndex/foxmlTestObjects";
    
    private String m_modelURI;
    private LocalSessionFactory m_factory;
    private DatabaseSession m_session;
    private Model m_model;
    private RIStore m_kowariRIStore;
    private ResourceIndex ri;
    private DigitalObject digitalObject, m_bDef, m_bMech;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestResourceIndexImpl.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        URI serverURI = new URI("rmi", SERVER_HOST, "/" + SERVER_NAME, null);
        m_modelURI = serverURI.toString() + "#" + MODEL_NAME;
        File serverDir = new File(LOCAL_SERVER_PATH + "/" + MODEL_NAME);
        serverDir.mkdirs();
        m_factory = (LocalSessionFactory)SessionFactoryFinder.newSessionFactory(serverURI);
		if (m_factory.getDirectory() == null) {
		    m_factory.setDirectory(serverDir);
		}
		m_session = (DatabaseSession) m_factory.newSession();

        // create the model
        GraphKowariMaker graphMaker = new GraphKowariMaker(m_session, serverURI,
                ReificationStyle.Minimal);
        ModelKowariMaker modelMaker = new ModelKowariMaker(graphMaker);
        m_model = modelMaker.createModel(MODEL_NAME);
        m_kowariRIStore = new KowariRIStore(m_session, m_model);
        ri = new ResourceIndexImpl(3, m_kowariRIStore, null);
        //digitalObject = getDigitalObject();
        
        // needed by the deserializer
        System.setProperty("fedoraServerHost", "localhost");
		System.setProperty("fedoraServerPort", "8080");
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        try {
            if (m_model != null) {
                try {
                    m_model.close();
                } finally {
                    m_model = null;
                }
            }
            if (m_session != null) {
                m_session.close();
            }
            if (m_factory != null) {
                m_factory.delete();
            }

        } finally {
            ri = null;
            m_kowariRIStore = null;
            digitalObject = null;
        }
        
    }

//    public void testExecuteQuery() {
//    }

//    public void testAddDigitalObjects() throws Exception {
//        addBDefs();
//        addBMechs();
//        addDataObjects();
//    	
//    	m_kowariRIStore.write(new FileOutputStream("/tmp/out.rdf"));
//    }
    
    public void testAddDigitalObject() throws Exception {
        //addBDefs();
        //addBMechs();
        //addDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR + "/dataobjects/demo_5.xml"));
        
        //addDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR + "/bdefs/demo_1.xml"));
        //addDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR + "/bmechs/demo_2.xml"));
        //addDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR + "/dataobjects/demo_5.xml"));
        addDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR + "/bdefs/demo_8.xml"));
        addDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR + "/bmechs/demo_9.xml"));
        addDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR + "/dataobjects/demo_10.xml"));
        
    	m_kowariRIStore.write(new FileOutputStream("/tmp/out.rdf"));
    }

//    public void testAddDatastream() {
//    }
//
//    public void testAddDissemination() {
//    }
//
//    public void testModifyDigitalObject() {
//    }
//
//    public void testModifyDatastream() {
//    }
//
//    public void testModifyDissemination() {
//    }
//
//    public void testDeleteDigitalObject() {
//    }
//
//    public void testDeleteDatastream() {
//    }
//
//    public void testDeleteDissemination() {
//    }
    
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
        FOXMLDODeserializer deser=new FOXMLDODeserializer();
        deser.deserialize(in, obj, "UTF-8", 0);
        ri.addDigitalObject(obj);
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

}
