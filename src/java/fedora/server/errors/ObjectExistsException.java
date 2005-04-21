package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ObjectExistsException.java</p>
 * <p><b>Description:</b> Signals that an object existed when it wasn't
 * expected to have existed.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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