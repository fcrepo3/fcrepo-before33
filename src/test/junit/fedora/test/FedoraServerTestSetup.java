package fedora.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import junit.extensions.TestSetup;
import junit.framework.Test;
import fedora.server.config.BasicServerParameters;
import fedora.server.config.ServerConfiguration;
import fedora.server.config.ServerConfigurationParser;
import fedora.utilities.ExecUtility;

/**
 * A wrapper (decorator) for Test responsible for starting and stopping a 
 * Fedora server test fixture.
 * 
 * n.b. This class makes many assumptions about various filesystem locations.
 * 
 * @author Edwin Shin
 */
public class FedoraServerTestSetup 
  extends    TestSetup 
  implements FedoraTestConstants, BasicServerParameters {
    private boolean doSetup;
    
    /**
     * @param test
     */
    public FedoraServerTestSetup(Test test) {
        super(test);
    }

    public void setUp() throws Exception {
        doSetup = getSetup();
        
        if (doSetup) {
            // setup actions go here
            startServer();
        } else {
            System.out.println("    skipping setUp()");
        }
    }
    
    public void tearDown() {
        if (doSetup) {
            System.setProperty(PROP_SETUP, "true");
            // tear down actions go here
            stopServer();
        } else {
            System.out.println("    skipping tearDown()");
        }
    }
    
    public static String getFedoraHome() {
        return System.getProperty(PROP_FEDORA_HOME, new File("dist").getAbsolutePath());
    }
    
    public static ServerConfiguration getServerConfiguration() throws Exception {
        return new ServerConfigurationParser(
                new FileInputStream(
                  new File(getFedoraHome() + "/server/config/fedora.fcfg"))).parse();
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
    
    private void startServer() {
        System.out.println("+ doing setUp(): starting server...");
        // TODO drop db tables
        String cmd = getFedoraHome() + "/server/bin/fedora-start";
        
        try {
	        Process cp = Runtime.getRuntime().exec(cmd, null);
	        String line;
	        BufferedReader input = new BufferedReader(
	                new InputStreamReader(cp.getInputStream()));
	        while ((line = input.readLine()) != null) {
	            System.out.println(line);
	            if ( line.equals("OK") ) break;
	        }
	        input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void stopServer() {
        System.out.println("- doing tearDown(): stopping server...");
        ExecUtility.exec(getFedoraHome() + "/server/bin/fedora-stop");
        
        // TODO delete low-level store
        
        // TODO drop db tables
    }
}
