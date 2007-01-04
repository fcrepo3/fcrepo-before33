package fedora.server.journal.entry;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.journal.JournalException;
import fedora.server.journal.JournalWriter;
import fedora.server.management.ManagementDelegate;

/**
 * 
 * <p>
 * <b>Title:</b> CreatorJournalEntry.java
 * </p>
 * <p>
 * <b>Description:</b> The JournalEntry to use when creating a journal file.
 * When invoking the management method, take a moment to write to the journal
 * before returning.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class CreatorJournalEntry extends JournalEntry {
    /**
     * Don't store the Context that was given; store a writable version of it.
     */
    public CreatorJournalEntry(String methodName, Context context) {
        super(methodName, new JournalEntryContext(context));
    }

    /**
     * Process the management method:
     * <ul>
     * <li>prepare the writer in case we need to initialize a new file with a
     * repository hash</li>
     * <li>invoke the method on the ManagementDelegate</li>
     * <li>write the full journal entry, including any context changes from the
     * Management method</li>
     * </ul>
     * Note that these operations occur within a synchronized block. We must be
     * sure that any pending operations are complete before we get the
     * repository hash, so we are confident that the hash accurately reflects
     * the state of the repository. Since all API-M operations go through this
     * synchronized block, we can be confident that the previous one had
     * completed before the current one started.
     * <p>
     * Note also - there might be a way to enforce this at a lower level, thus
     * increasing throughput, but we haven't explored it yet.
     */
    public Object invokeMethod(ManagementDelegate delegate, JournalWriter writer)
            throws ServerException, JournalException {
        synchronized (JournalWriter.SYNCHRONIZER) {
            writer.prepareToWriteJournalEntry();
            Object result = super.getMethod().invoke(delegate);
            writer.writeJournalEntry(this);
            return result;
        }
    }

    /**
     * A convenience method that invokes the management method and then closes
     * the JournalEntry, thereby cleaning up any temp files.
     */
    public Object invokeAndClose(ManagementDelegate delegate,
            JournalWriter writer) throws ServerException, JournalException {
        Object result = this.invokeMethod(delegate, writer);
        this.close();
        return result;
    }

}
