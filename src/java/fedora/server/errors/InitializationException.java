package fedora.server.errors;

/**
 * Superclass for initialization-related exceptions.
 *
 * Note that this class cannot be instantiated from outside this
 * package, so when creating an initializaton exception, you must
 * choose either a ServerIntializationException or a 
 * ModuleInitializationException.
 *
 * @author cwilper@cs.cornell.edu
 */
public class InitializationException 
        extends Exception {

    /**
     * Creates an InitializationException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    protected InitializationException(String message) {
        super(message);
    }

}