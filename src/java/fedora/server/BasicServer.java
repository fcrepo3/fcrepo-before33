/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server;

import java.io.File;

import org.apache.log4j.Logger;

import org.w3c.dom.Element;

import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.utilities.status.ServerState;
import fedora.server.utilities.status.ServerStatusFile;

/**
 * Fedora Server.
 * 
 * @author Chris Wilper
 */
public class BasicServer
        extends Server {

    /** Logger for this class. */
    private static Logger LOG = Logger.getLogger(BasicServer.class.getName());

    public BasicServer(Element rootElement, File fedoraHomeDir)
            throws ServerInitializationException, ModuleInitializationException {
        super(rootElement, fedoraHomeDir);
    }

    @Override
    public void initServer() throws ServerInitializationException {

        String fedoraServerHost = null;
        String fedoraServerPort = null;

        // fedoraServerHost (required)
        fedoraServerHost = getParameter("fedoraServerHost");
        if (fedoraServerHost == null) {
            throw new ServerInitializationException("Parameter fedoraServerHost "
                    + "not given, but it's required.");
        }
        // fedoraServerPort (required)
        fedoraServerPort = getParameter("fedoraServerPort");
        if (fedoraServerPort == null) {
            throw new ServerInitializationException("Parameter fedoraServerPort "
                    + "not given, but it's required.");
        }

        LOG.info("Fedora Version: " + VERSION_MAJOR + "." + VERSION_MINOR);
        LOG.info("Fedora Build: " + BUILD_NUMBER);

        ServerStatusFile status = getStatusFile();
        try {
            status.append(ServerState.STARTING, "Fedora Version: "
                    + VERSION_MAJOR + "." + VERSION_MINOR);
            status
                    .append(ServerState.STARTING, "Fedora Build: "
                            + BUILD_NUMBER);
            status.append(ServerState.STARTING, "Server Host Name: "
                    + fedoraServerHost);
            status.append(ServerState.STARTING, "Server Port: "
                    + fedoraServerPort);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerInitializationException("Unable to write to status file: "
                    + e.getMessage());
        }
    }

    /**
     * Gets the names of the roles that are required to be fulfilled by modules
     * specified in this server's configuration file.
     * 
     * @return String[] The roles.
     */
    @Override
    public String[] getRequiredModuleRoles() {
        return new String[] {"fedora.server.storage.DOManager"};
    }

}
