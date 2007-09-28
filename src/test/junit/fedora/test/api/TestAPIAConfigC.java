package fedora.test.api;

import junit.framework.Test;
import junit.framework.TestSuite;

import fedora.client.FedoraClient;

import fedora.server.access.FedoraAPIA;
import fedora.server.types.gen.MIMETypedStream;
import fedora.server.types.gen.Property;

import fedora.test.DemoObjectTestSetup;
import fedora.test.FedoraServerTestCase;

/**
 * Test API-A SOAP in configuration C (Fedora remotely accessible).
 * 
 * @author cwilper@cs.cornell.edu
 */
public class TestAPIAConfigC extends FedoraServerTestCase {

	private FedoraAPIA apia;
	
	public static Test suite() {
		TestSuite suite = new TestSuite("APIAConfigC TestSuite");
		suite.addTestSuite(TestAPIAConfigC.class);
		return new DemoObjectTestSetup(suite);
	}
	
	public void testGetRemoteDissemination() throws Exception {
		// test dissemination with E datastream as input to a remote bmech service (MrSID)
        MIMETypedStream diss = apia.getDissemination("demo:11", "demo:8", "getThumbnail", new Property[0], null);
		assertEquals(diss.getMIMEType(),"image/jpeg");
		assertTrue(diss.getStream().length > 0);
		
		// test dissemination using remote bmech service (MrSID) with user input parms		
		Property[] parms = new Property[2];
		Property p = new Property();
		p.setName("ZOOM");
		p.setValue("no");
		parms[0] = p;
		Property p2 = new Property();
		p2.setName("SIZE");
		p2.setValue("small");
		parms[1] = p2;
		diss = apia.getDissemination("demo:11", "demo:8", "getImage", parms, null);
		assertEquals(diss.getMIMEType(),"image/jpeg");
		assertTrue(diss.getStream().length > 0);
    }
	
	public void setUp() throws Exception {
		FedoraClient client = getFedoraClient();
		apia = client.getAPIA();
    }
	
}
