package fedora.test.integration;  

import java.io.InputStream;
import fedora.client.HttpInputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.custommonkey.xmlunit.SimpleXpathEngine;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;


import fedora.client.FedoraClient;
import fedora.server.config.ServerConfiguration;
import fedora.test.FedoraServerTestCase;
import fedora.test.FedoraServerTestSetup;
import fedora.test.SuperAPIALite;
import fedora.test.Trial;
import fedora.test.DescribeRepositoryTest;
import fedora.test.HttpDataSource;
/**
 * Test of API-A-Lite using demo objects
 * 
 * @author Bill Niebel 
 */
public class TestAPIALite extends SuperAPIALite {
	
	private DescribeRepositoryTest describeRepositoryTestXmlOnly = new DescribeRepositoryTest("Fedora Repository", true);
	private HttpDataSource HTTP200 = null;
	private HttpDataSource HTTPS200 = null;
	
    public TestAPIALite() throws Exception {
    	super();
    	HTTP200 = new HttpDataSource(Trial.HTTP_BASE_URL, 200);
    	HTTPS200 = new HttpDataSource(Trial.HTTPS_BASE_URL, 200);
    }
    
    public static Test suite() {
        //TestSuite suite = new TestSuite(TestAPIALite.class);
        TestSuite suite = new TestSuite("APIALite TestSuite");
        suite.addTestSuite(TestAPIALite.class);
        //TestSetup wrapper = new TestSetup(suite, TestAPIALite.class.getName()) {        
        TestSetup wrapper = new TestSetup(suite) {
            public void setUp() throws Exception {
                TestIngestDemoObjects.ingestDemoObjects();
                fcfg = getServerConfiguration();
                client = new FedoraClient(getBaseURL(), getUsername(), getPassword());
                factory = DocumentBuilderFactory.newInstance();
                builder = factory.newDocumentBuilder();
                demoObjects = TestIngestDemoObjects.getDemoObjects(null);
                SimpleXpathEngine.registerNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
                SimpleXpathEngine.registerNamespace(NS_FEDORA_TYPES_PREFIX, NS_FEDORA_TYPES);
                SimpleXpathEngine.registerNamespace("demo", "http://example.org/ns#demo");
                SimpleXpathEngine.registerNamespace(NS_XHTML_PREFIX, NS_XHTML); //                
            }
            
            public void tearDown() throws Exception {
                SimpleXpathEngine.clearNamespaces();
                TestIngestDemoObjects.purgeDemoObjects();
            }
        };
        return new FedoraServerTestSetup(wrapper);
    }
    

    public void testDescribeRepository() throws Exception {
    	run(describeRepositoryTestXmlOnly, HTTP200, "", "");
    	run(describeRepositoryTestXmlOnly, HTTPS200, "", "");
    }    
    
    /*
    public void testDatastreamDisseminationDemoPidsXML() throws Exception {
    	if (TEST_XML) datastreamDissemination(demoObjects.iterator(), true, XML);
    }

    public void testDatastreamDisseminationBadPidsXML() throws Exception {
    	if (TEST_XML) datastreamDissemination(badPids.iterator(), false, XML);
	}
    
    // no default disseminators return non-XHTML XML, so there are no methods testDisseminationDemoPidsXML() or testDisseminationBadPidsXML()
    
    public void testObjectHistoryDemoObjectsXML() throws Exception {
    	if (TEST_XML) objectHistory(demoObjects.iterator(), true, XML);
    }
    
    public void testObjectHistoryBadPidsXML() throws Exception {
    	if (TEST_XML) objectHistory(badPids.iterator(), false, XML);
    }

    public void testObjectProfileDemoObjectsXML() throws Exception {
    	if (TEST_XML) objectProfile(demoObjects.iterator(), true, XML);
    }

    public void testObjectProfileBadPidsXML() throws Exception {
    	if (TEST_XML) objectProfile(badPids.iterator(), false, XML);
    }

    public void testFindObjectsXML() throws Exception {
    	if (TEST_XML) findObjects(1000000, XML);
    }

    public void testListDatastreamsDemoObjectsXML() throws Exception {
    	if (TEST_XML) listDatastreams(demoObjects.iterator(), true, XML);
    }

    public void testListDatastreamsBadPidsXML() throws Exception {
    	if (TEST_XML) listDatastreams(badPids.iterator(), false, XML);
    }

    public void testListMethodsDemoObjectsXML() throws Exception {
    	if (TEST_XML) listMethods(TestIngestDemoObjects.getDemoObjects(new String[] {"O"}).iterator(), true, XML);
    }    
    
    public void testListMethodsBadPidsXML() throws Exception {
    	if (TEST_XML) listMethods(badPids.iterator(), false, XML);
    }    

    public void testResumeFindObjectsXML() throws Exception {
    	if (TEST_XML) findObjects(10, XML);
    }

    // no demo XHTML datastreams to test so no methods testDatastreamDisseminationDemoPidsXML() or testDatastreamDisseminationBadPidsXML()

    public void testDisseminationDemoObjectsXHTML() throws Exception {
    	if (TEST_XHTML) dissemination(demoObjects.iterator(), true, XHTML);
    }
    
    public void testDisseminationBadPidsXHTML() throws Exception {
    	if (TEST_XHTML) dissemination(badPids.iterator(), false, XHTML);
    }
        
    public void testFindObjectsXHTML() throws Exception {
    	if (TEST_XHTML) findObjects(1000000, XHTML);
    }
        
    public void testObjectHistoryDemoObjectsXHTML() throws Exception {
    	if (TEST_XHTML) objectHistory(demoObjects.iterator(), true, XHTML);    	
    }

    public void testObjectHistoryBadPidsXHTML() throws Exception {
    	if (TEST_XHTML) objectHistory(badPids.iterator(), false, XHTML);    	
    }

    public void testObjectProfileDemoObjectsXHTML() throws Exception {
    	if (TEST_XHTML) objectProfile(demoObjects.iterator(), true, XHTML);    	
    }

    public void testObjectProfileBadPidsXHTML() throws Exception {
    	if (TEST_XHTML) objectProfile(badPids.iterator(), false, XHTML);    	
    }

    public void testListDatastreamsDemoObjectsXHTML() throws Exception {
    	if (TEST_XHTML) listDatastreams(demoObjects.iterator(), true, XHTML);
    }

    public void testListDatastreamsBadPidsXHTML() throws Exception {
    	if (TEST_XHTML) listDatastreams(badPids.iterator(), false, XHTML);
    }

    public void testListMethodsDemoObjectsXHTML() throws Exception {
    	if (TEST_XHTML) listMethods(TestIngestDemoObjects.getDemoObjects(new String[] {"O"}).iterator(), true, XHTML);
    }    

    public void testListMethodsBadPidsXHTML() throws Exception {
    	if (TEST_XHTML) listMethods(badPids.iterator(), false, XHTML);
    }    

    public void testResumeFindObjectsXHTML() throws Exception {
    	if (TEST_XHTML) findObjects(10, XHTML);
    }
    */

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIALite.class);
    }
}
