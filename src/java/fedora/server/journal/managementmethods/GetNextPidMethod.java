
package fedora.server.journal.managementmethods;

import fedora.common.Constants;

import fedora.server.errors.ServerException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * <p>
 * <b>Title:</b> GetNextPidMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.getNextPID()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: GetNextPidMethod.java 5025 2006-09-01 22:08:17 +0000 (Fri, 01
 *          Sep 2006) cwilper $
 */

public class GetNextPidMethod
        extends ManagementMethod {

    public GetNextPidMethod(JournalEntry parent) {
        super(parent);
    }

    @Override
    public Object invoke(ManagementDelegate delegate) throws ServerException {
        String[] pidList =
                delegate.getNextPID(parent.getContext(), parent
                        .getIntegerArgument(ARGUMENT_NAME_NUM_PIDS), parent
                        .getStringArgument(ARGUMENT_NAME_NAMESPACE));

        // Store the list of PIDs for writing to the journal.
        parent.setRecoveryValues(Constants.RECOVERY.PID_LIST.uri, pidList);

        return pidList;
    }

}
