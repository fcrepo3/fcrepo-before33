package fedora.server.management;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import fedora.server.Context;
import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;
import fedora.server.errors.ServerException;
import fedora.server.security.IPRestriction;
import fedora.server.storage.DOReader;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOWriter;
import fedora.server.storage.types.DatastreamContent;
import fedora.server.storage.types.DatastreamManagedContent;
import fedora.server.storage.types.DatastreamReferencedContent;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.Datastream;
import fedora.server.utilities.DateUtility;
import fedora.server.utilities.StreamUtility;

/**
 *
 * <p><b>Title:</b> DefaultManagement.java</p>
 * <p><b>Description:</b> The Management Module, providing support for API-M.</p>
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
public class DefaultManagement
        extends Module implements Management {

    private DOManager m_manager;
    private IPRestriction m_ipRestriction;
    private String m_fedoraServerHost;
    private String m_fedoraServerPort;

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
        m_fedoraServerHost=getServer().getParameter("fedoraServerHost");
        m_fedoraServerPort=getServer().getParameter("fedoraServerPort");
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
            return pid;
        } finally {
            m_manager.releaseWriter(w);
            Runtime r=Runtime.getRuntime();
            getServer().logFinest("Memory: " + r.freeMemory() + " bytes free of " + r.totalMemory() + " available.");
            getServer().logFinest("Exiting DefaultManagement.ingestObject");
        }
    }

    public InputStream getObjectXML(Context context, String pid, String format, String encoding) throws ServerException {
        logFinest("Entered DefaultManagement.getObjectXML");
        m_ipRestriction.enforce(context);
        DOReader reader=m_manager.getReader(context, pid);
        InputStream instream=reader.GetObjectXML();
        logFinest("Exiting DefaultManagement.getObjectXML");
        return instream;
    }

    public InputStream exportObject(Context context, String pid, String format,
            String encoding) throws ServerException {
        logFinest("Entered DefaultManagement.exportObject");
        m_ipRestriction.enforce(context);
        DOReader reader=m_manager.getReader(context, pid);
        InputStream instream=reader.ExportObject();
        logFinest("Exiting DefaultManagement.exportObject");
        return instream;
    }

/*
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

    public AuditRecord[] getObjectAuditTrail(Context context, String pid) { return null; }

    public String addDatastreamExternal(Context context, String pid, String dsLabel, String dsLocation) { return null; }

    public String addDatastreamManagedContent(Context context, String pid, String dsLabel, String MimeType, InputStream dsContent) { return null; }

    public String addDatastreamXMLMetadata(Context context, String pid, String dsLabel, String MdType, InputStream dsInlineMetadata) { return null; }
*/

    private String getNextID(String id) {
        // naive impl... just add "1" to the string
        return id + "1";
    }

    public void modifyDatastreamByReference(Context context, String pid,
            String datastreamId, String dsLabel, String logMessage,
            String dsLocation, String dsState)
            throws ServerException {
        m_ipRestriction.enforce(context);
        DOWriter w=null;
        try {
            w=m_manager.getWriter(context, pid);
            fedora.server.storage.types.Datastream orig=w.GetDatastream(datastreamId, null);
            if (orig.DSControlGrp.equals("M")) {
                    // copy the original datastream, replacing its DSLocation with
                    // the new location (or the old datastream's default dissemination location, if empty or null),
                    // triggering to doCommit that it needs to
                    // be loaded from a new remote location
                    DatastreamManagedContent newds=new DatastreamManagedContent();
                    newds.metadataIdList().addAll(((DatastreamContent) orig).metadataIdList());
                    newds.DatastreamID=orig.DatastreamID;
                    // make sure it has a different id
                    newds.DSVersionID=getNextID(orig.DSVersionID);
                    newds.DSLabel=dsLabel;
                    newds.DSMIME=orig.DSMIME;
                    Date nowUTC=DateUtility.convertLocalDateToUTCDate(new Date());
                    newds.DSCreateDT=nowUTC;
                    //newds.DSSize will be computed later
                    newds.DSControlGrp="M";
                    newds.DSInfoType=orig.DSInfoType;
                    newds.DSState=dsState;
                    //newds.DSState=orig.DSState;
                    if (dsLocation==null || dsLocation.equals("")) {
                        // if location unspecified, use the location of
                        // the datastream on the system, thus making a copy
                        newds.DSLocation="http://" + m_fedoraServerHost + ":"
                                + m_fedoraServerPort
                                + "/fedora/get/" + pid + "/fedora-system:3/getItem?itemID="
                                + datastreamId;
                    } else {
                        newds.DSLocation=dsLocation;
                    }
                    newds.auditRecordIdList().addAll(orig.auditRecordIdList());
                    // just add the datastream
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
                // Deal with other kinds, except xml (that must be passed in by value).
                if (orig.DSControlGrp.equals("X")) {
                    throw new GeneralException("Inline XML datastreams must be modified by value, not by reference.");
                }
                DatastreamReferencedContent newds=new DatastreamReferencedContent();
                newds.metadataIdList().addAll(((DatastreamContent) orig).metadataIdList());
                newds.DatastreamID=orig.DatastreamID;
                // make sure it has a different id
                newds.DSVersionID=getNextID(orig.DSVersionID);
                newds.DSLabel=dsLabel;
                newds.DSMIME=orig.DSMIME;
                Date nowUTC=DateUtility.convertLocalDateToUTCDate(new Date());
                newds.DSCreateDT=nowUTC;
                newds.DSControlGrp=orig.DSControlGrp;
                newds.DSInfoType=orig.DSInfoType;
                newds.DSState=dsState;
                //newds.DSState=orig.DSState;
                if (dsLocation==null || dsLocation.equals("")) {
                    // if location unspecified, use the location of
                    // the datastream on the system, thus making a copy
                    newds.DSLocation="http://" + m_fedoraServerHost + ":"
                            + m_fedoraServerPort
                            + "/fedora/get/" + pid + "/fedora-system:3/getItem?itemID="
                            + datastreamId;
                } else {
                    newds.DSLocation=dsLocation;
                }
                newds.auditRecordIdList().addAll(orig.auditRecordIdList());
                // just add the datastream
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
            }
            // if all went ok, commit
            w.commit(logMessage);
        } finally {
            if (w!=null) {
                m_manager.releaseWriter(w);
            }
        }
    }

//
    public void deleteDatastream(Context context, String pid,
                String datastreamId, String logMessage)
                throws ServerException {

          m_ipRestriction.enforce(context);
          DOWriter w=null;
          try {
              w=m_manager.getWriter(context, pid);
              fedora.server.storage.types.Datastream orig=w.GetDatastream(datastreamId, null);
              if (orig.DSControlGrp.equals("X")) {
                DatastreamXMLMetadata newds=new DatastreamXMLMetadata();
                newds.DSMDClass=((DatastreamXMLMetadata) orig).DSMDClass;
                ByteArrayOutputStream bytes=new ByteArrayOutputStream();
                try {
                  StreamUtility.pipeStream(orig.getContentStream(), bytes, 1024);
                } catch (Exception ex) {
                }
                newds.xmlContent=bytes.toByteArray();
                newds.DatastreamID=orig.DatastreamID;
                newds.DSVersionID=orig.DSVersionID;
                newds.DSLabel=orig.DSLabel;
                newds.DSMIME=orig.DSMIME;
                Date nowUTC=DateUtility.convertLocalDateToUTCDate(new Date());
                newds.DSCreateDT=nowUTC;
                newds.DSControlGrp=orig.DSControlGrp;
                newds.DSInfoType=orig.DSInfoType;
                newds.DSState="D";
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
                // Deal with other kinds
                DatastreamReferencedContent newds=new DatastreamReferencedContent();
                newds.metadataIdList().addAll(((DatastreamContent) orig).metadataIdList());
                newds.DatastreamID=orig.DatastreamID;
                newds.DSVersionID=orig.DSVersionID;
                newds.DSLabel=orig.DSLabel;
                newds.DSMIME=orig.DSMIME;
                Date nowUTC=DateUtility.convertLocalDateToUTCDate(new Date());
                newds.DSCreateDT=nowUTC;
                newds.DSControlGrp=orig.DSControlGrp;
                newds.DSInfoType=orig.DSInfoType;
                newds.DSState="D";
                newds.DSLocation=orig.DSLocation;
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
              }
              // if all went ok, commit
              w.commit(logMessage);
          } finally {
              if (w!=null) {
                  m_manager.releaseWriter(w);
              }
          }
    }
//

//
    public void withdrawDatastream(Context context, String pid,
                String datastreamId, String logMessage)
                throws ServerException {

          m_ipRestriction.enforce(context);
          DOWriter w=null;
          try {
              w=m_manager.getWriter(context, pid);
              fedora.server.storage.types.Datastream orig=w.GetDatastream(datastreamId, null);
              if (orig.DSControlGrp.equals("X")) {
                DatastreamXMLMetadata newds=new DatastreamXMLMetadata();
                newds.DSMDClass=((DatastreamXMLMetadata) orig).DSMDClass;
                ByteArrayOutputStream bytes=new ByteArrayOutputStream();
                try {
                  StreamUtility.pipeStream(orig.getContentStream(), bytes, 1024);
                } catch (Exception ex) {
                }
                newds.xmlContent=bytes.toByteArray();
                newds.DatastreamID=orig.DatastreamID;
                newds.DSVersionID=orig.DSVersionID;
                newds.DSLabel=orig.DSLabel;
                newds.DSMIME=orig.DSMIME;
                Date nowUTC=DateUtility.convertLocalDateToUTCDate(new Date());
                newds.DSCreateDT=nowUTC;
                newds.DSControlGrp=orig.DSControlGrp;
                newds.DSInfoType=orig.DSInfoType;
                newds.DSState="W";
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
                // Deal with other kinds
                DatastreamReferencedContent newds=new DatastreamReferencedContent();
                newds.metadataIdList().addAll(((DatastreamContent) orig).metadataIdList());
                newds.DatastreamID=orig.DatastreamID;
                newds.DSVersionID=orig.DSVersionID;
                newds.DSLabel=orig.DSLabel;
                newds.DSMIME=orig.DSMIME;
                Date nowUTC=DateUtility.convertLocalDateToUTCDate(new Date());
                newds.DSCreateDT=nowUTC;
                newds.DSControlGrp=orig.DSControlGrp;
                newds.DSInfoType=orig.DSInfoType;
                newds.DSState="W";
                newds.DSLocation=orig.DSLocation;
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
              }
              // if all went ok, commit
              w.commit(logMessage);
          } finally {
              if (w!=null) {
                  m_manager.releaseWriter(w);
              }
          }
    }
//

    public void modifyDatastreamByValue(Context context, String pid,
            String datastreamId, String dsLabel, String logMessage,
            InputStream dsContent, String dsState) throws ServerException {
        m_ipRestriction.enforce(context);
        DOWriter w=null;
        try {
            w=m_manager.getWriter(context, pid);
            fedora.server.storage.types.Datastream orig=w.GetDatastream(datastreamId, null);
            if (!orig.DSControlGrp.equals("X")) {
                throw new GeneralException("Only inline XML datastreams may be modified by value.");
            }
            if (orig.DatastreamID.equals("METHODMAP")
                    || orig.DatastreamID.equals("DSINPUTSPEC")
                    || orig.DatastreamID.equals("WSDL")) {
                throw new GeneralException("METHODMAP, DSINPUTSPEC, and WSDL datastreams cannot be modified.");
            }
            DatastreamXMLMetadata newds=new DatastreamXMLMetadata();
            newds.DSMDClass=((DatastreamXMLMetadata) orig).DSMDClass;
            ByteArrayOutputStream bytes=new ByteArrayOutputStream();
            try {
                StreamUtility.pipeStream(dsContent, bytes, 1024);
            } catch (Exception ex) {
            }
            newds.xmlContent=bytes.toByteArray();
            newds.DatastreamID=orig.DatastreamID;
            // make sure it has a different id
            newds.DSVersionID=getNextID(orig.DSVersionID);
            newds.DSLabel=dsLabel;
            newds.DSMIME=orig.DSMIME;
            Date nowUTC=DateUtility.convertLocalDateToUTCDate(new Date());
            newds.DSCreateDT=nowUTC;
            newds.DSControlGrp=orig.DSControlGrp;
            newds.DSInfoType=orig.DSInfoType;
            newds.DSState=dsState;
            newds.auditRecordIdList().addAll(orig.auditRecordIdList());
            // just add the datastream
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
            // if all went ok, commit
            w.commit(logMessage);
        } finally {
            if (w!=null) {
                m_manager.releaseWriter(w);
            }
        }
    }

/*
    public void withdrawDatastream(Context context, String pid,
            String datastreamId) throws ServerException {
        m_ipRestriction.enforce(context);
    }

    public void withdrawDisseminator(Context context, String pid, String disseminatorId) { }

    public void deleteDatastream(Context context, String pid, String datastreamID) { }

*/
    public Calendar[] purgeDatastream(Context context, String pid,
            String datastreamID, Calendar startDT, Calendar endDT)
            throws ServerException {
        m_ipRestriction.enforce(context);
        DOWriter w=null;
        try {
            w=m_manager.getWriter(context, pid);
            Date start=null;
            if (startDT!=null) {
                start=startDT.getTime();
            }
            Date end=null;
            if (endDT!=null) {
                end=endDT.getTime();
            }
            Date[] deletedDates=w.removeDatastream(datastreamID, start, end);
            // check if there's at least one version with this id...
            if (w.GetDatastream(datastreamID, null)==null) {
                // Deleting all versions of a datastream is currently unsupported
                // FIXME: In the future, this exception should be replaced with an
                // integrity check.
                throw new GeneralException("Purge was aborted because it would"
                        + " result in the permanent deletion of ALL versions "
                        + "of the datastream.");
            }
            // make a log messsage explaining what happened
            String logMessage=getPurgeLogMessage("datastream", datastreamID,
                    start, end, deletedDates);
            Date nowUTC=DateUtility.convertLocalDateToUTCDate(new Date());
            fedora.server.storage.types.AuditRecord audit=new fedora.server.storage.types.AuditRecord();
            audit.id="AUDIT" + w.getAuditRecords().size() + 1;
            audit.processType="Fedora API-M";
            audit.action="purgeDatastream";
            audit.responsibility=context.get("userId");
            audit.date=nowUTC;
            audit.justification=logMessage;
            // Normally we associate an audit record with a specific version
            // of a datastream, but in this case we are talking about a range
            // of versions.  So we'll just add it to the object, but not associate
            // it with anything.
            w.getAuditRecords().add(audit);
            // It looks like all went ok, so commit
            w.commit(logMessage);
            // ... then give the response
            return dateArrayToCalendarArray(deletedDates);
        } finally {
            if (w!=null) {
                m_manager.releaseWriter(w);
            }
        }
    }

    private Calendar[] dateArrayToCalendarArray(Date[] dates) {
        Calendar response[]=new Calendar[dates.length];
        for (int i=0; i<dates.length; i++) {
            response[i]=new GregorianCalendar();
            response[i].setTime(dates[i]);
        }
        return response;
    }

    private String getPurgeLogMessage(String kindaThing, String id, Date start,
            Date end, Date[] deletedDates) {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        StringBuffer buf=new StringBuffer();
        buf.append("Purged ");
        buf.append(kindaThing);
        buf.append(" (ID=");
        buf.append(id);
        buf.append("), versions ranging from ");
        if (start==null) {
            buf.append("the beginning of time");
        } else {
            buf.append(formatter.format(start));
        }
        buf.append(" to ");
        if (end==null) {
            buf.append("the end of time");
        } else {
            buf.append(formatter.format(end));
        }
        buf.append(".  This resulted in the permanent removal of ");
        buf.append(deletedDates.length + " ");
        buf.append(kindaThing);
        buf.append(" version(s) (");
        for (int i=0; i<deletedDates.length; i++) {
            if (i>0) {
                buf.append(", ");
            }
            buf.append(formatter.format(deletedDates[i]));
        }
        buf.append(") and all associated audit records.");
        return buf.toString();
    }

    public Datastream getDatastream(Context context, String pid, String datastreamID, Calendar asOfDateTime)
            throws ServerException {
        m_ipRestriction.enforce(context);
        DOReader r=m_manager.getReader(context, pid);
        Date d=null;
        if (asOfDateTime!=null) {
            d=asOfDateTime.getTime();
        }
		Datastream ds=r.GetDatastream(datastreamID, d);
		// in the case of managed content OR xml, change the location to the 
		// getItem request instead of using the internal identifier or null/blank,
		// so that clients can easily retrieve the content
        if (ds.DSControlGrp.equalsIgnoreCase("M") || ds.DSControlGrp.equalsIgnoreCase("X")) {
		    StringBuffer buf=new StringBuffer();
			buf.append("http://" + m_fedoraServerHost);
			if (!m_fedoraServerPort.equals("80")) {
			    buf.append(":" + m_fedoraServerPort);
			}
			buf.append("/fedora/get/" + pid + "/fedora-system:3/getItem");
			if (d!=null) {
                SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
				buf.append("/" + formatter.format(d));
			}
			buf.append("?itemID=" + ds.DatastreamID);
			ds.DSLocation=buf.toString();
		}
		return ds;
    }
/*
    public Datastream[] getDatastreams(Context context, String pid, Calendar asOfDateTime) { return null; }
*/
    public String[] listDatastreamIDs(Context context, String pid, String state)
            throws ServerException {
        m_ipRestriction.enforce(context);
        DOReader r=m_manager.getReader(context, pid);
        return r.ListDatastreamIDs(state);
    }

    public Calendar[] getDatastreamHistory(Context context, String pid, String datastreamID)
            throws ServerException {
        m_ipRestriction.enforce(context);
        DOReader r=m_manager.getReader(context, pid);
        return dateArrayToCalendarArray(r.getDatastreamVersions(datastreamID));
    }

/*
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