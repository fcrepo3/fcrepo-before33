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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import fedora.server.errors.InitializationException;
import fedora.server.errors.ShutdownException;
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version 1.0
 */
public class ServerController
        extends HttpServlet {

    private static Server s_server;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action=request.getParameter("action");
        String requestInfo="Got controller '" + action + "' request from " + request.getRemoteAddr();
        System.out.println(requestInfo);
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        if (action==null) {
            System.err.println("Error in controller request: action was not specified.");
            out.write("ERROR");
        } else if (action.equals("shutdown")) {
            if (Server.hasInstance(new File(System.getProperty("fedora.home")))) {
                try {
                    s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
                    s_server.logInfo(requestInfo);
                    if (request.getParameter("password")!=null
                            && request.getParameter("password").equals(
                            s_server.getParameter("adminPassword"))) {
                        try {
                            s_server.shutdown();
                        } catch (Exception e) {
                            System.err.println("Error shutting down Fedora server: " + e.getClass().getName() + ": " + e.getMessage());
                        }
                        out.write("OK");
                    } else {
                        s_server.logWarning("Incorrect password used in shutdown request: " + request.getParameter("password"));
                        out.write("ERROR");
                    }
                } catch (InitializationException ie) {
                    System.err.println("Error shutting down Fedora server: " + ie.getClass().getName() + ": " + ie.getMessage());
                    out.write("ERROR");
                }
            } else {
                out.write("OK");
            }
        } else if (action.equals("startup")) {
            if (Server.hasInstance(new File(System.getProperty("fedora.home")))) {
                out.write("OK");
            } else {
                try {
                    s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
                    out.write("OK");
                } catch (Exception e) {
                    System.err.println("Error starting Fedora server: " + e.getClass().getName() + ": " + e.getMessage());
                    out.write("ERROR");
                }
            }
        } else if (action.equals("status")) {
            if (Server.hasInstance(new File(System.getProperty("fedora.home")))) {
                out.write("RUNNING");
            } else {
                out.write("STOPPED");
            }
        } else {
            System.err.println("Error in controller request: action '" + action + "' was not recognized.");
            out.write("ERROR");
        }
    }

    public void init() {
    }

    public void destroy() {
    }

    public static String getResponse(URL url) {
        try {
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();
            if (conn.getResponseCode()!=200) {
                return "ERROR: Request to control servlet failed, response code was " + conn.getResponseCode();
            }
            BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String firstLine=in.readLine();
            if (firstLine==null) {
                return "ERROR: control servlet response was empty.";
            }
            return firstLine;
        } catch (Exception e) {
            return "ERROR: can't connect to control servlet.";
        }
    }

    public static void main(String[] args) {
        String fedoraHome=System.getProperty("fedora.home");
        if (fedoraHome==null) {
            System.err.println("ERROR: fedora.home system property not set.");
            System.exit(1);
        }
        if (args.length!=1) {
            System.err.println("ERROR: Need one argument: 'startup', 'shutdown', or 'status'");
            System.exit(1);
        }
        String action=args[0];
        URL url=null;
        try {
            File fedoraHomeDir=new File(fedoraHome);
            File fcfgFile=new File(fedoraHomeDir, "server/config/fedora.fcfg");
            String port="8080";
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder=factory.newDocumentBuilder();
            Element rootElement=builder.parse(fcfgFile).getDocumentElement();
            NodeList params=rootElement.getElementsByTagName("param");
            String password=null;
            for (int i=0; i<params.getLength(); i++) {
                Node nameNode=params.item(i).getAttributes().getNamedItem("name");
                Node valueNode=params.item(i).getAttributes().getNamedItem("value");
                if (nameNode.getNodeValue().equals("fedoraServerPort")) {
                    port=valueNode.getNodeValue();
                }
                if (nameNode.getNodeValue().equals("adminPassword")) {
                    password=valueNode.getNodeValue();
                }
            }
            String passString="";
            if (action.equals("shutdown")) {
                passString="&password=" + password;
            }
            url=new URL("http://localhost:" + port + "/fedora/management/control?action=" + action + passString);
        } catch (Exception e) {
            System.err.println("ERROR: Cannot determine server port: " + e.getMessage());
        }
        if (action.equals("startup")) {
            String response=getResponse(url);
            System.out.println(response);
        } else if (action.equals("shutdown")) {
            String response=getResponse(url);
            System.out.println(response);
        } else if (action.equals("status")) {
            String response=getResponse(url);
            System.out.println(response);
        } else {
            System.err.println("ERROR: Argument must be: 'startup', 'shutdown', or 'status'");
        }
    }
}