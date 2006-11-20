package fedora.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import org.w3c.dom.Element;

import fedora.server.errors.ServerInitializationException;
import fedora.server.errors.ServerShutdownException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.utilities.ServerUtility;
import fedora.server.utilities.status.ServerState;
import fedora.server.utilities.status.ServerStatusFile;

/**
 * Fedora Server.
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class BasicServer
        extends Server {

    /** Logger for this class. */
    private static Logger LOG = Logger.getLogger(BasicServer.class.getName());

    private File logDir;

    public BasicServer(Element rootElement, File fedoraHomeDir)
            throws ServerInitializationException,
                   ModuleInitializationException {
        super(rootElement, fedoraHomeDir);
    }

    public void initServer()
            throws ServerInitializationException {

        String fedoraServerHost = null;
        String fedoraServerPort = null;

        // fedoraServerHost (required)
        fedoraServerHost=getParameter("fedoraServerHost");
        if (fedoraServerHost==null) {
            throw new ServerInitializationException("Parameter fedoraServerHost "
                + "not given, but it's required.");
        }
        // fedoraServerPort (required)
        fedoraServerPort=getParameter("fedoraServerPort");
        if (fedoraServerPort==null) {
            throw new ServerInitializationException("Parameter fedoraServerPort "
                + "not given, but it's required.");
        }

        LOG.info("Fedora Version: " + VERSION_MAJOR + "." + VERSION_MINOR);
        LOG.info("Fedora Build: " + BUILD_NUMBER);

        ServerStatusFile status = getStatusFile();
        try {
            status.append(ServerState.STARTING, "Fedora Version: " + VERSION_MAJOR + "." + VERSION_MINOR);
            status.append(ServerState.STARTING, "Fedora Build: " + BUILD_NUMBER);
            status.append(ServerState.STARTING, "Server Host Name: " + fedoraServerHost);
            status.append(ServerState.STARTING, "Server Port: " + fedoraServerPort);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerInitializationException("Unable to write to status file: " + e.getMessage());
        }
    }

    private int getLoggerIntParam(String paramName)
            throws ServerInitializationException {
        String s=getParameter(paramName);
        int ret;
        if (s==null) {
            ret=0;
            LOG.debug(paramName + " not specified, defaulting to 0 (infinite)");
        } else {
            try {
                ret=Integer.parseInt(s);
                if (ret<0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException nfe) {
                throw new ServerInitializationException(paramName
                        + " must be an integer from 0 to " + Integer.MAX_VALUE);
            }
            String retString;
            if (ret==0) {
                retString="0 (infinite)";
            } else {
                retString="" + ret;
            }
            LOG.debug(paramName + " specified = " + retString + ", ok.");
        }
        return ret;
    }

    /**
     * Gets the names of the roles that are required to be fulfilled by
     * modules specified in this server's configuration file.
     *
     * @return String[] The roles.
     */
    public String[] getRequiredModuleRoles() {
        return new String[] {"fedora.server.storage.DOManager"};
    }

}
