package fedora.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
//import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

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
public class HttpClient {

    public static GetMethod doGetMethod(String url, String username, String password,
    		org.apache.commons.httpclient.HttpClient client, 
			int millisecondsWait, int redirectDepth) throws Exception {
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
    
    public static String getLineResponse(String protocolP, String hostP, String portP, 
    		String usernameP, String passwordP, String url) {
    	StringBuffer protocol = new StringBuffer(protocolP);
    	StringBuffer host = new StringBuffer(hostP);
    	StringBuffer port = new StringBuffer(portP);
    	StringBuffer username = new StringBuffer(usernameP);
    	StringBuffer password = new StringBuffer(passwordP);
        resolve(protocol, host, port, username, password); 
        url = makeUrl(protocol.toString(), host.toString(), port.toString(), url);
    	String textResponse = "";
    	GetMethod getMethod = null;
        try {
        	org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient(new MultiThreadedHttpConnectionManager());
        	getMethod = doGetMethod(url, username.toString(), password.toString(), httpClient, 20000, 25);
            if (getMethod.getStatusCode() != 200) {
            	textResponse = "ERROR: request failed, response code was " + getMethod.getStatusCode();
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream()));
                textResponse = in.readLine();
                if (textResponse == null) {
                	textResponse = "ERROR: response was empty.";
                }            	
                in.close();
            }
        } catch (Exception e) {
        	textResponse =  "ERROR: can't connect";
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
 

    public static void resolve(StringBuffer protocol, StringBuffer host, StringBuffer port, 
    		StringBuffer username, StringBuffer password) {
        String fedoraHome=System.getProperty("fedora.home");
        if (fedoraHome==null) {
            System.err.println("ERROR: fedora.home system property not set.");
            System.exit(1);
        }
        if (protocol == null) {
    		protocol = new StringBuffer();
        }
        if (protocol.length() == 0) {
        	protocol.append("https");        	
        }
        if (host == null) {
        	host = new StringBuffer();
        }
        if (host.length() == 0) {
        	host.append("localhost");        	
        }
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
                	port = new StringBuffer();
                }
            	if (port.length() == 0) {
            		if ("http".equals(protocol)) {
                		if (nameNode.getNodeValue().equals("fedoraServerPort")) {
                    		port.append(valueNode.getNodeValue());
                    	} else {
                    		port.append("8080");
                    	}            			
            		} else if ("https".equals(protocol)) {
                		if (nameNode.getNodeValue().equals("fedoraRedirectPort")) {
                    		port.append(valueNode.getNodeValue());
                    	} else {
                    		port.append("8443");
                    	}  
            		}
                }
                if (username == null) {
                	username = new StringBuffer();
                }            	
            	if (username.length() == 0) {
            		if (nameNode.getNodeValue().equals("adminUser")) {
            			username.append(valueNode.getNodeValue());
            		} else {
            			username.append("fedoraAdmin");
            		}
            	}
                if (password == null) {
                	password = new StringBuffer();
                }            	
            	if (password.length() == 0) {
            		if (nameNode.getNodeValue().equals("adminPassword")) {
            			password.append(valueNode.getNodeValue());
            		} else {
            			password.append("fedoraAdmin");
            		}
            	}
            }
        } catch (Exception e) {
            System.err.println("ERROR: Cannot determine server port: " + e.getMessage());
        }
    }

    public static String makeUrl(String protocol, String host, String port, String more) {
    	String url = protocol + "://" + host 
		+ (((port != null) && ! "".equals(port)) ? (":" + port) : "") 
		+ "/fedora" + more;
    	return url;
    }
    
}
