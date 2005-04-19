package fedora.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import org.apache.commons.httpclient.methods.GetMethod;

import fedora.common.HttpClient;
import fedora.common.Constants;
import fedora.server.errors.AuthzException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.NotAuthorizedException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.utilities.ServerUtility;
import fedora.server.Server;

/**
 *
 * <p><b>Title:</b> ServerController.java</p>
 * <p><b>Description:</b> </p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
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

    //private static final String USAGE = "ERROR: Need one argument: 'startup', 'shutdown', or 'status'";
    private static final String USAGE = "USAGE for ServerController.main(): startup|shutdown|status [http|https] [username] [passwd]";
    
    public static void main(String[] args) {
    	try {
            if (args.length < 1) {
            	throw new Exception(USAGE);
            }
            String action = args[0];
            if (! "startup".equals(action)
            &&  ! "shutdown".equals(action)
            &&  ! "status".equals(action)) {
            	throw new Exception(USAGE);
            }
            String protocol = args.length > 1 ? args[1] : "http";        
            if (! "http".equals(protocol) && ! "https".equals(protocol)) {
            	throw new Exception(USAGE);
            }
            String optionalUsername = null;
            String optionalPassword = null;            
            if (args.length > 2) {
            	if (args.length == 3) {
                	throw new Exception(USAGE);
            	}
            	optionalUsername = args[2];
            	optionalPassword = args[3];
            }
       		Properties serverProperties = ServerUtility.getServerProperties("http".equals(protocol), "https".equals(protocol));
       		System.err.println("SC:call HttpClient()...");
      		HttpClient client = new HttpClient(protocol, 
      				serverProperties.getProperty(ServerUtility.FEDORA_SERVER_HOST), 
      				serverProperties.getProperty( "http".equals(protocol) ? ServerUtility.FEDORA_SERVER_PORT : ServerUtility.FEDORA_REDIRECT_PORT),
      				"/fedora/management/control?action=" + action
      				);
       		System.err.println("...SC:call HttpClient()"); 
       		System.err.println("SC:call HttpClient.doAuthnGet()...");        		
      		GetMethod getMethod = client.doAuthnGet(20000, 25,
      			(optionalUsername == null) ? serverProperties.getProperty(ServerUtility.ADMIN_USER) : optionalUsername,
      			(optionalPassword == null) ? serverProperties.getProperty(ServerUtility.ADMIN_PASSWORD) : optionalPassword
      		);
       		System.err.println("...SC:call HttpClient.doAuthnGet()");		      		
       		System.err.println("SC:call HttpClient.getLineResponse()...");
      		String response = client.getLineResponseUrl();
            System.out.println(response);        	
    	} catch (Exception e) {
    	    System.err.println(e.getMessage());
            System.exit(1);
    	}
    }
    
    private static boolean log = false;
    
    private static final void slog(String msg) {
    	if (log) {
  	  	System.err.println(msg);	  		
    	}
    }
    
    
}
