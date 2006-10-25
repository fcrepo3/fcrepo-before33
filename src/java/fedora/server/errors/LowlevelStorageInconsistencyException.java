package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> LowLevelStorageInconsistencyException.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class LowlevelStorageInconsistencyException extends LowlevelStorageException {
	private static final long serialVersionUID = 1L;
	
	public LowlevelStorageInconsistencyException(String message, Throwable cause) {
		super(true, message, cause);
	}
	public LowlevelStorageInconsistencyException(String message) {
		this(message, null);
	}
}
