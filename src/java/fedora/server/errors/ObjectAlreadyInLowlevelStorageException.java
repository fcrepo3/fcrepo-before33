/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ObjectAlreadyInLowLevelStorageException.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class ObjectAlreadyInLowlevelStorageException extends LowlevelStorageException {
	private static final long serialVersionUID = 1L;
	
	public ObjectAlreadyInLowlevelStorageException(String message, Throwable cause) {
		super(false, message, cause);
	}
	public ObjectAlreadyInLowlevelStorageException(String message) {
		this(message, null);
	}
}
