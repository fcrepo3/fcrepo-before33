package fedora.common.rdf;

import fedora.common.Constants;

public class FedoraViewNamespace extends RDFNamespace {

    // Properties
    public final RDFName DISSEMINATES;
    public final RDFName DISSEMINATION_TYPE;
    public final RDFName IS_DIRECT;
    public final RDFName IS_VOLATILE;
    public final RDFName LAST_MODIFIED_DATE;
    public final RDFName MIME_TYPE;

    protected FedoraViewNamespace() {

        this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "/view#";

        // Properties
        this.DISSEMINATES        = new RDFName(this, "disseminates");
        this.DISSEMINATION_TYPE  = new RDFName(this, "disseminationType");
        this.IS_DIRECT           = new RDFName(this, "isDirect");
        this.IS_VOLATILE         = new RDFName(this, "isVolatile");
        this.LAST_MODIFIED_DATE  = new RDFName(this, "lastModifiedDate");
        this.MIME_TYPE           = new RDFName(this, "mimeType");
    }

}
