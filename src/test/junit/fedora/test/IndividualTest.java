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
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

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
import junit.framework.Assert;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Bill Niebel 
 */
public abstract class IndividualTest extends FedoraTestCase {
	
	private boolean xml = true;
	private boolean xhtml = false;
	
	public IndividualTest(boolean xml, boolean xhtml) {
		this.xml = xml;
		this.xhtml = xhtml;
	}
	
    protected class UrlString {
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
    
    public static final String getXpath(String inpath, String namespacePrefix) {
    	inpath = inpath.replaceAll("/@", "@@"); //i.e., exclude from next replaceAll
    	inpath = inpath.replaceAll("/\\*", "\\*\\*"); //i.e., exclude from next replaceAll
    	inpath = inpath.replaceAll("/", "/" + namespacePrefix + ":");    
    	inpath = inpath.replaceAll("/" + namespacePrefix + ":/", "//"); //fixup after too aggressive        	
    	inpath = inpath.replaceAll("@@", "/@"); //"
    	inpath = inpath.replaceAll("\\*\\*", "/\\*"); //"
    	//inpath = inpath.replaceAll("@", "@" + namespacePrefix + ":");
    	return inpath;
    }
    
    public static final String getXhtmlXpath(String inpath) {
		return getXpath(inpath, NS_XHTML_PREFIX);    	
    }
    public static final String NS_XHTML_PREFIX = "xhtml";
	
    public abstract String getUrl(boolean xml);
    
    public abstract void checkResultsXml(Document result) throws Exception;

    public abstract void checkResultsXhtml(Document result) throws Exception;

}
