/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.common;

import fedora.common.policy.ActionNamespace;
import fedora.common.policy.BDefNamespace;
import fedora.common.policy.BMechNamespace;
import fedora.common.policy.DatastreamNamespace;
import fedora.common.policy.DisseminatorNamespace;
import fedora.common.policy.EnvironmentNamespace;
import fedora.common.policy.HttpRequestNamespace;
import fedora.common.policy.ObjectNamespace;
import fedora.common.policy.ResourceNamespace;
import fedora.common.policy.SubjectNamespace;
import fedora.common.rdf.DublinCoreNamespace;
import fedora.common.rdf.FedoraModelNamespace;
import fedora.common.rdf.FedoraNamespace;
import fedora.common.rdf.FedoraRelsExtNamespace;
import fedora.common.rdf.FedoraViewNamespace;
import fedora.common.rdf.MulgaraNamespace;
import fedora.common.rdf.RDFSyntaxNamespace;
import fedora.common.rdf.RDFXSDNamespace;
import fedora.common.rdf.RecoveryNamespace;
import fedora.common.xml.format.FOXML1_0Format;
import fedora.common.xml.format.FOXML1_1Format;
import fedora.common.xml.format.FedoraAudit1_0Format;
import fedora.common.xml.format.FedoraBESecurity1_0Format;
import fedora.common.xml.format.FedoraBatchModify1_1Format;
import fedora.common.xml.format.FedoraDSCompositeModel1_0Format;
import fedora.common.xml.format.FedoraObjectDatastreams1_0Format;
import fedora.common.xml.format.FedoraObjectHistory1_0Format;
import fedora.common.xml.format.FedoraObjectItems1_0Format;
import fedora.common.xml.format.FedoraObjectMethods1_0Format;
import fedora.common.xml.format.FedoraObjectProfile1_0Format;
import fedora.common.xml.format.FedoraPIDList1_0Format;
import fedora.common.xml.format.FedoraRepositoryDesc1_0Format;
import fedora.common.xml.format.METSFedoraExt1_0Format;
import fedora.common.xml.format.METSFedoraExt1_1Format;
import fedora.common.xml.format.OAIDC2_0Format;
import fedora.common.xml.format.OAIFriends2_0Format;
import fedora.common.xml.format.OAIIdentifier2_0Format;
import fedora.common.xml.format.OAIPMH2_0Format;
import fedora.common.xml.format.OAIProvenance2_0Format;
import fedora.common.xml.namespace.FOXMLNamespace;
import fedora.common.xml.namespace.FedoraAPINamespace;
import fedora.common.xml.namespace.FedoraAccessNamespace;
import fedora.common.xml.namespace.FedoraAuditNamespace;
import fedora.common.xml.namespace.FedoraBESecurityNamespace;
import fedora.common.xml.namespace.FedoraBatchModifyNamespace;
import fedora.common.xml.namespace.FedoraBindingSpecNamespace;
import fedora.common.xml.namespace.FedoraDSCompositeModelNamespace;
import fedora.common.xml.namespace.FedoraFCFGNamespace;
import fedora.common.xml.namespace.FedoraManagementNamespace;
import fedora.common.xml.namespace.FedoraMethodMapNamespace;
import fedora.common.xml.namespace.FedoraServiceProfileNamespace;
import fedora.common.xml.namespace.FedoraTypesNamespace;
import fedora.common.xml.namespace.METSFedoraExtNamespace;
import fedora.common.xml.namespace.METSNamespace;
import fedora.common.xml.namespace.OAIDCNamespace;
import fedora.common.xml.namespace.OAIFriendsNamespace;
import fedora.common.xml.namespace.OAIIdentifierNamespace;
import fedora.common.xml.namespace.OAIPMHNamespace;
import fedora.common.xml.namespace.OAIProvenanceNamespace;
import fedora.common.xml.namespace.OldXLinkNamespace;
import fedora.common.xml.namespace.SOAPEncNamespace;
import fedora.common.xml.namespace.SOAPNamespace;
import fedora.common.xml.namespace.WSDLHTTPNamespace;
import fedora.common.xml.namespace.WSDLMIMENamespace;
import fedora.common.xml.namespace.WSDLNamespace;
import fedora.common.xml.namespace.XLinkNamespace;
import fedora.common.xml.namespace.XMLNSNamespace;
import fedora.common.xml.namespace.XMLXSDNamespace;
import fedora.common.xml.namespace.XSINamespace;

/**
 * Constants of general utility.
 */
@SuppressWarnings("deprecation")
public interface Constants {

    /**
     * The "Fedora Home" directory.
     * <p>
     * This is normally derived from the <code>FEDORA_HOME</code> environment
     * variable, but if defined, the <code>fedora.home</code> system property
     * will be used instead.
     * </p>
     */
    public static final String FEDORA_HOME = FedoraHome.getValue();

    //---
    // RDF Namespaces
    //---

    /**
     * The Dublin Core RDF namespace;
     * <code>http://purl.org/dc/elements/1.1/</code>
     */
    public static final DublinCoreNamespace DC = new DublinCoreNamespace();

    /**
     * The Fedora RDF namespace; <code>info:fedora/</code>
     */
    public static final FedoraNamespace FEDORA = new FedoraNamespace();

    /**
     * The Fedora Model RDF namespace;
     * <code>info:fedora/fedora-system:def/model#</code>
     */
    public static final FedoraModelNamespace MODEL = new FedoraModelNamespace();

    /**
     * The Fedora RELS-EXT RDF namespace;
     * <code>info:fedora/fedora-system:def/relations-external#</code>
     */
    public static final FedoraRelsExtNamespace RELS_EXT =
            new FedoraRelsExtNamespace();

    /**
     * The Fedora View RDF namespace;
     * <code>info:fedora/fedora-system:def/view#</code>
     */
    public static final FedoraViewNamespace VIEW = new FedoraViewNamespace();

    /**
     * The Fedora Recovery RDF namespace;
     * <code>info:fedora/fedora-system:def/recovery#</code>
     */
    public static final RecoveryNamespace RECOVERY = new RecoveryNamespace();

    /**
     * The RDF Syntax RDF namespace;
     * <code>http://www.w3.org/1999/02/22-rdf-syntax-ns#</code>
     */
    public static final RDFSyntaxNamespace RDF = new RDFSyntaxNamespace();

    /**
     * The Mulgara RDF namespace; <code>http://mulgara.org/mulgara#</code>
     */
    public static final MulgaraNamespace MULGARA = new MulgaraNamespace();

    /**
     * The XSD RDF namespace; <code>http://www.w3.org/2001/XMLSchema#</code>
     */
    public static final RDFXSDNamespace RDF_XSD = new RDFXSDNamespace();

    //---
    // XACML Namespaces
    //---

    /**
     * The Fedora Action XACML namespace;
     * <code>urn:fedora:names:fedora:2.1:action</code>
     */
    public static final ActionNamespace ACTION = ActionNamespace.getInstance();

    /**
     * The Fedora BDef XACML namespace;
     * <code>urn:fedora:names:fedora:2.1:resource:bdef</code>
     */
    public static final BDefNamespace BDEF = BDefNamespace.getInstance();

    /**
     * The Fedora BMech XACML namespace;
     * <code>urn:fedora:names:fedora:2.1:resource:bmech</code>
     */
    public static final BMechNamespace BMECH = BMechNamespace.getInstance();

    /**
     * The Fedora Datastream XACML namespace;
     * <code>urn:fedora:names:fedora:2.1:resource:datastream</code>
     */
    public static final DatastreamNamespace DATASTREAM =
            DatastreamNamespace.getInstance();

    /**
     * The Fedora Disseminator XACML namespace;
     * <code>urn:fedora:names:fedora:2.1:resource:disseminator</code>
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public static final DisseminatorNamespace DISSEMINATOR =
            DisseminatorNamespace.getInstance();

    /**
     * The Fedora Environment XACML namespace;
     * <code>urn:fedora:names:fedora:2.1:environment</code>
     */
    public static final EnvironmentNamespace ENVIRONMENT =
            EnvironmentNamespace.getInstance();

    /**
     * The Fedora HTTP Request XACML namespace;
     * <code>urn:fedora:names:fedora:2.1:environment</code>
     */
    public static final HttpRequestNamespace HTTP_REQUEST =
            HttpRequestNamespace.getInstance();

    /**
     * The Fedora Object XACML namespace;
     * <code>urn:fedora:names:fedora:2.1:resource:object</code>
     */
    public static final ObjectNamespace OBJECT = ObjectNamespace.getInstance();

    /**
     * The Fedora Resource XACML namespace;
     * <code>urn:fedora:names:fedora:2.1:resource</code>
     */
    public static final ResourceNamespace RESOURCE =
            ResourceNamespace.getInstance();

    /**
     * The Fedora Subject XACML namespace;
     * <code>urn:fedora:names:fedora:2.1:subject</code>
     */
    public static final SubjectNamespace SUBJECT =
            SubjectNamespace.getInstance();

    //---
    // XML Namespaces
    //---

    /**
     * The Fedora Access XML namespace;
     * <code>http://www.fedora.info/definitions/1/0/access/</code>
     */
    public static final FedoraAccessNamespace ACCESS =
            FedoraAccessNamespace.getInstance();

    /**
     * The Fedora API XML namespace;
     * <code>http://www.fedora.info/definitions/1/0/api/</code>
     */
    public static final FedoraAPINamespace API =
            FedoraAPINamespace.getInstance();

    /**
     * The Fedora Audit XML namespace;
     * <code>info:fedora/fedora-system:def/audit#</code>
     */
    public static final FedoraAuditNamespace AUDIT =
            FedoraAuditNamespace.getInstance();

    /**
     * The Fedora Batch Modify XML namespace;
     * <code>http://www.fedora.info/definitions/</code>
     */
    public static final FedoraBatchModifyNamespace BATCH_MODIFY =
            FedoraBatchModifyNamespace.getInstance();

    /**
     * The Fedora BE Security XML namespace;
     * <code>info:fedora/fedora-system:def/beSecurity#</code>
     */
    public static final FedoraBESecurityNamespace BE_SECURITY =
            FedoraBESecurityNamespace.getInstance();

    /**
     * The Fedora Binding Specification XML namespace;
     * <code>http://fedora.comm.nsdlib.org/service/bindspec</code>
     */
    public static final FedoraBindingSpecNamespace BINDING_SPEC =
            FedoraBindingSpecNamespace.getInstance();

    /**
     * The Fedora DS Composite Model XML namespace;
     * <code>info:fedora/fedora-system:def/dsCompositeModel#</code>
     */
    public static final FedoraDSCompositeModelNamespace DS_COMPOSITE_MODEL =
            FedoraDSCompositeModelNamespace.getInstance();

    /**
     * The Fedora Configuration XML namespace;
     * <code>http://www.fedora.info/definitions/1/0/config/</code>
     */
    public static final FedoraFCFGNamespace FCFG =
            FedoraFCFGNamespace.getInstance();

    /**
     * The FOXML XML namespace;
     * <code>info:fedora/fedora-system:def/foxml#</code>
     */
    public static final FOXMLNamespace FOXML = FOXMLNamespace.getInstance();

    /**
     * The Fedora Management XML namespace;
     * <code>http://www.fedora.info/definitions/1/0/management/</code>
     */
    public static final FedoraManagementNamespace MANAGEMENT =
            FedoraManagementNamespace.getInstance();

    /**
     * The Fedora Method Map XML namespace;
     * <code>http://fedora.comm.nsdlib.org/service/methodmap</code>
     */
    public static final FedoraMethodMapNamespace METHOD_MAP =
            FedoraMethodMapNamespace.getInstance();

    /**
     * The METS XML namespace; <code>http://www.loc.gov/METS/</code>
     */
    public static final METSNamespace METS = METSNamespace.getInstance();

    /**
     * The METS Fedora Extension XML namespace;
     * <code>http://www.loc.gov/METS/</code>
     */
    public static final METSFedoraExtNamespace METS_EXT =
            METSFedoraExtNamespace.getInstance();

    /**
     * The OAI DC XML namespace;
     * <code>http://www.openarchives.org/OAI/2.0/oai_dc/</code>
     */
    public static final OAIDCNamespace OAI_DC = OAIDCNamespace.getInstance();

    /**
     * The OAI Friends XML namespace;
     * <code>http://www.openarchives.org/OAI/2.0/friends/</code>
     */
    public static final OAIFriendsNamespace OAI_FRIENDS =
            OAIFriendsNamespace.getInstance();

    /**
     * The OAI Identifier XML namespace;
     * <code>http://www.openarchives.org/OAI/2.0/oai-identifier</code>
     */
    public static final OAIIdentifierNamespace OAI_IDENTIFIER =
            OAIIdentifierNamespace.getInstance();

    /**
     * The OAI-PMH XML namespace;
     * <code>http://www.openarchives.org/OAI/2.0/</code>
     */
    public static final OAIPMHNamespace OAI_PMH = OAIPMHNamespace.getInstance();

    /**
     * The OAI Provenance XML namespace;
     * <code>http://www.openarchives.org/OAI/2.0/provenance</code>
     */
    public static final OAIProvenanceNamespace OAI_PROV =
            OAIProvenanceNamespace.getInstance();

    /**
     * The XLink XML namespace used in old versions of the METS format;
     * <code>http://www.w3.org/TR/xlink</code>
     */
    public static final OldXLinkNamespace OLD_XLINK =
            OldXLinkNamespace.getInstance();

    /**
     * The Fedora Service Profile XML namespace;
     * <code>http://fedora.comm.nsdlib.org/service/profile</code>
     */
    public static final FedoraServiceProfileNamespace SERVICE_PROFILE =
            FedoraServiceProfileNamespace.getInstance();

    /**
     * The SOAP XML namespace; <code>http://schemas.xmlsoap.org/wsdl/soap</code>
     */
    public static final SOAPNamespace SOAP = SOAPNamespace.getInstance();

    /**
     * The SOAP Encoding XML namespace;
     * <code>http://schemas.xmlsoap.org/wsdl/soap/encoding</code>
     */
    public static final SOAPEncNamespace SOAP_ENC =
            SOAPEncNamespace.getInstance();

    /**
     * The Fedora Types XML namespace;
     * <code>http://www.fedora.info/definitions/1/0/types/</code>
     */
    public static final FedoraTypesNamespace TYPES =
            FedoraTypesNamespace.getInstance();

    /**
     * The WSDL XML namespace; <code>http://schemas.xmlsoap.org/wsdl/</code>
     */
    public static final WSDLNamespace WSDL = WSDLNamespace.getInstance();

    /**
     * The WSDL HTTP XML namespace;
     * <code>http://schemas.xmlsoap.org/wsdl/http/</code>
     */
    public static final WSDLHTTPNamespace WSDL_HTTP =
            WSDLHTTPNamespace.getInstance();

    /**
     * The WSDL MIME XML namespace;
     * <code>http://schemas.xmlsoap.org/wsdl/mime/</code>
     */
    public static final WSDLMIMENamespace WSDL_MIME =
            WSDLMIMENamespace.getInstance();

    /**
     * The XLink XML namespace; <code>http://www.w3.org/1999/xlink</code>
     */
    public static final XLinkNamespace XLINK = XLinkNamespace.getInstance();

    /**
     * The XMLNS XML namespace; <code>http://www.w3.org/2000/xmlns/</code>
     */
    public static final XMLNSNamespace XMLNS = XMLNSNamespace.getInstance();

    /**
     * The XSD XML namespace; <code>http://www.w3.org/2001/XMLSchema</code>
     */
    public static final XMLXSDNamespace XML_XSD = XMLXSDNamespace.getInstance();

    /**
     * The XML Schema Instance XML namespace;
     * <code>http://www.w3.org/2001/XMLSchema-instance</code>
     */
    public static final XSINamespace XSI = XSINamespace.getInstance();

    //---
    // XML Formats
    //---

    /**
     * The Fedora Audit 1.0 XML format;
     * <code>info:fedora/fedora-system:format/xml.fedora.audit</code>
     */
    public static final FedoraAudit1_0Format AUDIT1_0 =
            FedoraAudit1_0Format.getInstance();

    /**
     * The Fedora Batch Modify 1.1 XML format;
     * <code>info:fedora/fedora-system:FedoraBatchModify-1.1</code>
     */
    public static final FedoraBatchModify1_1Format BATCH_MODIFY1_1 =
            FedoraBatchModify1_1Format.getInstance();

    /**
     * The Fedora BE Security 1.0 XML format;
     * <code>info:fedora/fedora-system:FedoraBESecurity-1.0</code>
     */
    public static final FedoraBESecurity1_0Format BE_SECURITY1_0 =
            FedoraBESecurity1_0Format.getInstance();

    /**
     * The Fedora DS Composite Model 1.0 XML format;
     * <code>info:fedora/fedora-system:FedoraDSCompositeModel-1.0</code>
     */
    public static final FedoraDSCompositeModel1_0Format DS_COMPOSITE_MODEL1_0 =
            FedoraDSCompositeModel1_0Format.getInstance();

    /**
     * The Fedora Object Datastreams 1.0 XML format;
     * <code>info:fedora/fedora-system:FedoraObjectDatastreams-1.0</code>
     */
    public static final FedoraObjectDatastreams1_0Format OBJ_DATASTREAMS1_0 =
            FedoraObjectDatastreams1_0Format.getInstance();

    /**
     * The Fedora Object History 1.0 XML format;
     * <code>info:fedora/fedora-system:FedoraObjectHistory-1.0</code>
     */
    public static final FedoraObjectHistory1_0Format OBJ_HISTORY1_0 =
            FedoraObjectHistory1_0Format.getInstance();

    /**
     * The Fedora Object Items 1.0 XML format;
     * <code>info:fedora/fedora-system:FedoraObjectItems-1.0</code>
     */
    public static final FedoraObjectItems1_0Format OBJ_ITEMS1_0 =
            FedoraObjectItems1_0Format.getInstance();

    /**
     * The Fedora Object Methods 1.0 XML format;
     * <code>info:fedora/fedora-system:FedoraObjectMethods-1.0</code>
     */
    public static final FedoraObjectMethods1_0Format OBJ_METHODS1_0 =
            FedoraObjectMethods1_0Format.getInstance();

    /**
     * The Fedora Object Profile 1.0 XML format;
     * <code>info:fedora/fedora-system:FedoraObjectProfile-1.0</code>
     */
    public static final FedoraObjectProfile1_0Format OBJ_PROFILE1_0 =
            FedoraObjectProfile1_0Format.getInstance();

    /**
     * The Fedora PID List 1.0 XML format;
     * <code>info:fedora/fedora-system:FedoraPIDList-1.0</code>
     */
    public static final FedoraPIDList1_0Format PID_LIST1_0 =
            FedoraPIDList1_0Format.getInstance();

    /**
     * The Fedora Repsitory Description 1.0 XML format;
     * <code>info:fedora/fedora-system:FedoraRepositoryDesc-1.0</code>
     */
    public static final FedoraRepositoryDesc1_0Format REPO_DESC1_0 =
            FedoraRepositoryDesc1_0Format.getInstance();

    /**
     * The FOXML 1.0 XML format;
     * <code>info:fedora/fedora-system:FOXML-1.0</code>
     */
    public static final FOXML1_0Format FOXML1_0 = FOXML1_0Format.getInstance();

    /**
     * The FOXML 1.1 XML format;
     * <code>info:fedora/fedora-system:FOXML-1.1</code>
     */
    public static final FOXML1_1Format FOXML1_1 = FOXML1_1Format.getInstance();

    /**
     * The METS Fedora Extension 1.0 XML format;
     * <code>info:fedora/fedora-system:METSFedoraExt-1.0</code>
     */
    public static final METSFedoraExt1_0Format METS_EXT1_0 =
            METSFedoraExt1_0Format.getInstance();

    /**
     * The METS Fedora Extension 1.1 XML format;
     * <code>info:fedora/fedora-system:METSFedoraExt-1.1</code>
     */
    public static final METSFedoraExt1_1Format METS_EXT1_1 =
            METSFedoraExt1_1Format.getInstance();

    /**
     * The OAI DC 2.0 XML format;
     * <code>http://www.openarchives.org/OAI/2.0/oai_dc/</code>
     */
    public static final OAIDC2_0Format OAI_DC2_0 = OAIDC2_0Format.getInstance();

    /**
     * The OAI Friends 2.0 XML format;
     * <code>http://www.openarchives.org/OAI/2.0/friends/</code>
     */
    public static final OAIFriends2_0Format OAI_FRIENDS2_0 =
            OAIFriends2_0Format.getInstance();

    /**
     * The OAI Identifier 2.0 XML format;
     * <code>http://www.openarchives.org/OAI/2.0/oai-identifier</code>
     */
    public static final OAIIdentifier2_0Format OAI_IDENTIFIER2_0 =
            OAIIdentifier2_0Format.getInstance();

    /**
     * The OAI-PMH 2.0 XML format;
     * <code>http://www.openarchives.org/OAI/2.0/</code>
     */
    public static final OAIPMH2_0Format OAI_PMH2_0 =
            OAIPMH2_0Format.getInstance();

    /**
     * The OAI Provenance 2.0 XML format;
     * <code>http://www.openarchives.org/OAI/2.0/provenance</code>
     */
    public static final OAIProvenance2_0Format OAI_PROV2_0 =
            OAIProvenance2_0Format.getInstance();

    //---
    // Static helpers
    //---

    /**
     * Utility to determine the value of "Fedora Home" based on the current
     * environment.
     */
    static class FedoraHome {

        /**
         * Determines the value of "Fedora Home" based on the
         * <code>fedora.home</code> system property (checked first) or the
         * <code>FEDORA_HOME</code> environment variable.
         * 
         * @returns the value, or <code>null</code> if undefined in both
         *          places.
         */
        public static final String getValue() {
            if (System.getProperty("fedora.home") != null) {
                return System.getProperty("fedora.home");
            } else {
                return System.getenv("FEDORA_HOME");
            }
        }
    }

}
