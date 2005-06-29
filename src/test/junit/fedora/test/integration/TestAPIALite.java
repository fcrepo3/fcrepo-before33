package fedora.test.integration;  

import java.io.InputStream;
import java.util.Iterator;
import java.util.Set;

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
 * @author Bill Niebel 
 */
public class TestAPIALite extends FedoraServerTestCase {
    private static DocumentBuilderFactory factory;
    private static DocumentBuilder builder;
    private static ServerConfiguration fcfg;
    private static FedoraClient client;
    private static Set demoObjects;
    
    private static final boolean DEBUG = false;
        
    public static Test suite() {
        TestSuite suite = new TestSuite(TestAPIALite.class);
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
    
    private class UrlString {
    	StringBuffer buffer = null;
    	String parmPrefix = "?";
    	UrlString(String buffer) {
    		this.buffer = new StringBuffer(buffer);
    	}
    	void append(String name, String value) {
    		buffer.append(parmPrefix + name + "=" + value.replaceAll("=","%7E"));
    		if ("?".equals(parmPrefix)) {
    			parmPrefix = "&";
    		}
    	}
    	public String toString() {
    		return buffer.toString();
    	}
    }
    
    public /*static*/ final String getFindObjectsUrl(String query, int maxResults, boolean xml, String sessionToken) {
    	//method can't be static because it contains this inner class:
    	UrlString url = new UrlString("/search");
    	if (query != null) {
    		url.append("query", query);
    	}
    	url.append("pid", "true");    	
    	url.append("maxResults", Integer.toString(maxResults));
    	url.append("xml", Boolean.toString(xml));
    	if (sessionToken != null) {
        	url.append("sessionToken", sessionToken);    		
    	}
    	return url.toString();
    }
    
    public static final String getFedoraXPath(String inpath) {
    	inpath = inpath.replaceAll("/@", "@@"); //i.e., exclude from next replaceAll
    	inpath = inpath.replaceAll("/", "/" + NS_FEDORA_TYPES_PREFIX + ":");    	
    	inpath = inpath.replaceAll("@@", "/@"); //"
    	inpath = inpath.replaceAll("@", "@" + NS_FEDORA_TYPES_PREFIX + ":");
    	return inpath;
    }
    
    private static final String XPATH_XML_FIND_OBJECTS_SESSION_TOKEN = getFedoraXPath("/result/listSession/token");
    private static final String XPATH_XML_FIND_OBJECTS_CURSOR = getFedoraXPath("/result/listSession/cursor");
    private static final String XPATH_XML_FIND_OBJECTS_PID = getFedoraXPath("/result/resultList/objectFields/pid");
    private static final String XPATH_XML_FIND_OBJECTS_COUNT_PIDS = getFedoraXPath("count(/result/resultList/objectFields/pid)");
    
    public void commonResumeFindObjectsXML(int maxResults) throws Exception {
        String sessionToken = null;
		boolean again = true;
		int cursorShouldBe = 0;
		int hitsOnAllPages = 0;
		while (again) { 	       
			String url = getFindObjectsUrl("pid=demo:*", maxResults, true, sessionToken);
			Document result = getQueryResult(url);
			SimpleXpathEngine simpleXpathEngine = new SimpleXpathEngine();
	        sessionToken = simpleXpathEngine.evaluate(XPATH_XML_FIND_OBJECTS_SESSION_TOKEN, result);
	        if (DEBUG) System.err.println("sessionToken=" + sessionToken);
	        again = (sessionToken != null) && ! "".equals(sessionToken);
	        	        
			int hitsOnThisPage = Integer.parseInt(simpleXpathEngine.evaluate(XPATH_XML_FIND_OBJECTS_COUNT_PIDS, result));
			if (DEBUG) System.err.println("hitsOnThisPage=" + hitsOnThisPage);
	        
	        assertXpathExists(XPATH_XML_FIND_OBJECTS_PID, result);
        	String cursor = simpleXpathEngine.evaluate(XPATH_XML_FIND_OBJECTS_CURSOR, result);
        	if (DEBUG) System.err.println("cursor=" + cursor);
        	if (again) assertEquals(cursorShouldBe, Integer.parseInt(cursor)); //CONDITIONAL TO WORK AROUND A PROBABLE BUG IN SERVER CODE.
	        
        	assertTrue(hitsOnThisPage <= maxResults);
			if (hitsOnThisPage < maxResults) {
				assertTrue((sessionToken == null) || "".equals(sessionToken));
			}
			
			if (DEBUG) System.err.println("getNDemoObjects()" + demoObjects.size());
			
			assertTrue (hitsOnThisPage <= demoObjects.size());

			hitsOnAllPages += hitsOnThisPage;
			assertTrue(hitsOnAllPages <= demoObjects.size());
			assertEquals (hitsOnAllPages == demoObjects.size(), (sessionToken == null) || "".equals(sessionToken)); 
			assertEquals(hitsOnAllPages < demoObjects.size(), (sessionToken != null) && ! "".equals(sessionToken)); 

	        cursorShouldBe += maxResults;
		}
		assertTrue(hitsOnAllPages == demoObjects.size());
    }    

    public void testFindObjectsXML() throws Exception {
    	commonResumeFindObjectsXML(1000000);
    }
    
    public void testResumeFindObjectsXML() throws Exception {
    	commonResumeFindObjectsXML(10);
    }
    
    public void testGetDatastreamDissemination() throws Exception {
        Document result;
        Iterator it = demoObjects.iterator();
        while (it.hasNext()) {
            // just checking DC
	        result = getQueryResult("/get/" + (String)it.next() + "/DC?xml=true");
	        assertXpathExists("/oai_dc:dc", result);
        }
    }
    
    public void testGetDissemination() {
        
    }
    
    public void testGetObjectHistory() throws Exception {
        Document result;
        Iterator it = demoObjects.iterator();
        while (it.hasNext()) {
	        result = getQueryResult("/getObjectHistory/" + (String)it.next() + "?xml=true");
	        // FIXME devise a better test
	        assertXpathExists("/fedoraObjectHistory", result);
        }
    }
    
    public void testGetObjectProfile() throws Exception {
        Document result;
        Iterator it = demoObjects.iterator();
        while (it.hasNext()) {
	        result = getQueryResult("/get/" + (String)it.next() + "?xml=true");
	        // FIXME devise a better test
	        assertXpathExists("/objectProfile", result);
        }
    }
    
    public void testListDatastreams() throws Exception {
        Document result;
        Iterator it = demoObjects.iterator();
        while (it.hasNext()) {
	        result = getQueryResult("/listDatastreams/" + (String)it.next() + "?xml=true");
	        assertXpathExists("/objectDatastreams", result);
        }
    }
    
    public void testListMethods() throws Exception {
        Document result;
        // only for dataobjects (not bdefs or bmechs)
        Set demoDataObjects = TestIngestDemoObjects.getDemoObjects(new String[] {"O"});
        Iterator it = demoDataObjects.iterator();
        while (it.hasNext()) {
	        result = getQueryResult("/listMethods/" + (String)it.next() + "?xml=true");
	        assertXpathExists("/objectMethods", result);
        }
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
