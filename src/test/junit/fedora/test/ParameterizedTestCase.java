package fedora.test;

import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Edwin Shin
 */
public class ParameterizedTestCase extends FedoraServerTestCase {
    private Properties fcfg;
    
    public ParameterizedTestCase(Properties fcfg) {
        super("doTest");
        this.fcfg = fcfg;
    }
    
    public static Test suite() throws Exception {
        TestSuite suite = new TestSuite();
        
        
        return suite;
    }
    
    public void doTest() {
        
    }
}
