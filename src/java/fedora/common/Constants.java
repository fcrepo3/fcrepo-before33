package fedora.common;

import fedora.common.rdf.*;

/**
 * Constants of general utility.
 */
public interface Constants {

    /** The PID of the Fedora system definition object. */
    public static final PID FEDORA_SYSTEM_DEF_PID = PID.getInstance("fedora-system:def");
    public static final String FEDORA_SYSTEM_DEF_URI = FEDORA_SYSTEM_DEF_PID.toURI();

    public static final DublinCoreNamespace  DC    = new DublinCoreNamespace();
    public static final FedoraModelNamespace MODEL = new FedoraModelNamespace();
    public static final RDFSyntaxNamespace   RDF   = new RDFSyntaxNamespace();
    public static final FedoraViewNamespace  VIEW  = new FedoraViewNamespace();
    public static final XSDNamespace         XSD   = new XSDNamespace();

}
