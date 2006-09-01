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
import java.io.StringWriter;
import java.util.Map;

import fedora.server.errors.ModuleInitializationException;
import fedora.server.journal.ServerInterface;
import fedora.server.journal.helpers.JournalHelper;

/**
 * 
 * <p>
 * <b>Title:</b> BufferedJournalRecoveryLog.java
 * </p>
 * <p>
 * <b>Description:</b> A simple implementation of JournalRecoveryLog that keeps
 * the entire log in a StringBuffer, and writes it to a file on shutdown().
 * </p>
 * This is memory-intensive, so it should only be used for System Tests, where 
 * the presence of the log file can be treated as a signal that the recovery is
 * complete.
 * <p>
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class BufferedJournalRecoveryLog extends JournalRecoveryLog {
    private final File logFile;
    private final StringWriter buffer;

    private boolean open = true;

    /**
     * Get the name of the log file from the server parameters, but don't create
     * the file yet. Just create the StringWriter that we will use as a buffer.
     */
    public BufferedJournalRecoveryLog(Map parameters, String role, ServerInterface server)
            throws ModuleInitializationException {
        super(parameters, role, server);

        this.buffer = new StringWriter();

        if (!parameters.containsKey(PARAMETER_RECOVERY_LOG_FILENAME)) {
            throw new ModuleInitializationException("Parameter '"
                    + PARAMETER_RECOVERY_LOG_FILENAME + "' is not set.", role);
        }
        String fileName = (String) parameters
                .get(PARAMETER_RECOVERY_LOG_FILENAME);
        this.logFile = new File(fileName);
        
        super.logHeaderInfo(parameters);
    }

    /**
     * Any request to log a message just adds it to the buffer in the
     * StringWriter (if shutdown has not been called).
     */
    public synchronized void log(String message) {
        if (open) {
            log(message, buffer);
        }
    }

    /**
     * On the first call to this method, write the buffer to the log file. Set
     * the flag so no more logging calls will be accepted.
     */
    public synchronized void shutdown() {
        try {
            if (open) {
                open = false;
                FileWriter logWriter = new FileWriter(logFile);
                logWriter.write(buffer.toString());
                logWriter.close();
            }
        } catch (IOException e) {
            server.logSevere(JournalHelper.captureStackTrace(e));
        }
    }

    public String toString() {
        return super.toString() + ", logFile='" + this.logFile.getPath() + "'";
    }

}
