/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.common.rdf;

/**
 * The Fedora Model RDF namespace.
 * 
 * <pre>
 * Namespace URI    : info:fedora/fedora-system:def/model#
 * Preferred Prefix : fedora-model
 * </pre>
 * 
 * @author Chris Wilper
 */
public class FedoraModelNamespace
        extends RDFNamespace {

    private static final long serialVersionUID = 1L;

    // Properties

    /**
     * Deprecated as of Fedora 3.0. Replaced by HAS_CONTENT_MODEL.
     */
    @Deprecated
    public final RDFName CONTENT_MODEL;

    public final RDFName CREATED_DATE;

    public final RDFName DEFINES_METHOD;

    /**
     * Deprecated as of Fedora 3.0. No replacement. This information is no
     * longer recorded.
     */
    @Deprecated
    public final RDFName DEPENDS_ON;

    /**
     * Deprecated as of Fedora 3.0. Replaced by HAS_BDEF.
     */
    @Deprecated
    public final RDFName IMPLEMENTS_BDEF;

    public final RDFName LABEL;

    public final RDFName OWNER;

    public final RDFName STATE;

    /**
     * Deprecated as of Fedora 3.0. No direct replacement. Objects now point to
     * content models via HAS_CMODEL. Behavior Mechanisms used by an object are
     * those that point to the content model of the object via IS_CONTRACTOR.
     */
    @Deprecated
    public final RDFName USES_BMECH;

    // Values
    public final RDFName ACTIVE;

    public final RDFName DELETED;

    public final RDFName INACTIVE;

    // Types
    public final RDFName BDEF_OBJECT;

    public final RDFName BMECH_OBJECT;

    public final RDFName CMODEL_OBJECT;

    public final RDFName DATA_OBJECT;

    // CMDA RDF Relationships
    public final RDFName HAS_BDEF;

    public final RDFName IS_CONTRACTOR;

    public final RDFName HAS_CONTENT_MODEL;

    public FedoraModelNamespace() {

        uri = "info:fedora/fedora-system:def/model#";
        prefix = "fedora-model";

        // Properties
        CONTENT_MODEL = new RDFName(this, "contentModel");
        CREATED_DATE = new RDFName(this, "createdDate");
        DEFINES_METHOD = new RDFName(this, "definesMethod");
        DEPENDS_ON = new RDFName(this, "dependsOn");
        IMPLEMENTS_BDEF = new RDFName(this, "implementsBDef");
        LABEL = new RDFName(this, "label");

        OWNER = new RDFName(this, "ownerId");
        STATE = new RDFName(this, "state");
        USES_BMECH = new RDFName(this, "usesBMech");

        // Values
        ACTIVE = new RDFName(this, "Active");
        DELETED = new RDFName(this, "Deleted");
        INACTIVE = new RDFName(this, "Inactive");

        // Types
        BDEF_OBJECT = new RDFName(this, "FedoraBDefObject");
        BMECH_OBJECT = new RDFName(this, "FedoraBMechObject");
        CMODEL_OBJECT = new RDFName(this, "FedoraCModelObject");
        DATA_OBJECT = new RDFName(this, "FedoraObject");

        // CMDA RDF Relationships
        HAS_BDEF = new RDFName(this, "hasBDef");
        IS_CONTRACTOR = new RDFName(this, "isContractor");
        HAS_CONTENT_MODEL = new RDFName(this, "hasContentModel");
    }

}
