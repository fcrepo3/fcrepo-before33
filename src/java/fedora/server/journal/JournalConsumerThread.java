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

package fedora.server.journal;

import java.util.Map;

import fedora.server.journal.entry.ConsumerJournalEntry;
import fedora.server.journal.helpers.JournalHelper;
import fedora.server.journal.recoverylog.JournalRecoveryLog;
import fedora.server.management.ManagementDelegate;

/**
 * 
 * <p>
 * <b>Title:</b> JournalConsumerThread.java
 * </p>
 * <p>
 * <b>Description:</b> Process the journal entries as a separate Thread, while
 * the JournalConsumer is blocking all calls from outside.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */
public class JournalConsumerThread extends Thread {
    private final Map parameters;
    private final String role;
    private final ServerInterface server;
    private final JournalReader reader;
    private final JournalRecoveryLog recoveryLog;
    private ManagementDelegate delegate;
    private boolean shutdown = false;

    /**
     * Store references to all of this stuff, but we can't start work without a
     * ManagementDelegate is provided, and we won't get that until the
     * post-initialization stage.
     */
    public JournalConsumerThread(Map parameters, String role, ServerInterface server,
            JournalReader reader, JournalRecoveryLog recoveryLog) {
        this.parameters = parameters;
        this.role = role;
        this.server = server;
        this.reader = reader;
        this.recoveryLog = recoveryLog;
    }

    /**
     * Now that we have a ManagementDelegate to perform the operations, we can
     * start working.
     */
    public void setManagementDelegate(ManagementDelegate delegate) {
        this.delegate = delegate;
        this.start();
    }

    /**
     * Wait until the server completes its initialization, then process journal
     * entries until the reader says there are no more, or until a shutdown is
     * requested.
     */
    public void run() {
        try {
            waitUntilServerIsInitialized();

            recoveryLog.log("Start recovery.");

            while (true) {
                if (shutdown) {
                    break;
                }
                ConsumerJournalEntry cje = reader.readJournalEntry();
                if (cje == null) {
                    break;
                }
                cje.invokeMethod(delegate, recoveryLog);
            }
            reader.shutdown();

            recoveryLog.log("Recovery complete.");
        } catch (Throwable e) {
            /*
             * It makes sense to catch Exception here, because any uncaught
             * exception will not be reported - there is no console to print the
             * stack trace!
             * 
             * It might not be appropriate to catch Throwable, but it's the only
             * way we can know about missing class files and such. Of course, if
             * we catch an OutOfMemoryError or a VirtualMachineError, all bets
             * are off.
             */
            String stackTrace = JournalHelper.captureStackTrace(e);
            server.logSevere("Error during Journal recovery. " + stackTrace);
            recoveryLog.log("PROBLEM: " + stackTrace);
            recoveryLog.log("Recovery terminated prematurely.");
        } finally {
            recoveryLog.shutdown();
        }
    }

    /**
     * Wait for the server to initialize. If we wait too long, give up and shut
     * down the thread.
     */
    private void waitUntilServerIsInitialized() {
        int i = 0;
        for (; i < 60; i++) {
            if (server.hasInitialized() || shutdown) {
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                server.logSevere("Thread was interrupted");
            }
        }
        server.logSevere("Can't recover from the Journal - "
                + "the server hasn't initialized after " + i + " seconds.");
        shutdown = true;
    }

    /**
     * Set the flag saying that it's time to quit.
     */
    public void shutdown() {
        recoveryLog.log("Shutdown requested by server");
        shutdown = true;
    }
}
