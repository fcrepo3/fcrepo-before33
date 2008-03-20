
package fedora.server.journal.managementmethods;

import fedora.server.errors.ServerException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * <p>
 * <b>Title:</b> PurgeDatastreamMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.purgeDatastream()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: PurgeDatastreamMethod.java 5046 2006-09-05 18:11:02 +0000 (Tue,
 *          05 Sep 2006) cwilper $
 */

public class PurgeDatastreamMethod
        extends ManagementMethod {

    public PurgeDatastreamMethod(JournalEntry parent) {
        super(parent);
    }

    @Override
    public Object invoke(ManagementDelegate delegate) throws ServerException {
        return delegate.purgeDatastream(parent.getContext(), parent
                .getStringArgument(ARGUMENT_NAME_PID), parent
                .getStringArgument(ARGUMENT_NAME_DS_ID), parent
                .getDateArgument(ARGUMENT_NAME_START_DATE), parent
                .getDateArgument(ARGUMENT_NAME_END_DATE), parent
                .getStringArgument(ARGUMENT_NAME_LOG_MESSAGE), parent
                .getBooleanArgument(ARGUMENT_NAME_FORCE));
    }

}
