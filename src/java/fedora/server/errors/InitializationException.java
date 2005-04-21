package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> InitializationException.java</p>
 * <p><b>Description:</b> Superclass for initialization-related exceptions.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class InitializationException
        extends ServerException {

    /**
     * Creates an InitializationException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public InitializationException(String message) {
        super(null, message, null, null, null);
    }

    public InitializationException(String message, Throwable cause) {
        super(null, message, null, null, cause);
    }

    public InitializationException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}