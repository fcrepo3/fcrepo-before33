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

    /**
     * Test describeUser(ADMIN_USER) as ADMIN_USER with the correct password.
     */
    public void testDescribeUser() throws Exception {
        FedoraClient client = new FedoraClient(BASE_URL, 
                                               ADMIN_USER, 
                                               ADMIN_PASS);
        FedoraAPIM apim = client.getAPIM();
        UserInfo info = apim.describeUser(ADMIN_USER);
    }

    /**
     * Test describeUser(ADMIN_USER) as ADMIN_USER with an incorrect password.
     */
    public void testDescribeUserBadPassword() throws Exception {
    }

    /**
     * Test describeUser(ADMIN_USER) as RESTRICTED_USER with the correct 
     * password, using a policy that states that the user IS NOT authorized to 
     * run the command.
     */
    public void testDescribeUserUnauthorized() throws Exception {
    }

    /**
     * Test describeUser(ADMIN_USER) as RESTRICTED_USER with the correct 
     * password, using a policy that states that the user IS authorized to 
     * run the command.
     */
    public void testDescribeUserNowAuthorized() throws Exception {
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestConfigExample.class);
    }

}