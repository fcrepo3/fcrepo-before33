package fedora.server.errors;

/**
 * Superclass for initialization-related exceptions.
 *
 * @author cwilper@cs.cornell.edu
 */
public class InitializationException 
        extends ServerException {

    /**
     * Creates an InitializationException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public InitializationException(String message) {
        super(null, message, null, null, null);
    }
    
    public InitializationException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}