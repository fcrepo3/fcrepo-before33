package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> InvalidContextException.java</p>
 * <p><b>Description:</b> Thrown when context is invalid.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class InvalidContextException
        extends ServerException {

    /**
     * Creates an InvalidContextException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public InvalidContextException(String message) {
        super(null, message, null, null, null);
    }

    public InvalidContextException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}