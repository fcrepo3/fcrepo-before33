
package fedora.server.journal.managementmethods;

import fedora.common.Constants;

import fedora.server.errors.ServerException;
import fedora.server.journal.JournalException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * <p>
 * <b>Title:</b> IngestObjectMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.ingestObject()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: IngestObjectMethod.java 5025 2006-09-01 22:08:17 +0000 (Fri, 01
 *          Sep 2006) cwilper $
 */

public class IngestObjectMethod
        extends ManagementMethod {

    public IngestObjectMethod(JournalEntry parent) {
        super(parent);
    }

    @Override
    public Object invoke(ManagementDelegate delegate) throws ServerException,
            JournalException {
        String pid =
                delegate.ingestObject(parent.getContext(), parent
                        .getStreamArgument(ARGUMENT_NAME_SERIALIZATION), parent
                        .getStringArgument(ARGUMENT_NAME_LOG_MESSAGE), parent
                        .getStringArgument(ARGUMENT_NAME_FORMAT), parent
                        .getStringArgument(ARGUMENT_NAME_ENCODING), parent
                        .getBooleanArgument(ARGUMENT_NAME_NEW_PID));

        // Store the PID for writing to the journal.
        parent.setRecoveryValue(Constants.RECOVERY.PID.uri, pid);

        return pid;
    }

}
