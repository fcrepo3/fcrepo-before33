package fedora.server.errors;
public class LowlevelStorageException extends StorageException {
	public LowlevelStorageException(boolean serverCaused, String bundleName, String code, String[] values,
			String[] details, Throwable cause) {
		super(null, code, null, null, cause);
		if (serverCaused) {
			setWasServer();
		}
	}
	public LowlevelStorageException(boolean serverCaused, String message, Throwable cause) {
		this(serverCaused, null, message, null, null, cause);
	}
	public LowlevelStorageException(boolean serverCaused, String message) {
		this(serverCaused, message, null);
	}
	
	public String getMessage() {
		Throwable e = getCause();
		String temp = super.getMessage();
		if (e != null) {
			temp += ("\t" + e.getMessage());
		}
		return temp;
	}

}




