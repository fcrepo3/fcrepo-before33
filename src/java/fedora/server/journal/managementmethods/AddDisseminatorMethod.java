package fedora.server.journal.managementmethods;

import fedora.common.Constants;
import fedora.server.errors.ServerException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * 
 * <p>
 * <b>Title:</b> AddDisseminatorMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.addDisseminator()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class AddDisseminatorMethod extends ManagementMethod {

    public AddDisseminatorMethod(JournalEntry parent) {
        super(parent);
    }

    public Object invoke(ManagementDelegate delegate) throws ServerException {
        String disseminatorId = delegate.addDisseminator(parent.getContext(),
                parent.getStringArgument(ARGUMENT_NAME_PID), parent
                        .getStringArgument(ARGUMENT_NAME_BDEF_PID), parent
                        .getStringArgument(ARGUMENT_NAME_BMECH_PID), parent
                        .getStringArgument(ARGUMENT_NAME_DISSEMINATOR_LABEL),
                parent.getDSBindingMapArgument(ARGUMENT_NAME_BINDING_MAP),
                parent.getStringArgument(ARGUMENT_NAME_DISSEMINATOR_STATE),
                parent.getStringArgument(ARGUMENT_NAME_LOG_MESSAGE));

        // Store the Disseminator ID for writing to the journal.
        parent.setRecoveryValue(Constants.RECOVERY.DISSEMINATOR_ID.uri,
                disseminatorId);

        return disseminatorId;
    }

}
