
package fedora.server.journal.managementmethods;

import fedora.common.Constants;

import fedora.server.errors.ServerException;
import fedora.server.journal.JournalException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * <p>
 * <b>Title:</b> PutTempStreamMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.putTempStream()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: PutTempStreamMethod.java 5025 2006-09-01 22:08:17 +0000 (Fri,
 *          01 Sep 2006) cwilper $
 */

public class PutTempStreamMethod
        extends ManagementMethod {

    public PutTempStreamMethod(JournalEntry parent) {
        super(parent);
    }

    @Override
    public Object invoke(ManagementDelegate delegate) throws ServerException,
            JournalException {
        String uploadId =
                delegate.putTempStream(parent.getContext(), parent
                        .getStreamArgument(ARGUMENT_NAME_IN));

        // Store the Upload ID for writing to the journal.
        parent.setRecoveryValue(Constants.RECOVERY.UPLOAD_ID.uri, uploadId);

        return uploadId;
    }

}
