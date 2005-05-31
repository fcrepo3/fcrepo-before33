package fedora.test.integration;

import fedora.test.FedoraServerTestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Edwin Shin
 */
public class AllAPITests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllAPITests.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All API tests");
        //$JUnit-BEGIN$
        suite.addTest(TestAPIALite.suite());
        suite.addTestSuite(TestAPIMLite.class);
        suite.addTestSuite(TestAPIA.class);
        suite.addTestSuite(TestAPIM.class);
        //$JUnit-END$
        return new FedoraServerTestSetup(suite);
    }
}
