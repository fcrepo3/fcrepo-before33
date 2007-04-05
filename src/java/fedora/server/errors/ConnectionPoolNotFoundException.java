/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.errors;

/**
 * <p>
 * <b>Title: </b>ConnectionPoolNotFoundException.java
 * </p>
 * <p>
 * <b>Description: </b>Signals a database ConnectionPool could not be found.
 * </p>
 * 
 * @author rlw@virginia.edu
 * @version $Id: ConnectionPoolNotFoundException.java,v 1.7 2005/04/21 12:59:22
 *          rlw Exp $
 */
public class ConnectionPoolNotFoundException extends StorageException {

	private static final long serialVersionUID = 1L;

	public ConnectionPoolNotFoundException(String message) {
		super(message);
	}

}
