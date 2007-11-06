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
 * @author cwilper@fedora-commons.org
 */
public class FedoraModelNamespace extends RDFNamespace {

    // Properties
    public final RDFName CONTENT_MODEL;
    public final RDFName CREATED_DATE;
    public final RDFName DATASTREAM_STATE;
    public final RDFName DEFINES_METHOD;
    public final RDFName DEPENDS_ON;
    public final RDFName IMPLEMENTS_BDEF;
    public final RDFName LABEL;
    public final RDFName OWNER;
    public final RDFName STATE;
    public final RDFName USES_BMECH;
    public final RDFName OBJECT_STATE;

    // Values
    public final RDFName ACTIVE;
    public final RDFName DELETED;
    public final RDFName INACTIVE;

    // Types
    public final RDFName BDEF_OBJECT;
    public final RDFName BMECH_OBJECT;
    public final RDFName CMODEL_OBJECT;
    public final RDFName DATA_OBJECT;

    public FedoraModelNamespace() {

        this.uri = "info:fedora/fedora-system:def/model#";
        this.prefix = "fedora-model";

        // Properties
        this.CONTENT_MODEL        = new RDFName(this, "contentModel");
        this.CREATED_DATE         = new RDFName(this, "createdDate");
        this.DEFINES_METHOD       = new RDFName(this, "definesMethod");
        this.DEPENDS_ON           = new RDFName(this, "dependsOn");
        this.IMPLEMENTS_BDEF      = new RDFName(this, "implementsBDef");
        this.LABEL                = new RDFName(this, "label");

        this.OWNER                = new RDFName(this, "ownerId");
        this.STATE                = new RDFName(this, "state");
        this.USES_BMECH           = new RDFName(this, "usesBMech");
        this.OBJECT_STATE         = new RDFName(this, "objectState");
        this.DATASTREAM_STATE     = new RDFName(this, "datastreamState");

        // Values
        this.ACTIVE               = new RDFName(this, "Active");
        this.DELETED              = new RDFName(this, "Deleted");
        this.INACTIVE             = new RDFName(this, "Inactive");

        // Types
        this.BDEF_OBJECT          = new RDFName(this, "FedoraBDefObject");
        this.BMECH_OBJECT         = new RDFName(this, "FedoraBMechObject");
        this.CMODEL_OBJECT        = new RDFName(this, "FedoraCModelObject");
        this.DATA_OBJECT          = new RDFName(this, "FedoraObject");
    }

}
