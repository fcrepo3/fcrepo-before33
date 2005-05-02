package fedora.server.errors.authorization;


/**
 *
 * <p><b>Title:</b> AuthorizedPermittedException.java</p>
 * <p><b>Description:</b> Thrown when authorization is denied.</p>
 *
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class AuthzPermittedException
        extends AuthzException {
	
	public static final String BRIEF_DESC = "Authorized Permitted";

    public AuthzPermittedException(String message) {
        super(null, message, null, null, null);
    }

    public AuthzPermittedException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}