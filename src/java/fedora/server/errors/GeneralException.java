package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> GeneralException.java</p>
 * <p><b>Description:</b> A general exception indicating something went wrong
 * on the server.</p>
 *
 * <p>This type of exception doesn't characterize the error by java type,
 * but may still classify it by message and code.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public final class GeneralException
        extends ServerException {

    /**
     * Creates a GeneralException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public GeneralException(String message) {
        super(null, message, null, null, null);
    }

    public GeneralException(String message, Throwable cause) {
        super(null, message, null, null, cause);
    }

    public GeneralException(String bundleName, String code, String[] values,
            String[] details, Throwable cause) {
        super(bundleName, code, values, details, cause);
    }

}