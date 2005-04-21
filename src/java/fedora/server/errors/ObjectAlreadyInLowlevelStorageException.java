package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ObjectAlreadyInLowLevelStorageException.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class ObjectAlreadyInLowlevelStorageException extends LowlevelStorageException {
	public ObjectAlreadyInLowlevelStorageException(String message, Throwable cause) {
		super(false, message, cause);
	}
	public ObjectAlreadyInLowlevelStorageException(String message) {
		this(message, null);
	}
}
