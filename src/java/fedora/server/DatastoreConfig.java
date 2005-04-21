package fedora.server;

import java.util.Map;

/**
 *
 * <p><b>Title:</b> DatastoreConfig.java</p>
 * <p><b>Description:</b> A holder of configuration name-value pairs for a
 * datastore.</p>
 *
 * <p>A datastore is a system for retrieving and storing information.  This
 * class is a convenient placeholder for the configuration values of such
 * a system.</p>
 *
 * <p>Configuration values for datastores are set in the server configuration
 * file. (see fedora-config.xsd)</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DatastoreConfig
        extends Parameterized {

    /**
     * Creates and initializes the <code>DatastoreConfig</code>.
     * <p></p>
     * When the server is starting up, this is invoked as part of the
     * initialization process.
     *
     * @param parameters A pre-loaded Map of name-value pairs
     *        comprising the intended configuration for the datastore.
     */
    public DatastoreConfig(Map parameters) {
        super(parameters);
    }

}