package fedora.test.integration;

import junit.framework.Test;
import junit.framework.TestSuite;
import fedora.test.FedoraServerTestSetup;

/**
 * The grand, complete, and slow mother of all tests.
 * 
 * @author Edwin Shin
 */
public class AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTests.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("All API tests");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestIngestDemoObjects.class);
        suite.addTest(AllAPITests.suite());
        suite.addTest(TestFedoraConfigurations.suite());
        // add more tests here
        
        //$JUnit-END$
        return new FedoraServerTestSetup(suite);
    }
}
