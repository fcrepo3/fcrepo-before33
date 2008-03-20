
package fedora.server.journal.managementmethods;

import fedora.server.errors.ServerException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * <p>
 * <b>Title:</b> PurgeObjectMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.purgeObject()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: PurgeObjectMethod.java 5025 2006-09-01 22:08:17 +0000 (Fri, 01
 *          Sep 2006) cwilper $
 */

public class PurgeObjectMethod
        extends ManagementMethod {

    public PurgeObjectMethod(JournalEntry parent) {
        super(parent);
    }

    @Override
    public Object invoke(ManagementDelegate delegate) throws ServerException {
        return delegate.purgeObject(parent.getContext(), parent
                .getStringArgument(ARGUMENT_NAME_PID), parent
                .getStringArgument(ARGUMENT_NAME_LOG_MESSAGE), parent
                .getBooleanArgument(ARGUMENT_NAME_FORCE));
    }

}
