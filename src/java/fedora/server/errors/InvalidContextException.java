package fedora.server.errors;

/**
 * Thrown when context is invalid.
 *
 * @author cwilper@cs.cornell.edu
 */
public class InvalidContextException
        extends ServerException {

    /**
     * Creates an InvalidContextException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public InvalidContextException(String message) {
        super(null, message, null, null, null);
    }
    
    public InvalidContextException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}