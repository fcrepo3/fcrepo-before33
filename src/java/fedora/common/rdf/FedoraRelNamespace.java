package fedora.common.rdf;

import fedora.common.Constants;

public class FedoraRelNamespace extends RDFNamespace {

    // Properties
    public final RDFName IS_MEMBER_OF;

    // Values

    // Types

    public FedoraRelNamespace() {

        this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "/rel#";

        // Properties
        this.IS_MEMBER_OF     = new RDFName(this, "isMemberOf");

        // Values


        // Types

    }

}
