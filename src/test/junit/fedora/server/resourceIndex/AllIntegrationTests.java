package fedora.server.resourceIndex;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
 
@RunWith(Suite.class)
@Suite.SuiteClasses({
  ResourceIndexDatePrecisionIntegrationTest.class,
  ResourceIndexAddDelDissIntegrationTest.class,
  ResourceIndexAddDelDSIntegrationTest.class,
  ResourceIndexAddDelMiscIntegrationTest.class,
  ResourceIndexModDissIntegrationTest.class,
  ResourceIndexModDSIntegrationTest.class,
  ResourceIndexModMiscIntegrationTest.class
})
public class AllIntegrationTests extends TestCase {

    public static Test suite() throws Exception {

        TestSuite suite = new TestSuite(
                AllIntegrationTests.class.getName());
   
        suite.addTest(ResourceIndexDatePrecisionIntegrationTest.suite());
   
        suite.addTest(ResourceIndexAddDelDissIntegrationTest.suite());
        suite.addTest(ResourceIndexAddDelDSIntegrationTest.suite());
        suite.addTest(ResourceIndexAddDelMiscIntegrationTest.suite());

        suite.addTest(ResourceIndexModDissIntegrationTest.suite());
        suite.addTest(ResourceIndexModDSIntegrationTest.suite());
        suite.addTest(ResourceIndexModMiscIntegrationTest.suite());

        return suite;

    }

}
