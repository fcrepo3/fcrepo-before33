
package fedora.server.journal.managementmethods;

import fedora.server.errors.ServerException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * <p>
 * <b>Title:</b> SetDisseminatorStateMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.setDisseminatorState()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: SetDisseminatorStateMethod.java 5025 2006-09-01 22:08:17 +0000
 *          (Fri, 01 Sep 2006) cwilper $
 */

public class SetDisseminatorStateMethod
        extends ManagementMethod {

    public SetDisseminatorStateMethod(JournalEntry parent) {
        super(parent);
    }

    @Override
    public Object invoke(ManagementDelegate delegate) throws ServerException {
        return delegate.setDisseminatorState(parent.getContext(), parent
                .getStringArgument(ARGUMENT_NAME_PID), parent
                .getStringArgument(ARGUMENT_NAME_DISSEMINATOR_ID), parent
                .getStringArgument(ARGUMENT_NAME_DISSEMINATOR_STATE), parent
                .getStringArgument(ARGUMENT_NAME_LOG_MESSAGE));
    }

}
