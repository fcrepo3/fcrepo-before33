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
public class HttpDataSource extends DataSource {
	
	private String baseurl = "";
	private int expectedStatus = -1;
    HttpInputStream stream = null;
	
	public HttpDataSource(String baseurl, int expectedStatus) throws Exception {
		super(expectedStatus == 200);
		this.baseurl = baseurl;
		this.expectedStatus = expectedStatus;
	}
	
    protected final void reset(IndividualTest test, boolean xml, String username, String password) throws Exception {
    	FedoraClient client = Trial.getClient(baseurl, username, password);
        stream = client.get(baseurl + test.getUrl(xml), expectingSuccess());
    }
    
	protected final boolean expectedStatusObtained() {
		boolean expectedStatusObtained = false;
		if (stream != null) {
			expectedStatusObtained = (stream.getStatusCode() == expectedStatus);
		}
		return expectedStatusObtained;
	}

	protected final Document getResults() throws Exception {
		Document results = null;
        try {
    		results = builder.parse(stream);        	
        } finally {
            stream.close();        	
        }
		return results;
	}

}
