
package fedora.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.custommonkey.xmlunit.XMLTestCase;

import fedora.client.FedoraClient;

import fedora.server.config.ServerConfiguration;
import fedora.server.config.ServerConfigurationParser;

/**
 * Base class for Fedora Test Cases
 * 
 * @author Edwin Shin
 */
public abstract class FedoraTestCase
        extends XMLTestCase
        implements FedoraTestConstants {

    public static String ssl = "http";

    public FedoraTestCase() {
        super();
    }

    public FedoraTestCase(String name) {
        super(name);
    }

    public static ServerConfiguration getServerConfiguration() {
        try {
            return new ServerConfigurationParser(new FileInputStream(FCFG))
                    .parse();
        } catch (Exception e) {
            fail(e.getMessage());
            return null;
        }
    }

    public static String getBaseURL() {
        if (System.getProperty("fedora.baseURL") != null) {
            return System.getProperty("fedora.baseURL");
        } else {
            return getProtocol() + "://" + getHost() + ":" + getPort()
                    + "/fedora";
        }
    }

    public static String getHost() {
        return getServerConfiguration().getParameter("fedoraServerHost")
                .getValue();
    }

    public static String getPort() {
        String port = null;
        if (getProtocol().equals("http")) {
            port =
                    getServerConfiguration().getParameter("fedoraServerPort")
                            .getValue();
        } else {
            port =
                    getServerConfiguration().getParameter("fedoraRedirectPort")
                            .getValue();
        }
        return port;
    }

    // hack to dynamically set protocol based on settings in beSecurity
    // Settings for fedoraInternalCall-1 should have callSSL=true when server is secure
    public static String getProtocol() {
        BufferedReader br = null;
        try {
            br =
                    new BufferedReader(new InputStreamReader(new FileInputStream(BESECURITY)));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.indexOf("role=\"fedoraInternalCall-1\"") > 0
                        && line.indexOf("callSSL=\"true\"") > 0) {
                    ssl = "https";
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("fedora.home: " + FEDORA_HOME);
            fail("beSecurity file Not found: " + BESECURITY.getAbsolutePath());
        } finally {
            try {
                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (Exception e) {
                System.out.println("Unable to close BufferdReader");
            }
        }
        return ssl;
    }

    public static String getUsername() {
        return FEDORA_USERNAME;
    }

    public static String getPassword() {
        return FEDORA_PASSWORD;
    }

    public static FedoraClient getFedoraClient() throws Exception {
        return getFedoraClient(getBaseURL(), getUsername(), getPassword());
    }

    public static FedoraClient getFedoraClient(String baseURL,
                                               String username,
                                               String password)
            throws Exception {
        return new FedoraClient(baseURL, username, password);
    }
}
