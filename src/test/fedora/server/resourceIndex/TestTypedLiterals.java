package fedora.server.resourceIndex;

import java.io.File;
import java.net.URI;

import org.kowari.server.driver.SessionFactoryFinder;
import org.kowari.server.local.LocalSessionFactory;
import org.kowari.store.DatabaseSession;
import org.kowari.store.jena.GraphKowariMaker;
import org.kowari.store.jena.KowariQueryEngine;
import org.kowari.store.jena.ModelKowariMaker;
import org.kowari.store.jena.RdqlQuery;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdql.Query;
import com.hp.hpl.jena.rdql.QueryEngine;
import com.hp.hpl.jena.rdql.QueryExecution;
import com.hp.hpl.jena.rdql.QueryResults;
import com.hp.hpl.jena.rdql.ResultBinding;
import com.hp.hpl.jena.rdql.Value;
import com.hp.hpl.jena.shared.ReificationStyle;

import junit.framework.TestCase;

/**
 * @author eddie
 *  
 */
public class TestTypedLiterals extends TestCase {
    private Model m_model;

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
        m_model = null;
    }
    
    public void testKowari() throws Exception {
        System.out.println("**************************************************");
        System.out.println("* Begin Kowari Test ...");
        String LOCAL_SERVER_PATH = "/tmp/kowariTest";
        String MODEL_NAME = "testResourceIndex";
        String SERVER_HOST = "localhost";
        String SERVER_NAME = "testFedora";
        LocalSessionFactory m_factory;
        DatabaseSession m_session;
        
        URI serverURI = new URI("rmi", SERVER_HOST, "/" + SERVER_NAME, null);
        String modelURI = serverURI.toString() + "#" + MODEL_NAME;
        File serverDir = new File(LOCAL_SERVER_PATH + "/" + MODEL_NAME);
        serverDir.mkdirs();
        m_factory = (LocalSessionFactory)SessionFactoryFinder.newSessionFactory(serverURI);
        m_factory.setDirectory(serverDir);
		m_session = (DatabaseSession) m_factory.newSession();

        GraphKowariMaker graphMaker = new GraphKowariMaker(m_session, serverURI,
                ReificationStyle.Minimal);
        ModelKowariMaker modelMaker = new ModelKowariMaker(graphMaker);
        m_model = modelMaker.createModel(MODEL_NAME);
        
        populateQueryAndTest();
        System.out.println("* End Kowari Test");
        System.out.println("**************************************************");
    }
    
    private void populateQueryAndTest() {
        // populate the model
        String litString = "3.14";
    	String type = "http://www.w3.org/2001/XMLSchema#double";
    	double d = 3.14;
    	int i = 3;
        Resource S = m_model.createResource("local:S");
        Property P = m_model.createProperty("local:P");
        RDFNode O = m_model.createTypedLiteral(litString, type);
        //RDFNode O = m_model.createTypedLiteral(d);
        m_model.add(S, P, O);
        
        // query the model
		RdqlQuery q = new RdqlQuery("SELECT ?s ?p ?o WHERE (?s ?p ?o)");
		q.setSource(m_model);
		QueryExecution qe = new KowariQueryEngine(q);
		QueryResults results = qe.exec();
		
		if (results.hasNext()) {
		    ResultBinding result = (ResultBinding)results.next();
	        Value v = (Value)result.getValue("o");
	        assertFalse(v.isBoolean());
	        assertTrue(v.isNumber());
	        assertTrue(v.isDouble());
	        assertTrue(v.isRDFLiteral());
	        assertFalse(v.isRDFResource());
	        assertFalse(v.isURI());
	        assertTrue(v.isString());
	        
	        System.out.println("* O: " + O);
	        System.out.println("* rdfLiteral: " + v.getRDFLiteral());
			System.out.println("* lexicalForm: " + v.getRDFLiteral().getLexicalForm());
			System.out.println("* datatype: " + v.getRDFLiteral().getDatatype());
			System.out.println("* datatypeURI: " + v.getRDFLiteral().getDatatypeURI());
			
			//assertEquals(v.getRDFLiteral(), O);
		} else {
		    fail("no results to test!");
		}
    }
}