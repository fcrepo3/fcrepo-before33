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
public class FedoraViewNamespace extends RDFNamespace {

    // Properties
    public final RDFName DISSEMINATES;
    public final RDFName DISSEMINATION_TYPE;
    public final RDFName IS_DIRECT;
    public final RDFName IS_VOLATILE;
    public final RDFName LAST_MODIFIED_DATE;
    public final RDFName MIME_TYPE;

    public FedoraViewNamespace() {

        this.uri = Constants.FEDORA_SYSTEM_DEF_URI + "/view#";

        // Properties
        this.DISSEMINATES        = new RDFName(this, "disseminates");
        this.DISSEMINATION_TYPE  = new RDFName(this, "disseminationType");
        this.IS_DIRECT           = new RDFName(this, "isDirect");
        this.IS_VOLATILE         = new RDFName(this, "isVolatile");
        this.LAST_MODIFIED_DATE  = new RDFName(this, "lastModifiedDate");
        this.MIME_TYPE           = new RDFName(this, "mimeType");
    }

}
