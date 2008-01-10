
package fedora.test.api;

import junit.framework.Test;
import junit.framework.TestSuite;

import fedora.test.integration.TestCommandLineUtilities;

/**
 * @author Edwin Shin
 */
public class AllAPITests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllAPITests.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All API tests");

        //        suite.addTest(TestAuthentication.suite());
        suite.addTest(TestAPIA.suite());
        suite.addTest(TestAPIALite.suite());
        suite.addTest(TestAPIM.suite());
        suite.addTest(TestAPIMLite.suite());
        suite.addTest(TestXACMLPolicies.suite());
        suite.addTest(TestCommandLineUtilities.suite());
        return suite;
    }
}
