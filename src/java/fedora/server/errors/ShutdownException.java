package fedora.server.errors;

/**
 * Superclass for shutdown-related exceptions.
 *
 * Note that this class cannot be instantiated from outside this
 * package, so when creating a shutdown exception, you must
 * choose either a ServerShutdownException or a ModuleShutdownException.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ShutdownException 
        extends Exception {

    /**
     * Creates a ShutdownException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    protected ShutdownException(String message) {
        super(message);
    }

}