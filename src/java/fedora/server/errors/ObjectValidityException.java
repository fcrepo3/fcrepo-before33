package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ObjectValidityException.java</p>
 * <p><b>Description:</b> Signals that an object is not valid.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class ObjectValidityException extends ServerException {

    /**
     * Creates an ObjectValidityException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public ObjectValidityException(String message) {
        super(null, message, null, null, null);
    }

    public ObjectValidityException(String message, Throwable cause) {
        super(null, message, null, null, cause);
    }

    public ObjectValidityException(String a, String message, String[] b, String[] c, Throwable th) {
        super(a, message, b, c, th);
    }

}