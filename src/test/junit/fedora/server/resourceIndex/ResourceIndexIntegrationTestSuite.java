package fedora.server.resourceIndex;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ResourceIndexIntegrationTestSuite extends TestCase {

    public static Test suite() throws Exception {

        TestSuite suite = new TestSuite(
                ResourceIndexIntegrationTestSuite.class.getName());
   
        // classes in this package
        suite.addTestSuite(ResourceIndexAddDelDissIntegrationTest.class);
        suite.addTestSuite(ResourceIndexAddDelDSIntegrationTest.class);
        suite.addTestSuite(ResourceIndexAddDelMiscIntegrationTest.class);
        suite.addTestSuite(ResourceIndexModDissIntegrationTest.class);
        suite.addTestSuite(ResourceIndexModDSIntegrationTest.class);
        suite.addTestSuite(ResourceIndexModMiscIntegrationTest.class);

        // sub-package suites
        //suite.addTest(SomeUnitTestSuite.suite());

        return suite;

    }

}
