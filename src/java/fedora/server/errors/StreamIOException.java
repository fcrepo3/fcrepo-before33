package fedora.server.errors;

/**
 * Superclass for low-level stream i/o problems.
 *
 * @author cwilper@cs.cornell.edu
 */
public class StreamIOException
        extends ServerException {

    /**
     * Creates a StreamIOException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public StreamIOException(String message) {
        super(null, message, null, null, null);
    }
    
    public StreamIOException(String bundleName, String code, String[] values, 
            String[] details, Throwable cause) {
        super(bundleName, code, values, details, cause);
    }

}