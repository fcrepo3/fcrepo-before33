package fedora.server.errors;

/**
 * Abstract superclass for storage-related exceptions.
 *
 * @author cwilper@cs.cornell.edu
 */
public abstract class StorageException 
        extends ServerException {

    /**
     * Creates a StorageException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public StorageException(String message) {
        super(null, message, null, null, null);
    }
    
    public StorageException(String bundleName, String code, String[] values, 
            String[] details, Throwable cause) {
        super(bundleName, code, values, details, cause);
    }

}