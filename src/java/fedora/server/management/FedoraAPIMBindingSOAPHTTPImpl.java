/**
 * FedoraAPIMBindingSOAPHTTPImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */


package fedora.server.management;

import fedora.server.Server;
import fedora.server.errors.InitializationException;

import java.io.File;
import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;

public class FedoraAPIMBindingSOAPHTTPImpl implements fedora.server.management.FedoraAPIM{

    private static Server s_server;

    static {
        try {
            s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
        } catch (InitializationException ie) {
            System.err.println(ie.getMessage());
        }
    }

    /**
     * The (SOAP[version-specific] spec-dictated) namespace for fault codes.
     * See http://www.w3.org/TR/SOAP/#_Toc478383510 for SOAPv1.1 (what Axis currently
     * conforms to) and http://www.w3.org/TR/soap12-part1/#faultcodeelement for SOAPv1.2
     * This should go in some utility class for the APIs to share. SOAP v1.1 here.
     */
    public static String SOAP_FAULT_CODE_NAMESPACE="http://schemas.xmlsoap.org/soap/envelope/";
    
    /**
     * Similar to above.  This is "actor" in soap1.1 and "role"  in 1.2.
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
    
    public java.lang.String createObject() throws java.rmi.RemoteException {
        if (1==2) {
            AxisFault fault=new AxisFault(new QName(SOAP_FAULT_CODE_NAMESPACE, 
                    "Server.api.methodNotImplemented"), 
                    "The createObject() method hasn't been implemented yet.",
                    SOAP_ULTIMATE_RECEIVER, null);
            fault.setFaultDetailString("No further information is available.");
            throw fault;
        }
        return "This would be a PID if this operation implementation wasn't a stub.  BTW, the scope of this service (as defined by the scope property in the wsdd file) is '" 
                + AxisEngine.getCurrentMessageContext().getStrProp("scope") + "'. Also, fedora.home=" + System.getProperty("fedora.home");
        //return null;
    }

    public java.lang.String ingestObject(byte[] METSXML) throws java.rmi.RemoteException {
        return null;
    }

    public byte[] getObjectXML(java.lang.String PID) throws java.rmi.RemoteException {
        return null;
    }

    public byte[] exportObject(java.lang.String PID) throws java.rmi.RemoteException {
        return null;
    }

    public void withdrawObject(java.lang.String PID) throws java.rmi.RemoteException {
    }

    public void deleteObject(java.lang.String PID) throws java.rmi.RemoteException {
    }

    public void purgeObject(java.lang.String PID) throws java.rmi.RemoteException {
    }

    public void obtainLock(java.lang.String PID) throws java.rmi.RemoteException {
    }

    public void releaseLock(java.lang.String PID, java.lang.String logMessage, boolean commit) throws java.rmi.RemoteException {
    }

    public java.lang.String getLockingUser(java.lang.String PID) throws java.rmi.RemoteException {
        StringBuffer pNames=new StringBuffer();
        pNames.append("Server parameter names: ");
        Iterator iter=s_server.parameterNames();
        boolean first=true;
        while (iter.hasNext()) {
            if (!first) {
                pNames.append(',');
            }
            pNames.append((String) iter.next());
            first=false;
        }
        return pNames.toString();
    }

    public java.lang.String getObjectState(java.lang.String PID) throws java.rmi.RemoteException {
        return null;
    }

    public java.util.Calendar getObjectCreateDate(java.lang.String PID) throws java.rmi.RemoteException {
        return null;
    }

    public java.util.Calendar getObjectLastModDate(java.lang.String PID) throws java.rmi.RemoteException {
        return null;
    }

    public fedora.server.types.gen.AuditRecord[] getObjectAuditTrail(java.lang.String PID) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String[] listObjectPIDs(java.lang.String state) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String addDatastreamExternal(java.lang.String PID, java.lang.String dsLabel, java.lang.String dsLocation) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String addDatastreamManagedContent(java.lang.String PID, java.lang.String dsLabel, java.lang.String MIMEType, byte[] dsContent) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String addDatastreamXMLMetadata(java.lang.String PID, java.lang.String dsLabel, java.lang.String MDType, byte[] dsInlineMetadata) throws java.rmi.RemoteException {
        return null;
    }

    public void modifyDatastreamExternal(java.lang.String PID, java.lang.String datastreamID, java.lang.String dsLabel, java.lang.String dsLocation) throws java.rmi.RemoteException {
    }

    public void modifyDatastreamManagedContent(java.lang.String PID, java.lang.String datastreamID, java.lang.String dsLabel, java.lang.String MIMEType, byte[] dsContent) throws java.rmi.RemoteException {
    }

    public void modifyDatastreamXMLMetadata(java.lang.String PID, java.lang.String datastreamID, java.lang.String dsLabel, java.lang.String MDType, byte[] dsInlineMetadata) throws java.rmi.RemoteException {
    }

    public void withdrawDatastream(java.lang.String PID, java.lang.String datastreamID) throws java.rmi.RemoteException {
    }

    public void withdrawDisseminator(java.lang.String PID, java.lang.String disseminatorID) throws java.rmi.RemoteException {
    }

    public void deleteDatastream(java.lang.String PID, java.lang.String datastreamID) throws java.rmi.RemoteException {
    }

    public java.util.Calendar[] purgeDatastream(java.lang.String PID, java.lang.String datastreamID, java.util.Calendar startDT, java.util.Calendar endDT) throws java.rmi.RemoteException {
        return null;
    }

    public fedora.server.types.gen.Datastream getDatastream(java.lang.String PID, java.lang.String datastreamID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
        return null;
    }

    public fedora.server.types.gen.Datastream[] getDatastreams(java.lang.String PID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String[] listDatastreamIDs(java.lang.String PID, java.lang.String state) throws java.rmi.RemoteException {
        return null;
    }

    public fedora.server.types.gen.ComponentInfo[] getDatastreamHistory(java.lang.String PID, java.lang.String datastreamID) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String addDisseminator(java.lang.String PID, java.lang.String bMechPID, java.lang.String dissLabel, fedora.server.types.gen.DatastreamBindingMap bindingMap) throws java.rmi.RemoteException {
        return null;
    }

    public void modifyDisseminator(java.lang.String PID, java.lang.String disseminatorID, java.lang.String bMechPID, java.lang.String dissLabel, fedora.server.types.gen.DatastreamBindingMap bindingMap) throws java.rmi.RemoteException {
    }

    public void deleteDisseminator(java.lang.String PID, java.lang.String disseminatorID) throws java.rmi.RemoteException {
    }

    public java.util.Calendar[] purgeDisseminator(java.lang.String PID, java.lang.String disseminatorID, java.util.Calendar startDT, java.util.Calendar endDT) throws java.rmi.RemoteException {
        return null;
    }

    public fedora.server.types.gen.Disseminator getDisseminator(java.lang.String PID, java.lang.String disseminatorID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
        return null;
    }

    public fedora.server.types.gen.Disseminator[] getDisseminators(java.lang.String PID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String[] listDisseminatorIDs(java.lang.String PID, java.lang.String state) throws java.rmi.RemoteException {
        return null;
    }

    public fedora.server.types.gen.ComponentInfo[] getDisseminatorHistory(java.lang.String PID, java.lang.String disseminatorID) throws java.rmi.RemoteException {
        return null;
    }

}
