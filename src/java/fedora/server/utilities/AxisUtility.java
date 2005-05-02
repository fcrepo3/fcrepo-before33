package fedora.server.utilities;

import java.io.File;
import java.util.Properties;
import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;
import org.apache.axis.client.AdminClient;
import fedora.server.errors.ServerException;
import fedora.server.errors.authorization.AuthzDeniedException;
import fedora.server.errors.authorization.AuthzException;
import fedora.server.errors.authorization.AuthzOperationalException;
import fedora.server.errors.authorization.AuthzPermittedException;

/**
 *
 * <p><b>Title:</b> AxisUtility.java</p>
 * <p><b>Description:</b> </p>
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
    
    public static AxisFault getFault(AuthzException e) {
        AxisFault fault=new AxisFault(new QName(SOAP_FAULT_CODE_NAMESPACE, e.getCode()), 
				e.getMessage(), SOAP_ULTIMATE_RECEIVER,
                null);
    	String reason = "";
    	if (e instanceof AuthzOperationalException) {
    		reason = AuthzOperationalException.BRIEF_DESC;
    	} else if (e instanceof AuthzDeniedException) {
    		reason = AuthzDeniedException.BRIEF_DESC;
    	} else if (e instanceof AuthzPermittedException) {
    		reason = AuthzPermittedException.BRIEF_DESC;   		
    	}
    	fault.addFaultDetail(new QName("Authz"), reason);
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
        System.out.println("    AxisUtility deploy wsdd_file timeout_seconds [finished_url] [username] [passwd]");
    }
    public static void main(String args[]) {
        if (args.length>0) {
           if (args[0].equals("deploy")) {
               if (args.length < 3) {
                   showDeployUsage();
               } else {
                   File wsddFile=new File(args[1]);
                   if (!wsddFile.exists()) {
                       System.out.println("Error: wsdd_file " + args[1] + " does not exist.");
                       showDeployUsage();
                   } else {
                       try {
                       		Properties serverProperties = ServerUtility.getServerProperties();
                       	   StringBuffer adminUrl=new StringBuffer("http://localhost:" 
                       	   		+ serverProperties.getProperty(ServerUtility.FEDORA_SERVER_PORT) 
								+ "/fedora/AdminService");
                           String[] parms=new String[] {"-l" + adminUrl, wsddFile.toString(), 
                               		"-u" + serverProperties.getProperty(ServerUtility.ADMIN_USERNAME_KEY), 
									"-w" + serverProperties.getProperty(ServerUtility.ADMIN_PASSWORD_KEY)}; 
                           //http://ws.apache.org/axis/java/install.html#RunTheAdminClient
                           int timeoutSeconds=Integer.parseInt(args[2]);
                           if (ServerUtility.pingServletContainerStartup("/", timeoutSeconds)) {
                               AdminClient.main(parms);
                               for (int i=0; i<args.length; i++) {
                               	System.err.println("audit parms " + args[i]);
                               }
                               if ((3 < args.length) && (args[3] != null) && ! "".equals(args[3])) {
                               		ServerUtility.pingServletContainerRunning(args[3], 2);
                               }
                           } else {
                               System.out.println("Giving up deployment... no response from server after " + timeoutSeconds + " seconds.");
                               System.exit(1);
                           }
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