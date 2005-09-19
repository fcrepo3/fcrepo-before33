package fedora.test;  

import org.w3c.dom.Document;

/**
 * @author Bill Niebel 
 */

public class DisseminationTest extends IndividualTest {
	
	String pid = "";
	String bDef = "";
	String method = "";
	
	public DisseminationTest(String pid, String bDef, String method, boolean xml) {
		super(xml, false);
		this.pid = pid;
		this.bDef = bDef;
		this.method = method;
	}
	
    /** 
     *  
     */
    public final String getUrl(boolean xml) throws Exception {
    	UrlString url = new UrlString("/get");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}    	
    	if (bDef != null) {
    		url.appendPathinfo(bDef);
    	}
    	if (method != null) {
    		url.appendPathinfo(method);
    	}
    	return url.toString();
    }  

    public final void checkResultsXml(Document result) throws Exception {
    	assertXpathExists(XPATH_XHTML_DISSEMINATION_DC, result);	        	
    }

    public final void checkResultsXhtml(Document result) throws Exception {
    	//no XHTML disseminations in demo objects    	        	
    }	

    public final void checkResultsXmlElse(Document result) throws Exception {
    	assertXpathNotExists(XPATH_XHTML_DISSEMINATION_DC, result);	        	
    }

    public final void checkResultsXhtmlElse(Document result) throws Exception {
    	//no XHTML disseminations in demo objects  	        	
    }	

    private static final String XPATH_XHTML_DISSEMINATION_DC = "/oai_dc:dc"; //<<<<<<<<<<<<<<<<<<<
  
}


