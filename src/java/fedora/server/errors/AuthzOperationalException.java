package fedora.server.errors;

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
        extends NotAuthorizedException {

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