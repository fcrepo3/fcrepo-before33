package fedora.test.api;

import junit.framework.Test;
import junit.framework.TestSuite;

import fedora.client.FedoraClient;
import fedora.client.HttpInputStream;

import fedora.test.DemoObjectTestSetup;
import fedora.test.FedoraServerTestCase;

/**
 * Test API-A Lite in configuration A (Authentication disabled on API-A).
 * 
 * @author cwilper@cs.cornell.edu
 */
public class TestAPIALiteConfigA extends FedoraServerTestCase {

    private static FedoraClient client;
    
    public static Test suite() {
        TestSuite suite = new TestSuite("APIALiteConfigA TestSuite");
		suite.addTestSuite(TestAPIALiteConfigA.class);
		return new DemoObjectTestSetup(suite);
    }
    
    public void testGetChainedDissemination() throws Exception {
		// test chained dissemination using local bmech services
		// The object contains an E datastream that is a dissemination of the local SAXON service.
		// This datastream is input to another dissemination that uses the local FOP service.
    	HttpInputStream his = client.get("/get/demo:26/demo:19/getPDF", false);
    	assertEquals(his.getContentType(),"application/pdf");    	
    	his.close();
    }
    
    public void setUp() throws Exception {
        client = getFedoraClient();
    }
    
}
