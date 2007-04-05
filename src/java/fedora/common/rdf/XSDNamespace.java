/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.common.rdf;

/**
 *
 */
public class XSDNamespace extends RDFNamespace {

    public final RDFName DATE_TIME;
    public final RDFName INT;
    public final RDFName LONG;
    public final RDFName FLOAT;
    public final RDFName DOUBLE;

    public XSDNamespace() {

        this.uri = "http://www.w3.org/2001/XMLSchema#";

        this.DATE_TIME = new RDFName(this, "dateTime");
        this.INT = new RDFName(this, "int");
        this.LONG = new RDFName(this, "long");
        this.FLOAT = new RDFName(this, "float");
        this.DOUBLE = new RDFName(this, "double");
    }

}
