/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.common.rdf;

/**
 * The Fedora RDF namespace.
 * 
 * <pre>
 * Namespace URI    : info:fedora/
 * Preferred Prefix : fedora
 * </pre>
 *
 * @see <a href="http://info-uri.info/registry/OAIHandler?verb=GetRecord&metadataPrefix=reg&identifier=info:fedora/">
 *      "info" URI Scheme Registry page</a>
 * @author cwilper@fedora-commons.org
 */
public class FedoraNamespace extends RDFNamespace {

    /**
     * The content model for Behavior Definition objects;
     * <code>info:fedora/fedora-system:BehaviorDefinition</code>
     */
    public final RDFName BDEF_CMODEL;

    /**
     * The content model for Behavior Mechanism objects;
     * <code>info:fedora/fedora-system:BehaviorMechanism</code>
     */
    public final RDFName BMECH_CMODEL;

    /**
     * The content model for Content Model objects;
     * <code>info:fedora/fedora-system:ContentModel</code>
     */
    public final RDFName CMODEL_CMODEL;

    /**
     * The default content model for Fedora data objects;
     * <code>info:fedora/fedora-system:DefaultContentModel-1</code>
     */
    public final RDFName DEFAULT_CMODEL;

    private static final long serialVersionUID = 1L;
    
    public FedoraNamespace() {

        this.uri = "info:fedora/";
        this.prefix = "fedora";

        final String sys = "fedora-system";
        this.BDEF_CMODEL    = new RDFName(this, sys + ":BehaviorDefinition");
        this.BMECH_CMODEL   = new RDFName(this, sys + ":BehaviorMechanism");
        this.CMODEL_CMODEL  = new RDFName(this, sys + ":ContentModel");
        this.DEFAULT_CMODEL = new RDFName(this, sys + ":DefaultContentModel-1");
    }

}
