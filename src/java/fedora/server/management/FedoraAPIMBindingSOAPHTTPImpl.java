package fedora.server.management;

import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.storage.TestFileStreamStorage;
import fedora.server.utilities.AxisUtility;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import org.apache.axis.AxisEngine;
import org.apache.axis.MessageContext;

public class FedoraAPIMBindingSOAPHTTPImpl 
        implements fedora.server.management.FedoraAPIM {

    /** The Fedora Server instance */
    private static Server s_server;

    /** The Fedora Server instance */
    private static boolean s_initialized;

    /** The exception indicating that initialization failed. */
    private static InitializationException s_initException;

    /** Before fulfilling any requests, make sure we have a server instance. */
    static {
        try {
            String fedoraHome=System.getProperty("fedora.home");
            if (fedoraHome==null) {
                s_initialized=false;
                s_initException=new ServerInitializationException(
                    "Server failed to initialize: The 'fedora.home' "
                    + "system property was not set.");
            } else {
                s_server=Server.getInstance(new File(fedoraHome));
                s_initialized=true;
            }
        } catch (InitializationException ie) {
            System.err.println(ie.getMessage());
            s_initialized=false;
            s_initException=ie;
        }
    }

    public java.lang.String createObject() throws java.rmi.RemoteException {
        assertInitialized();
        return "This would be a PID if this operation implementation wasn't "
                + "a stub.  BTW, the scope of this service (as defined by the "
                + "scope property in the wsdd file) is '" 
                + AxisEngine.getCurrentMessageContext().getStrProp("scope") + "'. Also, fedora.home=" + System.getProperty("fedora.home");
        //return null;
    }

    public java.lang.String ingestObject(byte[] METSXML) throws java.rmi.RemoteException {
        assertInitialized();
        try {
            String pid="1234";
            TestFileStreamStorage st=new TestFileStreamStorage(new File(s_server.getHomeDir(), "data"), 4096);
            
            // DefinitiveDOWriter w=new DefinitiveDOWriter(pid, st, st, null, );
            return pid;
        } catch (ServerException se) {
            AxisUtility.throwFault(se);
        } catch (Exception e) {
            AxisUtility.throwFault(new ServerInitializationException(e.getClass().getName() + ": " + e.getMessage()));
        }
       /* 
    public DefinitiveDOWriter(String pid, TestStreamStorage storage, 
            TestStreamStorage tempStorage, StreamValidator validator,
            DODeserializer importDeserializer, DOSerializer storageSerializer,
            DODeserializer storageDeserializer, DOSerializer exportSerializer,
            InputStream initialContent, boolean useContentPid) 
            throws ObjectIntegrityException, 
            StreamIOException, StreamReadException {        
        */    
            
        return null;
    }

    public byte[] getObjectXML(java.lang.String PID) throws java.rmi.RemoteException {
        assertInitialized();
        if (1==1) {
            AxisUtility.throwFault(new ObjectIntegrityException(null, 
                    "Method not implemented", null, new String[] {
                    "testing detail line one", "this is line two", 
                    "guess which line this is?  right.  three."}, null));
        }
        return null;
    }

    public byte[] exportObject(java.lang.String PID) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public void withdrawObject(java.lang.String PID) throws java.rmi.RemoteException {
        assertInitialized();
    }

    public void deleteObject(java.lang.String PID) throws java.rmi.RemoteException {
        assertInitialized();
    }

    public void purgeObject(java.lang.String PID) throws java.rmi.RemoteException {
        assertInitialized();
    }

    public void obtainLock(java.lang.String PID) throws java.rmi.RemoteException {
        assertInitialized();
    }

    public void releaseLock(java.lang.String PID, java.lang.String logMessage, boolean commit) throws java.rmi.RemoteException {
        assertInitialized();
    }

    public java.lang.String getLockingUser(java.lang.String PID) throws java.rmi.RemoteException {
        assertInitialized();
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
        assertInitialized();
        return null;
    }

    public java.util.Calendar getObjectCreateDate(java.lang.String PID) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public java.util.Calendar getObjectLastModDate(java.lang.String PID) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public fedora.server.types.gen.AuditRecord[] getObjectAuditTrail(java.lang.String PID) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public java.lang.String[] listObjectPIDs(java.lang.String state) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public java.lang.String addDatastreamExternal(java.lang.String PID, java.lang.String dsLabel, java.lang.String dsLocation) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public java.lang.String addDatastreamManagedContent(java.lang.String PID, java.lang.String dsLabel, java.lang.String MIMEType, byte[] dsContent) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public java.lang.String addDatastreamXMLMetadata(java.lang.String PID, java.lang.String dsLabel, java.lang.String MDType, byte[] dsInlineMetadata) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public void modifyDatastreamExternal(java.lang.String PID, java.lang.String datastreamID, java.lang.String dsLabel, java.lang.String dsLocation) throws java.rmi.RemoteException {
        assertInitialized();
    }

    public void modifyDatastreamManagedContent(java.lang.String PID, java.lang.String datastreamID, java.lang.String dsLabel, java.lang.String MIMEType, byte[] dsContent) throws java.rmi.RemoteException {
        assertInitialized();
    }

    public void modifyDatastreamXMLMetadata(java.lang.String PID, java.lang.String datastreamID, java.lang.String dsLabel, java.lang.String MDType, byte[] dsInlineMetadata) throws java.rmi.RemoteException {
        assertInitialized();
    }

    public void withdrawDatastream(java.lang.String PID, java.lang.String datastreamID) throws java.rmi.RemoteException {
        assertInitialized();
    }

    public void withdrawDisseminator(java.lang.String PID, java.lang.String disseminatorID) throws java.rmi.RemoteException {
        assertInitialized();
    }

    public void deleteDatastream(java.lang.String PID, java.lang.String datastreamID) throws java.rmi.RemoteException {
        assertInitialized();
    }

    public java.util.Calendar[] purgeDatastream(java.lang.String PID, java.lang.String datastreamID, java.util.Calendar startDT, java.util.Calendar endDT) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public fedora.server.types.gen.Datastream getDatastream(java.lang.String PID, java.lang.String datastreamID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public fedora.server.types.gen.Datastream[] getDatastreams(java.lang.String PID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public java.lang.String[] listDatastreamIDs(java.lang.String PID, java.lang.String state) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public fedora.server.types.gen.ComponentInfo[] getDatastreamHistory(java.lang.String PID, java.lang.String datastreamID) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public java.lang.String addDisseminator(java.lang.String PID, java.lang.String bMechPID, java.lang.String dissLabel, fedora.server.types.gen.DatastreamBindingMap bindingMap) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public void modifyDisseminator(java.lang.String PID, java.lang.String disseminatorID, java.lang.String bMechPID, java.lang.String dissLabel, fedora.server.types.gen.DatastreamBindingMap bindingMap) throws java.rmi.RemoteException {
        assertInitialized();
    }

    public void deleteDisseminator(java.lang.String PID, java.lang.String disseminatorID) throws java.rmi.RemoteException {
        assertInitialized();
    }

    public java.util.Calendar[] purgeDisseminator(java.lang.String PID, java.lang.String disseminatorID, java.util.Calendar startDT, java.util.Calendar endDT) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public fedora.server.types.gen.Disseminator getDisseminator(java.lang.String PID, java.lang.String disseminatorID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public fedora.server.types.gen.Disseminator[] getDisseminators(java.lang.String PID, java.util.Calendar asOfDateTime) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public java.lang.String[] listDisseminatorIDs(java.lang.String PID, java.lang.String state) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }

    public fedora.server.types.gen.ComponentInfo[] getDisseminatorHistory(java.lang.String PID, java.lang.String disseminatorID) throws java.rmi.RemoteException {
        assertInitialized();
        return null;
    }
    
    private void assertInitialized()
            throws java.rmi.RemoteException {
        if (!s_initialized) {
            AxisUtility.throwFault(s_initException);
        }
    }

}
