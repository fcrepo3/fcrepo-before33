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


