package fedora.test.junit;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Edwin Shin
 */
public class FooSuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(FooSuite.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for fedora.test.junit");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestFoo.class);
        suite.addTest(BarSuite.suite());
        //$JUnit-END$
        
        return new FedoraServerTestSetup(suite);
    }
}
