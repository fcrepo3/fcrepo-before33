/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.errors;

/**
 *
 * <p><b>Title: </b>DisseminationException.java</p>
 * <p><b>Description: </b>Signals an error in processing a dissemination request.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class DisseminationException extends ServerException {
	
	private static final long serialVersionUID = 1L;
	
    /**
     * Creates a DisseminationException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public DisseminationException(String message) {
        super(null, message, null, null, null);
    }

    public DisseminationException(String bundleName, String code, String[] values,
        String[] details, Throwable cause) {
    super(bundleName, code, values, details, cause);
    }

}