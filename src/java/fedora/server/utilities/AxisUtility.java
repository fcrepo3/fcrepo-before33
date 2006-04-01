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
     * and http://www.w3.org/TR/soap12-part1/#faultcodeelement for SOAPv1.2
     * SOAP v1.2 here.
     */
    public static String SOAP_FAULT_CODE_NAMESPACE="http://www.w3.org/2003/05/soap-envelope";

    /**
     * Similar to above, this is "actor" in soap1_1 and "role"  in 1_2.
     * Soap 1.1 provides (see http://www.w3.org/TR/SOAP/#_Toc478383499) a special
     * URI for intermediaries, http://schemas.xmlsoap.org/soap/actor/next,
     * and leaves other URIs up to the application.  Soap 1.2 provides
     * (see http://www.w3.org/TR/soap12-part1/#soaproles) three special URIs --
     * one of which is for ultimate recievers, which is the category Fedora
     * falls into.  http://www.w3.org/2002/06/soap-envelope/role/ultimateReceiver
     * is the URI v1.2 provides.
     */
    public static String SOAP_ULTIMATE_RECEIVER="http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver";

    public static void throwFault(ServerException se)
            throws AxisFault {
    	AxisFault fault =  AxisFault.makeFault(se);
    	String[] details=se.getDetails();
    	
    	if (details.length > 0) {
	        StringBuffer buf = new StringBuffer();
	        for (int i = 0; i < details.length; i++) {
	            buf.append("<detail>");
	            buf.append(details[i]);
	            buf.append("</detail>\n");
	        }
	    	fault.setFaultDetailString(buf.toString());
    	}
        throw fault;
    }

    public static AxisFault getFault(ServerException se) {    	
    	AxisFault fault =  AxisFault.makeFault(se);
    	String[] details=se.getDetails();
    	
    	if (details.length > 0) {
	        StringBuffer buf = new StringBuffer();
	        for (int i = 0; i < details.length; i++) {
	            buf.append("<detail>");
	            buf.append(details[i]);
	            buf.append("</detail>\n");
	        }
	    	fault.setFaultDetailString(buf.toString());
    	}
    	return fault;
    }
    
    public static AxisFault getFault(AuthzException e) {
        AxisFault fault=AxisFault.makeFault(e);
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

    public static AxisFault getFault(Throwable e) {
        AxisFault fault = new AxisFault(
        				  	new QName(SOAP_FAULT_CODE_NAMESPACE, "Uncaught"),
        				  e.getClass().getName() + ":" + e.getMessage(), 
        				  SOAP_ULTIMATE_RECEIVER,
        				  null);
        return fault;
    }

}
