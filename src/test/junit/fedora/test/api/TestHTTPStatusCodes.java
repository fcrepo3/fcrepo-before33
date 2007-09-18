package fedora.test.api;

import junit.extensions.TestSetup;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import fedora.client.FedoraClient;
import fedora.client.HttpInputStream;

import fedora.server.security.servletfilters.xmluserfile.FedoraUsers;

import fedora.test.DemoObjectTestSetup;
import fedora.test.FedoraServerTestCase;

/**
 * Tests correct/incorrect http status codes with api requests over API-A/API-M Lite.
 * For non-200 requests, this also tests the response body for the string "Fedora: # "
 * (where # is the status code) to ensure that the correct jsp has been delivered.
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

    private final static String TEST_OBJ = "demo:SmileyBucket";
    private final static String BOGUS_DS = "NonExistingDS";
    private final static String BOGUS_METHOD = "nonExistingMethod";
    private final static String BOGUS_OBJ = "demo:NonExistingObject";
    private final static String BOGUS_BDEF = "demo:NonExistingBDef";

    private static FedoraClient CLIENT_VALID_USER_VALID_PASS;
    private static FedoraClient CLIENT_VALID_USER_VALID_PASS_UNAUTHORIZED;
    private static FedoraClient CLIENT_VALID_USER_BOGUS_PASS;
    private static FedoraClient CLIENT_BOGUS_USER;

    private static final String GET_NEXT_PID_PATH = "/management/getNextPID?xml=true";

    private static final String DESCRIBE_REPOSITORY_PATH = "/describe?xml=true";

    private static final String GET_DS_DISSEM_PATH = "/get/" + TEST_OBJ + "/DC";
    private static final String GET_DS_DISSEM_BOGUS_DS_PATH = "/get/" + TEST_OBJ + "/" + BOGUS_DS;
    private static final String GET_DS_DISSEM_BOGUS_OBJ_PATH = "/get/" + BOGUS_OBJ + "/DC";

    private static final String GET_DEFAULT_DISSEM_PATH = "/get/" + TEST_OBJ + "/fedora-system:3/viewDublinCore";
    private static final String GET_DEFAULT_DISSEM_BOGUS_METHOD_PATH = "/get/" + TEST_OBJ + "/fedora-system:3/" + BOGUS_METHOD;
    private static final String GET_DEFAULT_DISSEM_BOGUS_OBJ_PATH = "/get/" + BOGUS_OBJ + "/fedora-system:3/viewDublinCore";

    private static final String GET_CUSTOM_DISSEM_PATH = "/get/" + TEST_OBJ + "/demo:DualResImage/mediumSize";
    private static final String GET_CUSTOM_DISSEM_BOGUS_METHOD_PATH = "/get/" + TEST_OBJ + "/demo:DualResImage/" + BOGUS_METHOD;
    private static final String GET_CUSTOM_DISSEM_BOGUS_BDEF_PATH = "/get/" + TEST_OBJ + "/" + BOGUS_BDEF + "/" + BOGUS_METHOD;
    private static final String GET_CUSTOM_DISSEM_BOGUS_OBJ_PATH = "/get/" + BOGUS_OBJ + "/demo:DualResImage/mediumSize";

    private static final String GET_OBJ_HISTORY_PATH = "/getObjectHistory/" + TEST_OBJ + "?xml=true";
    private static final String GET_OBJ_HISTORY_BOGUS_OBJ_PATH = "/getObjectHistory/" + BOGUS_OBJ + "?xml=true";

    private static final String GET_OBJ_PROFILE_PATH = "/get/" + TEST_OBJ + "?xml=true";
    private static final String GET_OBJ_PROFILE_BOGUS_OBJ_PATH = "/get/" + BOGUS_OBJ + "?xml=true";

    private static final String LIST_DATASTREAMS_PATH = "/listDatastreams/" + TEST_OBJ + "?xml=true";
    private static final String LIST_DATASTREAMS_BOGUS_OBJ_PATH = "/listDatastreams/" + BOGUS_OBJ + "?xml=true";

    private static final String LIST_METHODS_PATH = "/listMethods/" + TEST_OBJ + "?xml=true";
    private static final String LIST_METHODS_BOGUS_OBJ_PATH = "/listMethods/" + BOGUS_OBJ + "?xml=true";

    private static final String FIND_OBJECTS_PATH = "/search?pid=true&terms=&query=&maxResults=20&xml=true";
    private static final String FIND_OBJECTS_BADREQ_PATH = "/search?pid=true&terms=&query=&maxResults=unparsable&xml=true";

    public static Test suite() {
        TestSuite suite = new TestSuite("TestHTTPStatusCodes TestSuite");
		suite.addTestSuite(TestHTTPStatusCodes.class);
        return new DemoObjectTestSetup(suite);
    }

    //---
    // API-M Lite: getNextPID
    //---

    public void testGetNextPID_OK() throws Exception {
        checkOK(GET_NEXT_PID_PATH);
    }

    public void testGetNextPID_BadAuthN() throws Exception {
        checkBadAuthN(GET_NEXT_PID_PATH);
    }

    public void testGetNextPID_BadAuthZ() throws Exception {
        checkBadAuthZ(GET_NEXT_PID_PATH);
    }

    //---
    // API-A Lite: describeRepository
    //---

    public void testDescribeRepository_OK() throws Exception {
        checkOK(DESCRIBE_REPOSITORY_PATH);
    }

    public void testDescribeRepository_BadAuthN() throws Exception {
        checkBadAuthN(DESCRIBE_REPOSITORY_PATH);
    }

    public void testDescribeRepository_BadAuthZ() throws Exception {
        checkBadAuthZ(DESCRIBE_REPOSITORY_PATH);
    }

    //---
    // API-A Lite: getDatastreamDissemination
    //---

    public void testGetDatastreamDissemination_OK() throws Exception {
        checkOK(GET_DS_DISSEM_PATH);
    }

    public void testGetDatastreamDissemination_BadAuthN() throws Exception {
        checkBadAuthN(GET_DS_DISSEM_PATH);
    }

    public void testGetDatastreamDissemination_BadAuthZ() throws Exception {
        checkBadAuthZ(GET_DS_DISSEM_PATH);
    }

    public void testGetDatastreamDissemination_Datastream_NotFound() throws Exception {
        checkNotFound(GET_DS_DISSEM_BOGUS_DS_PATH);
    }

    public void testGetDatastreamDissemination_Object_NotFound() throws Exception {
        checkNotFound(GET_DS_DISSEM_BOGUS_OBJ_PATH);
    }

    //---
    // API-A Lite: getDissemination (default)
    //---

    public void testGetDissemination_Default_OK() throws Exception {
        checkOK(GET_DEFAULT_DISSEM_PATH);
    }

    public void testGetDissemination_Default_BadAuthN() throws Exception {
        checkBadAuthN(GET_DEFAULT_DISSEM_PATH);
    }

    public void testGetDissemination_Default_BadAuthZ() throws Exception {
        checkBadAuthZ(GET_DEFAULT_DISSEM_PATH);
    }

    public void testGetDissemination_Default_Method_NotFound() throws Exception {
        checkNotFound(GET_DEFAULT_DISSEM_BOGUS_METHOD_PATH);
    }

    public void testGetDissemination_Default_Object_NotFound() throws Exception {
        checkNotFound(GET_DEFAULT_DISSEM_BOGUS_OBJ_PATH);
    }

    //---
    // API-A Lite: getDissemination (custom)
    //---

    public void testGetDissemination_Custom_OK() throws Exception {
        checkOK(GET_CUSTOM_DISSEM_PATH);
    }

    public void testGetDissemination_Custom_BadAuthN() throws Exception {
        checkBadAuthN(GET_CUSTOM_DISSEM_PATH);
    }

    public void testGetDissemination_Custom_BadAuthZ() throws Exception {
        checkBadAuthZ(GET_CUSTOM_DISSEM_PATH);
    }

    public void testGetDissemination_Custom_Method_NotFound() throws Exception {
        checkNotFound(GET_CUSTOM_DISSEM_BOGUS_METHOD_PATH);
    }

    public void testGetDissemination_Custom_Object_NotFound() throws Exception {
        checkNotFound(GET_CUSTOM_DISSEM_BOGUS_OBJ_PATH);
    }

    //---
    // API-A Lite: getObjectHistory
    //---

    public void testGetObjectHistory_OK() throws Exception {
        checkOK(GET_OBJ_HISTORY_PATH);
    }

    public void testGetObjectHistory_BadAuthN() throws Exception {
        checkBadAuthN(GET_OBJ_HISTORY_PATH);
    }

    public void testGetObjectHistory_BadAuthZ() throws Exception {
        checkBadAuthZ(GET_OBJ_HISTORY_PATH);
    }

    public void testGetObjectHistory_Object_NotFound() throws Exception {
        checkNotFound(GET_OBJ_HISTORY_BOGUS_OBJ_PATH);
    }

    //---
    // API-A Lite: getObjectProfile
    //---

    public void testGetObjectProfile_OK() throws Exception {
        checkOK(GET_OBJ_PROFILE_PATH);
    }

    public void testGetObjectProfile_BadAuthN() throws Exception {
        checkBadAuthN(GET_OBJ_PROFILE_PATH);
    }

    public void testGetObjectProfile_BadAuthZ() throws Exception {
        checkBadAuthZ(GET_OBJ_PROFILE_PATH);
    }

    public void testGetObjectProfile_Object_NotFound() throws Exception {
        checkNotFound(GET_OBJ_PROFILE_BOGUS_OBJ_PATH);
    }

    //---
    // API-A Lite: listDatastreams
    //---

    public void testListDatastreams_OK() throws Exception {
        checkOK(LIST_DATASTREAMS_PATH);
    }

    public void testListDatastreams_BadAuthN() throws Exception {
        checkBadAuthN(LIST_DATASTREAMS_PATH);
    }

    public void testListDatastreams_BadAuthZ() throws Exception {
        checkBadAuthZ(LIST_DATASTREAMS_PATH);
    }

    public void testListDatastreams_Object_NotFound() throws Exception {
        checkNotFound(LIST_DATASTREAMS_BOGUS_OBJ_PATH);
    }

    //---
    // API-A Lite: listMethods
    //---

    public void testListMethods_OK() throws Exception {
        checkOK(LIST_METHODS_PATH);
    }

    public void testListMethods_BadAuthN() throws Exception {
        checkBadAuthN(LIST_METHODS_PATH);
    }

    public void testListMethods_BadAuthZ() throws Exception {
        checkBadAuthZ(LIST_METHODS_PATH);
    }

    public void testListMethods_Object_NotFound() throws Exception {
        checkNotFound(LIST_METHODS_BOGUS_OBJ_PATH);
    }

    //---
    // API-A Lite: findObjects
    //---

    public void testFindObjects_OK() throws Exception {
        checkOK(FIND_OBJECTS_PATH);
    }

    public void testFindObjects_BadAuthN() throws Exception {
        checkBadAuthN(FIND_OBJECTS_PATH);
    }

    public void testFindObjects_BadAuthZ() throws Exception {
        checkBadAuthZ(FIND_OBJECTS_PATH);
    }

    public void testFindObjects_BadRequest() throws Exception {
        checkBadRequest(FIND_OBJECTS_BADREQ_PATH);
    }

    //---
    // Static helpers
    //---

    private static void checkOK(String requestPath) throws Exception {
        checkCode(getClient(true, true, true), requestPath,
                "Expected HTTP 200 (OK) response for authenticated, "
                + "authorized request", 200);
    }

    private static void checkBadAuthN(String requestPath) throws Exception {
        checkCode(getClient(true, false, true), requestPath,
                "Expected HTTP 401 (Unauthorized) response for bad "
                + "authentication (valid user, bad pass) request", 401);
        checkCode(getClient(false, false, true), requestPath,
                "Expected HTTP 401 (Unauthorized) response for bad "
                + "authentication (invalid user) request", 401);
    }

    private static void checkBadAuthZ(String requestPath) throws Exception {
        try {
            activateUnauthorizedUserAndPolicy();
            checkCode(getClient(true, true, false), requestPath,
                    "Expected HTTP 403 (Forbidden) response for "
                    + "authenticated, unauthorized request", 403);
        } finally {
            deactivateUnauthorizedUserAndPolicy();
        }
    }

    private static void checkNotFound(String requestPath) throws Exception {
        checkCode(getClient(true, true, true), requestPath,
                "Expected HTTP 404 (Not Found) response for authenticated, "
                + "authorized request", 404);
    }

    private static void checkBadRequest(String requestPath) throws Exception {
        checkCode(getClient(true, true, true), requestPath,
                "Expected HTTP 400 (Bad Request) response for authenticated, "
                + "authorized request", 400);
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

    private static void checkCode(FedoraClient client,
            String requestPath, String errorMessage, int expectedCode)
            throws Exception {
        HttpInputStream in = client.get(requestPath, false);
        try {
            int gotCode = in.getStatusCode();
            assertEquals(errorMessage + " (" + requestPath + ")",
                expectedCode, gotCode);
            if (expectedCode != 200) {
                String expectedString = "Fedora: " + expectedCode + " ";
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in));
                boolean foundExpectedString = false;
                String line = reader.readLine();
                while (line != null) {
                    if (line.indexOf(expectedString) != -1) {
                        foundExpectedString = true;
                    }
                    line = reader.readLine();
                }
                assertTrue("HTTP status code was correct ("
                        + expectedCode + "), but body did not contain "
                        + "the string \"" + expectedString + "\"",
                        foundExpectedString);
            }
        } finally {
            in.close();
        }
    }
}
