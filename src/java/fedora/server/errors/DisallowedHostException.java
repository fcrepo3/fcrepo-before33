package fedora.server.errors;

/**
 * Thrown when a host requests access to a resource that it doesn't have permission to access.
 *
 * @author cwilper@cs.cornell.edu
 */
public class DisallowedHostException
        extends ServerException {

    /**
     * Creates a DisallowedHostException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public DisallowedHostException(String message) {
        super(null, message, null, null, null);
    }
    
    public DisallowedHostException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}