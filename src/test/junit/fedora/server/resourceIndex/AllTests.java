package fedora.server.resourceIndex;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Edwin Shin
 */
public class AllTests {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AllTests.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for fedora.server.resourceIndex");
        //$JUnit-BEGIN$
        //suite.addTestSuite(TestResourceIndexTimings.class);
        suite.addTestSuite(TestResourceIndexQueries.class);
        suite.addTestSuite(TestResourceIndexDB.class);
        suite.addTestSuite(TestResourceIndexImpl.class);
        suite.addTestSuite(TestResourceIndexLevels.class);
        suite.addTestSuite(TestResourceIndexDependencies.class);
        //$JUnit-END$
        return suite;
    }
}
