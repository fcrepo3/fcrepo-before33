/**
 * FedoraAPIMBindingSOAPHTTPImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis Wsdl2java emitter.
 */

package fedora.server.management;

public class FedoraAPIMBindingSOAPHTTPImpl implements fedora.server.management.FedoraAPIM {
    public java.lang.String createObject() throws java.rmi.RemoteException {
        return "This would be a PID if this operation implementation wasn't a stub.";
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
        return null;
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

    public fedora.server.auditing.AuditRecord[] getObjectAuditTrail(java.lang.String PID) throws java.rmi.RemoteException {
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

    public fedora.server.types.Datastream getDatastream(java.lang.String PID, java.lang.String datastreamID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
        return null;
    }

    public fedora.server.types.Datastream[] getDatastreams(java.lang.String PID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String[] listDatastreamIDs(java.lang.String PID, java.lang.String state) throws java.rmi.RemoteException {
        return null;
    }

    public fedora.server.types.ComponentInfo[] getDatastreamHistory(java.lang.String PID, java.lang.String datastreamID) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String addDisseminator(java.lang.String PID, java.lang.String bMechPID, java.lang.String dissLabel, fedora.server.types.DatastreamBindingMap bindingMap) throws java.rmi.RemoteException {
        return null;
    }

    public void modifyDisseminator(java.lang.String PID, java.lang.String disseminatorID, java.lang.String bMechPID, java.lang.String dissLabel, fedora.server.types.DatastreamBindingMap bindingMap) throws java.rmi.RemoteException {
    }

    public void deleteDisseminator(java.lang.String PID, java.lang.String disseminatorID) throws java.rmi.RemoteException {
    }

    public java.util.Calendar[] purgeDisseminator(java.lang.String PID, java.lang.String disseminatorID, java.util.Calendar startDT, java.util.Calendar endDT) throws java.rmi.RemoteException {
        return null;
    }

    public fedora.server.types.Disseminator getDisseminator(java.lang.String PID, java.lang.String disseminatorID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
        return null;
    }

    public fedora.server.types.Disseminator[] getDisseminators(java.lang.String PID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
        return null;
    }

    public java.lang.String[] listDisseminatorIDs(java.lang.String PID, java.lang.String state) throws java.rmi.RemoteException {
        return null;
    }

    public fedora.server.types.ComponentInfo[] getDisseminatorHistory(java.lang.String PID, java.lang.String disseminatorID) throws java.rmi.RemoteException {
        return null;
    }

}
