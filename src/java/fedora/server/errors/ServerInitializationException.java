package fedora.server.errors;

/**
 * Signifies that an error occurred during the server's initialization.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ServerInitializationException 
        extends InitializationException {

    /**
     * Creates a ServerInitializationException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public ServerInitializationException(String message) {
        super(message);
    }

}