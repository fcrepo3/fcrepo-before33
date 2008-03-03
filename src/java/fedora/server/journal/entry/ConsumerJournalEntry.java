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
        super.getMethod().invoke(delegate);
        recoveryLog.log("Call complete:" + super.getMethodName());
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "ConsumerJournalEntry[identifier=" + identifier
                + ", methodName=" + getMethodName() + ", context="
                + getContext() + "]";
    }

}
