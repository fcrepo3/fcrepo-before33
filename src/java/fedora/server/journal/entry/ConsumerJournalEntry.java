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

import fedora.server.errors.ServerException;
import fedora.server.journal.JournalException;
import fedora.server.journal.recoverylog.JournalRecoveryLog;
import fedora.server.management.ManagementDelegate;

/**
 * 
 * <p>
 * <b>Title:</b> ConsumerJournalEntry.java
 * </p>
 * <p>
 * <b>Description:</b> The JournalEntry to use when consuming a journal file.
 * Before invoking a method, write the entry to the recovery log. After invoking
 * the method, log a completion message.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class ConsumerJournalEntry extends JournalEntry {
    private String identifier = "no identifier";

    public ConsumerJournalEntry(String methodName, JournalEntryContext context) {
        super(methodName, context);
    }

    public void invokeMethod(ManagementDelegate delegate,
            JournalRecoveryLog recoveryLog)
            throws ServerException, JournalException {
        recoveryLog.log(this);
        this.method.invoke(delegate);
        recoveryLog.log("Call complete:" + methodName);
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getIdentifier() {
        return identifier;
    }

}
