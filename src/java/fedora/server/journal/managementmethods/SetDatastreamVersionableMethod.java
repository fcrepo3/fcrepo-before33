
package fedora.server.journal.managementmethods;

import fedora.server.errors.ServerException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * <p>
 * <b>Title:</b> SetDatastreamVersionableMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.setDatastreamVersionable()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: SetDatastreamVersionableMethod.java 5048 2006-09-05 18:18:28
 *          +0000 (Tue, 05 Sep 2006) cwilper $
 */

public class SetDatastreamVersionableMethod
        extends ManagementMethod {

    public SetDatastreamVersionableMethod(JournalEntry parent) {
        super(parent);
    }

    @Override
    public Object invoke(ManagementDelegate delegate) throws ServerException {
        return delegate.setDatastreamVersionable(parent.getContext(), parent
                .getStringArgument(ARGUMENT_NAME_PID), parent
                .getStringArgument(ARGUMENT_NAME_DS_ID), parent
                .getBooleanArgument(ARGUMENT_NAME_VERSIONABLE), parent
                .getStringArgument(ARGUMENT_NAME_LOG_MESSAGE));
    }

}
