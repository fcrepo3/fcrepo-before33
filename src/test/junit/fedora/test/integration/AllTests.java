package fedora.test.integration;

import junit.framework.Test;
import junit.framework.TestSuite;
import fedora.test.FedoraServerTestSetup;
import fedora.test.config.*;

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

        TestSuite defaultTests = new TestSuite("Default configuration tests");
        defaultTests.addTest(TestIngestDemoObjects.suite());
        defaultTests.addTest(TestCommandLineUtilities.suite());
//
// NOTE: This is currently commented out because it fails at
//       TestAPIA.testListMethods() ... when that is fixed,
//       this can be un-commented.
//
//        defaultTests.addTest(AllAPITests.suite());

        TestSuite configTests = new TestSuite("Other configuration tests");
        configTests.addTest(TestConfigExample.suite());

        TestSuite allTests = new TestSuite("All tests");
        allTests.addTest(new FedoraServerTestSetup(defaultTests));
        allTests.addTest(configTests);

        return allTests;
    }
}
