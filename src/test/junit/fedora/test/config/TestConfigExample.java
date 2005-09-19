package fedora.test.config;

import junit.framework.Test;
import junit.framework.TestSuite;
import fedora.client.FedoraClient;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.UserInfo;
import fedora.test.FedoraServerTestCase;
import fedora.test.FedoraServerTestSetup;

/**
 * An example TestSuite that uses it's own set of configuration files.
 *
 * This example uses the default fedora.fcfg, secure-web.xml, a custom 
 * tomcat-users.xml, and the default jaas.config.
 *
 * It uses the default beSecurity.xml file, and a custom repository-policies
 * directory, the default object-policies directory, the default surrogate-policies
 * directory, and the default repository-policies-generated-by-policyguitool
 * directory.
 *
 * So 
 *
 * can be set multiple times:
 *   beSecurity.xml
 *   repository-policies/
 *   object-policies/
 *   surrogate-policies/
 *   repository-policies-generated-by-policyguitool/
 */
public class TestConfigExample extends FedoraServerTestCase {

    public static final String BASE_URL         = "https://localhost:8443/fedora";

    public static final String ADMIN_USER       = getUsername();
    public static final String ADMIN_PASS       = getPassword();
    public static final String BAD_ADMIN_PASS   = "fuddorahAddmean";

    public static final String RESTRICTED_USER  = "kmitnick";
    public static final String RESTRICTED_PASS  = "notorious";

    public static Test suite() {
        TestSuite suite = new TestSuite("Configuration Example TestSuite");
        suite.addTestSuite(TestConfigExample.class);
        return new FedoraServerTestSetup(suite, TestConfigExample.class.getName());
    }
    
    /**
     * Test describeUser(ADMIN_USER) as ADMIN_USER with the correct password.
     */
    public void testDescribeUser() throws Exception {
        System.out.println("Running testDescribeUser...");

        UserInfo info = null;
        try {
            info = getUserInfo(ADMIN_USER, ADMIN_PASS, ADMIN_USER);
        } catch (Exception e) {
            fail("describeUser should have succeeded for admin, but failed: " + e.getClass().getName() + ": " + e.getMessage());
        }

        String id = info.getId();
        System.out.println("info.getId() returned '" + id + "'");

        assertTrue("info.getId() returned " + info.getId() 
                + ", but should have returned " + ADMIN_USER,
                info.getId().equals(ADMIN_USER));
    }

    /**
     * Test describeUser(ADMIN_USER) as ADMIN_USER with an incorrect password.
     */
    public void testDescribeUserBadPassword() throws Exception {
        System.out.println("Running testDescribeUserBadPassword...");

        UserInfo info = null;
        try {
            info = getUserInfo(ADMIN_USER, BAD_ADMIN_PASS, ADMIN_USER);
        } catch (Exception e) {
            System.out.println("describeUser for admin with bad password"
                    + " correctly failed with an 'Unauthorized' error.");
        }

        if (info != null) {
            fail("describeUser should have failed: password was wrong.");
        }
    }

    /**
     * Test describeUser(ADMIN_USER) as RESTRICTED_USER with the correct 
     * password, using a policy that states that the user IS NOT authorized to 
     * run the command.
     */
    public void testDescribeUserUnauthorized() throws Exception {
        System.out.println("Running testDescribeUserUnauthorized...");

        usePolicies("describeUserUnauthorized");

        UserInfo info = null;
        try {
            info = getUserInfo(RESTRICTED_USER, RESTRICTED_PASS, RESTRICTED_USER);
        } catch (Exception e) {
            System.out.println("describeUser for restricted user correctly "
                   + "failed with an 'Unauthorized' error.");
        }

        if (info != null) {
            fail("describeUser should have failed: user was restricted.");
        }
    }

    /**
     * Test describeUser(ADMIN_USER) as RESTRICTED_USER with the correct 
     * password, using a policy that states that the user IS authorized to 
     * run the command.
     */
    public void testDescribeUserAuthorized() throws Exception {
        System.out.println("Running testDescribeUserAuthorized...");

        usePolicies("describeUserAuthorized");

        UserInfo info = null;
        try {
            info = getUserInfo(RESTRICTED_USER, RESTRICTED_PASS, RESTRICTED_USER);
        } catch (Exception e) {
            fail("describeUser should have succeeded for now-authorized user, but failed: " + e.getClass().getName() + ": " + e.getMessage());
        }

        String id = info.getId();
        System.out.println("info.getId() returned '" + id + "'");

        assertTrue("info.getId() returned " + info.getId() 
                + ", but should have returned " + RESTRICTED_USER,
                info.getId().equals(RESTRICTED_USER));
    }

    private UserInfo getUserInfo(String user, 
                                 String pass, 
                                 String who) throws Exception {
        FedoraClient client = new FedoraClient(BASE_URL, user, pass);
        FedoraAPIM apim = client.getAPIM();
        return apim.describeUser(who);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestConfigExample.class);
    }

}