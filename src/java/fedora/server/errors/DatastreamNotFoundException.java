package fedora.server.errors;

/**
 * Signals that a datastream could not be found.
 *
 * @author cwilper@cs.cornell.edu
 */
public class DatastreamNotFoundException 
        extends StorageException {

    /**
     * Creates a DatastreamNotFoundException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public DatastreamNotFoundException(String message) {
        super(message);
    }

}