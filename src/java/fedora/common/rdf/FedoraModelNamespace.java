package fedora.common.rdf;

import fedora.common.Constants;

public class FedoraModelNamespace extends RDFNamespace {

    public static final String uri = Constants.FEDORA_SYSTEM_DEF_URI + "/model#";

    //
    // Properties
    //

    public static final RDFQName CONTENT_MODEL   = new RDFQName(uri, "contentModel");
    public static final RDFQName CREATED_DATE    = new RDFQName(uri, "createdDate");
    public static final RDFQName DEFINES_METHOD  = new RDFQName(uri, "definesMethod");
    public static final RDFQName IMPLEMENTS_BDEF = new RDFQName(uri, "implementsBDef");
    public static final RDFQName LABEL           = new RDFQName(uri, "label");
    public static final RDFQName OWNER           = new RDFQName(uri, "owner");
    public static final RDFQName STATE           = new RDFQName(uri, "state");
    public static final RDFQName USES_BMECH      = new RDFQName(uri, "usesBMech");


    //
    // Values
    //

    public static final RDFQName ACTIVE          = new RDFQName(uri, "Active");
    public static final RDFQName DELETED         = new RDFQName(uri, "Deleted");
    public static final RDFQName INACTIVE        = new RDFQName(uri, "Inactive");


    //
    // Types
    //

    public static final RDFQName BDEF            = new RDFQName(uri, "BDefObject");
    public static final RDFQName BMECH           = new RDFQName(uri, "BMechObject");
    public static final RDFQName DATAOBJECT      = new RDFQName(uri, "DataObject");

}
