package fedora.test.junit;

import junit.framework.TestCase;

/**
 * @author Edwin Shin
 */
public class FedoraTestCase extends TestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(FedoraTestCase.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    protected void fedoraStartServer() throws Exception {
        
    }
    
    protected void fedoraStopServer() throws Exception {
        
    }

}
