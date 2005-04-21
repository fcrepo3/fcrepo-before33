package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> UnrecognizedFieldException.java</p>
 * <p><b>Description:</b> Thrown when a field is not recognized.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class UnrecognizedFieldException
        extends ServerException {

    /**
     * Creates an UnrecognizedFieldException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public UnrecognizedFieldException(String message) {
        super(null, message, null, null, null);
    }

    public UnrecognizedFieldException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}