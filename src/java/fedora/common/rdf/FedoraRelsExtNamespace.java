package fedora.common.rdf;

import fedora.common.Constants;

/**
 * Namespace of Fedora external relationships.
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
public class FedoraRelsExtNamespace extends RDFNamespace {

    // Properties
    public final RDFName IS_MEMBER_OF;

    // Values

    // Types

    public FedoraRelsExtNamespace() {

        this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "/relations-external#";

        // Properties
        this.IS_MEMBER_OF     = new RDFName(this, "isMemberOf");

        // Values


        // Types

    }

}
