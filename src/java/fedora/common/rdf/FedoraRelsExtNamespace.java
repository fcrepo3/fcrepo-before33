/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.common.rdf;

/**
 * The Fedora RELS-EXT RDF namespace.
 * 
 * <pre>
 * Namespace URI    : info:fedora/fedora-system:def/relations-external#
 * Preferred Prefix : rel
 * </pre>
 *
 * @author cwilper@fedora-commons.org
 */
public class FedoraRelsExtNamespace extends RDFNamespace {
    private static final long serialVersionUID = 1L;
    
    // Properties
    public final RDFName IS_MEMBER_OF;
    public final RDFName HAS_BDEF;
    public final RDFName IS_CONTRACTOR;
    public final RDFName HAS_FORMAL_CONTENT_MODEL;

    // Values

    // Types

    public FedoraRelsExtNamespace() {

        this.uri = "info:fedora/fedora-system:def/relations-external#";
        this.prefix = "rel";

        // Properties
        this.IS_MEMBER_OF               = new RDFName(this, "isMemberOf");
        this.HAS_BDEF                   = new RDFName(this, "hasBDef");
        this.IS_CONTRACTOR              = new RDFName(this, "isContractor");
        this.HAS_FORMAL_CONTENT_MODEL   = new RDFName(this, "hasFormalContentModel");

        // Values


        // Types

    }

}
