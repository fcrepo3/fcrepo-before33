package fedora.server.errors;
public class ObjectAlreadyInLowlevelStorageException extends LowlevelStorageException {
	public ObjectAlreadyInLowlevelStorageException(String message, Throwable cause) {
		super(false, message, cause);
	}
	public ObjectAlreadyInLowlevelStorageException(String message) {
		this(message, null);
	}
}
