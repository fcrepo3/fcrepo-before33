package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ShutdownException.java</p>
 * <p><b>Description:</b> Superclass for shutdown-related exceptions.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ShutdownException
        extends ServerException {

    /**
     * Creates an ShutdownException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public ShutdownException(String message) {
        super(null, message, null, null, null);
    }

    public ShutdownException(String message, Throwable cause) {
        super(null, message, null, null, cause);
    }

    public ShutdownException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}
