package fedora.common.rdf;

import fedora.common.Constants;

public class RDFSyntaxNamespace extends RDFNamespace {

    public final RDFName TYPE;

    public RDFSyntaxNamespace() {

        this.uri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        this.TYPE = new RDFName(this, "type");
    }

}
