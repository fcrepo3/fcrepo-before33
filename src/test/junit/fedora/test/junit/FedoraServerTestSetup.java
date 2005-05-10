package fedora.test.junit;

import junit.extensions.TestSetup;
import junit.framework.Test;

/**
 * A wrapper (decorator) for Test responsible for starting and stopping a 
 * Fedora server test fixture.
 * 
 * @author Edwin Shin
 */
public class FedoraServerTestSetup extends TestSetup implements FedoraTestConstants {
    private boolean doSetup;
    
    /**
     * @param test
     */
    public FedoraServerTestSetup(Test test) {
        super(test);
    }

    public void setUp() {
        doSetup = getSetup();
        
        if (doSetup) {
            System.out.println("+ doing setUp()");
        } else {
            System.out.println("    skipping setUp()");
        }
    }
    
    public void tearDown() {
        if (doSetup) {
            System.out.println("- doing tearDown()");
            System.setProperty(PROP_SETUP, "true");
        } else {
            System.out.println("    skipping tearDown()");
        }
    }
    
    /**
     * @return
     */
    private boolean getSetup() {
        String setup = System.getProperty(PROP_SETUP);
        if (setup == null) {
            System.setProperty(PROP_SETUP, "false");
            return true;
        } else {
            return setup.equalsIgnoreCase("true");
        }
        
        
    }
}
