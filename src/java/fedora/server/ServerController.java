package fedora.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import fedora.common.HttpClient;
import org.apache.commons.httpclient.Header;
//import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import fedora.common.Constants;
import fedora.server.errors.HttpServiceNotFoundException;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.NotAuthorizedException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.errors.StreamIOException;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.Property;
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
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
    	String lineResponse = "UNKNOWN";
        if (action==null) {
            System.err.println("Error in controller request: action was not specified.");
            lineResponse = "ERROR";
            //fixup for xacml
        } else if (action.equals("startup")) {
            //the trouble with startup -- fixup for xacml
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
        	System.err.println("shutdown 1");
            if (Server.hasInstance(new File(System.getProperty("fedora.home")))) {
            	System.err.println("shutdown 2");            	
                try {
                	System.err.println("shutdown 3");                	
                    s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
                	System.err.println("shutdown 4");                    
                    s_server.logInfo(requestInfo);
                	System.err.println("shutdown 5");                    
                	Context context 
					= ReadOnlyContext.getContext(Constants.HTTP_REQUEST.REST.uri, request, false);
                	System.err.println("shutdown 6");                	
                    s_server.shutdown(context);
                	System.err.println("shutdown 7");                    
                    lineResponse = "OK";
                	System.err.println("shutdown 8");                    
                } catch (Throwable t) {
                	System.err.println("shutdown 9");                	
                    lineResponse = "ERROR";
                	System.err.println("shutdown 10");                    
                    System.err.println("Error shutting down Fedora server: " + t.getClass().getName() + ": " + t.getMessage());
                }
            	System.err.println("shutdown 11");
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
				} catch (ServerInitializationException e) {
					//we can do no more
				} catch (ModuleInitializationException e) {
					//since 2nd parm above is "false", this is unexpected
				} catch (NotAuthorizedException e) {
					//don't tell
				}
            }
        } else {
            System.err.println("Error in controller request: action '" + action + "' was not recognized.");
            lineResponse = "ERROR";
        }
        out.write(lineResponse);
    }

    public void init() {
    }

    public void destroy() {
    }

    /*
    public static GetMethod doGetMethod(String url, String username, String password,
    		HttpClient client, int millisecondsWait, int redirectDepth) throws Exception {
    	System.err.println("doing get for " + url + "for " + username + " (" + password );
	  	GetMethod get = null;
	  	try {
	  		client.setConnectionTimeout(millisecondsWait);
	  		client.getState().setCredentials(null, null, new UsernamePasswordCredentials(username, password));
	  		client.getState().setAuthenticationPreemptive(true);
	  		System.err.println("in getExternalContent(), after setup");
	  		int resultCode = -1;
	  		for (int loops = 0; (url != null) && (loops < redirectDepth); loops++) {
	  			System.err.println("in getExternalContent(), new loop, url=" + url);
	  			get = new GetMethod(url);
	  			url = null;
	  			System.err.println("in getExternalContent(), got GetMethod object=" + get);
	  			get.setDoAuthentication(true);
	  			get.setFollowRedirects(true);
	  			resultCode=client.executeMethod(get);
	  			if (300 <= resultCode && resultCode <= 399) {
	  				url=get.getResponseHeader("Location").getValue();
	  				System.err.println("in getExternalContent(), got redirect, new url=" + url);
	  			}
	  		}
	  	} catch (Throwable th) {
	  		if (get != null) {
	  			get.releaseConnection();
	  		}
	  		System.err.println("x " + th.getMessage());
	  		if (th.getCause() != null) {
		  		System.err.println("x " + th.getCause().getMessage());	  			
	  		}
	  		throw new Exception("failed connection");
	    }
	  	return get;
    }
    
    public static String getLineResponse(String url, String username, String password) {
    	String textResponse = "";
    	GetMethod getMethod = null;
        try {
        	HttpClient httpClient = new HttpClient(new MultiThreadedHttpConnectionManager());
        	getMethod = doGetMethod(url, username, password, httpClient, 20000, 25);
            if (getMethod.getStatusCode() != 200) {
            	textResponse = "ERROR: Request to control servlet failed, response code was " + getMethod.getStatusCode();
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream()));
                textResponse = in.readLine();
                if (textResponse == null) {
                	textResponse = "ERROR: control servlet response was empty.";
                }            	
            }
        } catch (Exception e) {
        	textResponse =  "ERROR: can't connect to control servlet.";
	  		System.err.println("y " + e.getMessage());
	  		if (e.getCause() != null) {
		  		System.err.println("y " + e.getCause().getMessage());	  			
	  		}
        } finally {
  			if (getMethod != null) {
  				getMethod.releaseConnection();
  			}
        }
        return textResponse;
    }
 */

    public static void main(String[] args) {
    	/*
        String fedoraHome=System.getProperty("fedora.home");
        if (fedoraHome==null) {
            System.err.println("ERROR: fedora.home system property not set.");
            System.exit(1);
        }
        */
        if (args.length < 1) {
            System.err.println("ERROR: Need one argument: 'startup', 'shutdown', or 'status'");
            System.exit(1);
        }
        String action = args[0];
        /*        
        String protocol = args.length > 1 ? args[1] : "http"; //fixup for xacml
        String host = args.length > 2 ? args[2] : "localhost";
        String port = args.length > 3 ? args[3] : null;        
        String username = args.length > 4 ? args[4] : null;        
        String password = args.length > 5 ? args[5] : null; //fixup for xacml        
        try {
            File fedoraHomeDir=new File(fedoraHome);
            File fcfgFile=new File(fedoraHomeDir, "server/config/fedora.fcfg");
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder=factory.newDocumentBuilder();
            Element rootElement=builder.parse(fcfgFile).getDocumentElement();
            NodeList params=rootElement.getElementsByTagName("param");
            for (int i=0; i<params.getLength(); i++) {
                Node nameNode=params.item(i).getAttributes().getNamedItem("name");
                Node valueNode=params.item(i).getAttributes().getNamedItem("value");
            	if (port == null) {
            		if ("http".equals(protocol)) {
                		if (nameNode.getNodeValue().equals("fedoraServerPort")) {
                    		port = valueNode.getNodeValue();
                    	} else {
                    		port = "8080";
                    	}            			
            		} else if ("https".equals(protocol)) {
                		if (nameNode.getNodeValue().equals("fedoraRedirectPort")) {
                    		port = valueNode.getNodeValue();
                    	} else {
                    		port = "8443";
                    	}  
            		}
                }
            	if (username == null) {
            		if (nameNode.getNodeValue().equals("adminUser")) {
            			username = valueNode.getNodeValue();
            		} else {
            			username = "fedoraAdmin";
            		}
            	}
            	if (password == null) {
            		if (nameNode.getNodeValue().equals("adminPassword")) {
            			password = valueNode.getNodeValue();
            		} else {
            			password = "fedoraAdmin";
            		}
            	}
            }
        } catch (Exception e) {
            System.err.println("ERROR: Cannot determine server port: " + e.getMessage());
        }
        */
        if (! "startup".equals(action)
        &&  ! "shutdown".equals(action)
        &&  ! "status".equals(action)) {
            System.err.println("ERROR: Argument must be: 'startup', 'shutdown', or 'status'");        	
        } else {
            //fixup for xacml
        	/*
            String url = protocol + "://" + host 
			+ (((port != null) && ! "".equals(port)) ? (":" + port) : "") 
			+ "/fedora/management/control?action=" + action;
            String response = getLineResponse(url, username, password);
            */
        	String response = HttpClient.getLineResponse(
        			args.length > 1 ? args[1] : null,
          			args.length > 2 ? args[2] : null,
          			args.length > 3 ? args[3] : null,
                    args.length > 4 ? args[4] : null,
                    args.length > 5 ? args[5] : null,
                  	"/management/control?action=" + action);        	
            System.out.println(response);        	
        }
    }
}
