package fedora.test;  

import org.w3c.dom.Document;

/**
 * @author Bill Niebel 
 */

public class ObjectHistoryTest extends IndividualTest {
	
	String pid = "";
	
	public ObjectHistoryTest(String pid, boolean xml) {
		super(xml, false);
		this.pid = pid;
	}
	
    /** 
     *  http://localhost:8080/fedora/getObjectHistory/demo:10?xml=true    
     */
    public final String getUrl(boolean xml) throws Exception {
    	UrlString url = new UrlString("/getObjectHistory");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }  

    public final void checkResultsXml(Document result) throws Exception {
        assertXpathEvaluatesTo(pid, XPATH_XML_OBJECT_HISTORY_PID, result);	        	
        assertXpathExists(XPATH_XML_OBJECT_HISTORY_CHANGE_DATE, result);	 	    	
    }
    
    public final void checkResultsXmlElse(Document result) throws Exception {
    	try {
    		assertXpathNotExists(XPATH_XML_OBJECT_HISTORY_PID, result);
    	} catch (Exception e) {
            assertXpathEvaluatesTo("", XPATH_XML_OBJECT_HISTORY_PID, result);
    	}
    }

    public final void checkResultsXhtml(Document result) throws Exception {
        assertXpathEvaluatesTo(pid, XPATH_XHTML_OBJECT_HISTORY_PID, result);
        assertXpathEvaluatesTo("Object History HTML Presentation", XPATH_XHTML_OBJECT_HISTORY_HEAD_TITLE, result);
        assertXpathEvaluatesTo("Object History View", XPATH_XHTML_OBJECT_HISTORY_BODY_TITLE, result);       	
    }	

    public final void checkResultsXhtmlElse(Document result) throws Exception {
    	try {
    		assertXpathNotExists(XPATH_XHTML_OBJECT_HISTORY_PID, result);
    	} catch (Exception e) {
            assertXpathEvaluatesTo("", XPATH_XHTML_OBJECT_HISTORY_PID, result);
    	}
    }	

    private static final String XPATH_XML_OBJECT_HISTORY_PID = "/fedoraObjectHistory/@pid";
    private static final String XPATH_XML_OBJECT_HISTORY_CHANGE_DATE = "/fedoraObjectHistory/objectChangeDate";
    private static final String XPATH_XHTML_OBJECT_HISTORY_PID = getXhtmlXpath("/html/body//font[@id='pid']");
    private static final String XPATH_XHTML_OBJECT_HISTORY_HEAD_TITLE = getXhtmlXpath("/html/head/title");
    private static final String XPATH_XHTML_OBJECT_HISTORY_BODY_TITLE = getXhtmlXpath("/html/body//h3");    
		
}


