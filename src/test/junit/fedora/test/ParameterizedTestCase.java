package fedora.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;
import fedora.server.config.ServerConfiguration;
import fedora.server.config.ServerConfigurationParser;

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
        suite.addTest(
            new ParameterizedTestCase(
                new Properties() ) );
        
        return suite;
    }
    
    public void doTest() {
        // start server with fcfg
        
        // run tests with 
        
        // stop server
        
    }
    
    private void foo() throws Exception {
        FileInputStream fis = new FileInputStream(FCFG_LOCATION);
        ServerConfigurationParser scp = new ServerConfigurationParser(fis);
        ServerConfiguration config = scp.parse();
        
        File propDir = new File("");
        if (propDir.exists() && propDir.isDirectory()) {
            File[] files = propDir.listFiles();
            //Arrays.sort(files, String.CASE_INSENSITIVE_ORDER);
            int count = files.length;
            for (int i = 0; i < count; i++) { //for each file:
                File f = files[i];
                if (f.isFile() && f.getName().endsWith(".properties") && f.getName().startsWith("junit")) {
                    FileInputStream pfis = new FileInputStream(f);
                    Properties props = new Properties();
                    props.load(pfis);
                    config.applyProperties(props);
                    
                    // run a suite of tests w/ the new config
                }
            }//next file

            propDir.delete(); //finally delete (empty) input directory
        }
        
        
        config.applyProperties(new Properties());
    }
    
}
