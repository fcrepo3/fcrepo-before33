package fedora.common;

import fedora.common.rdf.*;

/**
 * Constants of general utility.
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
public interface Constants {
    public static final FedoraNamespace      FEDORA= new FedoraNamespace();
    
    /** The PID of the Fedora system definition object. */
    public static final PID FEDORA_SYSTEM_DEF_PID = PID.getInstance("fedora-system:def");
    public static final String FEDORA_SYSTEM_DEF_URI = FEDORA_SYSTEM_DEF_PID.toURI();

    public static final DublinCoreNamespace      DC       = new DublinCoreNamespace();
    public static final FedoraModelNamespace     MODEL    = new FedoraModelNamespace();
    public static final FedoraRelsExtNamespace   RELS_EXT = new FedoraRelsExtNamespace();
    public static final RDFSyntaxNamespace       RDF      = new RDFSyntaxNamespace();
    public static final FedoraViewNamespace      VIEW     = new FedoraViewNamespace();
    public static final XSDNamespace             XSD      = new XSDNamespace();
    public static final FedoraPolicyActionNamespace     POLICY_ACTION    = new FedoraPolicyActionNamespace();
    public static final FedoraPolicyEnvironmentNamespace     POLICY_ENVIRONMENT    = new FedoraPolicyEnvironmentNamespace();
    public static final FedoraPolicyResourceNamespace     POLICY_RESOURCE    = new FedoraPolicyResourceNamespace();

}
