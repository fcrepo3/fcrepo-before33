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
 * <b>Title:</b> PurgeDatastreamMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.purgeDatastream()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class PurgeDatastreamMethod extends ManagementMethod {

    public PurgeDatastreamMethod(JournalEntry parent) {
        super(parent);
    }

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
