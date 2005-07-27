package fedora.test.config;

import java.io.*;
import junit.framework.*;

import fedora.client.*;
import fedora.server.access.*;
import fedora.server.management.*;
import fedora.server.types.gen.*;
import fedora.test.*;

/**
 * An example TestSuite that uses it's own set of configuration files.
 */
public class TestConfigExample extends FedoraServerTestCase {

    public static final String BASE_URL         = "https://localhost:443/fedora";

    public static final String ADMIN_USER       = getUsername();
    public static final String ADMIN_PASS       = getPassword();
    public static final String BAD_ADMIN_PASS   = "fuddorahAddmean";

    public static final String RESTRICTED_USER  = "kmitnick";
    public static final String RESTRICTED_PASS  = "notorious";

    public static Test suite() {
        TestSuite suite = new TestSuite("Configuration Example TestSuite");
        suite.addTestSuite(TestConfigExample.class);
        return new FedoraServerTestSetup(suite);
    }
    
    public void setUp() throws Exception {
        // nothing to do here
    }

    public void testDescribeUser() throws Exception {
        FedoraClient client = new FedoraClient(BASE_URL, 
                                               ADMIN_USER, 
                                               ADMIN_PASS);
        FedoraAPIM apim = client.getAPIM();
        UserInfo info = apim.describeUser(ADMIN_USER);
    }

    public void testDescribeUserBadPassword() throws Exception {
    }

    public void testDescribeUserUnauthorized() throws Exception {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestConfigExample.class);
    }

}