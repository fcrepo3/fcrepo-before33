package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ObjectIntegrityException.java</p>
 * <p><b>Description:</b> Signals that an object (serialized or deserialized)
 * is inappropriately formed in the context that it is being examined.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ObjectIntegrityException
        extends StorageException {

    /**
     * Creates an ObjectIntegrityException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public ObjectIntegrityException(String message) {
        super(null, message, null, null, null);
    }

    public ObjectIntegrityException(String message, Throwable th) {
        super(null, message, null, null, th);
    }

    public ObjectIntegrityException(String a, String message, String[] b, String[] c, Throwable th) {
        super(a, message, b, c, th);
    }

}