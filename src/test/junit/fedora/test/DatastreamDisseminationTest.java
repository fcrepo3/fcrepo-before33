package fedora.test;  

import org.w3c.dom.Document;

/**
 * @author Bill Niebel 
 */

public class DatastreamDisseminationTest extends IndividualTest {
	
	String pid = "";
	String datastream = "";
	
	public DatastreamDisseminationTest(String pid, String datastream, boolean xml) {
		super(xml, false);
		this.pid = pid;
		this.datastream = datastream;
	}
	
    /** 
     *  http://localhost:8080/fedora/get/demo:10/DC?xml=true 
     */
    public final String getUrl(boolean xml) throws Exception {
    	UrlString url = new UrlString("/get");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}
    	if (pid != null) {
    		url.appendPathinfo(datastream);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }  

    public final void checkResultsXml(Document result) throws Exception {
    	assertXpathExists(XPATH_XML_DATASTREAM_DISSEMINATION_DC, result);
    }

    public final void checkResultsXhtml(Document result) throws Exception {
    	//no XHTML datastreams in demo objects    	        	
    }	

    public final void checkResultsXmlElse(Document result) throws Exception {
    	assertXpathNotExists(XPATH_XML_DATASTREAM_DISSEMINATION_DC, result);    	        	
    }

    public final void checkResultsXhtmlElse(Document result) throws Exception {
    	//no XHTML datastreams in demo objects  	        	
    }	

    private static final String XPATH_XML_DATASTREAM_DISSEMINATION_DC = "/oai_dc:dc"; 


  
}


