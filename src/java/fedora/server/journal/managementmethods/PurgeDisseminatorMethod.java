package fedora.server.journal.managementmethods;

import fedora.server.errors.ServerException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * 
 * <p>
 * <b>Title:</b> PurgeDisseminatorMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.purgeDisseminator()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class PurgeDisseminatorMethod extends ManagementMethod {

    public PurgeDisseminatorMethod(JournalEntry parent) {
        super(parent);
    }

    public Object invoke(ManagementDelegate delegate) throws ServerException {
        return delegate.purgeDisseminator(parent.getContext(), parent
                .getStringArgument(ARGUMENT_NAME_PID), parent
                .getStringArgument(ARGUMENT_NAME_DISSEMINATOR_ID), parent
                .getDateArgument(ARGUMENT_NAME_END_DATE), parent
                .getStringArgument(ARGUMENT_NAME_LOG_MESSAGE));
    }

}
