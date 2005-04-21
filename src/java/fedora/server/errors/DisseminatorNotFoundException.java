package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> DisseminatorNotFound.java</p>
 * <p><b>Description:</b> Signals that a disseminator could not be found.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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