package fedora.test;  

import java.io.InputStream;
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
import junit.framework.Assert;

import org.custommonkey.xmlunit.SimpleXpathEngine;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import fedora.client.FedoraClient;
import fedora.client.HttpInputStream;
import fedora.server.config.ServerConfiguration;
import fedora.test.FedoraServerTestCase;
import fedora.test.FedoraServerTestSetup;

/**
 * @author Bill Niebel 
 */

public class ObjectProfileTest extends IndividualTest {
	
	String pid = "";
	
	public ObjectProfileTest(String pid, boolean xml) {
		super(xml, false);
		this.pid = pid;
	}
	
    /** 
     *  http://localhost:8080/fedora/getObjectProfile/demo:10?xml=true
     */
    public final String getUrl(boolean xml) throws Exception {
    	UrlString url = new UrlString("/getObjectProfile");
    	if (pid != null) {
    		url.appendPathinfo(pid);
    	}
    	url.appendParm("xml", Boolean.toString(xml));
    	return url.toString();
    }  

    public final void checkResultsXml(Document result) throws Exception {
        assertXpathExists(XPATH_XML_OBJECT_PROFILE_LABEL, result);	
        assertXpathExists(XPATH_XML_OBJECT_PROFILE_CONTENT_MODEL, result);	
        assertXpathExists(XPATH_XML_OBJECT_PROFILE_CREATE_DATE, result);	
        assertXpathExists(XPATH_XML_OBJECT_PROFILE_LASTMOD_DATE, result);	
        assertXpathExists(XPATH_XML_OBJECT_PROFILE_OBJTYPE, result);	
        assertXpathExists(XPATH_XML_OBJECT_PROFILE_DISS_INDEX_VIEW_URL, result);	
        assertXpathExists(XPATH_XML_OBJECT_PROFILE_ITEM_INDEX_VIEW_URL, result);
        assertXpathEvaluatesTo(pid, XPATH_XML_OBJECT_PROFILE_PID, result);      	    	
    }
    
    public final void checkResultsXmlElse(Document result) throws Exception {
    	try {
    		assertXpathNotExists(XPATH_XML_OBJECT_PROFILE_PID, result);
    	} catch (Exception e) {
            assertXpathEvaluatesTo("", XPATH_XML_OBJECT_PROFILE_PID, result);
    	}
    }    

    public final void checkResultsXhtml(Document result) throws Exception {
        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_LABEL, result);	
        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_CONTENT_MODEL, result);	
        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_CREATE_DATE, result);	
        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_LASTMOD_DATE, result);	
        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_OBJTYPE, result);	
        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_DISS_INDEX_VIEW_URL, result);	
        assertXpathExists(XPATH_XHTML_OBJECT_PROFILE_ITEM_INDEX_VIEW_URL, result);
        assertXpathEvaluatesTo(pid, XPATH_XHTML_OBJECT_PROFILE_PID, result);
        assertXpathEvaluatesTo("Object Profile HTML Presentation", XPATH_XHTML_OBJECT_PROFILE_HEAD_TITLE, result);
        assertXpathEvaluatesTo("Object Profile View", XPATH_XHTML_OBJECT_PROFILE_BODY_TITLE, result);
    }	

    
    public final void checkResultsXhtmlElse(Document result) throws Exception {
    	try {
    		assertXpathNotExists(XPATH_XHTML_OBJECT_PROFILE_PID, result);
    	} catch (Exception e) {
            assertXpathEvaluatesTo("", XPATH_XHTML_OBJECT_PROFILE_PID, result);
    	}
    }	

	//Fedora namespace not declared in result, so these xpaths don't include namespace prefixes
    private static final String XPATH_XML_OBJECT_PROFILE_PID = "/objectProfile/@pid";
    private static final String XPATH_XML_OBJECT_PROFILE_LABEL = "/objectProfile/objLabel";
    private static final String XPATH_XML_OBJECT_PROFILE_CONTENT_MODEL = "/objectProfile/objContentModel";    
    private static final String XPATH_XML_OBJECT_PROFILE_CREATE_DATE = "/objectProfile/objCreateDate";
    private static final String XPATH_XML_OBJECT_PROFILE_LASTMOD_DATE = "/objectProfile/objLastModDate";
    private static final String XPATH_XML_OBJECT_PROFILE_OBJTYPE = "/objectProfile/objType";
    private static final String XPATH_XML_OBJECT_PROFILE_DISS_INDEX_VIEW_URL = "/objectProfile/objDissIndexViewURL";
    private static final String XPATH_XML_OBJECT_PROFILE_ITEM_INDEX_VIEW_URL = "/objectProfile/objItemIndexViewURL";    
    private static final String XPATH_XHTML_OBJECT_PROFILE_PID = getXhtmlXpath("/html/body//td[@id='pid']");
    private static final String XPATH_XHTML_OBJECT_PROFILE_LABEL = getXhtmlXpath("/html/body//td[@id='objLabel']");
    private static final String XPATH_XHTML_OBJECT_PROFILE_CONTENT_MODEL = getXhtmlXpath("/html/body//td[@id='objContentModel']");
    private static final String XPATH_XHTML_OBJECT_PROFILE_CREATE_DATE = getXhtmlXpath("/html/body//td[@id='objCreateDate']");
    private static final String XPATH_XHTML_OBJECT_PROFILE_LASTMOD_DATE = getXhtmlXpath("/html/body//td[@id='objLastModDate']");
    private static final String XPATH_XHTML_OBJECT_PROFILE_OBJTYPE = getXhtmlXpath("/html/body//td[@id='objType']");
    private static final String XPATH_XHTML_OBJECT_PROFILE_DISS_INDEX_VIEW_URL = getXhtmlXpath("/html/body//a[@id='objDissIndexViewURL']/@href");
    private static final String XPATH_XHTML_OBJECT_PROFILE_ITEM_INDEX_VIEW_URL = getXhtmlXpath("/html/body//a[@id='objItemIndexViewURL']/@href");
    private static final String XPATH_XHTML_OBJECT_PROFILE_HEAD_TITLE = getXhtmlXpath("/html/head/title");
    private static final String XPATH_XHTML_OBJECT_PROFILE_BODY_TITLE = getXhtmlXpath("/html/body//h3");
		
}


