package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> UnknownSessionTokenException.java</p>
 * <p><b>Description:</b> Signals that the requested session was not found.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class UnknownSessionTokenException
        extends ServerException {

	private static final long serialVersionUID = 1L;
	
    /**
     * Creates an UnknownSessionTokenException
     *
     * @param msg Description of the exception.
     */
    public UnknownSessionTokenException(String msg) {
           super(null, msg, null, null, null);
    }
}
