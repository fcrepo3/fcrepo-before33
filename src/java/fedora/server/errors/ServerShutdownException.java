package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ServerShutdownException.java</p>
 * <p><b>Description:</b> Signifies that an error occurred during the server's
 * shutdown.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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