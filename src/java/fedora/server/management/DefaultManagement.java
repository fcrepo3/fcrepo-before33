package fedora.server.management;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;

import fedora.server.Context;
import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOWriter;
import fedora.server.types.gen.AuditRecord;
import fedora.server.types.gen.ComponentInfo;
import fedora.server.types.gen.Datastream;
import fedora.server.types.gen.DatastreamBindingMap;
import fedora.server.types.gen.Disseminator;

/**
 * The Management Module, providing support for API-M.
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DefaultManagement 
        extends Module implements Management {
        
    private DOManager m_manager;

    /**
     * Creates and initializes the Management Module.
     * <p></p>
     * When the server is starting up, this is invoked as part of the
     * initialization process.
     * 
     * @param moduleParameters A pre-loaded Map of name-value pairs comprising
     *        the intended configuration of this Module.
     * @param server The <code>Server</code> instance.
     * @param role The role this module fulfills, a java class name.
     * @throws ModuleInitializationException If initilization values are
     *         invalid or initialization fails for some other reason.
     */
    public DefaultManagement(Map moduleParameters, Server server, String role)
            throws ModuleInitializationException {
        super(moduleParameters, server, role);
    }
    
    public void initModule()
            throws ModuleInitializationException {
    }

    public void postInitModule()
            throws ModuleInitializationException {
        m_manager=(DOManager) getServer().getModule(
                "fedora.server.storage.DOManager");
        if (m_manager==null) {
            throw new ModuleInitializationException("Can't get a DOManager "
                    + "from Server.getModule", getRole());
        }
    }

    public String createObject(Context context) 
            throws ServerException {
        DOWriter w=m_manager.newWriter(context);
        return "hi"; //return w.GetObjectPID();
    }

    public String ingestObject(Context context, InputStream serialization, String format, String encoding, boolean newPid) 
            throws ServerException {
        DOWriter w=m_manager.newWriter(context, serialization, format, encoding, newPid);
        return w.GetObjectPID();
    }

    public InputStream getObjectXML(Context context, String pid, String format, String encoding) { return null; }

    public InputStream exportObject(Context context, String pid, String format, String encoding) { return null; }

    public void withdrawObject(Context context, String pid) { }

    public void deleteObject(Context context, String pid) { }

    public void purgeObject(Context context, String pid) { }

    public void obtainLock(Context context, String pid) { }

    public void releaseLock(Context context, String pid, String logMessage, boolean commit) { }

    public String getLockingUser(Context context, String pid) { return null; }

    public String getObjectState(Context context, String pid) { return null; }

    public Calendar getObjectCreateDate(Context context, String pid) { return null; }

    public Calendar getObjectLastModDate(Context context, String pid) { return null; }

    public AuditRecord[] getObjectAuditTrail(Context context, String pid) { return null; }

    public String[] listObjectPIDs(Context context, String state) 
            throws ServerException { 
        return m_manager.listObjectPIDs(context, state);
    }

    public String addDatastreamExternal(Context context, String pid, String dsLabel, String dsLocation) { return null; }

    public String addDatastreamManagedContent(Context context, String pid, String dsLabel, String MimeType, InputStream dsContent) { return null; }

    public String addDatastreamXMLMetadata(Context context, String pid, String dsLabel, String MdType, InputStream dsInlineMetadata) { return null; }

    public void modifyDatastreamExternal(Context context, String pid, String datastreamId, String dsLabel, String dsLocation) { }

    public void modifyDatastreamManagedContent(Context context, String pid, String datastreamId, String dsLabel, String MimeType, InputStream dsContent) { }

    public void modifyDatastreamXMLMetadata(Context context, String pid, String datastreamId, String dsLabel, String MdType, InputStream dsInlineMetadata) { }

    public void withdrawDatastream(Context context, String pid, String datastreamId) { }

    public void withdrawDisseminator(Context context, String pid, String disseminatorId) { }

    public void deleteDatastream(Context context, String pid, String datastreamID) { }

    public Calendar[] purgeDatastream(Context context, String pid, String datastreamID, Calendar startDT, Calendar endDT) { return null; }

    public Datastream getDatastream(Context context, String pid, String datastreamID, Calendar asOfDateTime) { return null; }

    public Datastream[] getDatastreams(Context context, String pid, Calendar asOfDateTime) { return null; }

    public String[] listDatastreamIDs(Context context, String pid, String state) { return null; }

    public ComponentInfo[] getDatastreamHistory(Context context, String pid, String datastreamID) { return null; }

    public String addDisseminator(Context context, String pid, String bMechPid, String dissLabel, DatastreamBindingMap bindingMap) { return null; }

    public void modifyDisseminator(Context context, String pid, String disseminatorId, String bMechPid, String dissLabel, DatastreamBindingMap bindingMap) { }

    public void deleteDisseminator(Context context, String pid, String disseminatorId) { }

    public Calendar[] purgeDisseminator(Context context, String pid, String disseminatorId, Calendar startDateTime, Calendar endDateTime) { return null; }

    public Disseminator getDisseminator(Context context, String pid, String disseminatorId, Calendar asOfDateTime) { return null; }

    public Disseminator[] getDisseminators(Context context, String pid, Calendar asOfDateTime) { return null; }

    public String[] listDisseminatorIDs(Context context, String pid, String state) { return null; }

    public ComponentInfo[] getDisseminatorHistory(Context context, String pid, String disseminatorId) { return null; }
    
}