package fedora.server.resourceIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Map;

import org.kowari.itql.ItqlInterpreterBean;
import org.kowari.query.Answer;
import org.kowari.query.rdf.Tucana;
import org.kowari.server.driver.SessionFactoryFinder;
import org.kowari.server.local.LocalSessionFactory;
import org.kowari.store.DatabaseSession;
import org.kowari.store.jena.GraphKowariMaker;
import org.kowari.store.jena.ModelKowariMaker;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdql.Value;
import com.hp.hpl.jena.shared.ReificationStyle;

import fedora.server.storage.translation.FOXMLDODeserializer;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.Disseminator;
import junit.framework.TestCase;

/**
 * @author eddie
 *
 */
public class TestFullTextModel extends TestCase {
    private static final String MODEL_NAME = "resourceIndex";
	private static final String FULL_TEXT_MODEL_NAME = MODEL_NAME +"-fullText";
	private static final String TEXT_MODEL_TYPE = Tucana.NAMESPACE + "LuceneModel";
	private static final String SERVER_NAME = "fedora";
	private URI modelURI, fullTextModelURI;
	private ResourceIndexImpl m_resourceIndex;
	private RIStore m_kowariRIStore;
	private Model model;
	private LocalSessionFactory factory;
	private KowariResultIterator results;
	
    private static final String SERVER_HOST = "localhost";
    private static final String LOCAL_SERVER_PATH = "/tmp/kowariTest";
    
    private static final String DEMO_OBJECTS_ROOT_DIR = "src/test/fedora/server/resourceIndex/foxmlTestObjects";
    
    private ResourceIndex ri;
    
    private DatabaseSession session;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestFullTextModel.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
		String serverhost = "localhost";
		URI serverURI = new URI("rmi", serverhost, "/" + SERVER_NAME, null);
		modelURI = new URI(serverURI.toString() + "#" + MODEL_NAME);
		fullTextModelURI = new URI(serverURI.toString() + "#" + FULL_TEXT_MODEL_NAME);
		File serverDir = new File(LOCAL_SERVER_PATH + "/" + MODEL_NAME);
		serverDir.mkdirs();
		factory = (LocalSessionFactory) SessionFactoryFinder.newSessionFactory(serverURI);
		if (factory.getDirectory() == null) {
		    factory.setDirectory(serverDir);
		}
		session = (DatabaseSession) factory.newSession();

		//add
		GraphKowariMaker graphMaker = new GraphKowariMaker(session,
				serverURI, ReificationStyle.Minimal);
		ModelKowariMaker modelMaker = new ModelKowariMaker(graphMaker);
		model = modelMaker.openModel(MODEL_NAME);
		
		// add the full-text model
		if (!session.modelExists(fullTextModelURI)) {
		    System.out.println("*** fullTextModel does not exist (yet) ***");
		    URI fullTextModelTypeURI = new URI(TEXT_MODEL_TYPE);
		    session.createModel(fullTextModelURI, fullTextModelTypeURI);
		}
		
        m_kowariRIStore = new KowariRIStore(session, model, fullTextModelURI);
        ri = new ResourceIndexImpl(3, m_kowariRIStore, null);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {      
        try {
            if (model != null) {
                try {
                    model.close();
                } finally {
                    model = null;
                }
            }
            if (session != null) {
                session.close();
            }
            if (factory != null) {
                factory.delete();
            }

        } finally {
            results = null;
            deleteDirectory(LOCAL_SERVER_PATH);
        }
    }

    public void testAddDigitalObject() throws Exception {
        // needed by the deserializer
        System.setProperty("fedoraServerHost", "localhost");
		System.setProperty("fedoraServerPort", "8080");
        addDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR + "/bdefs/demo_8.xml"));
        addDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR + "/bmechs/demo_9.xml"));
        addDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR + "/dataobjects/demo_10.xml"));
        
    	m_kowariRIStore.write(new FileOutputStream("/tmp/out.rdf"));
    
        String queryString = "select $s $p $o " +
        			         "from <" + modelURI + "> " +
        			         "where $s $p $o and $s $p 'J*ff*' " +
        			         "in <" + fullTextModelURI.toString() + ">;";
        
        //queryString = "select $s $p $o from <"+ modelURI +"> where $s $p $o and ($o <http://tucana.org/tucana#is> 'lawn');";
        //queryString = "select $s $o from <"+ modelURI +"> where $s $p $o;";
        
        RIQuery query = new ITQLQuery(queryString);
        results = (KowariResultIterator)ri.executeQuery(query);
        
        //
        //KowariResultIterator it = query(queryString);
        
        while (results.hasNext()) {
            Map map = results.next();
            Value s = (Value)map.get("s");
            //Value p = (Value)map.get("p");
            Value o = (Value)map.get("o");
            System.out.println(s + " " + o);
            //System.out.println(s + " " + p + " " + o);
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
    
    private KowariResultIterator query(String queryString) throws Exception {
        ItqlInterpreterBean interpreter = new ItqlInterpreterBean(session, null);
		Answer answer = interpreter.executeQuery(queryString);
		return new KowariResultIterator(answer);
    }
    
    private boolean deleteDirectory(String directory) {
        boolean result = false;

        if (directory != null) {
            File file = new File(directory);
            if (file.exists() && file.isDirectory()) {
                //1. delete content of directory:
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
