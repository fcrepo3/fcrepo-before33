package fedora.common.rdf;

import fedora.common.Constants;

/**
 * Context attributes used for recovery.
 *
 * These values represent potentially auto-generated ids.  
 *
 * When Fedora is in journaling mode, these values are logged in the
 * journal so that they can be reused in recovery mode.  This helps
 * to ensure that the recovery process populates the repository in
 * the same way it was originally populated.
 */
public class RecoveryNamespace extends RDFNamespace {

    /** The ID of the datastream, whether given or generated at add time. */
    public final RDFName DATASTREAM_ID;

    /** The ID of the disseminator, whether given or generated at add time. */
    public final RDFName DISSEMINATOR_ID;

    /** The PID of the object, whether given or generated at ingest time. */
    public final RDFName PID;

    /** The list of generated PIDs. */
    public final RDFName PID_LIST;

    /** The temporary ID that was assigned to the stream at upload time. */
    public final RDFName UPLOAD_ID;

    public RecoveryNamespace() {

        this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "/recovery#";

        // Properties
        this.DATASTREAM_ID   = new RDFName(this, "dsID");
        this.DISSEMINATOR_ID = new RDFName(this, "dissID");
        this.PID             = new RDFName(this, "pid");
        this.PID_LIST        = new RDFName(this, "pidList");
        this.UPLOAD_ID       = new RDFName(this, "uploadID");
    }

}
