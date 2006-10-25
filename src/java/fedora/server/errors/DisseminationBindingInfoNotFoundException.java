package fedora.server.errors;

/**
 * <p>
 * <b>Title: </b>DisseminationBindingInfoNotFoundException.java
 * </p>
 * <p>
 * <b>Description: </b>Signals that an instance of DisseminationBindingInfo
 * could not be found or was null.
 * </p>
 * 
 * @author rlw@virginia.edu
 * @version $Id: DisseminationBindingInfoNotFoundException.java,v 1.7 2005/04/21
 *          12:59:22 rlw Exp $
 */
public class DisseminationBindingInfoNotFoundException extends StorageException {
	private static final long serialVersionUID = 1L;
	/**
	 * <p>
	 * Creates a DisseminationBindingInfoNotFoundException.
	 * </p>
	 * 
	 * @param message
	 *            An informative message explaining what happened and (possibly)
	 *            how to fix it.
	 */
	public DisseminationBindingInfoNotFoundException(String message) {
		super(message);
	}
}