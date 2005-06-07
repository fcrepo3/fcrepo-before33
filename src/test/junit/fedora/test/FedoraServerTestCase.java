package fedora.test;

import fedora.server.config.ServerConfiguration;

/**
 * Base class for JUnit tests that need a running Fedora server.
 * 
 * 
 * @author Edwin Shin
 */
public abstract class FedoraServerTestCase extends FedoraTestCase {
    private FedoraServerTestSetup testSetup;
    private String baseURL;
    
    public FedoraServerTestCase() {
        super();
    }
    
    public FedoraServerTestCase(String name) {
        super(name);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(FedoraServerTestCase.class);
    }

    public void setUp() throws Exception {
        // FedoraServerTestSetup starts a Fedora server if needed
        testSetup = new FedoraServerTestSetup(this);
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
}
