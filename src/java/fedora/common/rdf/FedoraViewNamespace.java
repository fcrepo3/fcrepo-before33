/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.common.rdf;

/**
 * The Fedora View RDF namespace.
 * 
 * <pre>
 * Namespace URI    : info:fedora/fedora-system:def/view#
 * Preferred Prefix : fedora-view
 * </pre>
 *
 * @author cwilper@fedora-commons.org
 */
public class FedoraViewNamespace extends RDFNamespace {
    private static final long serialVersionUID = 1L;
    
    // Properties
   
    /**
     * Deprecated as of Fedora 3.0.
     * No replacement.
     */
    @Deprecated
	public final RDFName HAS_DATASTREAM;
	public final RDFName DISSEMINATES;
    public final RDFName DISSEMINATION_TYPE;
    public final RDFName IS_VOLATILE;
    public final RDFName LAST_MODIFIED_DATE;
    public final RDFName MIME_TYPE;

    public FedoraViewNamespace() {

        this.uri = "info:fedora/fedora-system:def/view#";
        this.prefix = "fedora-view";

        // Properties
        this.HAS_DATASTREAM		 = new RDFName(this, "hasDatastream");
        this.DISSEMINATES		 = new RDFName(this, "disseminates");
        this.DISSEMINATION_TYPE  = new RDFName(this, "disseminationType");
        this.IS_VOLATILE         = new RDFName(this, "isVolatile");
        this.LAST_MODIFIED_DATE  = new RDFName(this, "lastModifiedDate");
        this.MIME_TYPE           = new RDFName(this, "mimeType");
    }

}
