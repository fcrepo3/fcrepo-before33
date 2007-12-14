package fedora.server.journal.readerwriter.multifile;

import java.io.InputStream;
import java.util.Date;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.management.ManagementDelegate;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Property;
import fedora.server.storage.types.RelationshipTuple;

class MockManagementDelegateForJournalTesting implements ManagementDelegate {

    // -------------------------------------------------------------------------
    // Mocking infrastructure.
    // -------------------------------------------------------------------------

    private int ingestCalls = 0;
    private Runnable ingestOperation;

    public void setIngestOperation(Runnable ingestOperation) {
        this.ingestOperation = ingestOperation;
    }

    public int getIngestCalls() {
        return ingestCalls;
    }

    // -------------------------------------------------------------------------
    // Mocked methods.
    // -------------------------------------------------------------------------

    /**
     * Increment the count of ingested objects. If an ingest operation has been
     * requested, run it before completing this call.
     */
    public String ingestObject(Context context, InputStream serialization,
            String logMessage, String format, String encoding, boolean newPid)
            throws ServerException {
        ingestCalls++;
        if (ingestOperation != null) {
            ingestOperation.run();
        }
        return "IngestObject:" + ingestCalls;
    }

    // -------------------------------------------------------------------------
    // Non-implemented methods.
    // -------------------------------------------------------------------------

    public Date modifyObject(Context context, String pid, String state,
            String label, String ownerId, String logMessage) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.modifyObject not implemented"); // KLUGE

    }

    public Property[] getObjectProperties(Context context, String pid)
            throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.getObjectProperties not implemented"); // KLUGE

    }

    public InputStream getObjectXML(Context context, String pid, String encoding)
            throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.getObjectXML not implemented"); // KLUGE

    }

    public InputStream exportObject(Context context, String pid, String format,
            String exportContext, String encoding) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.exportObject not implemented"); // KLUGE

    }

    public Date purgeObject(Context context, String pid, String logMessage,
            boolean force) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.purgeObject not implemented"); // KLUGE

    }

    public String addDatastream(Context context, String pid, String dsID,
            String[] altIDs, String dsLabel, boolean versionable,
            String MIMEType, String formatURI, String location,
            String controlGroup, String dsState, String checksumType,
            String checksum, String logMessage)
            throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.addDatastream not implemented"); // KLUGE

    }

    public Date modifyDatastreamByReference(Context context, String pid,
            String datastreamID, String[] altIDs, String dsLabel,
            String mimeType, String formatURI, String dsLocation, 
            String checksumType, String checksum, String logMessage,
            boolean force)
            throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.modifyDatastreamByReference not implemented"); // KLUGE

    }

    public Date modifyDatastreamByValue(Context context, String pid,
            String datastreamID, String[] altIDs, String dsLabel,
            String mimeType, String formatURI,InputStream dsContent,
            String checksumType, String checksum, String logMessage,
            boolean force) 
            throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.modifyDatastreamByValue not implemented"); // KLUGE

    }

    public Date[] purgeDatastream(Context context, String pid,
            String datastreamID, Date endDT, String logMessage, boolean force)
            throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.purgeDatastream not implemented"); // KLUGE

    }

    public Datastream getDatastream(Context context, String pid,
            String datastreamID, Date asOfDateTime) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.getDatastream not implemented"); // KLUGE

    }

    public Datastream[] getDatastreams(Context context, String pid,
            Date asOfDateTime, String dsState) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.getDatastreams not implemented"); // KLUGE

    }

    public Datastream[] getDatastreamHistory(Context context, String pid,
            String datastreamID) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.getDatastreamHistory not implemented"); // KLUGE

    }

    public String putTempStream(Context context, InputStream in)
            throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.putTempStream not implemented"); // KLUGE

    }

    public InputStream getTempStream(String id) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.getTempStream not implemented"); // KLUGE

    }

    public Date setDatastreamState(Context context, String pid, String dsID,
            String dsState, String logMessage) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.setDatastreamState not implemented"); // KLUGE

    }

    public Date setDisseminatorState(Context context, String pid, String dsID,
            String dsState, String logMessage) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.setDisseminatorState not implemented"); // KLUGE

    }

    public String[] getNextPID(Context context, int numPIDs, String namespace)
            throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.getNextPID not implemented"); // KLUGE

    }

    public boolean adminPing(Context context) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.adminPing not implemented"); // KLUGE

    }

	public Date modifyDatastreamByReference(Context context, String pid, String datastreamID, String[] altIDs, String dsLabel, String mimeType, String formatURI, String dsLocation, String logMessage, boolean force) throws ServerException {
        throw new RuntimeException(
        "MockManagementDelegateForJournalTesting.modifyDatastreamByReference not implemented"); // KLUGE
	}

	public Date modifyDatastreamByValue(Context context, String pid, String datastreamID, String[] altIDs, String dsLabel, String mimeType, String formatURI, InputStream dsContent, String logMessage, boolean force) throws ServerException {
        throw new RuntimeException(
        "MockManagementDelegateForJournalTesting.modifyDatastreamByValue not implemented"); // KLUGE
	}

	public Date[] purgeDatastream(Context context, String pid, String datastreamID, Date startDT, Date endDT, String logMessage, boolean force) throws ServerException {
        throw new RuntimeException(
        "MockManagementDelegateForJournalTesting.purgeDatastream not implemented"); // KLUGE
	}

	public Date setDatastreamVersionable(Context context, String pid, String dsID, boolean versionable, String logMessage) throws ServerException {
        throw new RuntimeException(
        "MockManagementDelegateForJournalTesting.setDatastreamVersionable not implemented"); // KLUGE
	}
       
    public String compareDatastreamChecksum(Context context, String pid, String dsID, Date versionDate) throws ServerException {
        throw new RuntimeException(
        "MockManagementDelegateForJournalTesting.compareDatastreamChecksum not implemented"); // KLUGE
    }

    public RelationshipTuple[] getRelationships(Context context, String pid, String relationship) throws ServerException
    {
        throw new RuntimeException(
        "MockManagementDelegateForJournalTesting.getRelationships not implemented"); // KLUGE
    }

    public boolean addRelationship(Context context, String pid, String relationship, String object, boolean isLiteral, String datatype) throws ServerException
    {
        throw new RuntimeException(
        "MockManagementDelegateForJournalTesting.addRelationship not implemented"); // KLUGE
    }

    public boolean purgeRelationship(Context context, String pid, String relationship, String object, boolean isLiteral, String datatype) throws ServerException
    {
        throw new RuntimeException(
        "MockManagementDelegateForJournalTesting.purgeRelationship not implemented"); // KLUGE
    }

}