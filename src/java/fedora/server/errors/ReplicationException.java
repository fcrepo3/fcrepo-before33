package fedora.server.errors;

/**
 * 
 * <p>
 * <b>Title:</b> ReplicationException.java
 * </p>
 * <p>
 * <b>Description:</b> Signals a problem during replication.
 * </p>
 * 
 * @author Paul Charlton
 * @version $Id$
 */
public class ReplicationException extends ServerException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a ReplicationException.
	 * 
	 * @param msg
	 *            Description of the exception.
	 * 
	 */
	public ReplicationException(String msg) {
		super(null, msg, null, null, null);
	}
}
