package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> AuthzException.java</p>
 * <p><b>Description:</b> Thrown when functional processing should discontinue.</p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class AuthzException
        extends ServerException {

    public AuthzException(String message) {
        super(null, message, null, null, null);
    }

    public AuthzException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}