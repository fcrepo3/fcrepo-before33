package fedora.server.errors;

/**
 * Signals that a storage device failed to behave as expected.
 *
 * @author cwilper@cs.cornell.edu
 */
public class StorageDeviceException 
        extends StorageException {

    /**
     * Creates a StorageDeviceException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public StorageDeviceException(String message) {
        super(message);
    }

}