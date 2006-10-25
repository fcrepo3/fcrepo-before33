package fedora.server.errors;

/**
 *
 * @author eddie
 */
public class UnsupportedQueryLanguageException extends ResourceIndexException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * @param message
	 */
	public UnsupportedQueryLanguageException(String message) {
		super(message);
	}
}
