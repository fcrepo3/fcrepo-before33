/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.errors.authorization;


/**
 *
 * <p><b>Title:</b> AuthzOperationalException.java</p>
 * <p><b>Description:</b> Thrown when authorization cannot be completed.</p>
 *
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class AuthzOperationalException
        extends AuthzDeniedException {
	private static final long serialVersionUID = 1L;
	
	public static final String BRIEF_DESC = "Authorized Failed";

    public AuthzOperationalException(String message) {
        super(null, message, null, null, null);
    }
    
    public AuthzOperationalException(String message, Throwable cause) {
        super(null, message, null, null, cause);
    }

    public AuthzOperationalException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}