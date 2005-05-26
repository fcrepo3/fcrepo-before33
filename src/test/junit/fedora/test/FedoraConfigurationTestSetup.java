/*
 * Created on May 26, 2005
 *
 */
package fedora.test;

import junit.extensions.TestSetup;
import junit.framework.Test;

/**
 * @author Edwin Shin
 *
 */
public class FedoraConfigurationTestSetup extends TestSetup {
    public FedoraConfigurationTestSetup(Test test) {
        super(test);
    }
    
    protected void setUp() {
        System.out.println("+ fcfg setup");
    }
    
    protected void tearDown() {
        System.out.println("+ fcfg teardown");
    }
}
