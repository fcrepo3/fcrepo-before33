/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> InvalidXMLNameException.java</p>
 * <p><b>Description:</b> Thrown when a string does not meet the criteria for an XML name.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class InvalidXMLNameException
        extends ServerException {

	private static final long serialVersionUID = 1L;
	
    /**
     * Creates an InvalidStateException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public InvalidXMLNameException(String message) {
        super(null, message, null, null, null);
    }

    public InvalidXMLNameException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}