package fedora.server.errors;

/**
 * Signals that an object is not valid.
 *
 * @author Sandy Payette, payette@cs.cornell.edu
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

    public ObjectValidityException(String a, String message, String[] b, String[] c, Throwable th) {
        super(a, message, b, c, th);
    }

}