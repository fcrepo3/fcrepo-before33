package fedora.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import fedora.common.Constants;
import fedora.server.errors.AuthzException;
import fedora.server.errors.NotAuthorizedException;
import fedora.server.Server;

/**
 *
 * <p><b>Title:</b> ServerController.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ServerController
        extends HttpServlet {

    private static Server s_server;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action=request.getParameter("action");
        String requestInfo="Got controller '" + action + "' request from " + request.getRemoteAddr();
        if (fedora.server.Debug.DEBUG) System.out.println(requestInfo);
    	String lineResponse = null;
        if (action==null) {
            System.err.println("Error in controller request: action was not specified.");
            lineResponse = "ERROR";
        } else if (action.equals("startup")) {
            if (Server.hasInstance(new File(System.getProperty("fedora.home")))) {
                lineResponse = "ERROR";
            } else {
                try {
                    s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
                    lineResponse = "OK";
                } catch (Exception e) {
                    System.err.println("Error starting Fedora server: " + e.getClass().getName() + ": " + e.getMessage());
                    lineResponse = "ERROR";
                }
            }            
        } else if (action.equals("shutdown")) {
            if (Server.hasInstance(new File(System.getProperty("fedora.home")))) {
                try {
                    s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
                    s_server.logInfo(requestInfo);
                	Context context 
					= ReadOnlyContext.getContext(Constants.HTTP_REQUEST.REST.uri, request, false);
                    s_server.shutdown(context);
                    lineResponse = "OK";
        		} catch (NotAuthorizedException na) {
                    System.err.println("Authz Error 403 shutting down Fedora server: " + na.getClass().getName() + ": " + na.getMessage());        			
        			response.sendError(HttpServletResponse.SC_FORBIDDEN);                    
				} catch (AuthzException na) {
                    System.err.println("Authz Error 100 shutting down Fedora server: " + na.getClass().getName() + ": " + na.getMessage());        			
                    response.sendError(HttpServletResponse.SC_CONTINUE);					    			
                } catch (Throwable t) {
                    lineResponse = "ERROR";
                    System.err.println("Error shutting down Fedora server: " + t.getClass().getName() + ": " + t.getMessage());
                }
            }
        } else if (action.equals("status")) {
        	Context context 
			= ReadOnlyContext.getContext(Constants.HTTP_REQUEST.REST.uri, request, false);
        	File fedoraHome = new File(System.getProperty("fedora.home"));
            if (! Server.hasInstance(fedoraHome)) {
            	lineResponse = "STOPPED";
            } else {
            	Server server;
				try {
					server = Server.getInstance(fedoraHome, false);
					lineResponse = server.status(context);
				} catch (NotAuthorizedException na) {
                    System.err.println("Authz Error 403 getting Fedora server status: " + na.getClass().getName() + ": " + na.getMessage());        			
					response.sendError(HttpServletResponse.SC_FORBIDDEN);		
				} catch (AuthzException na) {
                    System.err.println("Authz Error 100 getting Fedora server status: " + na.getClass().getName() + ": " + na.getMessage());        			
                    response.sendError(HttpServletResponse.SC_CONTINUE);		
				} catch (Throwable t) {
                    System.err.println("other error: " + t.getMessage());        			
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);							
				}
            }
        } else {
            System.err.println("Error in controller request: action '" + action + "' was not recognized.");
            lineResponse = "ERROR";
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);            
        }
        if (lineResponse != null) {
            PrintWriter out = response.getWriter();
            response.setContentType("text/plain");        
            out.write(lineResponse);
        }
    }

    public void init() {
    }

    public void destroy() {
    }
    
}
