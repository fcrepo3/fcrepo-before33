package fedora.server.management;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import fedora.server.Context;
import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;
import fedora.server.errors.ServerException;
import fedora.server.security.IPRestriction;
import fedora.server.storage.DOReader;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOWriter;
import fedora.server.storage.types.DatastreamManagedContent;
//import fedora.server.types.gen.AuditRecord;
//import fedora.server.types.gen.ComponentInfo;
import fedora.server.types.gen.Datastream;
//import fedora.server.types.gen.DatastreamBindingMap;
//import fedora.server.types.gen.Disseminator;
import fedora.server.types.gen.ObjectInfo;
import fedora.server.utilities.DateUtility;

/**
 * The Management Module, providing support for API-M.
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DefaultManagement 
        extends Module implements Management {
        
    private DOManager m_manager;
    private IPRestriction m_ipRestriction;

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
        String allowHosts=getParameter("allowHosts");
        String denyHosts=getParameter("denyHosts");
        try {
            m_ipRestriction=new IPRestriction(allowHosts, denyHosts);
        } catch (ServerException se) {
            throw new ModuleInitializationException("Error setting IP restriction "
                    + "for Access subsystem: " + se.getClass().getName() + ": "
                    + se.getMessage(), getRole());
        }        
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

/*
    public String createObject(Context context) 
            throws ServerException {
        getServer().logFinest("Entered DefaultManagement.createObject");
        m_ipRestriction.enforce(context);
        DOWriter w=m_manager.newWriter(context);
        String pid=w.GetObjectPID();
        m_manager.releaseWriter(w);
        getServer().logFinest("Exiting DefaultManagement.createObject");
        return pid;
    }
*/

    public String ingestObject(Context context, InputStream serialization, String logMessage, String format, String encoding, boolean newPid) 
            throws ServerException {
        getServer().logFinest("Entered DefaultManagement.ingestObject");
        m_ipRestriction.enforce(context);
        DOWriter w=m_manager.newWriter(context, serialization, format, encoding, newPid);
        String pid=w.GetObjectPID();
        // FIXME: this logic should go in clients,
        // but it happens that it's convenient to put here for now.
        // the below does a purgeObject if commit fails... kind of an auto-cleanup
        // in the future this "initial state" stuff will be reconsidered anyway,
        // applying the ideas of workflow, etc..
        try {
            w.commit(logMessage);
        } catch (ServerException se) {
            logFinest("Auto-purging as a result of a failed auto-commit in ingestObject.");
            purgeObject(context, pid, "Purging because auto-commit (which is temporarily taken care of by the server's ingestObject operation) failed: " + se.getMessage());
            throw se;
        }
        m_manager.releaseWriter(w);
        Runtime r=Runtime.getRuntime();
        getServer().logFinest("Memory: " + r.freeMemory() + " bytes free of " + r.totalMemory() + " available.");
        getServer().logFinest("Exiting DefaultManagement.ingestObject");
        return pid;
    }

    public InputStream getObjectXML(Context context, String pid, String format, String encoding) throws ServerException { 
        logFinest("Entered DefaultManagement.getObjectXML");
        m_ipRestriction.enforce(context);
        DOReader reader=m_manager.getReader(context, pid);
        InputStream instream=reader.GetObjectXML(); 
        logFinest("Exiting DefaultManagement.getObjectXML");
        return instream;
    }

/*
    public InputStream exportObject(Context context, String pid, String format, String encoding) { return null; }

    public void withdrawObject(Context context, String pid, String logMessage) { }

    public void deleteObject(Context context, String pid, String logMessage) { }
*/
    public void purgeObject(Context context, String pid, String logMessage) 
            throws ServerException { 
        logFinest("Entered DefaultManagement.purgeObject");
        m_ipRestriction.enforce(context);
        // FIXME: This should get a writer and call remove, then commit instead...but this works for now
        // fedora.server.storage.types.BasicDigitalObject obj=new fedora.server.storage.types.BasicDigitalObject();
        // obj.setPid(pid);
        // ((fedora.server.storage.DefaultDOManager) m_manager).doCommit(context, obj, logMessage, true);
        DOWriter w=m_manager.getWriter(context, pid);
        w.remove();
        w.commit(logMessage);
        m_manager.releaseWriter(w);
        logFinest("Exiting DefaultManagement.purgeObject");
    }

// obsolete: methods that require a lock will create one automatically
// if one doesn't already exist... it's easier that way.
/*    public void obtainLock(Context context, String pid) {
        
    }
*/
/*
    public void releaseLock(Context context, String pid, String logMessage, 
            boolean commit) 
            throws ServerException { 
        getServer().logFinest("Entered DefaultManagement.releaseLock");
        m_ipRestriction.enforce(context);
        DOWriter w=m_manager.getWriter(context, pid);
        if (commit) {
            w.commit(logMessage); // FIXME: make the audit record HERE
        } else {
            w.cancel();
        }
        m_manager.releaseWriter(w);
        getServer().logFinest("Exiting DefaultManagement.releaseLock");
    }
*/
    public ObjectInfo getObjectInfo(Context context, String pid) 
            throws ServerException { 
        getServer().logFinest("Entered DefaultManagement.getObjectInfo");
        m_ipRestriction.enforce(context);
        ObjectInfo inf=new ObjectInfo();
        DOReader r=m_manager.getReader(context, pid);
        inf.setLabel(r.GetObjectLabel());
        inf.setFoType(r.getFedoraObjectType());
        inf.setContentModelId(r.getContentModelId());
        inf.setState(r.GetObjectState());
        String lockedBy=r.getLockingUser();
        if (lockedBy==null) {
            inf.setLockedBy("");
        } else {
            inf.setLockedBy(lockedBy);
        }
        GregorianCalendar createDate=new GregorianCalendar();
        createDate.setTime(r.getCreateDate());
        inf.setCreateDate(createDate);
        GregorianCalendar lastModDate=new GregorianCalendar();
        lastModDate.setTime(r.getLastModDate());
        inf.setLastModDate(lastModDate);
        getServer().logFinest("Exiting DefaultManagement.getObjectInfo");
        return inf;
    }
/*
    public AuditRecord[] getObjectAuditTrail(Context context, String pid) { return null; }
*/
    public String[] listObjectPIDs(Context context, String pidPattern, 
            String foType, String lockedByPattern, String state, 
            String labelPattern, String contentModelIdPattern, 
            Calendar createDateMin, Calendar createDateMax, 
            Calendar lastModDateMin, Calendar lastModDateMax) 
            throws ServerException {
        m_ipRestriction.enforce(context);
        return m_manager.listObjectPIDs(context, pidPattern,
                foType, lockedByPattern, state, labelPattern,
                contentModelIdPattern, createDateMin, createDateMax, 
                lastModDateMin, lastModDateMax);
    }

/*
    public String addDatastreamExternal(Context context, String pid, String dsLabel, String dsLocation) { return null; }

    public String addDatastreamManagedContent(Context context, String pid, String dsLabel, String MimeType, InputStream dsContent) { return null; }

    public String addDatastreamXMLMetadata(Context context, String pid, String dsLabel, String MdType, InputStream dsInlineMetadata) { return null; }
*/
    public void modifyDatastreamByReference(Context context, String pid, 
            String datastreamId, String dsLabel, String logMessage, 
            String dsLocation) 
            throws ServerException { 
        m_ipRestriction.enforce(context);
        DOWriter w=null;
        try {
            w=m_manager.getWriter(context, pid);
            fedora.server.storage.types.Datastream orig=w.GetDatastream(datastreamId, null);
            if (orig.DSControlGrp.equals("M")) {
                    // copy the original datastream, replacing its DSLocation with
                    // the new location, triggering to doCommit that it needs to
                    // be loaded from a new remote location
                    DatastreamManagedContent newds=new DatastreamManagedContent();
                    newds.DatastreamID=orig.DatastreamID;
                    newds.DSVersionID=orig.DSVersionID;
                    newds.DSLabel=dsLabel;
                    newds.DSMIME=orig.DSMIME;
                    Date nowUTC=DateUtility.convertLocalDateToUTCDate(new Date());
                    newds.DSCreateDT=nowUTC;
                    //newds.DSSize will be computed later
                    newds.DSControlGrp="M";
                    newds.DSInfoType=orig.DSInfoType;
                    newds.DSState=orig.DSState;
                    newds.DSLocation=dsLocation;
                    newds.auditRecordIdList().addAll(orig.auditRecordIdList());
                    // remove, then add the datastream
                    w.removeDatastream(datastreamId, null, null);
                    w.addDatastream(newds);
                    // add the audit record
                    fedora.server.storage.types.AuditRecord audit=new fedora.server.storage.types.AuditRecord();
                    audit.id="AUDIT" + w.getAuditRecords().size() + 1;
                    audit.processType="Fedora API-M";
                    audit.action="modifyDatastreamByReference";
                    audit.responsibility=context.get("userId");
                    audit.date=nowUTC;
                    audit.justification=logMessage;
                    w.getAuditRecords().add(audit);
                    newds.auditRecordIdList().add(audit.id);
            } else {
                // TODO: other control groups for current datastream
            }
            // now commit it... note: committing here is probably temporary.
            w.commit(logMessage);
        } finally {
            if (w!=null) {
                m_manager.releaseWriter(w);
            }
        }
    }

    public void modifyDatastreamByValue(Context context, String pid, 
            String datastreamId, String dsLabel, String logMessage, 
            InputStream dsContent) throws ServerException { 
        m_ipRestriction.enforce(context);
        DOWriter w=m_manager.getWriter(context, pid);
        // Replace the datastream.
        // - First, get a handle on it
        // - Then remove it
        // - Then re-add it, then commit.
        fedora.server.storage.types.Datastream origDatastream=w.GetDatastream(datastreamId, null);
        if (origDatastream.DSControlGrp.equals("M")) {
        }
        w.removeDatastream(datastreamId, null, null);
        /*
        modifyByRef
   X  // must be xml!

   E or R
      // just change location
   M
      was already in lowlevel store...
      somehow trigger, using defaultdomanager's doCommit stuff,
      looking at the dslocation syntax?


modifyByValue
   X  // must be xml!
      newds.xmlContent=byte array
   E or R
      changes it to external datastream
   M
      // maybe don't enable this yet...throw exception?
      it was already in lowlevel store...
      just needs its content replaced.
      should be able to trigger thi
      
      */
        
        
    }
/*
    public void withdrawDatastream(Context context, String pid, 
            String datastreamId) throws ServerException { 
        m_ipRestriction.enforce(context);
    }

    public void withdrawDisseminator(Context context, String pid, String disseminatorId) { }

    public void deleteDatastream(Context context, String pid, String datastreamID) { }

    public Calendar[] purgeDatastream(Context context, String pid, String datastreamID, Calendar startDT, Calendar endDT) { return null; }
*/
    public Datastream getDatastream(Context context, String pid, String datastreamID, Calendar asOfDateTime) { return null; }
/*
    public Datastream[] getDatastreams(Context context, String pid, Calendar asOfDateTime) { return null; }
*/
    public String[] listDatastreamIDs(Context context, String pid, String state) { return null; }
/*
    public ComponentInfo[] getDatastreamHistory(Context context, String pid, String datastreamID) { return null; }

    public String addDisseminator(Context context, String pid, String bMechPid, String dissLabel, DatastreamBindingMap bindingMap) { return null; }

    public void modifyDisseminator(Context context, String pid, String disseminatorId, String bMechPid, String dissLabel, DatastreamBindingMap bindingMap) { }

    public void deleteDisseminator(Context context, String pid, String disseminatorId) { }

    public Calendar[] purgeDisseminator(Context context, String pid, String disseminatorId, Calendar startDateTime, Calendar endDateTime) { return null; }

    public Disseminator getDisseminator(Context context, String pid, String disseminatorId, Calendar asOfDateTime) { return null; }

    public Disseminator[] getDisseminators(Context context, String pid, Calendar asOfDateTime) { return null; }

    public String[] listDisseminatorIDs(Context context, String pid, String state) { return null; }

    public ComponentInfo[] getDisseminatorHistory(Context context, String pid, String disseminatorId) { return null; }
 */   
}