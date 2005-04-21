package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ObjectLockedException.java</p>
 * <p><b>Description:</b> Signals that an object was locked.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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