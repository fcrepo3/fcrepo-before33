package fedora.server.errors;

/**
 * Signals that an object was locked.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ObjectLockedException 
        extends StorageException {

    /**
     * Creates an ObjectLockedException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public ObjectLockedException(String message) {
        super(message);
    }

}