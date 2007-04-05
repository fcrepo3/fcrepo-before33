/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ValidationException.java</p>
 * <p><b>Description:</b> Signals an error while validating.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class ValidationException
        extends ObjectIntegrityException {
	private static final long serialVersionUID = 1L;

    public ValidationException(String bundleName, String code, String[] values,
            String[] details, Throwable cause) {
        super(bundleName, code, values, details, cause);
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}