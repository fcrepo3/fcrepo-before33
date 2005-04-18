package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> NotAuthorizedException.java</p>
 * <p><b>Description:</b> Thrown when authorization is denied.</p>
 *
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class NotAuthorizedException
        extends AuthzException {

    public NotAuthorizedException(String message) {
        super(null, message, null, null, null);
    }

    public NotAuthorizedException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}