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
        synchronized (CreatorJournalEntry.class) {
            writer.prepareToWriteJournalEntry();
            Object result = this.method.invoke(delegate);
            writer.writeJournalEntry(this);
            return result;
        }
    }

}
