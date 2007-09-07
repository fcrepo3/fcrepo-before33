/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.common.rdf;

import fedora.common.Constants;

/**
 * Namespace of Fedora external relationships.
 *
 */
public class FedoraRelsExtNamespace extends RDFNamespace {

    // Properties
    public final RDFName IS_MEMBER_OF;
    public final RDFName HAS_BDEF;
    public final RDFName IS_CONTRACTOR;
    public final RDFName HAS_FORMAL_CONTENT_MODEL;

    // Values

    // Types

    public FedoraRelsExtNamespace() {

        this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "/relations-external#";

        // Properties
        this.IS_MEMBER_OF               = new RDFName(this, "isMemberOf");
        this.HAS_BDEF                   = new RDFName(this, "hasBDef");
        this.IS_CONTRACTOR              = new RDFName(this, "isContractor");
        this.HAS_FORMAL_CONTENT_MODEL   = new RDFName(this, "hasFormalContentModel");

        // Values


        // Types

    }

}
