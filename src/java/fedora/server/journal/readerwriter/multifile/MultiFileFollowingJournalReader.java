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

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fedora.server.journal.JournalException;
import fedora.server.journal.ServerInterface;
import fedora.server.journal.recoverylog.JournalRecoveryLog;

/**
 * 
 * <p>
 * <b>Title:</b> MultiFileFollowingJournalReader.java
 * </p>
 * <p>
 * <b>Description:</b> A JournalReader implementation for "following" a leading
 * server, when the leading server is using a {@link MultiFileJournalWriter},
 * or the equivalent. The recovery is never complete, as the reader continues to
 * poll for recently-created files, until the server shuts down.
 * </p>
 * <p>
 * This class should likely be superceded by
 * {@link LockingFollowingJournalReader}.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class MultiFileFollowingJournalReader extends MultiFileJournalReader {
    private final long pollingIntervalMillis;

    /**
     * Do the super-class constructor, and then find the polling interval.
     */
    public MultiFileFollowingJournalReader(Map parameters, String role,
            JournalRecoveryLog recoveryLog, ServerInterface server)
            throws JournalException {
        super(parameters, role, recoveryLog, server);
        pollingIntervalMillis = MultiFileJournalHelper
                .parseParametersForPollingInterval(parameters);
    }

    /**
     * Ask for a new file, using the superclass method, but if none is found,
     * wait for a while and ask again. This will continue until we get a server
     * shutdown signal.
     */
    protected synchronized JournalInputFile openNextFile()
            throws JournalException {
        while (open) {
            JournalInputFile nextFile = super.openNextFile();
            if (nextFile != null) {
                return nextFile;
            }
            try {
                wait(pollingIntervalMillis);
            } catch (InterruptedException e) {
                // no special action on interrupt.
            }
        }
        return null;
    }

    /**
     * If the server requests a shutdown, stop waiting the next file to come in.
     */
    public synchronized void shutdown() throws JournalException {
        super.shutdown();
        notifyAll();
    }
}
