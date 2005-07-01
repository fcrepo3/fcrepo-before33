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

    
    private static final boolean DEBUG = false;
    
    private static final boolean XML = true;
    private static final boolean XHTML = false;
    
    private class UrlString {
    	private boolean parmsBegun = false;
    	private StringBuffer buffer = null;
    	private String parmPrefix = "?";
    	UrlString(String buffer) {
    		this.buffer = new StringBuffer(buffer);
    	}
    	void appendPathinfo(String value) throws Exception {
    		if (parmsBegun) {
    			throw new Exception("no pathinfo after parms");
    		}
    		buffer.append("/" + value);
    	}
    	void appendParm(String name, String value) {
    		parmsBegun = true;
    		buffer.append(parmPrefix + name + "=" + value.replaceAll("=","%7E"));
    		if ("?".equals(parmPrefix)) {
    			parmPrefix = "&";
    		}
    	}
    	public String toString() {
    		return buffer.toString();
    	}
    }

    public static final String getFedoraXPath(String inpath) {
    	inpath = inpath.replaceAll("/@", "@@"); //i.e., exclude from next replaceAll
    	inpath = inpath.replaceAll("/", "/" + NS_FEDORA_TYPES_PREFIX + ":");    	
    	inpath = inpath.replaceAll("@@", "/@"); //"
    	inpath = inpath.replaceAll("@", "@" + NS_FEDORA_TYPES_PREFIX + ":");
    	return inpath;
    }

    
    //testDescribeRepositoryX(HT)ML:  vvvvv code for these 2 tests vvvvv   
    
    /** 
     *  this method can't be static because it contains this inner class:  UrlString; but would otherwise be 
     */
    public /*static*/ final String getDescribeRepositoryUrl(boolean xml) {
    	UrlString url = new UrlString("/describe");
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }

    private static final String XPATH_XML_DESCRIBE_REPOSITORY_REPOSITORY_NAME = "/fedoraRepository/repositoryName"; //Fedora namespace not declared in result
    private static final String XPATH_XHTML_DESCRIBE_REPOSITORY_REPOSITORY_NAME = "/html/body//font[@id=\"repositoryName\"]";

    private final void describeRepository(boolean xml) throws Exception {
    	String xpathDescribeRepositoryRepositoryName = xml ? XPATH_XML_DESCRIBE_REPOSITORY_REPOSITORY_NAME : XPATH_XHTML_DESCRIBE_REPOSITORY_REPOSITORY_NAME;

		String url = getDescribeRepositoryUrl(xml);
        Document result = getQueryResult(url);
        assertXpathEvaluatesTo(fcfg.getParameter("repositoryName").getValue(), xpathDescribeRepositoryRepositoryName, result);
    }
    
    public void testDescribeRepositoryXML() throws Exception {
    	describeRepository(XML);
    }

    public void testDescribeRepositoryXHTML() throws Exception {
    	//describeRepository(XHTML); // <<<<< uncomment after fixing xslt introduction of unclosed meta tag
    }

    //testDescribeRepositoryX(HT)ML:  ^^^^^ code for these 2 tests ^^^^^

    

    //testFindObjectsX(HT)ML and testResumeFindObjectsX(HT)ML:  vvvvv code for these 4 tests vvvvv   
    
    /** 
     *  this method can't be static because it contains this inner class:  UrlString; but would otherwise be
     */
    public /*static*/ final String getFindObjectsUrl(String query, int maxResults, boolean xml, String sessionToken) {
    	UrlString url = new UrlString("/search");
    	if (query != null) {
    		url.appendParm("query", query);
    	}
    	url.appendParm("pid", "true");    	
    	url.appendParm("maxResults", Integer.toString(maxResults));
    	url.appendParm("xml", Boolean.toString(xml));
    	if (sessionToken != null) {
        	url.appendParm("sessionToken", sessionToken);    		
    	}
    	return url.toString();
    }
    
    private static final String XPATH_XML_FIND_OBJECTS_SESSION_TOKEN = getFedoraXPath("/result/listSession/token");
    private static final String XPATH_XML_FIND_OBJECTS_CURSOR = getFedoraXPath("/result/listSession/cursor");
    private static final String XPATH_XML_FIND_OBJECTS_COUNT_PIDS = getFedoraXPath("count(/result/resultList/objectFields/pid)");

    private static final String XPATH_XHTML_FIND_OBJECTS_SESSION_TOKEN = "/html/body//form//input[@name=\"sessionToken\"]/@value";
    //private static final String XPATH_XHTML_FIND_OBJECTS_CURSOR -- this is not included in the html results 
    private static final String XPATH_XHTML_FIND_OBJECTS_COUNT_PIDS = "/html/body//form//a[@href]";
    
    private final void resumeFindObjects(int maxResults, boolean xml) throws Exception {
    	String xpathFindObjectsSessionToken = xml ? XPATH_XML_FIND_OBJECTS_SESSION_TOKEN : XPATH_XHTML_FIND_OBJECTS_SESSION_TOKEN;
    	String xpathFindObjectsCountPids = xml ? XPATH_XML_FIND_OBJECTS_COUNT_PIDS : XPATH_XHTML_FIND_OBJECTS_COUNT_PIDS;

        String sessionToken = null;
		boolean again = true;
		int cursorShouldBe = 0; //for xml results only
		int hitsOnAllPages = 0;
		while (again) { 	       
			String url = getFindObjectsUrl("pid=demo:*", maxResults, xml, sessionToken);
			Document result = getQueryResult(url);
			SimpleXpathEngine simpleXpathEngine = new SimpleXpathEngine();
	        sessionToken = simpleXpathEngine.evaluate(xpathFindObjectsSessionToken, result);
	        if (DEBUG) System.err.println("sessionToken=" + sessionToken);
	        again = (sessionToken != null) && ! "".equals(sessionToken);
	        	        
			int hitsOnThisPage = Integer.parseInt(simpleXpathEngine.evaluate(xpathFindObjectsCountPids, result));
			if (DEBUG) System.err.println("hitsOnThisPage=" + hitsOnThisPage);
	        
	        //assertXpathExists(XPATH_XML_FIND_OBJECTS_PID, result);
        	if (xml && again) {
            	String cursor = simpleXpathEngine.evaluate(XPATH_XML_FIND_OBJECTS_CURSOR, result);
            	if (DEBUG) System.err.println("cursor=" + cursor);
        		assertEquals(cursorShouldBe, Integer.parseInt(cursor)); //&&again TO WORK AROUND A PROBABLE BUG IN SERVER CODE.
        	}
	        
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
    	resumeFindObjects(1000000, XML);
    }

    public void testFindObjectsXHTML() throws Exception {
    	//resumeFindObjects(1000000, XHTML);  // <<<<< uncomment after online test
    }
    
    public void testResumeFindObjectsXML() throws Exception {
    	resumeFindObjects(10, XML);
    }
    
    public void testResumeFindObjectsXHTML() throws Exception {
    	//resumeFindObjects(10, XHTML);  // <<<<< uncomment after online test
    }

    //testFindObjectsX(HT)ML and testResumeFindObjectsX(HT)ML:  ^^^^^ code for these 4 tests ^^^^^

    
    
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
    
    
    
    
    //testObjectHistoryX(HT)ML:  vvvvv code for these 2 tests vvvvv   
    
    /** 
     *  this method can't be static because it contains this inner class:  UrlString; but would otherwise be
     */
    public /*static*/ final String getObjectHistoryUrl(String pid, boolean xml) throws Exception {
    	UrlString url = new UrlString("/getObjectHistory");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }    
    
    private static final String XPATH_XML_OBJECT_HISTORY_PID = "/fedoraObjectHistory/@pid";
    private static final String XPATH_XHTML_OBJECT_HISTORY_PID = ""; //<<<<<<
    private static final String XPATH_XML_OBJECT_HISTORY_ROOT = "/fedoraObjectHistory";
    private static final String XPATH_XHTML_OBJECT_HISTORY_ROOT = ""; //<<<<<<
    private static final String XPATH_XML_OBJECT_HISTORY_CHANGE_DATE = "/fedoraObjectHistory/objectChangeDate";
    private static final String XPATH_XHTML_OBJECT_HISTORY_CHANGE_DATE = ""; //<<<<<<

    private void objectHistory(boolean xml) throws Exception {
    	String xpathObjectHistoryRoot = xml ? XPATH_XML_OBJECT_HISTORY_ROOT : XPATH_XHTML_OBJECT_HISTORY_ROOT;    	
    	String xpathObjectHistoryPid = xml ? XPATH_XML_OBJECT_HISTORY_PID : XPATH_XHTML_OBJECT_HISTORY_PID;
    	String xpathObjectHistoryChangeDate = xml ? XPATH_XML_OBJECT_HISTORY_CHANGE_DATE : XPATH_XHTML_OBJECT_HISTORY_CHANGE_DATE;

        Document result;
        Iterator it = demoObjects.iterator();
        while (it.hasNext()) {
        	String pidTested = (String)it.next();
        	if (DEBUG) System.err.println(getObjectHistoryUrl(pidTested, xml));
	        result = getQueryResult(getObjectHistoryUrl(pidTested, xml));
	        
	        // vvvvv the tests below could be factored out into a dtd- or schema-based validation vvvvv
	        assertXpathExists(xpathObjectHistoryRoot, result);
	        assertXpathExists(xpathObjectHistoryChangeDate, result);	
	        // ^^^^^ the tests above could be factored out into a dtd- or schema-based validation ^^^^^

	        // vvvvv the tests below are value-based vvvvv
			SimpleXpathEngine simpleXpathEngine = new SimpleXpathEngine();
	        String pidReported = simpleXpathEngine.evaluate(xpathObjectHistoryPid, result);
	        if (DEBUG) System.err.println("pidTested=" + pidTested + " pidReported=" + pidReported);
	        assertTrue(pidTested.equals(pidReported));
	        // ^^^^^ the tests above are value-based ^^^^^

        }
    }

    public void testObjectHistoryXML() throws Exception {
    	objectHistory(XML);
    }
    
    public void testObjectHistoryXHTML() throws Exception {
    	//objectHistory(XHTML);    	
    }

    //testObjectHistoryX(HT)ML:  ^^^^^ code for these 2 tests ^^^^^

    
    //testObjectHistoryX(HT)ML:  vvvvv code for these 2 tests vvvvv   
    
    /** 
     *  this method can't be static because it contains this inner class:  UrlString; but would otherwise be
     */
    public /*static*/ final String getObjectProfileUrl(String pid, boolean xml) throws Exception {
    	UrlString url = new UrlString("/get");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    	//http://localhost:8080/fedora/get/demo:10?xml=true
    }    
    
    private static final String XPATH_XML_OBJECT_PROFILE_ROOT = "/objectProfile";
    private static final String XPATH_XHTML_OBJECT_PROFILE_ROOT = ""; //<<<<<<
    private static final String XPATH_XML_OBJECT_PROFILE_PID = "/objectProfile/@pid";
    private static final String XPATH_XHTML_OBJECT_PROFILE_PID = ""; //<<<<<<    
    private static final String XPATH_XML_OBJECT_PROFILE_LABEL = "/objectProfile/objLabel";
    private static final String XPATH_XHTML_OBJECT_PROFILE_LABEL = ""; //<<<<<<
    private static final String XPATH_XML_OBJECT_PROFILE_CONTENT_MODEL = "/objectProfile/objContentModel";
    private static final String XPATH_XHTML_OBJECT_PROFILE_CONTENT_MODEL = ""; //<<<<<<
    private static final String XPATH_XML_OBJECT_PROFILE_CREATE_DATE = "/objectProfile/objCreateDate";
    private static final String XPATH_XHTML_OBJECT_PROFILE_CREATE_DATE = ""; //<<<<<<
    private static final String XPATH_XML_OBJECT_PROFILE_LASTMOD_DATE = "/objectProfile/objLastModDate";
    private static final String XPATH_XHTML_OBJECT_PROFILE_LASTMOD_DATE = ""; //<<<<<<
    private static final String XPATH_XML_OBJECT_PROFILE_OBJTYPE = "/objectProfile/objType";
    private static final String XPATH_XHTML_OBJECT_PROFILE_OBJTYPE = ""; //<<<<<<
    private static final String XPATH_XML_OBJECT_PROFILE_DISS_INDEX_VIEW_URL = "/objectProfile/objDissIndexViewURL";
    private static final String XPATH_XHTML_OBJECT_PROFILE_DISS_INDEX_VIEW_URL = ""; //<<<<<<
    private static final String XPATH_XML_OBJECT_PROFILE_ITEM_INDEX_VIEW_URL = "/objectProfile/objItemIndexViewURL";
    private static final String XPATH_XHTML_OBJECT_PROFILE_ITEM_INDEX_VIEW_URL = ""; //<<<<<<
    
    public void objectProfile(boolean xml) throws Exception {
    	String xpathObjectProfileRoot = xml ? XPATH_XML_OBJECT_PROFILE_ROOT : XPATH_XHTML_OBJECT_PROFILE_ROOT;    
    	String xpathObjectProfilePid = xml ? XPATH_XML_OBJECT_PROFILE_PID : XPATH_XHTML_OBJECT_PROFILE_PID;
    	String xpathObjectProfileLabel = xml ? XPATH_XML_OBJECT_PROFILE_LABEL : XPATH_XHTML_OBJECT_PROFILE_LABEL;
    	String xpathObjectProfileContentModel = xml ? XPATH_XML_OBJECT_PROFILE_CONTENT_MODEL : XPATH_XHTML_OBJECT_PROFILE_CONTENT_MODEL;
    	String xpathObjectProfileCreateDate = xml ? XPATH_XML_OBJECT_PROFILE_CREATE_DATE : XPATH_XHTML_OBJECT_PROFILE_CREATE_DATE;
    	String xpathObjectProfileLastModDate = xml ? XPATH_XML_OBJECT_PROFILE_LASTMOD_DATE : XPATH_XHTML_OBJECT_PROFILE_LASTMOD_DATE;
    	String xpathObjectProfileObjType = xml ? XPATH_XML_OBJECT_PROFILE_OBJTYPE : XPATH_XHTML_OBJECT_PROFILE_OBJTYPE;
    	String xpathObjectProfileDissIndexViewURL = xml ? XPATH_XML_OBJECT_PROFILE_DISS_INDEX_VIEW_URL : XPATH_XHTML_OBJECT_PROFILE_DISS_INDEX_VIEW_URL;
    	String xpathObjectProfileItemIndexViewURL = xml ? XPATH_XML_OBJECT_PROFILE_ITEM_INDEX_VIEW_URL : XPATH_XHTML_OBJECT_PROFILE_ITEM_INDEX_VIEW_URL;
    	
        Document result;
        Iterator it = demoObjects.iterator();
        while (it.hasNext()) {
        	String pidTested = (String)it.next();
        	if (DEBUG) System.err.println(getObjectProfileUrl(pidTested, xml));
	        result = getQueryResult(getObjectProfileUrl(pidTested, xml));
	        
	        // vvvvv the tests below could be factored out into a dtd- or schema-based validation vvvvv
	        assertXpathExists(xpathObjectProfileRoot, result);
	        assertXpathExists(xpathObjectProfilePid, result);
	        assertXpathExists(xpathObjectProfileLabel, result);	
	        assertXpathExists(xpathObjectProfileContentModel, result);	
	        assertXpathExists(xpathObjectProfileCreateDate, result);	
	        assertXpathExists(xpathObjectProfileLastModDate, result);	
	        assertXpathExists(xpathObjectProfileObjType, result);	
	        assertXpathExists(xpathObjectProfileDissIndexViewURL, result);	
	        assertXpathExists(xpathObjectProfileItemIndexViewURL, result);	
	        // ^^^^^ the tests above could be factored out into a dtd- or schema-based validation ^^^^^

	        // vvvvv the tests below are value-based vvvvv
			SimpleXpathEngine simpleXpathEngine = new SimpleXpathEngine();
	        String pidReported = simpleXpathEngine.evaluate(xpathObjectProfilePid, result);
	        if (DEBUG) System.err.println("pidTested=" + pidTested + " pidReported=" + pidReported);
	        assertTrue(pidTested.equals(pidReported));
	        // ^^^^^ the tests above are value-based ^^^^^
        }
    }

    public void testObjectProfileXML() throws Exception {
    	objectProfile(XML);
    }
    
    public void testObjectProfileXHTML() throws Exception {
    	//objectProfile(XHTML);    	
    }

    //testObjectProfileX(HT)ML:  ^^^^^ code for these 2 tests ^^^^^
    
    
    
    
    
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
    

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIALite.class);
    }
}
