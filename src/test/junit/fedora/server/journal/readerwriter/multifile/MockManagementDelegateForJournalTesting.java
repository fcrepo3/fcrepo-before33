/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2006 by 
 * The Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

package fedora.server.journal.readerwriter.multifile;

import java.io.InputStream;
import java.util.Date;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.management.ManagementDelegate;
import fedora.server.storage.types.DSBindingMap;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.Property;

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
            String label, String logMessage) throws ServerException {
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
            String controlGroup, String dsState, String logMessage)
            throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.addDatastream not implemented"); // KLUGE

    }

    public Date modifyDatastreamByReference(Context context, String pid,
            String datastreamID, String[] altIDs, String dsLabel,
            boolean versionable, String mimeType, String formatURI,
            String dsLocation, String dsState, String logMessage, boolean force)
            throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.modifyDatastreamByReference not implemented"); // KLUGE

    }

    public Date modifyDatastreamByValue(Context context, String pid,
            String datastreamID, String[] altIDs, String dsLabel,
            boolean versionable, String mimeType, String formatURI,
            InputStream dsContent, String dsState, String logMessage,
            boolean force) throws ServerException {
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

    public String addDisseminator(Context context, String pid, String bDefPID,
            String bMechPid, String dissLabel, DSBindingMap bindingMap,
            String dissState, String logMessage) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.addDisseminator not implemented"); // KLUGE

    }

    public Date modifyDisseminator(Context context, String pid,
            String disseminatorID, String bMechPid, String dissLabel,
            DSBindingMap bindingMap, String dissState, String logMessage,
            boolean force) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.modifyDisseminator not implemented"); // KLUGE

    }

    public Date[] purgeDisseminator(Context context, String pid,
            String disseminatorID, Date endDT, String logMessage)
            throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.purgeDisseminator not implemented"); // KLUGE

    }

    public Disseminator getDisseminator(Context context, String pid,
            String disseminatorID, Date asOfDateTime) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.getDisseminator not implemented"); // KLUGE

    }

    public Disseminator[] getDisseminators(Context context, String pid,
            Date asOfDateTime, String dissState) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.getDisseminators not implemented"); // KLUGE

    }

    public Disseminator[] getDisseminatorHistory(Context context, String pid,
            String disseminatorID) throws ServerException {
        throw new RuntimeException(
                "MockManagementDelegateForJournalTesting.getDisseminatorHistory not implemented"); // KLUGE

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

}