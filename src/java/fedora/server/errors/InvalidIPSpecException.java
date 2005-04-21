package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> InvalidIPSpecException.java</p>
 * <p><b>Description:</b> Thrown when when an IP is bad.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class InvalidIPSpecException
        extends ServerException {

    /**
     * Creates an InvalidIPSpecException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public InvalidIPSpecException(String message) {
        super(null, message, null, null, null);
    }

    public InvalidIPSpecException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}