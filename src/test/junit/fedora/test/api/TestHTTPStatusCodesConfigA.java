package fedora.test.api;

import junit.framework.Test;
import junit.framework.TestSuite;

import fedora.test.DemoObjectTestSetup;
import fedora.test.FedoraServerTestCase;

import static fedora.test.api.TestHTTPStatusCodes.DESCRIBE_REPOSITORY_PATH;
import static fedora.test.api.TestHTTPStatusCodes.FIND_OBJECTS_PATH;
import static fedora.test.api.TestHTTPStatusCodes.GET_CUSTOM_DISSEM_PATH;
import static fedora.test.api.TestHTTPStatusCodes.GET_DEFAULT_DISSEM_PATH;
import static fedora.test.api.TestHTTPStatusCodes.GET_DS_DISSEM_PATH;
import static fedora.test.api.TestHTTPStatusCodes.GET_OBJ_HISTORY_PATH;
import static fedora.test.api.TestHTTPStatusCodes.GET_OBJ_PROFILE_PATH;
import static fedora.test.api.TestHTTPStatusCodes.LIST_DATASTREAMS_PATH;
import static fedora.test.api.TestHTTPStatusCodes.LIST_METHODS_PATH;
import static fedora.test.api.TestHTTPStatusCodes.RI_SEARCH_PATH;
import static fedora.test.api.TestHTTPStatusCodes.checkBadAuthN;
import static fedora.test.api.TestHTTPStatusCodes.checkBadAuthZ;
import static fedora.test.api.TestHTTPStatusCodes.checkError;
import static fedora.test.api.TestHTTPStatusCodes.checkOK;

/**
 * HTTP status code tests to be run when API-A authentication is off
 * and the resource index is disabled.
 *
 * @author cwilper@cs.cornell.edu
 */
public class TestHTTPStatusCodesConfigA
        extends FedoraServerTestCase {

    public static Test suite() {
        TestSuite suite = new TestSuite(
                "TestHTTPStatusCodesConfigA TestSuite");
		suite.addTestSuite(TestHTTPStatusCodesConfigA.class);
        return suite;
    }

    //---
    // API-A Lite: riSearch
    //---

    public void testRISearch_Disabled() throws Exception {
        checkError(RI_SEARCH_PATH);
    }

}
