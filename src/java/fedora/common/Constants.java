package fedora.common;

import fedora.common.rdf.*;

/**
 * Constants of general utility.
 */
public interface Constants {

    /** The PID of the Fedora system definition object. */
    public static final PID FEDORA_SYSTEM_DEF_PID = PID.getInstance("fedora-system:def");
    public static final String FEDORA_SYSTEM_DEF_URI = FEDORA_SYSTEM_DEF_PID.toURI();

    public static final FedoraModelNamespace MODEL = new FedoraModelNamespace();
/*
    public static FedoraViewNamespace  VIEW  = new FedoraViewNamespace();
*/

}
