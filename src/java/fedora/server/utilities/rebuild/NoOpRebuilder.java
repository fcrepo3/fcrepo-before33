package fedora.server.utilities.rebuild;

import java.io.*;
import java.util.*;

import fedora.server.config.ServerConfiguration;
import fedora.server.storage.types.DigitalObject;

/**
 * A Rebuilder that doesn't do anything useful, for testing purposes.
 * 
 * @version $Id$
 */
public class NoOpRebuilder implements Rebuilder {

    private File m_serverDir;
    private ServerConfiguration m_serverConfig;

    private String m_echoString;

    /**
     * Get a short phrase describing what the user can do with this rebuilder.
     */
    public String getAction() {
        return "Test the rebuilder interface.";
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
        m.put("startupDelay", 
              "Milliseconds to delay at start of rebuild. Default is zero.");
        m.put("echoString", 
              "What to echo each time an object is added. Default is nothing.");
        return m;
    }

    /**
     * Validate the provided options and perform any necessary startup tasks.
     */
    public void start(Map options) throws NumberFormatException {
        long startupDelay = 0;

        // validate options
        String s = (String) options.get("startupDelay");
        if (s != null) {
            startupDelay = Long.parseLong(s);
        }
        m_echoString = (String) options.get("echoString");

        // do startup tasks
        try { Thread.sleep(startupDelay); } catch (Throwable th) { }
    }

    /**
     * Add the data of interest for the given object.
     */
    public void addObject(DigitalObject object) {
        if (m_echoString != null) System.out.println(m_echoString);
    }

    /**
     * Free up any system resources associated with rebuilding.
     */
    public void finish() {
        // nothing to free up
    }

}