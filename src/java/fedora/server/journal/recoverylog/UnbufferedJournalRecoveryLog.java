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

package fedora.server.journal.recoverylog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import fedora.server.errors.ModuleInitializationException;
import fedora.server.journal.ServerInterface;
import fedora.server.journal.helpers.JournalHelper;

/**
 * 
 * <p>
 * <b>Title:</b> UnbufferedJournalRecoveryLog.java
 * </p>
 * <p>
 * <b>Description:</b> A basic implementation of RecoveryLog. All entries are
 * written to a log, which is flushed after each entry so the log will be up to
 * date even if the server crashes.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class UnbufferedJournalRecoveryLog extends JournalRecoveryLog {
    private final File logFile;

    private final FileWriter writer;

    private boolean open = true;

    /**
     * Get the name of the logfile from the server parameters and create the
     * file.
     */
    public UnbufferedJournalRecoveryLog(Map parameters, String role,
            ServerInterface server) throws ModuleInitializationException {
        super(parameters, role, server);

        try {
            if (!parameters.containsKey(PARAMETER_RECOVERY_LOG_FILENAME)) {
                throw new ModuleInitializationException("Parameter '"
                        + PARAMETER_RECOVERY_LOG_FILENAME + "' is not set.",
                        role);
            }
            String fileName = (String) parameters
                    .get(PARAMETER_RECOVERY_LOG_FILENAME);
            this.logFile = new File(fileName);
            this.writer = new FileWriter(this.logFile);

            super.logHeaderInfo(parameters);
        } catch (IOException e) {
            throw new ModuleInitializationException(
                    "Problem writing to the recovery log", role, e);
        }
    }

    /**
     * A request to log a message just writes it to the log file and flushes it
     * (if shutdown has not been called).
     */
    public synchronized void log(String message) {
        try {
            if (open) {
                log(message, writer);
                writer.flush();
            }
        } catch (IOException e) {
            server.logSevere(JournalHelper.captureStackTrace(e));
        }
    }

    /**
     * On the first call to this method, close the log file. Set the flag so no
     * more logging calls will be accepted.
     */
    public synchronized void shutdown() {
        try {
            if (open) {
                open = false;
                writer.close();
            }
        } catch (IOException e) {
            server.logSevere(JournalHelper.captureStackTrace(e));
        }
    }

    public String toString() {
        return super.toString() + ", logFile='" + this.logFile.getPath() + "'";
    }

}
