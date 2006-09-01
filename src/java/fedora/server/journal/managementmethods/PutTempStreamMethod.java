/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2006 by 
 * The Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

package fedora.server.journal.managementmethods;

import fedora.common.Constants;
import fedora.server.errors.ServerException;
import fedora.server.journal.JournalException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * 
 * <p>
 * <b>Title:</b> PutTempStreamMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Adapter class for Management.putTempStream()
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public class PutTempStreamMethod extends ManagementMethod {

    public PutTempStreamMethod(JournalEntry parent) {
        super(parent);
    }

    public Object invoke(ManagementDelegate delegate)
            throws ServerException, JournalException {
        String uploadId = delegate.putTempStream(parent.getContext(), parent
                .getStreamArgument(ARGUMENT_NAME_IN));

        // Store the Upload ID for writing to the journal.
        parent.setRecoveryValue(Constants.RECOVERY.UPLOAD_ID.uri, uploadId);

        return uploadId;
    }

}
