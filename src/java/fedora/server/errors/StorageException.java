package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> StorageException.java</p>
 * <p><b>Description:</b> Abstract superclass for storage-related exceptions.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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