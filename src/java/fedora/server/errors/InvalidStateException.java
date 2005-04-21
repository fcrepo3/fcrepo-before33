package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> InvalidStateException.java</p>
 * <p><b>Description:</b> Thrown when an object or component state is invalid.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class InvalidStateException
        extends ServerException {

    /**
     * Creates an InvalidStateException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public InvalidStateException(String message) {
        super(null, message, null, null, null);
    }

    public InvalidStateException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}