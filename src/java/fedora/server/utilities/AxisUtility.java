package fedora.server.utilities;

import java.io.IOException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Date;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.axis.AxisFault;
import org.apache.axis.client.AdminClient;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import fedora.server.errors.ServerException;

/**
 *
 * <p><b>Title:</b> AxisUtility.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public abstract class AxisUtility {

    /**
     * The (SOAP[version-specific] spec-dictated) namespace for fault codes.
     * See http://www.w3.org/TR/SOAP/#_Toc478383510 for SOAPv1.1
     * (what Axis currently conforms to) and
     * http://www.w3.org/TR/soap12-part1/#faultcodeelement for SOAPv1.2
     * SOAP v1.1 here.
     */
    public static String SOAP_FAULT_CODE_NAMESPACE="http://schemas.xmlsoap.org/soap/envelope/";

    /**
     * Similar to above, this is "actor" in soap1_1 and "role"  in 1_2.
     * Soap 1.1 provides (see http://www.w3.org/TR/SOAP/#_Toc478383499) a special
     * URI for intermediaries, http://schemas.xmlsoap.org/soap/actor/next,
     * and leaves other URIs up to the application.  Soap 1.2 provides
     * (see http://www.w3.org/TR/soap12-part1/#soaproles) three special URIs --
     * one of which is for ultimate recievers, which is the category Fedora
     * falls into.  http://www.w3.org/2002/06/soap-envelope/role/ultimateReceiver
     * is the URI v1.2 provides.  Since we're doing soap1.1 with axis, we
     * interpolate and use http://schemas.xmlsoap.org/soap/actor/ultimateReceiver.
     */
    public static String SOAP_ULTIMATE_RECEIVER="http://schemas.xmlsoap.org/soap/actor/ultimateReceiver";

    public static void throwFault(ServerException se)
            throws AxisFault {
        String[] details=se.getDetails();
        StringBuffer buf=new StringBuffer();
        for (int i=0; i<details.length; i++) {
            buf.append("<detail>");
            buf.append(details[i]);
            buf.append("</detail>\n");
        }
        AxisFault fault=new AxisFault(new QName(SOAP_FAULT_CODE_NAMESPACE,
                se.getCode()), se.getMessage(), SOAP_ULTIMATE_RECEIVER,
                null);
        fault.setFaultDetailString(buf.toString());
        throw fault;
    }

    public static AxisFault getFault(ServerException se) {
        String[] details=se.getDetails();
        StringBuffer buf=new StringBuffer();
        for (int i=0; i<details.length; i++) {
            buf.append("<detail>");
            buf.append(details[i]);
            buf.append("</detail>\n");
        }
        AxisFault fault=new AxisFault(new QName(SOAP_FAULT_CODE_NAMESPACE,
                se.getCode()), se.getMessage(), SOAP_ULTIMATE_RECEIVER,
                null);
        fault.setFaultDetailString(buf.toString());
        return fault;
    }

    public static AxisFault getFault(Exception e) {
        AxisFault fault=new AxisFault(new QName(SOAP_FAULT_CODE_NAMESPACE,
                "Uncaught"),
                e.getClass().getName() + ":" + e.getMessage(), SOAP_ULTIMATE_RECEIVER,
                null);
        return fault;
    }

    public static void showDeployUsage() {
        System.out.println("Usage:");
        System.out.println("    AxisUtility deploy wsdd_file timeout_seconds [finished_url]");
    }

    public static boolean serverActive(URL url, int timeoutSeconds) {
        long startms=new Date().getTime();
        long timeoutms=startms+(1000*timeoutSeconds);
        long endms=0;
        while (endms<timeoutms) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                return false;
            }
            try {
                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                if (conn.getResponseCode()==200) {
                    endms=new Date().getTime();
                    long total=endms-startms;
                    return true;
                }
            } catch (IOException ioe) {
                System.out.println("Waiting for server to start...");
            } catch (ClassCastException cce) {
            }
            endms=new Date().getTime();
        }
        long total=endms-startms;
        return false;
    }

    public static void main(String args[]) {
        if (args.length>0) {
           if (args[0].equals("deploy")) {
               if ((args.length!=3) && (args.length!=4)) {
                   showDeployUsage();
               } else {
                   File wsddFile=new File(args[1]);
                   if (!wsddFile.exists()) {
                       System.out.println("Error: wsdd_file " + args[1] + " does not exist.");
                       showDeployUsage();
                   } else {
                       try {
                           // figure out port from fedora.fcfg... and use it here
                           String fedoraHome=System.getProperty("fedora.home");
                           if ((fedoraHome==null) || (fedoraHome.equals(""))) {
                               throw new IOException("fedora.home system property is not set, but it's required.");
                           }
                           File fedoraHomeDir=new File(fedoraHome);
                           File fcfgFile=new File(fedoraHomeDir, "server/config/fedora.fcfg");
                           String port="8080";
                           DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
                           factory.setNamespaceAware(true);
                           DocumentBuilder builder=factory.newDocumentBuilder();
                           Element rootElement=builder.parse(fcfgFile).getDocumentElement();
                           NodeList params=rootElement.getElementsByTagName("param");
                           for (int i=0; i<params.getLength(); i++) {
                               Node nameNode=params.item(i).getAttributes().getNamedItem("name");
                               Node valueNode=params.item(i).getAttributes().getNamedItem("value");
                               if (nameNode.getNodeValue().equals("fedoraServerPort")) {
                                   port=valueNode.getNodeValue();
                               }
                           }

                           StringBuffer url=new StringBuffer("http://localhost:" + port + "/fedora/AdminService");
                           URL adminUrl=new URL(url.toString());
                           URL mainUrl=new URL("http://localhost:" + port + "/");
                           String[] parms=new String[] {"-l" + adminUrl, wsddFile.toString()};
                           int timeoutSeconds=Integer.parseInt(args[2]);
                           if (serverActive(mainUrl, timeoutSeconds)) {
                               AdminClient.main(parms);
                               if (args.length==4) {
                                   try {
                                       serverActive(new URL(args[3]), 2);
                                   } catch (MalformedURLException murle) {
                                       System.out.println("finished_url " + args[3] + " was malformed.");
                                       System.exit(1);
                                   }
                               }
                           } else {
                               System.out.println("Giving up deployment... no response from server after " + timeoutSeconds + " seconds.");
                               System.exit(1);
                           }
                       } catch (MalformedURLException murle) {
                           System.out.println("Error: malformed url.");
                           showDeployUsage();
                       } catch (NumberFormatException nfe) {
                           System.out.println("Error: timeout_seconds " + args[2] + " is not an integer.");
                           showDeployUsage();
                       } catch (Exception e) {
                           System.out.println("Error trying to read <FEDORA_HOME>/server/config/fedora.fcfg: " + e.getClass().getName() + ": " + e.getMessage());
                           showDeployUsage();
                       }
                   }
               }
           } else {
               System.out.println("Error: Unrecognized command: " + args[0]);
               System.out.println("The only valid command is deploy.");
           }
        } else {
           showDeployUsage();
        }
    }

}