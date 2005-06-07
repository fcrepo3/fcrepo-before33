package fedora.test.integration;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.custommonkey.xmlunit.SimpleXpathEngine;
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
        TestSuite suite = new TestSuite(TestAPIALite.class);
        TestSetup wrapper = new FedoraServerTestSetup(suite) {
            public void setUp() throws Exception {
                TestIngestDemoObjects.ingestDemoObjects();
                fcfg = getServerConfiguration();
                client = new FedoraClient(getBaseURL(), getUsername(), getPassword());
                factory = DocumentBuilderFactory.newInstance();
                builder = factory.newDocumentBuilder();
                demoObjects = TestIngestDemoObjects.demoObjects;
                SimpleXpathEngine.registerNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
                SimpleXpathEngine.registerNamespace(NS_FEDORA_TYPES_PREFIX, NS_FEDORA_TYPES);
                SimpleXpathEngine.registerNamespace("demo", "http://example.org/ns#demo");
            }
            
            public void tearDown() throws Exception {
                SimpleXpathEngine.clearNamespaces();
                TestIngestDemoObjects.purgeDemoObjects();
            }
        };
        return new FedoraServerTestSetup(wrapper);
                
    }
    
    public void testDescribeRepository() throws Exception {
        Document result = getQueryResult("/describe?xml=true");
        assertXpathEvaluatesTo(fcfg.getParameter("repositoryName").getValue(), 
                               "/fedoraRepository/repositoryName", 
                               result);
    }
    
    public void testFindObjects() throws Exception {
        Document result = getQueryResult("/search?query=pid%7Edemo:*&pid=true&maxResults=100&xml=true");
        assertXpathExists("/" + NS_FEDORA_TYPES_PREFIX + ":result/" + 
                          NS_FEDORA_TYPES_PREFIX + ":resultList/" + 
                          NS_FEDORA_TYPES_PREFIX + ":objectFields/" + 
                          NS_FEDORA_TYPES_PREFIX + ":pid", result);
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
	        // FIXME devise a better test
	        assertXpathExists("/fedoraObjectHistory", result);
        }
    }
    
    public void testGetObjectProfile() throws Exception {
        Document result;
        for (int i = 0; i < demoObjects.length; i ++) {
	        result = getQueryResult("/get/" + demoObjects[i] + "?xml=true");
	        // FIXME devise a better test
	        assertXpathExists("/objectProfile", result);
        }
    }
    
    public void testListDatastreams() throws Exception {
        Document result;
        for (int i = 0; i < demoObjects.length; i ++) {
	        result = getQueryResult("/listDatastreams/" + demoObjects[i] + "?xml=true");
	        assertXpathExists("/objectDatastreams", result);
        }
    }
    
    public void testListMethods() throws Exception {
        Document result;
        // FIXME need to ensure we don't try this on bdefs or bmechs
        String[] demoObjects = { "demo:5", "demo:6", "demo:7", "demo:10", 
                "demo:11", "demo:14", "demo:17", "demo:18", "demo:21", "demo:26", 
                "demo:29", "demo:30", "demo:31", "demo:SmileyBeerGlass", 
                "demo:SmileyBucket", "demo:SmileyDinnerware", 
                "demo:SmileyEarring", "demo:SmileyKeychain", 
                "demo:SmileyNightlight", "demo:SmileyPens", 
                "demo:SmileyShortRoundCup", "demo:SmileyStuff", 
                "demo:SmileyTallRoundCup", "demo:SmileyToiletBrush", 
                "demo:SmileyWastebasket"
                };
        for (int i = 0; i < demoObjects.length; i ++) {
	        result = getQueryResult("/listMethods/" + demoObjects[i] + "?xml=true");
	        assertXpathExists("/objectMethods", result);
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
