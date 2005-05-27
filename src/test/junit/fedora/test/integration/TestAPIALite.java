package fedora.test.integration;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.w3c.dom.Document;

import fedora.client.FedoraClient;
import fedora.server.config.ServerConfiguration;
import fedora.test.FedoraServerTestCase;
import fedora.test.FedoraServerTestSetup;

/**
 * Test of API-A-Lite using demo objects
 * 
 * @author Edwin Shin
 */
public class TestAPIALite extends FedoraServerTestCase {
    private static DocumentBuilderFactory factory;
    private static DocumentBuilder builder;
    private static ServerConfiguration fcfg;
    private static FedoraClient client;
    private static String[] demoObjects;
    
    public static Test suite() {
        TestSuite testSuite = new TestSuite(TestAPIALite.class);
        TestSetup wrapper = new FedoraServerTestSetup(testSuite) {
            public void setUp() throws Exception {
                super.setUp();
                TestIngestDemoObjects.ingestDemoObjects();
                fcfg = getServerConfiguration();
                client = new FedoraClient(getBaseURL(), getUsername(), getPassword());
                factory = DocumentBuilderFactory.newInstance();
                builder = factory.newDocumentBuilder();
                demoObjects = TestIngestDemoObjects.demoObjects;
            }
            
            public void tearDown() throws Exception {
                TestIngestDemoObjects.purgeDemoObjects();
                super.tearDown();
            }
        };
        return wrapper;
                
    }
    
    public void testDescribeRepository() throws Exception {
        Document result = getQueryResult("/describe?xml=true");
        assertXpathEvaluatesTo(fcfg.getParameter("repositoryName").getValue(), 
                               "/fedoraRepository/repositoryName", 
                               result);
    }
    
    public void testFindObjects() throws Exception {
        Document result = getQueryResult("/search?query=pid%7Edemo:*&pid=true&maxResults=100&xml=true");
        assertXpathExists("/result/resultList/objectFields/pid", result);
    }
    
    public void testGetDatastreamDissemination() throws Exception {
        Document result;
        for (int i = 0; i < demoObjects.length; i ++) {
            // just checking DC
	        result = getQueryResult("/get/" + demoObjects[i] + "/DC?xml=true");
	        assertXpathExists("/oai_dc:dc", result);
        }
    }
    
    public void testGetDissemination() {
        
    }
    
    public void testGetObjectHistory() throws Exception {
        Document result;
        for (int i = 0; i < demoObjects.length; i ++) {
	        result = getQueryResult("/getObjectHistory/" + demoObjects[i] + "?xml=true");
	        assertXpathExists("/fedoraObjectHistory[@pid=" + demoObjects[i] + "]", result);
        }
    }
    
    public void testGetObjectProfile() throws Exception {
        Document result;
        for (int i = 0; i < demoObjects.length; i ++) {
	        result = getQueryResult("/get/" + demoObjects[i] + "?xml=true");
	        assertXpathExists("/objectProfile[@pid=" + demoObjects[i] + "]", result);
        }
    }
    
    public void testListDatastreams() throws Exception {
        Document result;
        for (int i = 0; i < demoObjects.length; i ++) {
	        result = getQueryResult("/listDatastreams/" + demoObjects[i] + "?xml=true");
	        assertXpathExists("/objectDatastreams[@pid=" + demoObjects[i] + "]", result);
        }
    }
    
    public void testListMethods() throws Exception {
        Document result;
        for (int i = 0; i < demoObjects.length; i ++) {
	        result = getQueryResult("/listMethods/" + demoObjects[i] + "?xml=true");
	        assertXpathExists("/objectMethods[@pid=" + demoObjects[i] + "]", result);
        }
    }
    
    public void testResumeFindObjects() {
        
    }
    
    /**
     * 
     * @param location a URL relative to the Fedora base URL
     * @return
     * @throws Exception
     */
    private Document getQueryResult(String location) throws Exception {
        InputStream is = client.get(getBaseURL() + location, true);
        return builder.parse(is);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIALite.class);
    }
}
