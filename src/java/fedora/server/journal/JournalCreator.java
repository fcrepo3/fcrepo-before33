package fedora.server.journal;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import fedora.server.Context;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;
import fedora.server.errors.ServerException;
import fedora.server.journal.entry.CreatorJournalEntry;
import fedora.server.journal.helpers.JournalHelper;
import fedora.server.management.ManagementDelegate;
import fedora.server.storage.types.DSBindingMap;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.Property;

/**
 * 
 * <p>
 * <b>Title:</b> JournalCreator.java
 * </p>
 * <p>
 * <b>Description:</b> This is the worker class to use in Journalling mode
 * (normal mode).
 * </p>
 * <p>
 * Each time a "writing" Management method is called, create a
 * CreatorJournalEntry and ask it to invoke the method on the
 * ManagementDelegate. If a "read-only" Management method is called, just pass
 * it along to the ManagementDelegate.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class JournalCreator implements JournalWorker, JournalConstants {

    /** Logger for this class. */
    private static final Logger LOG = Logger.getLogger(
            JournalCreator.class.getName());

    private final JournalWriter writer;
    private final String role;

    private ManagementDelegate delegate;

    /**
     * Get a JournalWriter to use, based on the server parameters.
     */
    public JournalCreator(Map parameters, String role, ServerInterface server)
            throws ModuleInitializationException {
        this.role = role;
        
        try {
            this.writer = JournalWriter.getInstance(parameters, role, server);
        } catch (JournalException e) {
            String msg = "Problem creating the JournalWriter";
            LOG.error(msg, e);
            throw new ModuleInitializationException(msg, role, e);
        }
    }

    /**
     * Receive a ManagementDelegate module to perform the Management operations.
     */
    public void setManagementDelegate(ManagementDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Server is shutting down, so tell the JournalWriter to shut down.
     */
    public void shutdown() throws ModuleShutdownException {
        try {
            writer.shutdown();
        } catch (JournalException e) {
            throw new ModuleShutdownException(
                    "JournalWriter generated an error on shutdown()", role, e);
        }
    }

    //
    // -------------------------------------------------------------------------
    // 
    // Create a Journal entry for each call to one of the Management API
    // "writing" methods.
    // 
    // -------------------------------------------------------------------------
    //

    /**
     * Let the delegate do it, and then write a journal entry.
     */
    public String ingestObject(Context context, InputStream serialization,
            String logMessage, String format, String encoding, boolean newPid)
            throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_INGEST_OBJECT, context);
            cje.addArgument(ARGUMENT_NAME_SERIALIZATION, serialization);
            cje.addArgument(ARGUMENT_NAME_LOG_MESSAGE, logMessage);
            cje.addArgument(ARGUMENT_NAME_FORMAT, format);
            cje.addArgument(ARGUMENT_NAME_ENCODING, encoding);
            cje.addArgument(ARGUMENT_NAME_NEW_PID, newPid);
            return (String) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public Date modifyObject(Context context, String pid, String state,
            String label, String logMessage) throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_MODIFY_OBJECT, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_STATE, state);
            cje.addArgument(ARGUMENT_NAME_LABEL, label);
            cje.addArgument(ARGUMENT_NAME_LOG_MESSAGE, logMessage);
            return (Date) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public Date purgeObject(Context context, String pid, String logMessage,
            boolean force) throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_PURGE_OBJECT, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_LOG_MESSAGE, logMessage);
            cje.addArgument(ARGUMENT_NAME_FORCE, force);
            return (Date) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public String addDatastream(Context context, String pid, String dsID,
            String[] altIDs, String dsLabel, boolean versionable,
            String MIMEType, String formatURI, String location,
            String controlGroup, String dsState, String checksumType,
            String checksum, String logMessage)
            throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_ADD_DATASTREAM, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_DS_ID, dsID);
            cje.addArgument(ARGUMENT_NAME_ALT_IDS, altIDs);
            cje.addArgument(ARGUMENT_NAME_DS_LABEL, dsLabel);
            cje.addArgument(ARGUMENT_NAME_VERSIONABLE, versionable);
            cje.addArgument(ARGUMENT_NAME_MIME_TYPE, MIMEType);
            cje.addArgument(ARGUMENT_NAME_FORMAT_URI, formatURI);
            cje.addArgument(ARGUMENT_NAME_LOCATION, location);
            cje.addArgument(ARGUMENT_NAME_CONTROL_GROUP, controlGroup);
            cje.addArgument(ARGUMENT_NAME_DS_STATE, dsState);
            cje.addArgument(ARGUMENT_NAME_CHECKSUM_TYPE, checksumType);
            cje.addArgument(ARGUMENT_NAME_CHECKSUM, checksum);
            cje.addArgument(ARGUMENT_NAME_LOG_MESSAGE, logMessage);
                 return (String) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public Date modifyDatastreamByValue(Context context, String pid,
            String datastreamID, String[] altIDs, String dsLabel,
            String mimeType, String formatURI, InputStream dsContent,
            String checksumType, String checksum, 
            String logMessage, boolean force) throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_MODIFY_DATASTREAM_BY_VALUE, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_DS_ID, datastreamID);
            cje.addArgument(ARGUMENT_NAME_ALT_IDS, altIDs);
            cje.addArgument(ARGUMENT_NAME_DS_LABEL, dsLabel);
            cje.addArgument(ARGUMENT_NAME_MIME_TYPE, mimeType);
            cje.addArgument(ARGUMENT_NAME_FORMAT_URI, formatURI);
            cje.addArgument(ARGUMENT_NAME_DS_CONTENT, dsContent);
            cje.addArgument(ARGUMENT_NAME_CHECKSUM_TYPE, checksumType);
            cje.addArgument(ARGUMENT_NAME_CHECKSUM, checksum);
            cje.addArgument(ARGUMENT_NAME_LOG_MESSAGE, logMessage);
            cje.addArgument(ARGUMENT_NAME_FORCE, force);
            return (Date) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public Date modifyDatastreamByReference(Context context, String pid,
            String datastreamID, String[] altIDs, String dsLabel,
            String mimeType, String formatURI, String dsLocation, 
            String checksumType, String checksum, 
            String logMessage, boolean force) throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_MODIFY_DATASTREAM_BY_REFERENCE, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_DS_ID, datastreamID);
            cje.addArgument(ARGUMENT_NAME_ALT_IDS, altIDs);
            cje.addArgument(ARGUMENT_NAME_DS_LABEL, dsLabel);
            cje.addArgument(ARGUMENT_NAME_MIME_TYPE, mimeType);
            cje.addArgument(ARGUMENT_NAME_FORMAT_URI, formatURI);
            cje.addArgument(ARGUMENT_NAME_DS_LOCATION, dsLocation);
            cje.addArgument(ARGUMENT_NAME_CHECKSUM_TYPE, checksumType);
            cje.addArgument(ARGUMENT_NAME_CHECKSUM, checksum);
            cje.addArgument(ARGUMENT_NAME_LOG_MESSAGE, logMessage);
            cje.addArgument(ARGUMENT_NAME_FORCE, force);
            return (Date) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public Date setDatastreamState(Context context, String pid, String dsID,
            String dsState, String logMessage) throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_SET_DATASTREAM_STATE, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_DS_ID, dsID);
            cje.addArgument(ARGUMENT_NAME_DS_STATE, dsState);
            cje.addArgument(ARGUMENT_NAME_LOG_MESSAGE, logMessage);
            return (Date) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public Date setDatastreamVersionable(Context context, String pid, 
            String dsID, boolean versionable, String logMessage) 
            throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_SET_DATASTREAM_VERSIONABLE, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_DS_ID, dsID);
            cje.addArgument(ARGUMENT_NAME_VERSIONABLE, versionable);
            cje.addArgument(ARGUMENT_NAME_LOG_MESSAGE, logMessage);
            return (Date) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }
    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public String setDatastreamChecksum(Context context, String pid, 
            String dsID, String algorithm) 
            throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_SET_DATASTREAM_VERSIONABLE, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_DS_ID, dsID);
            cje.addArgument(ARGUMENT_NAME_CHECKSUM_TYPE, algorithm);
            return (String) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }
    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public String compareDatastreamChecksum(Context context, String pid, 
            String dsID, String versionDate) 
            throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_SET_DATASTREAM_VERSIONABLE, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_DS_ID, dsID);
            cje.addArgument(ARGUMENT_NAME_VERSION_DATE, versionDate);
            return (String) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public Date[] purgeDatastream(Context context, String pid,
            String datastreamID, Date startDT, Date endDT, String logMessage, 
            boolean force)
            throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_PURGE_DATASTREAM, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_DS_ID, datastreamID);
            cje.addArgument(ARGUMENT_NAME_START_DATE, startDT);
            cje.addArgument(ARGUMENT_NAME_END_DATE, endDT);
            cje.addArgument(ARGUMENT_NAME_LOG_MESSAGE, logMessage);
            cje.addArgument(ARGUMENT_NAME_FORCE, force);
            return (Date[]) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public String addDisseminator(Context context, String pid, String bDefPID,
            String bMechPid, String dissLabel, DSBindingMap bindingMap,
            String dissState, String logMessage) throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_ADD_DISSEMINATOR, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_BDEF_PID, bDefPID);
            cje.addArgument(ARGUMENT_NAME_BMECH_PID, bMechPid);
            cje.addArgument(ARGUMENT_NAME_DISSEMINATOR_LABEL, dissLabel);
            cje.addArgument(ARGUMENT_NAME_BINDING_MAP, bindingMap);
            cje.addArgument(ARGUMENT_NAME_DISSEMINATOR_STATE, dissState);
            cje.addArgument(ARGUMENT_NAME_LOG_MESSAGE, logMessage);
            return (String) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public Date modifyDisseminator(Context context, String pid,
            String disseminatorID, String bMechPid, String dissLabel,
            DSBindingMap bindingMap, String dissState, String logMessage,
            boolean force) throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_MODIFY_DISSEMINATOR, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_DISSEMINATOR_ID, disseminatorID);
            cje.addArgument(ARGUMENT_NAME_BMECH_PID, bMechPid);
            cje.addArgument(ARGUMENT_NAME_DISSEMINATOR_LABEL, dissLabel);
            cje.addArgument(ARGUMENT_NAME_BINDING_MAP, bindingMap);
            cje.addArgument(ARGUMENT_NAME_DISSEMINATOR_STATE, dissState);
            cje.addArgument(ARGUMENT_NAME_LOG_MESSAGE, logMessage);
            cje.addArgument(ARGUMENT_NAME_FORCE, force);
            return (Date) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public Date setDisseminatorState(Context context, String pid,
            String disseminatorID, String dissState, String logMessage)
            throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_SET_DISSEMINATOR_STATE, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_DISSEMINATOR_ID, disseminatorID);
            cje.addArgument(ARGUMENT_NAME_DISSEMINATOR_STATE, dissState);
            cje.addArgument(ARGUMENT_NAME_LOG_MESSAGE, logMessage);
            return (Date) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public Date[] purgeDisseminator(Context context, String pid,
            String disseminatorID, Date endDT, String logMessage)
            throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_PURGE_DISSEMINATOR, context);
            cje.addArgument(ARGUMENT_NAME_PID, pid);
            cje.addArgument(ARGUMENT_NAME_DISSEMINATOR_ID, disseminatorID);
            cje.addArgument(ARGUMENT_NAME_END_DATE, endDT);
            cje.addArgument(ARGUMENT_NAME_LOG_MESSAGE, logMessage);
            return (Date[]) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public String putTempStream(Context context, InputStream in)
            throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_PUT_TEMP_STREAM, context);
            cje.addArgument(ARGUMENT_NAME_IN, in);
            return (String) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    /**
     * Create a journal entry, add the arguments, and invoke the method.
     */
    public String[] getNextPID(Context context, int numPIDs, String namespace)
            throws ServerException {
        try {
            CreatorJournalEntry cje = new CreatorJournalEntry(
                    METHOD_GET_NEXT_PID, context);
            cje.addArgument(ARGUMENT_NAME_NUM_PIDS, numPIDs);
            cje.addArgument(ARGUMENT_NAME_NAMESPACE, namespace);
            return (String[]) cje.invokeMethod(delegate, writer);
        } catch (JournalException e) {
            throw new GeneralException("Problem creating the Journal", e);
        }
    }

    //
    // -------------------------------------------------------------------------
    // 
    // For read-only methods, don't bother with a Journal entry.
    // 
    // -------------------------------------------------------------------------
    //

    /**
     * Let the delegate do it.
     */
    public Property[] getObjectProperties(Context context, String pid)
            throws ServerException {
        return delegate.getObjectProperties(context, pid);
    }

    /**
     * Let the delegate do it.
     */
    public InputStream getObjectXML(Context context, String pid, String encoding)
            throws ServerException {
        return delegate.getObjectXML(context, pid, encoding);
    }

    /**
     * Let the delegate do it.
     */
    public InputStream exportObject(Context context, String pid, String format,
            String exportContext, String encoding) throws ServerException {
        return delegate.exportObject(context, pid, format, exportContext,
                encoding);
    }

    /**
     * Let the delegate do it.
     */
    public Datastream getDatastream(Context context, String pid,
            String datastreamID, Date asOfDateTime) throws ServerException {
        return delegate.getDatastream(context, pid, datastreamID, asOfDateTime);
    }

    /**
     * Let the delegate do it.
     */
    public Datastream[] getDatastreams(Context context, String pid,
            Date asOfDateTime, String dsState) throws ServerException {
        return delegate.getDatastreams(context, pid, asOfDateTime, dsState);
    }

    /**
     * Let the delegate do it.
     */
    public Datastream[] getDatastreamHistory(Context context, String pid,
            String datastreamID) throws ServerException {
        return delegate.getDatastreamHistory(context, pid, datastreamID);
    }

    /**
     * Let the delegate do it.
     */
    public Disseminator getDisseminator(Context context, String pid,
            String disseminatorID, Date asOfDateTime) throws ServerException {
        return delegate.getDisseminator(context, pid, disseminatorID,
                asOfDateTime);
    }

    /**
     * Let the delegate do it.
     */
    public Disseminator[] getDisseminators(Context context, String pid,
            Date asOfDateTime, String dissState) throws ServerException {
        return delegate.getDisseminators(context, pid, asOfDateTime, dissState);
    }

    /**
     * Let the delegate do it.
     */
    public Disseminator[] getDisseminatorHistory(Context context, String pid,
            String disseminatorID) throws ServerException {
        return delegate.getDisseminatorHistory(context, pid, disseminatorID);
    }

    /**
     * Let the delegate do it.
     */
    public InputStream getTempStream(String id) throws ServerException {
        return delegate.getTempStream(id);
    }

    /**
     * Let the delegate do it.
     */
    public boolean adminPing(Context context) throws ServerException {
        return delegate.adminPing(context);
    }

}
