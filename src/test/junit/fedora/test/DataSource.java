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
public abstract class DataSource {
	
    protected static DocumentBuilderFactory factory;
    protected static DocumentBuilder builder;
    static {
    }
	
	private boolean expectingSuccess = false;
	private boolean clientThrowsStatusCodeException = false;
	
	public DataSource(boolean expectingSuccess, boolean clientThrowsStatusCodeException) throws Exception {
		this.expectingSuccess = expectingSuccess;
		this.clientThrowsStatusCodeException = clientThrowsStatusCodeException;
		if (factory == null) {
			factory = DocumentBuilderFactory.newInstance();
		}
		if (builder == null) {
			builder = factory.newDocumentBuilder();
		}
	}
	
	protected abstract boolean expectedStatusObtained();
	
    protected abstract void reset(IndividualTest test, boolean xml, String username, String password) throws Exception;
	    
	protected abstract Document getResults() throws Exception;
	
    protected final boolean expectingSuccess() {
    	return expectingSuccess;
    }

    protected final boolean clientThrowsStatusCodeException() {
    	return clientThrowsStatusCodeException;
    }
}
