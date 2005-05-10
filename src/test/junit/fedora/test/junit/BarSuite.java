package fedora.test.junit;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Edwin Shin
 */
public class BarSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(BarSuite.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for fedora.test.junit");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestBar.class);
        //$JUnit-END$
        return new FedoraServerTestSetup(suite);
    }
}
