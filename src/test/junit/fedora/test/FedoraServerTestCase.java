package fedora.test;

import java.io.File;

import fedora.client.*;
import fedora.server.config.Configuration;
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

        // currently just blows away existing policies and replaces them with
        // whatever's in dirName

        System.out.println("Replacing policies...");
        replacePolicies(new File(policyBaseDir, "repository-policies"), 
                        "REPOSITORY-POLICIES-DIRECTORY");
        replacePolicies(new File(policyBaseDir, "object-policies"), 
                        "OBJECT-POLICIES-DIRECTORY");
        replacePolicies(new File(policyBaseDir, "surrogate-policies"), 
                        "SURROGATE-POLICIES-DIRECTORY");
        replacePolicies(new File(policyBaseDir, "repository-policies-generated-by-policyguitool"), 
                        "REPOSITORY-POLICY-GUITOOL-POLICIES-DIRECTORY");

        File backendSecurityReplacement = new File(dirName, "beSecurity.xml");
        if (backendSecurityReplacement.exists()) {
            File currentBackendSecurity = new File(BESECURITY_PATH);
            currentBackendSecurity.delete();
            FedoraServerTestSetup.copy(backendSecurityReplacement, currentBackendSecurity);
        }
       
        System.out.println("Telling server to reload policies...");
        FedoraClient client = new FedoraClient(getBaseURL(), getUsername(), getPassword());
        client.reloadPolicies();
    }

    private void replacePolicies(File fromDir, String toDirProp) throws Exception {
        Configuration config = getServerConfiguration().getModuleConfiguration("fedora.server.security.Authorization");
        File toDir = new File(config.getParameter(toDirProp).getValue());
        if (toDir.exists()) {
            clearDir(toDir);
            if (fromDir.exists()) {
                copyFiles(fromDir, toDir);
            }
        }
    }

    private static void clearDir(File dir) throws Exception {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            files[i].delete();
        }
    }

    private static void copyFiles(File fromDir, File toDir) throws Exception {
        File[] files = fromDir.listFiles();
        for (int i = 0; i < files.length; i++) {
            FedoraServerTestSetup.copy(files[i], new File(toDir, files[i].getName()));
        }
    }

}
