package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> MalformedPidException.java</p>
 * <p><b>Description:</b> Tells if a PID is malformed.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class MalformedPidException
        extends ServerException {

    /**
     * Creates a MalformedPIDException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public MalformedPidException(String message) {
        super(null, message, null, null, null);
    }

    public MalformedPidException(String message, Throwable cause) {
        super(null, message, null, null, cause);
    }

    public MalformedPidException(String bundleName, String code, String[] values,
            String[] details, Throwable cause) {
        super(bundleName, code, values, details, cause);
    }

}