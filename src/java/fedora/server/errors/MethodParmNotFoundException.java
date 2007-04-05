/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.errors;

/**
 * <p>
 * <b>Title: </b>MethodParmNotFoundException.java
 * </p>
 * <p>
 * <b>Description: </b>Signals that a method parameter associated with a
 * Behavior
 * </p>
 * <p>
 * Mechanism could not be found.
 * </p>
 * 
 * @author rlw@virginia.edu
 * @version $Id: MethodParmNotFoundException.java,v 1.7 2005/04/21 12:59:22 rlw
 *          Exp $
 */
public class MethodParmNotFoundException extends StorageException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a MethodParmNotFoundException.
	 * 
	 * @param message
	 *            An informative message explaining what happened and (possibly)
	 *            how to fix it.
	 */
	public MethodParmNotFoundException(String message) {
		super(message);
	}
}
