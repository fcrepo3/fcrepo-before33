package fedora.server.errors;

/**
 * Signals that a disseminator could not be found.
 *
 * @author cwilper@cs.cornell.edu
 */
public class DisseminatorNotFoundException 
        extends StorageException {

    /**
     * Creates a DisseminatorNotFoundException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public DisseminatorNotFoundException(String message) {
        super(message);
    }

}