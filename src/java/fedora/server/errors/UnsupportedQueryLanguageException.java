package fedora.server.errors;

/**
 * @author eddie
 */
public class UnsupportedQueryLanguageException extends ResourceIndexException {

	/**
	 * @param message
	 */
	public UnsupportedQueryLanguageException(String message) {
		super(message);
	}
}
