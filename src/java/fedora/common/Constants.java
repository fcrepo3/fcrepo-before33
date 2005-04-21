package fedora.common;

import fedora.common.policy.HttpRequestNamespace;
import fedora.common.policy.ActionNamespace;
import fedora.common.policy.EnvironmentNamespace;
import fedora.common.policy.DatastreamNamespace;
import fedora.common.policy.DisseminatorNamespace;
import fedora.common.policy.ResourceNamespace;
import fedora.common.policy.ObjectNamespace;
import fedora.common.policy.SubjectNamespace;
import fedora.common.rdf.*;

/**
 * Constants of general utility.
 *
 */ 
public interface Constants {
    public static final FedoraNamespace      FEDORA= new FedoraNamespace();  
    
    /** The PID of the Fedora system definition object. */
    public static final PID FEDORA_SYSTEM_DEF_PID = PID.getInstance("fedora-system:def");
    public static final String FEDORA_SYSTEM_DEF_URI = FEDORA_SYSTEM_DEF_PID.toURI();

    public static final DublinCoreNamespace      DC       = new DublinCoreNamespace();
    public static final FedoraModelNamespace     MODEL    = new FedoraModelNamespace();
    public static final FedoraRelsExtNamespace   RELS_EXT = new FedoraRelsExtNamespace();
    public static final FedoraViewNamespace      VIEW     = new FedoraViewNamespace();
    public static final RDFSyntaxNamespace       RDF      = new RDFSyntaxNamespace();
    public static final TucanaNamespace          TUCANA   = new TucanaNamespace();
    public static final XSDNamespace             XSD      = new XSDNamespace();
    public static final SubjectNamespace SUBJECT = SubjectNamespace.getInstance();
    public static final ActionNamespace     ACTION    = ActionNamespace.getInstance();
    public static final ResourceNamespace     RESOURCE    = ResourceNamespace.getInstance();
    public static final ObjectNamespace     OBJECT    = ObjectNamespace.getInstance();
    public static final DatastreamNamespace     DATASTREAM    = DatastreamNamespace.getInstance();
    public static final DisseminatorNamespace     DISSEMINATOR    = DisseminatorNamespace.getInstance();
    public static final EnvironmentNamespace     ENVIRONMENT    = EnvironmentNamespace.getInstance();
    public static final HttpRequestNamespace     HTTP_REQUEST    = HttpRequestNamespace.getInstance();
}
