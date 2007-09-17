package fedora.test.api;

import junit.extensions.TestSetup;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import fedora.client.FedoraClient;
import fedora.client.HttpInputStream;

import fedora.server.security.servletfilters.xmluserfile.FedoraUsers;

import fedora.test.OneEmptyObjectTestSetup;
import fedora.test.FedoraServerTestCase;

/**
 * Tests correct/incorrect http status codes with api requests over API-A/API-M Lite.
 *
 * <p>Some of these tests depend on basic authentication.  To exercise the running
 * server properly, it should be configured to require basic authentication on all
 * interfaces.
 *
 * <p>By default, the tests will run against the "default" Fedora base URL,
 * but this can be overridden by setting the "fedora.baseURL" system property.
 *
 * @author cwilper@cs.cornell.edu
 */
public class TestHTTPStatusCodes
        extends FedoraServerTestCase {

    private final static String TEST_PID = "demo:HTTPStatusCodeTestObject";

    private static FedoraClient CLIENT_VALID_USER_VALID_PASS;
    private static FedoraClient CLIENT_VALID_USER_VALID_PASS_UNAUTHORIZED;
    private static FedoraClient CLIENT_VALID_USER_BOGUS_PASS;
    private static FedoraClient CLIENT_BOGUS_USER;

    private static final String GET_NEXT_PID_REQUEST = "/management/getNextPID?xml=true";

    public static Test suite() {
        TestSuite suite = new TestSuite("TestHTTPStatusCodes TestSuite");
		suite.addTestSuite(TestHTTPStatusCodes.class);
        return new OneEmptyObjectTestSetup(suite, TEST_PID);
    }

    //---
    // API-M Lite Tests
    //---

    public void testGetNextPID_OK() throws Exception {
        checkOK(GET_NEXT_PID_REQUEST);
    }

    public void testGetNextPID_BadAuthN() throws Exception {
        checkBadAuthN(GET_NEXT_PID_REQUEST);
    }

    public void testGetNextPID_BadAuthZ() throws Exception {
        checkBadAuthZ(GET_NEXT_PID_REQUEST);
    }

    //---
    // API-A Lite Tests
    //---

    //---
    // Static helpers
    //---

    private static void checkOK(String requestPath) throws Exception {
        int status = getStatus(getClient(true, true, true), requestPath);
        assertEquals("Expected HTTP 200 (OK) response for authenticated, "
                + "authorized request to " + requestPath, 200, status);
    }

    private static void checkBadAuthN(String requestPath) throws Exception {
        int status = getStatus(getClient(true, false, true), requestPath);
        assertEquals("Expected HTTP 401 (Unauthorized) response for bad "
                + "authentication (valid user, bad pass) request to "
                + requestPath, 401, status);
        status = getStatus(getClient(false, false, true), requestPath);
        assertEquals("Expected HTTP 401 (Unauthorized) response for bad "
                + "authentication (invalid user) request to "
                + requestPath, 401, status);
    }

    private static void checkBadAuthZ(String requestPath) throws Exception {
        try {
            activateUnauthorizedUserAndPolicy();
            int status = getStatus(getClient(true, true, false), requestPath);
            assertEquals("Expected HTTP 403 (Forbidden) response for "
                    + "authenticated, unauthorized request to " + requestPath,
                    403, status);
        } finally {
            deactivateUnauthorizedUserAndPolicy();
        }
    }

    private static int getStatus(FedoraClient client, String requestPath)
            throws Exception {
        HttpInputStream in = client.get(requestPath, false);
        try {
            return in.getStatusCode();
        } finally {
            in.close();
        }
    }

    private static FedoraClient getClient(boolean validUser,
                                          boolean validPass,
                                          boolean authorized)
            throws Exception {
        if (validUser) {
            if (validPass) {
                System.out.println("Using Fedora Client with valid user, valid pass");
                if (authorized) {
                    if (CLIENT_VALID_USER_VALID_PASS == null) {
                        CLIENT_VALID_USER_VALID_PASS = getFedoraClient();
                    }
                    return CLIENT_VALID_USER_VALID_PASS;
                } else {
                    if (CLIENT_VALID_USER_VALID_PASS_UNAUTHORIZED == null) {
                        CLIENT_VALID_USER_VALID_PASS_UNAUTHORIZED = 
                                getFedoraClient(getBaseURL(), "untrustedUser",
                                "password");
                    }
                    return CLIENT_VALID_USER_VALID_PASS_UNAUTHORIZED;
                }
            } else {
                System.out.println("Using Fedora Client with valid user, bogus pass");
                if (CLIENT_VALID_USER_BOGUS_PASS == null) {
                    CLIENT_VALID_USER_BOGUS_PASS = getFedoraClient(
                            getBaseURL(), getUsername(), "bogus");
                }
                return CLIENT_VALID_USER_BOGUS_PASS;
            }
        } else {
            System.out.println("Using Fedora Client with bogus user");
            if (CLIENT_BOGUS_USER == null) {
                CLIENT_BOGUS_USER = getFedoraClient(getBaseURL(), "bogus",
                        "bogus");
            }
            return CLIENT_BOGUS_USER;
        }
    }

    private static void activateUnauthorizedUserAndPolicy() throws Exception {
        backupFedoraUsersFile();
        writeFedoraUsersFile("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<fedora-users>\n"
                + "  <user name=\"" + getUsername() + "\" password=\"" + getPassword() + "\">\n"
                + "    <attribute name=\"fedoraRole\">\n"
                + "      <value>administrator</value>\n"
                + "    </attribute>\n"
                + "  </user>\n"
                + "  <user name=\"fedoraIntCallUser\" password=\"changeme\">\n"
                + "    <attribute name=\"fedoraRole\">\n"
                + "      <value>fedoraInternalCall-1</value>\n" 
                + "      <value>fedoraInternalCall-2</value>\n"
                + "    </attribute>\n"
                + "  </user>\n"
                + "  <user name=\"untrustedUser\" password=\"password\">\n"
                + "    <attribute name=\"fedoraRole\">\n"
                + "      <value>unauthorized</value>\n"
                + "    </attribute>\n"
                + "  </user>\n"
                + "</fedora-users>");
        addSystemWidePolicyFile("deny-all-if-unauthorized.xml", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<Policy xmlns=\"urn:oasis:names:tc:xacml:1.0:policy\"\n"
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
                + "    PolicyId=\"deny-all-if-unauthorized\""
                + "    RuleCombiningAlgId=\"urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable\">\n"
                + "  <Description>deny all api-a and api-m access if subject has fedoraRole unauthorized</Description>\n"
                + "  <Target>\n"
                + "    <Subjects>\n"
                + "      <AnySubject/>\n"
                + "    </Subjects>\n"
                + "    <Resources>\n"
                + "      <AnyResource/>\n"
                + "    </Resources>\n"
                + "    <Actions>\n"
                + "      <AnyAction/>\n"
                + "    </Actions>\n"
                + "  </Target>\n"
                + "  <Rule RuleId=\"1\" Effect=\"Deny\">\n"
                + "    <Condition FunctionId=\"urn:oasis:names:tc:xacml:1.0:function:string-is-in\">\n"
                + "      <AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">unauthorized</AttributeValue>\n"
                + "      <SubjectAttributeDesignator AttributeId=\"fedoraRole\" DataType=\"http://www.w3.org/2001/XMLSchema#string\"/>\n"
                + "    </Condition>\n"
                + "  </Rule>\n"
                + "</Policy>");
        reloadPolicies();
    }

    private static void deactivateUnauthorizedUserAndPolicy() throws Exception {
        restoreFedoraUsersFile();
        removeSystemWidePolicyFile("deny-all-if-unauthorized.xml");
        reloadPolicies();
    }

    private static void backupFedoraUsersFile() throws Exception {
        File sourceFile = FedoraUsers.fedoraUsersXML;
        File destFile = new File(FedoraUsers.fedoraUsersXML.getPath() + ".backup");
        copyFile(sourceFile, destFile);
    }

    private static void copyFile(File sourceFile, File destFile) throws Exception {
        FileInputStream in = new FileInputStream(sourceFile);
        FileOutputStream out = new FileOutputStream(destFile);
        byte[] buf = new byte[4096];
        int len;
        try {
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    private static void writeFedoraUsersFile(String xml) throws Exception {
        writeStringToFile(xml, FedoraUsers.fedoraUsersXML);
    }

    private static void writeStringToFile(String string, File file) throws Exception {
        FileOutputStream out = new FileOutputStream(file);
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
        try {
            writer.print(string);
        } finally {
            writer.close();
        }
    }

    private static void restoreFedoraUsersFile() throws Exception {
        File sourceFile = new File(FedoraUsers.fedoraUsersXML.getPath() + ".backup");
        File destFile = FedoraUsers.fedoraUsersXML;
        copyFile(sourceFile, destFile);
    }

    private static void addSystemWidePolicyFile(String filename, String xml) throws Exception {
        final String policyDir = "data/fedora-xacml-policies/repository-policies/junit";
        File dir = new File(FEDORA_HOME, policyDir);
        dir.mkdir();
        File policyFile = new File(dir, filename);
        writeStringToFile(xml, policyFile);
    }

    private static void removeSystemWidePolicyFile(String filename) throws Exception {
        final String policyDir = "data/fedora-xacml-policies/repository-policies/junit";
        File dir = new File(FEDORA_HOME, policyDir);
        File policyFile = new File(dir, filename);
        policyFile.delete();
        dir.delete(); // succeeds if empty
    }

    private static void reloadPolicies() throws Exception {
        getClient(true, true, true).reloadPolicies();
    }

}
