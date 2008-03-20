
package fedora.server.journal.managementmethods;

import fedora.server.errors.ServerException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * <p>
 * <b>Title:</b> ModifyDatastreamByReferenceMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for
 * Management.modifyDatastreamByReference()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: ModifyDatastreamByReferenceMethod.java 5213 2006-11-17 20:36:37
 *          +0000 (Fri, 17 Nov 2006) haschart $
 */

public class ModifyDatastreamByReferenceMethod
        extends ManagementMethod {

    public ModifyDatastreamByReferenceMethod(JournalEntry parent) {
        super(parent);
    }

    @Override
    public Object invoke(ManagementDelegate delegate) throws ServerException {
        return delegate.modifyDatastreamByReference(parent.getContext(), parent
                .getStringArgument(ARGUMENT_NAME_PID), parent
                .getStringArgument(ARGUMENT_NAME_DS_ID), parent
                .getStringArrayArgument(ARGUMENT_NAME_ALT_IDS), parent
                .getStringArgument(ARGUMENT_NAME_DS_LABEL), parent
                .getStringArgument(ARGUMENT_NAME_MIME_TYPE), parent
                .getStringArgument(ARGUMENT_NAME_FORMAT_URI), parent
                .getStringArgument(ARGUMENT_NAME_DS_LOCATION), parent
                .getStringArgument(ARGUMENT_NAME_CHECKSUM_TYPE), parent
                .getStringArgument(ARGUMENT_NAME_CHECKSUM), parent
                .getStringArgument(ARGUMENT_NAME_LOG_MESSAGE), parent
                .getBooleanArgument(ARGUMENT_NAME_FORCE));
    }

}
