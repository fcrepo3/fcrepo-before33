/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.errors.authorization;


/**
 *
 * <p><b>Title:</b> NotAuthorizedException.java</p>
 * <p><b>Description:</b> Thrown when authorization is denied.</p>
 *
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class AuthzDeniedException
        extends AuthzException {
	private static final long serialVersionUID = 1L;
	
	public static final String BRIEF_DESC = "Authorized Denied";

    public AuthzDeniedException(String message) {
        super(null, message, null, null, null);
    }

    public AuthzDeniedException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}