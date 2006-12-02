package fedora.server.utilities;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.httpclient.UsernamePasswordCredentials;

import org.apache.log4j.Logger;

import fedora.common.http.WebClient;

import fedora.server.config.ServerConfiguration;
import fedora.server.config.ServerConfigurationParser;
import fedora.server.errors.GeneralException;

public class ServerUtility {

    /** Logger for this class. */
    private static final Logger LOG = Logger.getLogger(
            ServerUtility.class.getName());

    public static final String HTTP = "http";

    public static final String HTTPS = "https";

    public static final String FEDORA_SERVER_HOST = "fedoraServerHost";

    public static final String FEDORA_SERVER_PORT = "fedoraServerPort";

    public static final String FEDORA_REDIRECT_PORT = "fedoraRedirectPort";

    private static ServerConfiguration CONFIG;

    static {
        String fedoraHome = System.getProperty("fedora.home");
        if (fedoraHome == null) {
            LOG.warn("fedora.home property not set; unable to initialize");
        } else {
            File fcfgFile = new File(fedoraHome, "server/config/fedora.fcfg");
            try {
                CONFIG = new ServerConfigurationParser(
                        new FileInputStream(fcfgFile)).parse();
            } catch (IOException e) {
                LOG.warn("Unable to read server configuration from "
                        + fcfgFile.getPath(), e);
            }
        }
    }

    /**
     * Tell whether the server is running by pinging it as a client.
     */
    public static boolean pingServer(String protocol, String user,
            String pass) {
        String url = null;
        try {
            url = protocol + "://"
                    + CONFIG.getParameter(FEDORA_SERVER_HOST).getValue() + ":"
                    + CONFIG.getParameter(FEDORA_SERVER_PORT).getValue()
                    + "/fedora/describe";
            UsernamePasswordCredentials creds = 
                    new UsernamePasswordCredentials(user, pass);
            new WebClient().getResponseAsString(url, true, creds);
            LOG.debug("Successfully pinged server at " + url);
            return true;
        } catch (Exception e) {
            LOG.debug("Assuming the server isn't running because "
                    + "request to " + url + " failed", e);
            return false;
        }
    }

    /**
     * Tell whether the given URL appears to be referring to somewhere
     * within the Fedora webapp.
     */
    public static boolean isURLFedoraServer(String url) {

        String fedoraServerHost = CONFIG.getParameter(FEDORA_SERVER_HOST).getValue();
        String fedoraServerPort = CONFIG.getParameter(FEDORA_SERVER_PORT).getValue();
        String fedoraServerRedirectPort = CONFIG.getParameter(FEDORA_REDIRECT_PORT).getValue();

        // Check for URLs that are callbacks to the Fedora server
        if (url.startsWith("http://"+fedoraServerHost+":"+fedoraServerPort+"/fedora/") ||
            url.startsWith("http://"+fedoraServerHost+"/fedora/") ||   
            url.startsWith("https://"+fedoraServerHost+":"+fedoraServerRedirectPort+"/fedora/") ||
            url.startsWith("https://"+fedoraServerHost+"/fedora/") ) {
            LOG.debug("URL was Fedora-to-Fedora callback: "+url);
            return true;
        } else {
            LOG.debug("URL was Non-Fedora callback: "+url);
            return false;
        }
            
    }    
    
}
