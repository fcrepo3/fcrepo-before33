package fedora.common.rdf;

import fedora.common.Constants;

public class FedoraViewNamespace extends RDFNamespace {

    // Properties
	public final RDFName HAS_DATASTREAM;
	public final RDFName DISSEMINATES;
    public final RDFName DISSEMINATION_TYPE;
    public final RDFName IS_VOLATILE;
    public final RDFName LAST_MODIFIED_DATE;
    public final RDFName MIME_TYPE;

    public FedoraViewNamespace() {

        this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "/view#";

        // Properties
        this.HAS_DATASTREAM		 = new RDFName(this, "hasDatastream");
        this.DISSEMINATES		 = new RDFName(this, "disseminates");
        this.DISSEMINATION_TYPE  = new RDFName(this, "disseminationType");
        this.IS_VOLATILE         = new RDFName(this, "isVolatile");
        this.LAST_MODIFIED_DATE  = new RDFName(this, "lastModifiedDate");
        this.MIME_TYPE           = new RDFName(this, "mimeType");
    }

}
