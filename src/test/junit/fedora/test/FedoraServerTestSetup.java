package fedora.test;

import java.io.File;
import java.io.FileInputStream;

import junit.extensions.TestSetup;
import junit.framework.Test;
import fedora.server.config.BasicServerParameters;
import fedora.server.config.Parameter;
import fedora.server.config.ServerConfiguration;
import fedora.server.config.ServerConfigurationParser;
import fedora.utilities.ExecUtility;

/**
 * A wrapper (decorator) for Test responsible for starting and stopping a 
 * Fedora server test fixture.
 * 
 * @author Edwin Shin
 */
public class FedoraServerTestSetup 
  extends    TestSetup 
  implements FedoraTestConstants, BasicServerParameters {
    private boolean doSetup;
    private String fedoraHome;
    private ServerConfiguration config;
    
    /**
     * @param test
     */
    public FedoraServerTestSetup(Test test) {
        super(test);
    }

    public void setUp() throws Exception {
        doSetup = getSetup();
        
        if (doSetup) {
            fedoraHome = getFedoraHome();
            config = getServerConfiguration();
            startServer();
        } else {
            System.out.println("    skipping setUp()");
        }
    }
    
    public void tearDown() {
        if (doSetup) {
            System.setProperty(PROP_SETUP, "true");
            stopServer();
        } else {
            System.out.println("    skipping tearDown()");
        }
    }
    
    public String getFedoraHome() {
        return System.getProperty("fedora.home", "/opt/fedora/current");
    }
    
    private ServerConfiguration getServerConfiguration() throws Exception {
        ServerConfiguration cfg = null;
        cfg = new ServerConfigurationParser(
                new FileInputStream(
                  new File(fedoraHome + "/server/config/fedora.fcfg"))).parse();
        return cfg;
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
    }
    
    private void stopServer() {
        System.out.println("- doing tearDown(): stopping server...");
    }
    
    private void ingestDemoObjects() {
        Parameter port = config.getParameter(PARAM_PORT);
        Parameter adminU = config.getParameter(PARAM_ADMIN_USER);
        Parameter adminP = config.getParameter(PARAM_ADMIN_PASS);
        String ingestDemos = fedoraHome + "/client/bin/fedora-ingest-demos " +
                             "localhost " + port.getValue() + " " + 
                             adminU.getValue() + " " + adminP.getValue() + 
                             " http";
        try {
            Process ingest = ExecUtility.exec(ingestDemos);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
    }
    
    /*--------------------------------------------------------------------------
     * 
     *------------------------------------------------------------------------*/
    
    

}
