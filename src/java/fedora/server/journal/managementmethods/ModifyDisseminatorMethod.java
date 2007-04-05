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
 * <b>Title:</b> ModifyDisseminatorMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.modifyDisseminator()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class ModifyDisseminatorMethod extends ManagementMethod {

    public ModifyDisseminatorMethod(JournalEntry parent) {
        super(parent);
    }

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
