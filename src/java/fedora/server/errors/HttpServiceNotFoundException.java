/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.errors;

/**
 * <p>
 * <b>Title: </b>HttpServiceNotFoundException.java
 * </p>
 * <p>
 * <b>Description: </b>Signals that a successful HTTP connection could NOT
 * </p>
 * <p>
 * be made to the designated URL.
 * </p>
 * 
 * @author rlw@virginia.edu
 * @version $Id: HttpServiceNotFoundException.java,v 1.8 2005/04/21 12:59:22 rlw
 *          Exp $
 */
public class HttpServiceNotFoundException extends StorageException {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a HttpServiceNotFoundException.
	 * 
	 * @param message
	 *            An informative message explaining what happened and (possibly)
	 *            how to fix it.
	 */
	public HttpServiceNotFoundException(String message) {
		super(message);
	}
}