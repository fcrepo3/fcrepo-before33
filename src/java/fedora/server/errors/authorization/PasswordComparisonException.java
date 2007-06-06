package fedora.server.errors.authorization;

import fedora.server.errors.ServerException;


/**
 *
 * <p><b>Title:</b> AuthorizedPermittedException.java</p>
 * <p><b>Description:</b> Thrown when authorization is denied.</p>
 *
 *
 * @author wdn5e@virginia.edu
 * @version $Id: AuthzPermittedException.java,v 1.2 2006/10/25 00:49:00 eddie Exp $
 */
public class PasswordComparisonException
        extends ServerException {
	
	private static final long serialVersionUID = 1L;
	
	public static final String BRIEF_DESC = "Authorized Permitted";

    public PasswordComparisonException(String message) {
        super(null, message, null, null, null);
    }
    
    public PasswordComparisonException(String message, Throwable cause) {
        super(null, message, null, null, cause);
    }

    public PasswordComparisonException(String bundleName, String code,
            String[] replacements, String[] details, Throwable cause) {
        super(bundleName, code, replacements, details, cause);
    }

}