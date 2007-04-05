/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.bmech;

/**
 * 
 * <p>
 * <b>Title:</b> BMechBuilderException.java
 * </p>
 * <p>
 * <b>Description:</b>
 * </p>
 * 
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class BMechBuilderException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public BMechBuilderException(String message, Throwable cause) {
		super(message, cause);
	}

	public BMechBuilderException(String message) {
		super(message);
	}

}