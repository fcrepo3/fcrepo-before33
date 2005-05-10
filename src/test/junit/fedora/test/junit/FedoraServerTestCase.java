package fedora.test.junit;

import junit.framework.TestCase;

/**
 * Base class for JUnit tests that need a running Fedora server.
 * 
 * 
 * @author Edwin Shin
 */
public abstract class FedoraServerTestCase extends TestCase {
    FedoraServerTestSetup testSetup;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(FedoraServerTestCase.class);
    }

    public void setUp() throws Exception {
        testSetup = new FedoraServerTestSetup(this);
        testSetup.setUp();
    }

    public void tearDown() throws Exception {
        testSetup.tearDown();
    }
}
