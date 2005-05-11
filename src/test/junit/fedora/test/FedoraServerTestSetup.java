package fedora.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import fedora.server.config.Configuration;
import fedora.server.config.Parameter;
import fedora.server.config.ServerConfiguration;
import fedora.server.config.ServerConfigurationParser;
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
    private String fedoraHome;
    private ServerConfiguration config;
    
    /**
     * @param test
     */
    public FedoraServerTestSetup(Test test) {
        super(test);
        fedoraHome = getFedoraHome();
        try {
            config = getServerConfig(fedoraHome, null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
        return System.getProperty(PROP_FEDORA_HOME, "");
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
        Runtime run = Runtime.getRuntime();
        Parameter port = config.getParameter("fedoraServerPort");
        Parameter adminU = config.getParameter("adminUsername");
        Parameter adminP = config.getParameter("adminPassword");
        try {
            //Process ingest = run.exec(
            System.out.println(fedoraHome + 
                                      "/client/bin/fedora-ingest-demos localhost " + 
                                      port.getValue() + " " + adminU.getValue() + 
                                      " " + adminP.getValue() + " http");
        } catch (Exception e) {
            fail(e.getMessage());
        }
        
    }
    
    private static ServerConfiguration getServerConfig(String dir,
            String profile) throws IOException {
        ServerConfigurationParser parser = new ServerConfigurationParser(
                new FileInputStream(new File(dir + "/server/config/test.fcfg")));
        ServerConfiguration serverConfig = parser.parse();
        // set all the values according to the profile, if specified
        if (profile != null) {
            int c = setValuesForProfile(serverConfig, profile);
            c += setValuesForProfile(serverConfig.getModuleConfigurations(),
                    profile);
            c += setValuesForProfile(serverConfig.getDatastoreConfigurations(),
                    profile);
            if (c == 0) {
                throw new IOException("Unrecognized server-profile: " + profile);
            }
            // System.out.println("Set " + c + " profile-specific values.");
        }
        return serverConfig;
    }

    private static int setValuesForProfile(Configuration config, String profile) {
        int c = 0;
        Iterator iter = config.getParameters().iterator();
        while (iter.hasNext()) {
            Parameter param = (Parameter) iter.next();
            String profileValue = (String) param.getProfileValues()
                    .get(profile);
            if (profileValue != null) {
                //System.out.println(param.getName() + " was '" +
                // param.getValue() + "', now '" + profileValue + "'.");
                param.setValue(profileValue);
                c++;
            }
        }
        return c;
    }

    private static int setValuesForProfile(List configs, String profile) {
        Iterator iter = configs.iterator();
        int c = 0;
        while (iter.hasNext()) {
            c += setValuesForProfile((Configuration) iter.next(), profile);
        }
        return c;
    }


}
