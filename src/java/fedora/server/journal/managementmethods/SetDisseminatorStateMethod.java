/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.journal.managementmethods;

import fedora.server.errors.ServerException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * 
 * <p>
 * <b>Title:</b> SetDisseminatorStateMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.setDisseminatorState()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class SetDisseminatorStateMethod extends ManagementMethod {

    public SetDisseminatorStateMethod(JournalEntry parent) {
        super(parent);
    }

    public Object invoke(ManagementDelegate delegate) throws ServerException {
        return delegate.setDisseminatorState(parent.getContext(), parent
                .getStringArgument(ARGUMENT_NAME_PID), parent
                .getStringArgument(ARGUMENT_NAME_DISSEMINATOR_ID), parent
                .getStringArgument(ARGUMENT_NAME_DISSEMINATOR_STATE), parent
                .getStringArgument(ARGUMENT_NAME_LOG_MESSAGE));
    }

}
