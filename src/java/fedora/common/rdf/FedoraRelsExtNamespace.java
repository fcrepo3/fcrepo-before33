package fedora.common.rdf;

import fedora.common.Constants;

/**
 * Namespace of Fedora external relationships.
 *
 */
public class FedoraRelsExtNamespace extends RDFNamespace {

    // Properties
    public final RDFName IS_MEMBER_OF;

    // Values

    // Types

    public FedoraRelsExtNamespace() {

        this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "/relations-external#";

        // Properties
        this.IS_MEMBER_OF     = new RDFName(this, "isMemberOf");

        // Values


        // Types

    }

}
