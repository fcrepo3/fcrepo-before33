package fedora.server.errors;

/**
 * Signifies that an error occurred during the server's shutdown.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ServerShutdownException 
        extends ShutdownException {

    /**
     * Creates a ServerShutdownException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public ServerShutdownException(String message) {
        super(message);
    }

}