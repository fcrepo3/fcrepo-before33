package fedora.common.rdf;

import fedora.common.Constants;

/**
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 */
public class FedoraModelNamespace extends RDFNamespace {

    // Properties
    public final RDFName CONTENT_MODEL;
    public final RDFName CREATED_DATE;
    public final RDFName DEFINES_METHOD;
    public final RDFName IMPLEMENTS_BDEF;
    public final RDFName LABEL;
    public final RDFName OWNER;
    public final RDFName STATE;
    public final RDFName USES_BMECH;
    public final RDFName OBJECT_STATE;
    public final RDFName DATASTREAM_STATE;

    // Values
    public final RDFName ACTIVE;
    public final RDFName DELETED;
    public final RDFName INACTIVE;

    // Types
    public final RDFName BDEF_OBJECT;
    public final RDFName BMECH_OBJECT;
    public final RDFName DATA_OBJECT;

    public FedoraModelNamespace() {

        this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "/model#";

        // Properties
//        this.ALTERNATE_IDENTIFIER = new RDFName(this, "alternateIdentifier");
        this.CONTENT_MODEL        = new RDFName(this, "contentModel");
        this.CREATED_DATE         = new RDFName(this, "createdDate");
        this.DEFINES_METHOD       = new RDFName(this, "definesMethod");
        this.IMPLEMENTS_BDEF      = new RDFName(this, "implementsBDef");
        this.LABEL                = new RDFName(this, "label");

        this.OWNER                = new RDFName(this, "owner");
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
        this.DATA_OBJECT          = new RDFName(this, "FedoraObject");
    }

}
