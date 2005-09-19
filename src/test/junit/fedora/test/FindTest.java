package fedora.test;  

import org.w3c.dom.Document;

/**
 * @author Bill Niebel 
 */

public class FindTest extends IndividualTest {
	
	String query = "";
	int maxResults = 0;
	int maxObjects = 0;
	String sessionToken = null;

	int hitsOnThisPage = -1;
	int cursorShouldBe = 0; //for xml results only (cursor is not included in xhtml results)
	int hitsOnAllPages = 0;

	
	public final void setSessionToken (String sessionToken) {
		this.sessionToken = sessionToken;
	}
	
	public FindTest(String query, int maxResults, int maxObjects, boolean xml) {
		super(xml, false);
		this.query = query;
		this.maxResults = maxResults;
		this.maxObjects = maxObjects;
		repeating = true;
	}
	
    public String getUrl(boolean xml) throws Exception {
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

    
    public final void checkResultsXml(Document result) throws Exception {
        System.out.println("beginning checkResultsXml()");
try {
    sessionToken = simpleXpathEngine.evaluate(XPATH_XML_FIND_OBJECTS_SESSION_TOKEN, result);
    System.out.println("NO EXCEPTION");
} catch (Exception e) {
    System.out.println("EXCEPTION E " + e.getMessage() + " " + ((e.getCause() == null) ? e.getCause().getMessage() : ""));
	throw e;
}
        again = (sessionToken != null) && ! "".equals(sessionToken);
        System.out.println("again set to " + again);
		hitsOnThisPage = Integer.parseInt(simpleXpathEngine.evaluate(XPATH_XML_FIND_OBJECTS_COUNT_PIDS, result));
    	if (again()) {
        	String cursor = simpleXpathEngine.evaluate(XPATH_XML_FIND_OBJECTS_CURSOR, result);
    		assertEquals(cursorShouldBe, Integer.parseInt(cursor)); //&&again TO WORK AROUND A PROBABLE BUG IN SERVER CODE.
    	}
    }

    public final void checkResultsXhtml(Document result) throws Exception {
        sessionToken = simpleXpathEngine.evaluate(XPATH_XHTML_FIND_OBJECTS_SESSION_TOKEN, result);
        again = (sessionToken != null) && ! "".equals(sessionToken);
        System.out.println("again set to " + again);        
		hitsOnThisPage = Integer.parseInt(simpleXpathEngine.evaluate(XPATH_XHTML_FIND_OBJECTS_COUNT_PIDS, result));
    }	

    public final void checkResults() throws Exception {
            
        
    	assertTrue(hitsOnThisPage <= maxResults);
		if (hitsOnThisPage < maxResults) {
			assertTrue((sessionToken == null) || "".equals(sessionToken));
		}
		assertTrue (hitsOnThisPage <= maxObjects);
		hitsOnAllPages += hitsOnThisPage;
		assertTrue(hitsOnAllPages <= maxObjects);
		assertEquals (hitsOnAllPages == maxObjects, (sessionToken == null) || "".equals(sessionToken)); 
		assertEquals(hitsOnAllPages < maxObjects, (sessionToken != null) && ! "".equals(sessionToken));     
        cursorShouldBe += maxResults;
        if (! again()) {
    		assertTrue(hitsOnAllPages == maxObjects);        	
        }
        
    }

    
	//Fedora namespace not declared in result, so these xpaths don't include namespace prefixes
    private static final String XPATH_XML_FIND_OBJECTS_SESSION_TOKEN = getFedoraXpath("/result/listSession/token");
    private static final String XPATH_XML_FIND_OBJECTS_CURSOR = getFedoraXpath("/result/listSession/cursor");
    private static final String XPATH_XML_FIND_OBJECTS_COUNT_PIDS = getFedoraXpath("count(/result/resultList/objectFields/pid)");
    private static final String XPATH_XML_FIND_OBJECTS_PID = getFedoraXpath("/result/resultList/objectFields/pid");
    private static final String XPATH_XHTML_FIND_OBJECTS_SESSION_TOKEN = getXhtmlXpath("/html/body//form//input[@name=\"sessionToken\"]/@value");
    private static final String XPATH_XHTML_FIND_OBJECTS_COUNT_PIDS = getXhtmlXpath("count(/html/body//form//a[@href])");
    private static final String XPATH_XHTML_FIND_OBJECTS_PID = getXhtmlXpath("/html/body//form//a[@href]");

		
}


