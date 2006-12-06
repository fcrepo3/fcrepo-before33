package fedora.server.journal;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import fedora.server.Context;
import fedora.server.errors.InvalidStateException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;
import fedora.server.errors.ServerException;
import fedora.server.journal.recoverylog.JournalRecoveryLog;
import fedora.server.management.ManagementDelegate;
import fedora.server.storage.types.DSBindingMap;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.Property;

/**
 * 
 * 
 * <p>
 * <b>Title:</b> JournalConsumer.java
 * </p>
 * <p>
 * <b>Description:</b> The JournalWorker class to use in recovery mode or in
 * following mode.
 * </p>
 * <p>
 * Create a <code>JournalConsumerThread</code> to process the journal. If any
 * calls to Management methods come in from outside, reject them.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */
public class JournalConsumer implements JournalWorker {
    private final ServerInterface server;
    private final String role;
    private final JournalConsumerThread consumerThread;
    private final JournalReader reader;
    private final JournalRecoveryLog recoveryLog;

    /**
     * Get the appropriate JournalReader and JournalRecoveryLog, based on the
     * server parameters, and create a JournalConsumerThread that will process
     * the journal entries, using that reader and that log.
     */
    public JournalConsumer(Map parameters, String role, ServerInterface server)
            throws ModuleInitializationException {
        this.server = server;
        this.role = role;
        this.recoveryLog = JournalRecoveryLog.getInstance(parameters, role,
                server);
        this.reader = JournalReader.getInstance(parameters, role, recoveryLog,
                server);
        this.consumerThread = new JournalConsumerThread(parameters, role,
                server, reader, recoveryLog);
    }

    /**
     * Get the ManagementDelegate module and pass it to the
     * JournalConsumerThread, so it can start working.
     */
    public void setManagementDelegate(ManagementDelegate delegate) {
        this.consumerThread.setManagementDelegate(delegate);
    }

    /**
     * Tell the thread, the reader and the log to shut down.
     */
    public void shutdown() throws ModuleShutdownException {
        try {
            consumerThread.shutdown();
            reader.shutdown();
            recoveryLog.shutdown("Server is shutting down.");
        } catch (JournalException e) {
            throw new ModuleShutdownException("Error closing journal reader.",
                    role, e);
        }
    }

    //
    // -------------------------------------------------------------------------
    // 
    // Reject any outside calls to the Management API methods.
    // 
    // -------------------------------------------------------------------------
    //

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public String ingestObject(Context context, InputStream serialization,
            String logMessage, String format, String encoding, boolean newPid)
            throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Date modifyObject(Context context, String pid, String state,
            String label, String ownerId, String logMessage) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Property[] getObjectProperties(Context context, String pid)
            throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public InputStream getObjectXML(Context context, String pid, String encoding)
            throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public InputStream exportObject(Context context, String pid, String format,
            String exportContext, String encoding) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Date purgeObject(Context context, String pid, String logMessage,
            boolean force) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public String addDatastream(Context context, String pid, String dsID,
            String[] altIDs, String dsLabel, boolean versionable,
            String MIMEType, String formatURI, String location,
            String controlGroup, String dsState, String checksumType, 
            String checksum, String logMessage)
            throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Date modifyDatastreamByReference(Context context, String pid,
            String datastreamID, String[] altIDs, String dsLabel,
            String mimeType, String formatURI, String dsLocation, 
            String checksumType, String checksum, String logMessage, 
            boolean force)
            throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Date modifyDatastreamByValue(Context context, String pid,
            String datastreamID, String[] altIDs, String dsLabel,
            String mimeType, String formatURI, InputStream dsContent, 
            String checksumType, String checksum, String logMessage, 
            boolean force)
            throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Date[] purgeDatastream(Context context, String pid,
            String datastreamID, Date startDT, Date endDT, String logMessage, 
            boolean force)
            throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Datastream getDatastream(Context context, String pid,
            String datastreamID, Date asOfDateTime) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Datastream[] getDatastreams(Context context, String pid,
            Date asOfDateTime, String dsState) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Datastream[] getDatastreamHistory(Context context, String pid,
            String datastreamID) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public String addDisseminator(Context context, String pid, String bDefPID,
            String bMechPid, String dissLabel, DSBindingMap bindingMap,
            String dissState, String logMessage) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Date modifyDisseminator(Context context, String pid,
            String disseminatorID, String bMechPid, String dissLabel,
            DSBindingMap bindingMap, String dissState, String logMessage,
            boolean force) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Date[] purgeDisseminator(Context context, String pid,
            String disseminatorID, Date endDT, String logMessage)
            throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Disseminator getDisseminator(Context context, String pid,
            String disseminatorID, Date asOfDateTime) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Disseminator[] getDisseminators(Context context, String pid,
            Date asOfDateTime, String dissState) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Disseminator[] getDisseminatorHistory(Context context, String pid,
            String disseminatorID) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public String putTempStream(Context context, InputStream in)
            throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public InputStream getTempStream(String id) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Date setDatastreamState(Context context, String pid, String dsID,
            String dsState, String logMessage) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Date setDatastreamVersionable(Context context, String pid, 
            String dsID, boolean versionable, String logMessage) 
        throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public String compareDatastreamChecksum(Context context, String pid, 
            String dsID, Date versionDate) 
        throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public Date setDisseminatorState(Context context, String pid, String dsID,
            String dsState, String logMessage) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public String[] getNextPID(Context context, int numPIDs, String namespace)
            throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * Reject API calls from outside while we are in recovery mode.
     */
    public boolean adminPing(Context context) throws ServerException {
        throw rejectCallsFromOutsideWhileInRecoveryMode();
    }

    /**
     * While the server is reading a Journal to recover its state, block any
     * attempt to use the Management API.
     * 
     * @throws ServerException
     */
    private ServerException rejectCallsFromOutsideWhileInRecoveryMode() {
        return new InvalidStateException("Server is in Journal Recovery mode.");
    }

}
