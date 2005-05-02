package fedora.server.errors.servletExceptionExtensions;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * <p><b>Title:</b> Exception401.java</p>
 * <p><b>Description:</b> Thrown to reach 401-Unauthorized error page.  
 * Can be used when forwarding can't, e.g., after some http output has already been written.</p>
 *
 * @author wdn5e@virginia.edu
 * @version $Id$
 */
public class Unauthorized401Exception
        extends RootException {

    public Unauthorized401Exception(HttpServletRequest request, String action, String detail, String[] details) {
        super(request, action, detail, details);
    }
    
    public Unauthorized401Exception(String message, HttpServletRequest request, String action, String detail, String[] details) {
        super(message, request, action, detail, details);
    }

    public Unauthorized401Exception(String message, Throwable cause, HttpServletRequest request, String action, String detail, String[] details) {
        super(message, cause, request, action, detail, details);
    }

}