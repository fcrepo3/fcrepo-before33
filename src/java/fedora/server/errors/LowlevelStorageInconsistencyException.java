package fedora.server.errors;
public class LowlevelStorageInconsistencyException extends LowlevelStorageException {
	public LowlevelStorageInconsistencyException(String message, Throwable cause) {
		super(true, message, cause);
	}
	public LowlevelStorageInconsistencyException(String message) {
		this(message, null);
	}
}
