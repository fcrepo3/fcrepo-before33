package fedora.test;

import java.io.File;

import fedora.server.config.ServerConfiguration;

/**
 * Base class for JUnit tests that need a running Fedora server.
 * 
 * 
 * @author Edwin Shin
 */
public abstract class FedoraServerTestCase extends FedoraTestCase {

    private FedoraServerTestSetup testSetup;
    private File m_configDir;
    
    public FedoraServerTestCase() {
        super();
        initConfigDir();
    }
    
    public FedoraServerTestCase(String name) {
        super(name);
        initConfigDir();
    }

    private void initConfigDir() {
        String testHome = System.getProperty(PROP_TEST_HOME);
        if (testHome == null) {
            throw new RuntimeException("Required system property not set: " 
                    + PROP_TEST_HOME);
        }

        m_configDir = new File(new File(testHome), 
                               this.getClass().getName().replaceAll("\\.", "/"));
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(FedoraServerTestCase.class);
    }

    public void setUp() throws Exception {
        // FedoraServerTestSetup starts a Fedora server if needed
        testSetup = new FedoraServerTestSetup(this, this.getClass().getName());
        testSetup.setUp();
    }

    public void tearDown() throws Exception {
        // FedoraServerTestSetup stops a Fedora server if we started it
        if (testSetup != null) {
            testSetup.tearDown();
        }
    }
   
    public static ServerConfiguration getServerConfiguration() {
        try {
            return FedoraServerTestSetup.getServerConfiguration();
        } catch(Exception e) {
            fail(e.getMessage());
            return null;
        }
    }
    
    public static String getBaseURL() {
        return getProtocol() + "://" + getHost() + ":" + getPort() + "/fedora";  
    }
    
    public static String getHost() {
        return getServerConfiguration().getParameter("fedoraServerHost").getValue();
    }
    
    public static String getPort() {
        return getServerConfiguration().getParameter("fedoraServerPort").getValue();
    }
    
    public static String getProtocol() {
        return "http"; // FIXME how to get this?
    }
    
    public static String getUsername() {
        return getServerConfiguration().getParameter("adminUsername").getValue();
    }
    
    public static String getPassword() {
        return getServerConfiguration().getParameter("adminPassword").getValue();
    }

    public void usePolicies(String dirName) throws Exception {
        File policyBaseDir = new File(m_configDir, dirName);
        System.out.println("Using policies from " + policyBaseDir.getPath());
    }
}
