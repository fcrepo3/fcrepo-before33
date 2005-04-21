package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> InvalidOperatorException.java</p>
 * <p><b>Description:</b> Thrown when an operator is invalid.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class InvalidOperatorException
        extends ServerException {

    /**
     * Creates an InvalidOperatorException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public InvalidOperatorException(String message) {
        super(null, message, null, null, null);
    }

    public InvalidOperatorException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}