/*
 * Created on May 23, 2005
 *
 */
package fedora.test.integration;

import org.custommonkey.xmlunit.SimpleXpathEngine;

import fedora.client.APIMStubFactory;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.Datastream;
import fedora.test.FedoraServerTestCase;

/**
 * @author Edwin Shin
 *
 */
public class TestAPIM extends FedoraServerTestCase {
    private FedoraAPIM apim;
    
    public void setUp() throws Exception {
        super.setUp();
        TestIngestDemoObjects.ingestDemoObjects();
        apim = APIMStubFactory.getStub(getProtocol(), getHost(), 
                Integer.parseInt(getPort()), getUsername(), getPassword());
        
        SimpleXpathEngine.registerNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
        SimpleXpathEngine.registerNamespace("dc", "http://purl.org/dc/elements/1.1/");
    }
    
    public void tearDown() throws Exception {
        SimpleXpathEngine.clearNamespaces();
        TestIngestDemoObjects.purgeDemoObjects();
        super.tearDown();
    }
    
    public void testGetDatastream() throws Exception {
        Datastream ds = apim.getDatastream("demo:1", "DC", null);
        assertEquals("DC", ds.getID());
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIM.class);
    }

}
