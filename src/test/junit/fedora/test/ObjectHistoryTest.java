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


