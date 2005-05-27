package fedora.test;

import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Edwin Shin
 */
public class ParameterizedTestCase extends TestCase {
    private Properties fcfg;
    
    public ParameterizedTestCase(Properties fcfg) {
        super("doTest");
        this.fcfg = fcfg;
    }
    
    public static Test suite() throws Exception {

        TestSuite suite = new TestSuite();
        suite.addTest( new ParameterizedTestCase(null) );
        
        return suite;
    }
    
    public void doTest() {
        
        System.out.println("* doTest() *");
        // start server with fcfg
        
        // run tests with 
        
        // stop server
        
    }
    
}
