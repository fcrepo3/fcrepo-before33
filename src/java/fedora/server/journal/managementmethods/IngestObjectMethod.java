/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.journal.managementmethods;

import fedora.common.Constants;

import fedora.server.errors.ServerException;
import fedora.server.journal.JournalException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * Adapter class for Management.ingestObject()
 * 
 * @author Jim Blake
 * @deprecated in Fedora 3.0, use IngestMethod instead
 */
@Deprecated
public class IngestObjectMethod
        extends ManagementMethod {

    public IngestObjectMethod(JournalEntry parent) {
        super(parent);
    }

    @Override
    public Object invoke(ManagementDelegate delegate) throws ServerException,
            JournalException {
        String pid =
                delegate.ingest(parent.getContext(), parent
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
