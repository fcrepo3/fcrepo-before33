package fedora.test.junit;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * An example of a custom TestSuite that starts and stops a single Fedora server 
 * for an entire Suite.
 * 
 * The key is for suite() to return a new instance of FedoraServerTestSetup 
 * and for the TestCases to extend FedoraServerTestCase. TestCases that override 
 * the setUp and tearDown methods must ensure the overriden methods call super()
 * for the server to be properly started and shutdown.
 * 
 * @author Edwin Shin
 */
public class FedoraServerTestSuite extends TestCase {
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(FedoraServerTestSuite.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for fedora.test.junit");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestFoo.class);
        //$JUnit-END$
        
        return new FedoraServerTestSetup(suite);
    }
}
