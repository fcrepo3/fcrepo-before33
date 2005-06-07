/*
 * Created on May 23, 2005
 *
 */
package fedora.test.integration;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.custommonkey.xmlunit.SimpleXpathEngine;

import fedora.client.APIAStubFactory;
import fedora.server.access.FedoraAPIA;
import fedora.server.types.gen.MIMETypedStream;
import fedora.server.types.gen.RepositoryInfo;
import fedora.test.FedoraServerTestCase;
import fedora.test.FedoraServerTestSetup;

/**
 * @author Edwin Shin
 *
 */
public class TestAPIA extends FedoraServerTestCase {
    private FedoraAPIA apia;
    
    public static Test suite() {
        TestSuite suite = new TestSuite(TestAPIA.class);
        TestSetup wrapper = new TestSetup(suite) {
            public void setUp() throws Exception {
                TestIngestDemoObjects.ingestDemoObjects();
                SimpleXpathEngine.registerNamespace("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");
                SimpleXpathEngine.registerNamespace("dc", "http://purl.org/dc/elements/1.1/");
            }
            
            public void tearDown() throws Exception {
                TestIngestDemoObjects.purgeDemoObjects();
                SimpleXpathEngine.clearNamespaces();
            }
        };
        return new FedoraServerTestSetup(wrapper);
    }
    
    public void setUp() throws Exception {
        apia = APIAStubFactory.getStub(getProtocol(), getHost(), 
                Integer.parseInt(getPort()), getUsername(), getPassword());
    }
    
    public void testDescribeRepository() throws Exception {
        RepositoryInfo describe = apia.describeRepository();
        assertTrue(!describe.getRepositoryName().equals(""));
    }
    
    public void testGetDatastreamDissemination() throws Exception {
        MIMETypedStream ds = apia.getDatastreamDissemination("demo:1", "DC", null);
        assertXpathExists("/oai_dc:dc", new String(ds.getStream()));
    }
    
    
    //TODO test the rest of APIA
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIA.class);
    }

}
