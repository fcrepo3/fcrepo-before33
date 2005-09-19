package fedora.test;  

import org.w3c.dom.Document;

/**
 * @author Bill Niebel 
 */

public class ListDatastreamsTest extends IndividualTest {
	
	String pid = "";
	
	public ListDatastreamsTest(String pid, boolean xml) {
		super(xml, false);
		this.pid = pid;
	}
	
    /** 
     *  http://localhost:8080/fedora/listDatastreams/demo:10?xml=true
     */
    public final String getUrl(boolean xml) throws Exception {
    	UrlString url = new UrlString("/listDatastreams");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }  

    public final void checkResultsXml(Document result) throws Exception {
    	assertXpathExists(XPATH_XML_LIST_DATASTREAMS_OBJECT_DATASTREAMS, result);
    }

    public final void checkResultsXhtml(Document result) throws Exception {
    	assertXpathExists(XPATH_XHTML_LIST_DATASTREAMS_OBJECT_DATASTREAMS, result);    	        	
    }	

    public final void checkResultsXmlElse(Document result) throws Exception {
    	assertXpathNotExists(XPATH_XML_LIST_DATASTREAMS_OBJECT_DATASTREAMS, result);    	        	
    }

    public final void checkResultsXhtmlElse(Document result) throws Exception {
    	assertXpathNotExists(XPATH_XHTML_LIST_DATASTREAMS_OBJECT_DATASTREAMS, result);    	        	
    }	

    private static final String XPATH_XML_LIST_DATASTREAMS_OBJECT_DATASTREAMS = "/objectDatastreams"; 
    private static final String XPATH_XHTML_LIST_DATASTREAMS_OBJECT_DATASTREAMS = getXhtmlXpath(""); 

  
}


