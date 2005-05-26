package fedora.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Edwin Shin
 */
public class SuiteBaz {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SuiteBaz.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for fedora.test.junit");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestBaz.class);
        //$JUnit-END$
        return suite;
    }
}
