package fedora.server.security.servletfilters;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import java.security.Principal;

/** l21
 * 
 *  @author Bill Niebel (niebel@virginia.edu)
 */
public class TestServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private Log log = LogFactory.getLog(TestServlet.class);
	
	/*
	public final void debug(String st) {
		if ((Properties.LOG_LEVEL == null) || "".equals(Properties.LOG_LEVEL)) {
			log.debug(st);
		} else {
			String logDest = Properties.ERR;
			if ((Properties.LOG_DEST != null) 
			&&  (Properties.OUT.equals(Properties.LOG_DEST) || Properties.ERR.equals(Properties.LOG_DEST)) ) {
				logDest = Properties.LOG_DEST;
			}
			if (Properties.OUT.equals(logDest)) {
				System.out.print(st);
			}
			else if (Properties.ERR.equals(logDest)) {
				System.out.print(st);
			}
		}
	}
	*/
	
	protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
		System.err.println("SERVICE! debug==" + log.isDebugEnabled());
		log.debug(this.getClass().getName() + ".service() ");
		
		Principal userPrincipal = httpServletRequest.getUserPrincipal();
		System.err.println("got principal of " + userPrincipal);
		
		boolean authenticated = (userPrincipal != null);
		
		if (authenticated) {
		} else {
			String realm = "fedora";
			String value = "BASIC realm=\"" + realm + "\"";
			String name = "WWW-Authenticate";
			int sc = HttpServletResponse.SC_UNAUTHORIZED;
			System.err.println(name + " " + value + " " + sc);
			System.err.println("isCommitted()==" + httpServletResponse.isCommitted());
			try {
				httpServletResponse.reset();
			} catch (Throwable th) {
				System.err.println("test servlet caught exception on trying response.reset()");				
			}
			//httpServletResponse.sendError(sc, "supply credentials"); //same as after
			if (httpServletResponse.containsHeader(name)) {
				System.err.println("already contains header " + name + ", so setting");
				httpServletResponse.setHeader(name, value);				
			} else {
				System.err.println("initially does not contain header " + name + ", so adding");
				httpServletResponse.addHeader(name, value);
			}
			httpServletResponse.sendError(sc, "supply credentials"); //here, no bad auth msg at wget
			if (httpServletResponse.containsHeader(name)) {
				System.err.println("afterwards, contains header " + name);
			} else {
				System.err.println("afterwards, does not contain header " + name);
			}
			httpServletResponse.setContentType("text/plain");
			httpServletResponse.flushBuffer();
			System.err.println("isCommitted()==" + httpServletResponse.isCommitted());
			System.err.println("httpServletResponse == " + httpServletResponse.toString());
			System.err.println("servlet (this) == " + this.toString());					
		}

	}

}
