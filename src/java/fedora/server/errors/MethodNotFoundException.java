/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.errors;

/**
 * <p>
 * <b>Title: </b>MethodNotFoundException.java
 * </p>
 * <p>
 * <b>Description: </b>Signals that a method associated with a Behavior
 * </p>
 * <p>
 * Mechanism could not be found.
 * </p>
 * 
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class MethodNotFoundException extends StorageException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a MethodNotFoundException.
	 * 
	 * @param message
	 *            An informative message explaining what happened and (possibly)
	 *            how to fix it.
	 */
	public MethodNotFoundException(String message) {
		super(message);
	}
}
