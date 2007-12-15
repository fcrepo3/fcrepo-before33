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

    // Properties
    public final RDFName IS_MEMBER_OF;
 
    // Values

    // Types

    public FedoraRelsExtNamespace() {

        this.uri = "info:fedora/fedora-system:def/relations-external#";
        this.prefix = "rel";

        // Properties
        this.IS_MEMBER_OF               = new RDFName(this, "isMemberOf");

        // Values


        // Types

    }

}
