package fedora.server.errors;
public class ObjectNotInLowlevelStorageException extends LowlevelStorageException {
	public ObjectNotInLowlevelStorageException(String message, Throwable cause) {
		super(false, message, cause);
	}
	public ObjectNotInLowlevelStorageException(String message) {
		this(message, null);
	}
}
