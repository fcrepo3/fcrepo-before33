package fedora.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Edwin Shin
 */
public class SuiteQuux {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SuiteQuux.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for fedora.test.junit");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestQuux.class);
        suite.addTest(SuiteBaz.suite());
        //$JUnit-END$
        return suite;
    }
}
