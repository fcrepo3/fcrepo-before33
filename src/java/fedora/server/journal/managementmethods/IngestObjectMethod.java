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
 * 
 * <p>
 * <b>Title:</b> IngestObjectMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.ingestObject()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class IngestObjectMethod extends ManagementMethod {

    public IngestObjectMethod(JournalEntry parent) {
        super(parent);
    }

    public Object invoke(ManagementDelegate delegate)
            throws ServerException, JournalException {
        String pid = delegate.ingestObject(parent.getContext(), parent
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
