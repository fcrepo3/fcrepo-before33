
package fedora.test.api;

import junit.framework.Test;
import junit.framework.TestSuite;

import fedora.client.FedoraClient;
import fedora.client.HttpInputStream;

import fedora.test.DemoObjectTestSetup;
import fedora.test.FedoraServerTestCase;

/**
 * Test API-A Lite in configuration C (Fedora remotely accessible).
 * 
 * @author Chris Wilper
 */
public class TestAPIALiteConfigC
        extends FedoraServerTestCase {

    private static FedoraClient client;

    public static Test suite() {
        TestSuite suite = new TestSuite("APIALiteConfigC TestSuite");
        suite.addTestSuite(TestAPIALiteConfigC.class);
        return new DemoObjectTestSetup(suite);
    }

    public void testGetRemoteDissemination() throws Exception {
        // test dissemination with E datastream as input to a remote bmech service (MrSID)
        HttpInputStream his =
                client.get("/get/demo:11/demo:8/getThumbnail", true);
        assertEquals(his.getContentType(), "image/jpeg");

        // test dissemination using remote bmech service (MrSID) with user input parms
        his =
                client.get("/get/demo:11/demo:8/getImage?ZOOM=no&SIZE=small",
                           true);
        assertEquals(his.getContentType(), "image/jpeg");

        his.close();
    }

    @Override
    public void setUp() throws Exception {
        client = getFedoraClient();
    }

}
