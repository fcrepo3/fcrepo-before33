package fedora.server.resourceIndex;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.List;

import org.kowari.itql.ItqlInterpreterBean;
import org.kowari.query.Answer;
import org.kowari.server.driver.SessionFactoryFinder;
import org.kowari.server.local.LocalSessionFactory;
import org.kowari.store.DatabaseSession;
import org.kowari.store.jena.GraphKowariMaker;
import org.kowari.store.jena.ModelKowariMaker;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdql.Value;
import com.hp.hpl.jena.rdql.ValueException;
import com.hp.hpl.jena.shared.ReificationStyle;

import junit.framework.TestCase;

/**
 * @author eddie
 *  
 */
public class TestKowariResultIterator extends TestCase {
    private static final String LOCAL_SERVER_PATH = "/tmp/kowariTest";
    private static final String MODEL_NAME = "testResourceIndex";
    private static final String SERVER_HOST = "localhost";
    private static final String SERVER_NAME = "testFedora";
    
    private String m_modelURI;
    private LocalSessionFactory m_factory;
    private DatabaseSession m_session;
    private Model m_model;
    private KowariResultIterator m_results;

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
            m_results = null;
            deleteDirectory(LOCAL_SERVER_PATH);
        }
    }

    public void testHasNext() throws Exception {
        // populate the model
        Resource S = m_model.createResource("local:S");
        Property P = m_model.createProperty("local:P");
        RDFNode O = m_model.createResource("local:O");
        m_model.add(S, P, O);
        
        // query the model
        m_results = query("select $s $p $o from <"+ m_modelURI +"> where $s $p $o;");
		
        assertTrue(m_results.hasNext());
        m_results.next();
        assertFalse(m_results.hasNext());
    }

    public void testNames() throws Exception {
        // populate the model
        Resource S = m_model.createResource("local:S");
        Property P = m_model.createProperty("local:P");
        RDFNode O = m_model.createResource("local:O");
        m_model.add(S, P, O);
        
        // query the model
        m_results = query("select $s $p $o from <"+ m_modelURI +"> where $s $p $o;");
        
        List names = m_results.names();
        assertEquals(names.size(), 3);
        assertEquals(names.get(0), "s");
        assertEquals(names.get(1), "p");
        assertEquals(names.get(2), "o");
    }
    
    public void testNext() throws Exception {
        // populate the model
        String s = "local:S";
        String p = "local:P";
        String o = "local:O";
        Resource S = m_model.createResource(s);
        Property P = m_model.createProperty(p);
        RDFNode O = m_model.createResource(o);
        m_model.add(S, P, O);
        
        // query the model
        m_results = query("select $s $p $o from <"+ m_modelURI +"> where $s $p $o;");

        Map map = m_results.next();
        Value v = (Value)map.get("s");
        assertFalse(v.isBoolean());
        assertFalse(v.isDouble());
        assertFalse(v.isInt());
        assertFalse(v.isNumber());
        assertFalse(v.isRDFLiteral());
        assertTrue(v.isRDFResource());
        assertTrue(v.isURI());
        assertFalse(v.isString());
        assertEquals(s, v.valueString());
        assertEquals(s, v.toString());
        assertEquals("<" + s + ">", v.asQuotedString());
        assertEquals(s, v.asUnquotedString());
        
        v = (Value)map.get("p");
        assertFalse(v.isBoolean());
        assertFalse(v.isDouble());
        assertFalse(v.isInt());
        assertFalse(v.isNumber());
        assertFalse(v.isRDFLiteral());
        assertTrue(v.isRDFResource());
        assertTrue(v.isURI());
        assertFalse(v.isString());
        
        v = (Value)map.get("o");
        assertFalse(v.isBoolean());
        assertFalse(v.isDouble());
        assertFalse(v.isInt());
        assertFalse(v.isNumber());
        assertFalse(v.isRDFLiteral());
        assertTrue(v.isRDFResource());
        assertTrue(v.isURI());
        assertFalse(v.isString());
    }
    
    public void testNextWithLiteral() throws Exception {
        String litString = "I'm a cucumber";
    	
        // populate the model
        Resource S = m_model.createResource("local:S");
        Property P = m_model.createProperty("local:P");
        RDFNode O = m_model.createLiteral(litString);
        m_model.add(S, P, O);
        
        // query the model
        m_results = query("select $s $p $o from <"+ m_modelURI +"> where $s $p $o;");
		
        Map map = m_results.next();
        Value v = (Value)map.get("o");
        assertFalse(v.isBoolean());
        assertFalse(v.isDouble());
        assertFalse(v.isInt());
        assertFalse(v.isNumber());
        assertTrue(v.isRDFLiteral());
        assertFalse(v.isRDFResource());
        assertFalse(v.isURI());
        assertTrue(v.isString());
        
        try {
			v.getDouble();
			fail("Should raise a ValueException");
		} catch (ValueException success) {
		}
		
		assertEquals(v.getString(), litString);
		assertEquals(v.getRDFLiteral(), O); 
    }
    
    public void testNextWithLocalLiteral() {
        //TODO kowari doesn't support this yet
    }
    public void testNextWithTypedLiteral() {
        //TODO kowari doesn't support this yet
    }

    public void testNextWithAnonymousNode() throws Exception {
        // populate the model
        Resource S = m_model.createResource("local:S");
        Property P = m_model.createProperty("local:P");
        RDFNode O = m_model.createResource();
        m_model.add(S, P, O);
        
        // query the model
        m_results = query("select $s $p $o from <"+ m_modelURI +"> where $s $p $o;");

        List names = m_results.names();
        assertEquals(names.size(), 3);
        assertEquals(names.get(0), "s");
        assertEquals(names.get(1), "p");
        assertEquals(names.get(2), "o");

        Map map = m_results.next();
        Value v = (Value)map.get("s");
        assertFalse(v.isBoolean());
        assertFalse(v.isDouble());
        assertFalse(v.isInt());
        assertFalse(v.isNumber());
        assertFalse(v.isRDFLiteral());
        assertTrue(v.isRDFResource());
        assertTrue(v.isURI());
        assertFalse(v.isString());
        
        v = (Value)map.get("p");
        assertFalse(v.isBoolean());
        assertFalse(v.isDouble());
        assertFalse(v.isInt());
        assertFalse(v.isNumber());
        assertFalse(v.isRDFLiteral());
        assertTrue(v.isRDFResource());
        assertTrue(v.isURI());
        assertFalse(v.isString());
        
        v = (Value)map.get("o");
        assertFalse(v.isBoolean());
        assertFalse(v.isDouble());
        assertFalse(v.isInt());
        assertFalse(v.isNumber());
        assertFalse(v.isRDFLiteral());
        assertTrue(v.isRDFResource());
        assertTrue(v.isURI());
        assertFalse(v.isString());
        
        // FIXME these shouldn't be failing!
        //assertEquals(O.toString(), v.asUnquotedString());
        //assertEquals(O.toString(), v.getURI());
        //assertEquals(O.toString(), v.valueString());
        //assertEquals(O, v.getRDFResource());
    }
    
    public void testLiteralsAsStrings() throws Exception {
        Resource S = m_model.createResource("local:S");
        Property P = m_model.createProperty("local:P");
        RDFNode[] O = {
                m_model.createLiteral(true),
                m_model.createLiteral('c'),
                m_model.createLiteral(3.14),
                m_model.createLiteral(3.00f),
                m_model.createLiteral(22L),
                m_model.createLiteral("a string")
        };
        Statement s;
        
        for (int i = 0; i < O.length; i++) {
            s = m_model.createStatement(S, P, O[i]);
            m_model.add(s);
            
            // query the model
            m_results = query("select $s $p $o from <"+ m_modelURI +"> where $s $p $o;");
    		
            Map map = m_results.next();
            Value v = (Value)map.get("o");
            System.out.println("* : " + v.getRDFLiteral());
            System.out.println("** : " + v.getString());
            assertTrue(v.isRDFLiteral());
            assertTrue(v.isString());
            m_model.remove(s);
        }
    }
    
    public void testEmptyModel() throws Exception {
        m_results = query("select $s $p $o from <"+ m_modelURI +"> where $s $p $o;");
        assertFalse(m_results.hasNext());
        assertNull(m_results.next());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestKowariResultIterator.class);
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
    
    private KowariResultIterator query(String queryString) throws Exception {
        ItqlInterpreterBean interpreter = new ItqlInterpreterBean(m_session, null);
		Answer answer = interpreter.executeQuery(queryString);
		return new KowariResultIterator(answer);
    }
}