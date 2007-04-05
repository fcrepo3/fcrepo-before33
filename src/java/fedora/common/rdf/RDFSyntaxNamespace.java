/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.common.rdf;

public class RDFSyntaxNamespace extends RDFNamespace {

    public final RDFName TYPE;

    public RDFSyntaxNamespace() {

        this.uri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

        this.TYPE = new RDFName(this, "type");
    }

}
