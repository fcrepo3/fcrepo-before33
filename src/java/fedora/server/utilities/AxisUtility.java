package fedora.server.utilities;

import fedora.server.errors.ServerException;

import javax.xml.namespace.QName;
import org.apache.axis.AxisFault;

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
    
    public static void main(String args[]) {
        if (args.length>0) {
           if (args[0].equals("deploy")) {
               System.out.println("TODO");
           } else {
               System.out.println("Unrecognized command: " + args[0]);
           }
        }
    }

}