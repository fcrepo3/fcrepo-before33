package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> DatastreamNotFoundException.java</p>
 * <p><b>Description:</b> Signals that a datastream could not be found.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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