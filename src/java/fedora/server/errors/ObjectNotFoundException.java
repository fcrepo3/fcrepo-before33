package fedora.server.errors;

/**
 * Signals that an object could not be found.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ObjectNotFoundException 
        extends StorageException {

    /**
     * Creates an ObjectNotFoundException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public ObjectNotFoundException(String message) {
        super(message);
    }

}