
package fedora.server.journal.managementmethods;

import fedora.server.errors.ServerException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * <p>
 * <b>Title:</b> ModifyObjectMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.modifyObject()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: ModifyObjectMethod.java 5334 2006-12-06 21:21:21 +0000 (Wed, 06
 *          Dec 2006) payette $
 */

public class ModifyObjectMethod
        extends ManagementMethod {

    public ModifyObjectMethod(JournalEntry parent) {
        super(parent);
    }

    @Override
    public Object invoke(ManagementDelegate delegate) throws ServerException {
        return delegate.modifyObject(parent.getContext(), parent
                .getStringArgument(ARGUMENT_NAME_PID), parent
                .getStringArgument(ARGUMENT_NAME_STATE), parent
                .getStringArgument(ARGUMENT_NAME_LABEL), parent
                .getStringArgument(ARGUMENT_NAME_OWNERID), parent
                .getStringArgument(ARGUMENT_NAME_LOG_MESSAGE));
    }

}
