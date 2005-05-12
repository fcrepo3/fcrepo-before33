package fedora.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Test;
import fedora.server.config.Configuration;
import fedora.server.config.Parameter;
import fedora.server.config.ServerConfiguration;
import fedora.server.config.ServerConfigurationParser;
import fedora.server.config.ServerParameters;
import fedora.utilities.ExecUtility;

/**
 * A wrapper (decorator) for Test responsible for starting and stopping a 
 * Fedora server test fixture.
 * 
 * @author Edwin Shin
 */
public class FedoraServerTestSetup 
  extends    TestSetup 
  implements FedoraTestConstants, ServerParameters {
    private boolean doSetup;
    private String fedoraHome;
    private ServerConfiguration config;
    
    /**
     * @param test
     */
    public FedoraServerTestSetup(Test test) {
        super(test);
        fedoraHome = getFedoraHome();
    }

    public void setUp() {
        doSetup = getSetup();
        
        if (doSetup) {
            System.out.println("+ doing setUp()");
            ingestDemoObjects();
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
    
    private String getFedoraHome() {
        return System.getProperty("fedora.home", "/opt/fedora/current");
    }
    
    private ServerConfiguration getServerConfiguration() {
        ServerConfiguration cfg = null;
        try {
            cfg = new ServerConfigurationParser(
                    new FileInputStream(
                      new File(fedoraHome + "/server/config/fedora.fcfg"))).parse();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
