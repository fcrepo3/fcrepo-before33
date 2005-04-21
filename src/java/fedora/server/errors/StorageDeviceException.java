package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> StorageDeviceException.java</p>
 * <p><b>Description:</b> Signals that a storage device failed to behave as
 * expected.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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