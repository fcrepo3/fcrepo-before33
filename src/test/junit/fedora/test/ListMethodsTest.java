package fedora.test;  

import org.w3c.dom.Document;

/**
 * @author Bill Niebel 
 */

public class ListMethodsTest extends IndividualTest {
	
	String pid = "";
	
	public ListMethodsTest(String pid, boolean xml) {
		super(xml, false);
		this.pid = pid;
	}
	
    /** 
     *  http://localhost:8080/fedora/listMethods/demo:10?xml=true 
     */
    public final String getUrl(boolean xml) throws Exception {
    	UrlString url = new UrlString("/listMethods");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }  

    public final void checkResultsXml(Document result) throws Exception {
    	System.out.println("IN SPECIFIC TEST CHECKRESULTS 1");
        assertXpathExists(XPATH_XML_LIST_METHODS_OBJECT_METHODS, result);    	        	
    	System.out.println("IN SPECIFIC TEST CHECKRESULTS 2");
    }

    public final void checkResultsXhtml(Document result) throws Exception {
        assertXpathExists(XPATH_XHTML_LIST_METHODS_OBJECT_METHODS, result);    	        	
    }	

    public final void checkResultsXmlElse(Document result) throws Exception {
        assertXpathNotExists(XPATH_XML_LIST_METHODS_OBJECT_METHODS, result);    	        	
    }

    public final void checkResultsXhtmlElse(Document result) throws Exception {
        assertXpathNotExists(XPATH_XHTML_LIST_METHODS_OBJECT_METHODS, result);    	        	
    }	

    private static final String XPATH_XML_LIST_METHODS_OBJECT_METHODS = "/objectMethods"; 
    private static final String XPATH_XHTML_LIST_METHODS_OBJECT_METHODS = getXhtmlXpath("");
  
}


