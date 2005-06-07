/*
 * Created on May 23, 2005
 *
 */
package fedora.test.integration;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.w3c.dom.Document;

import fedora.client.FedoraClient;
import fedora.test.FedoraServerTestCase;
import fedora.test.FedoraServerTestSetup;

/**
 * @author Edwin Shin
 *
 */
public class TestAPIMLite extends FedoraServerTestCase {
    private static DocumentBuilderFactory factory;
    private static DocumentBuilder builder;
    private static FedoraClient client;
    
    public static Test suite() {
        TestSuite suite = new TestSuite("APIMLite TestSuite");
        suite.addTestSuite(TestAPIMLite.class);
        return new FedoraServerTestSetup(suite);
    }
    
    public void setUp() throws Exception {
        client = new FedoraClient(getBaseURL(), getUsername(), getPassword());
        factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
    }
    
    public void testGetNextPID() throws Exception {
        Document result;
        result = getQueryResult("/mgmt/getNextPID?xml=true");
        assertXpathEvaluatesTo("1", "count(/pidList/pid)", result);
        
        result = getQueryResult("/mgmt/getNextPID?numpids=10&namespace=demo&xml=true");
        assertXpathEvaluatesTo("10", "count(/pidList/pid)", result);
    }
    
    /**
     * 
     * @param location a URL relative to the Fedora base URL
     * @return
     * @throws Exception
     */
    private Document getQueryResult(String location) throws Exception {
        InputStream is = client.get(getBaseURL() + location, true);
        return builder.parse(is);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIMLite.class);
    }

}
