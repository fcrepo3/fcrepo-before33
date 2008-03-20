
package fedora.server.journal.managementmethods;

import fedora.server.errors.ServerException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * <p>
 * <b>Title:</b> SetDatastreamStateMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.setDatastreamState()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: SetDatastreamStateMethod.java 5025 2006-09-01 22:08:17 +0000
 *          (Fri, 01 Sep 2006) cwilper $
 */

public class SetDatastreamStateMethod
        extends ManagementMethod {

    public SetDatastreamStateMethod(JournalEntry parent) {
        super(parent);
    }

    @Override
    public Object invoke(ManagementDelegate delegate) throws ServerException {
        return delegate.setDatastreamState(parent.getContext(), parent
                .getStringArgument(ARGUMENT_NAME_PID), parent
                .getStringArgument(ARGUMENT_NAME_DS_ID), parent
                .getStringArgument(ARGUMENT_NAME_DS_STATE), parent
                .getStringArgument(ARGUMENT_NAME_LOG_MESSAGE));
    }

}
