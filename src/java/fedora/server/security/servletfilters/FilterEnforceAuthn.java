package fedora.server.security.servletfilters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;
import java.util.Hashtable;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.security.Principal;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.lang.NoSuchMethodException;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;


/** 
 *  @author Bill Niebel (niebel@virginia.edu)
 */
public class FilterEnforceAuthn extends FilterSetup {
    protected static Log log = LogFactory.getLog(FilterEnforceAuthn.class);

    /*
    protected String getClassName() {
    	return this.getClass().getName();
    }
    */

	public void doThisSubclass(ExtendedHttpServletRequest request, HttpServletResponse response) throws Throwable {
		String method = "doThisSubclass() "; if (log.isDebugEnabled()) log.debug(enter(method));
		super.doThisSubclass(request, response);
		request.lockWrapper();
				
		if (request.getUserPrincipal() == null) {
			if (log.isDebugEnabled()) log.debug(format(method, "no principal found, sending 401"));
			String realm = "fedora";
			String value = "BASIC realm=\"" + realm + "\"";
			String name = "WWW-Authenticate";
			int sc = HttpServletResponse.SC_UNAUTHORIZED;
			response.reset();
			//httpServletResponse.sendError(sc, "supply credentials"); //same as after
			if (response.containsHeader(name)) {
				response.setHeader(name, value);				
			} else {
				response.addHeader(name, value);
			}
			try {
				response.sendError(sc, "supply credentials");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} //here, no bad auth msg at wget
			response.setContentType("text/plain");
			try {
				response.flushBuffer();
			} catch (IOException e) {
				showThrowable(e, log, "response flush error");
			}
		}
		
	}

	public void destroy() {
		String method = "destroy()"; if (log.isDebugEnabled()) log.debug(enter(method));
		super.destroy();
		if (log.isDebugEnabled()) log.debug(exit(method));
    }



}
