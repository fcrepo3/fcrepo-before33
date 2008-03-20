
package fedora.server.journal.managementmethods;

import fedora.server.errors.ServerException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * <p>
 * <b>Title:</b> ModifyDisseminatorMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.modifyDisseminator()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: ModifyDisseminatorMethod.java 5025 2006-09-01 22:08:17 +0000
 *          (Fri, 01 Sep 2006) cwilper $
 */

public class ModifyDisseminatorMethod
        extends ManagementMethod {

    public ModifyDisseminatorMethod(JournalEntry parent) {
        super(parent);
    }

    @Override
    public Object invoke(ManagementDelegate delegate) throws ServerException {
        return delegate.modifyDisseminator(parent.getContext(), parent
                .getStringArgument(ARGUMENT_NAME_PID), parent
                .getStringArgument(ARGUMENT_NAME_DISSEMINATOR_ID), parent
                .getStringArgument(ARGUMENT_NAME_BMECH_PID), parent
                .getStringArgument(ARGUMENT_NAME_DISSEMINATOR_LABEL), parent
                .getDSBindingMapArgument(ARGUMENT_NAME_BINDING_MAP), parent
                .getStringArgument(ARGUMENT_NAME_DISSEMINATOR_STATE), parent
                .getStringArgument(ARGUMENT_NAME_LOG_MESSAGE), parent
                .getBooleanArgument(ARGUMENT_NAME_FORCE));
    }

}
