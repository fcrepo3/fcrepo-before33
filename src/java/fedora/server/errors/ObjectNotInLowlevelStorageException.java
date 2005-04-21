package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ObjectNotInLowLevelStorage.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class ObjectNotInLowlevelStorageException extends LowlevelStorageException {
	public ObjectNotInLowlevelStorageException(String message, Throwable cause) {
		super(false, message, cause);
	}
	public ObjectNotInLowlevelStorageException(String message) {
		this(message, null);
	}
}
