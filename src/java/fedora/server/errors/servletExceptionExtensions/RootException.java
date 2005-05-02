package fedora.server.errors.servletExceptionExtensions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import fedora.server.errors.authorization.AuthzDeniedException;
import fedora.server.errors.authorization.AuthzException;
import fedora.server.errors.authorization.AuthzOperationalException;
import fedora.server.errors.authorization.AuthzPermittedException;


/**
 *
 * <p><b>Title:</b> Exception100.java</p>
 * <p><b>Description:</b> Thrown to reach 100-Continue error page.  
 * Can be used when forwarding can't, e.g., after some http output has already been written.</p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public abstract class RootException
        extends ServletException {
	
	private final HttpServletRequest request;
	
	private final String action;
	
	private final String detail;
	
	private final String[] details;
	
	public final String getAction() {
		return action;
	}
	
	public final String getDetail() {
		return detail;
	}
	
	public final String[] getDetails() {
		return details;
	}
	
	public final HttpServletRequest getRequest() {
		return request;
	}
	
    public RootException(HttpServletRequest request, String action, String detail, String[] details) {
        super();
        this.action = action;
        this.detail = detail;
        this.details = details;
        this.request = request;
    }
    
    public RootException(String message, HttpServletRequest request, String action, String detail, String[] details) {
        super(message);
        this.request = request;        
        this.action = action;
        this.detail = detail;
        this.details = details;
    }

    public RootException(String message, Throwable cause, HttpServletRequest request, String action, String detail, String[] details) {
        super(message, cause);
        this.request = request;        
        this.action = action;
        this.detail = detail;
        this.details = details;
    }	

	public static final ServletException getServletException (AuthzException ae, HttpServletRequest request, String action, String[] details) {
		if (ae instanceof AuthzOperationalException) {
	        return new Forbidden403Exception(request, action, AuthzOperationalException.BRIEF_DESC, details);                					
		} else if (ae instanceof AuthzDeniedException) {
			return new Forbidden403Exception(request, action, AuthzDeniedException.BRIEF_DESC, details);
		} else if (ae instanceof AuthzPermittedException) {
			return new Continue100Exception(request, action, AuthzPermittedException.BRIEF_DESC, details);
		} else {
			//AuthzException has only the above three extensions, so code shouldn't reach here
			return new InternalError500Exception(request, action, "bug revealed in throwServletException(ae,...)", new String[0]);		
		}
	}
	/*
	public static final ServletException getServletException (ServletException se, HttpServletRequest request, String action, String detail, String[] details) {

		if (se instanceof Continue100Exception) {
	        return new Continue100Exception(request, action, detail, details);                					
		} else if (se instanceof Ok200Exception) {
			return new Ok200Exception(request, action, detail, details);
		} else if (se instanceof BadRequest400Exception) {
			return new BadRequest400Exception(request, action, detail, details);
		} else if (se instanceof Unauthorized401Exception) {
			return new Unauthorized401Exception(request, action, detail, details);
		} else if (se instanceof Forbidden403Exception) {
			return new Forbidden403Exception(request, action, detail, details);
		} else if (se instanceof InternalError500Exception) {
			return new InternalError500Exception(request, action, detail, details);
		} else if (se instanceof Unavailable503Exception) {
			return new Unavailable503Exception(request, action, detail, details);
		} else {
			//AuthzException has only the above sever extensions, so code shouldn't reach here
			return new InternalError500Exception(request, action, "bug revealed in throwServletException(se,...)", new String[0]);		
		}
	}
*/

}