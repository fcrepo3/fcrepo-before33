package fedora.server.errors;

/**
 * Signals that an object existed when it wasn't expected to have existed.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ObjectExistsException 
        extends StorageException {

    /**
     * Creates an ObjectExistsException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public ObjectExistsException(String message) {
        super(message);
    }

}