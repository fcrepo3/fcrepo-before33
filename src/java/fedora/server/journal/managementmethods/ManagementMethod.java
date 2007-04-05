/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.journal.managementmethods;

import fedora.server.errors.ServerException;
import fedora.server.journal.JournalConstants;
import fedora.server.journal.JournalException;
import fedora.server.journal.entry.JournalEntry;
import fedora.server.management.ManagementDelegate;

/**
 * 
 * <p>
 * <b>Title:</b> ManagementMethod.java
 * </p>
 * <p>
 * <b>Description:</b> Abstract base class for the classes that act as adapters
 * to the Management methods.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public abstract class ManagementMethod implements JournalConstants {

    /**
     * Get an instance of the proper class, based on the method name.
     */
    public static ManagementMethod getInstance(String methodName,
            JournalEntry parent) {
        if (METHOD_INGEST_OBJECT.equals(methodName)) {
            return new IngestObjectMethod(parent);
        } else if (METHOD_MODIFY_OBJECT.equals(methodName)) {
            return new ModifyObjectMethod(parent);
        } else if (METHOD_PURGE_OBJECT.equals(methodName)) {
            return new PurgeObjectMethod(parent);
        } else if (METHOD_ADD_DATASTREAM.equals(methodName)) {
            return new AddDatastreamMethod(parent);
        } else if (METHOD_MODIFY_DATASTREAM_BY_REFERENCE.equals(methodName)) {
            return new ModifyDatastreamByReferenceMethod(parent);
        } else if (METHOD_MODIFY_DATASTREAM_BY_VALUE.equals(methodName)) {
            return new ModifyDatastreamByValueMethod(parent);
        } else if (METHOD_SET_DATASTREAM_STATE.equals(methodName)) {
            return new SetDatastreamStateMethod(parent);
        } else if (METHOD_SET_DATASTREAM_VERSIONABLE.equals(methodName)) {
            return new SetDatastreamVersionableMethod(parent);
        } else if (METHOD_PURGE_DATASTREAM.equals(methodName)) {
            return new PurgeDatastreamMethod(parent);
        } else if (METHOD_ADD_DISSEMINATOR.equals(methodName)) {
            return new AddDisseminatorMethod(parent);
        } else if (METHOD_MODIFY_DISSEMINATOR.equals(methodName)) {
            return new ModifyDisseminatorMethod(parent);
        } else if (METHOD_SET_DISSEMINATOR_STATE.equals(methodName)) {
            return new SetDisseminatorStateMethod(parent);
        } else if (METHOD_PURGE_DISSEMINATOR.equals(methodName)) {
            return new PurgeDisseminatorMethod(parent);
        } else if (METHOD_PUT_TEMP_STREAM.equals(methodName)) {
            return new PutTempStreamMethod(parent);
        } else if (METHOD_GET_NEXT_PID.equals(methodName)) {
            return new GetNextPidMethod(parent);
        } else {
            throw new IllegalArgumentException("Unrecognized method name: '"
                    + methodName + "'");
        }
    }

    protected final JournalEntry parent;

    protected ManagementMethod(JournalEntry parent) {
        this.parent = parent;
    }

    /**
     * Each concrete sub-class should use this method to pull the necessary
     * arguments from the map of the parent JournalEntry, call the appropriate
     * method on the ManagementDelegate, and perhaps store the result in the
     * context of the parent JournalEntry (depends on the sub-class).
     */
    public abstract Object invoke(ManagementDelegate delegate)
            throws ServerException, JournalException;
}
