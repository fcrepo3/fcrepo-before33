package fedora.server.resourceIndex;

import java.io.*;
import java.util.*;

import fedora.server.utilities.rebuild.*;
import fedora.server.config.ServerConfiguration;
import fedora.server.storage.types.DigitalObject;

/**
 * A Rebuilder for the resource index.
 * 
 * @version $Id$
 */
public class ResourceIndexRebuilder implements Rebuilder {

    private File m_serverDir;
    private ServerConfiguration m_serverConfig;

    /**
     * Get a short phrase describing what the user can do with this rebuilder.
     */
    public String getAction() {
        return "Rebuild the Resource Index.";
    }

    /**
     * Initialize the rebuilder, given the server configuration.
     *
     * @returns a map of option names to plaintext descriptions.
     */
    public Map init(File serverDir,
                    ServerConfiguration serverConfig) {
        m_serverDir = serverDir;
        m_serverConfig = serverConfig;
        Map m = new HashMap();

//        m.put("startupDelay", 
//              "Milliseconds to delay at start of rebuild. Default is zero.");
        return m;
    }

    /**
     * Validate the provided options and perform any necessary startup tasks.
     */
    public void start(Map options) throws NumberFormatException {
        // validate options
        // do startup tasks
    }

    /**
     * Add the data of interest for the given object.
     */
    public void addObject(DigitalObject object) {
        System.out.println("Rebuilding " + object.getPid());
    }

    /**
     * Free up any system resources associated with rebuilding.
     */
    public void finish() {
        // nothing to free up
    }

}