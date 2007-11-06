/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.common.rdf;

/**
 * The XSD RDF namespace.
 *
 * <pre>
 * Namespace URI    : http://www.w3.org/2001/XMLSchema#
 * Preferred Prefix : xsd
 * </pre>
 *
 * <p><em><b>NOTE:</b> This is subtly different from the XML XSD namespace, in
 * that its URI ends with a <code>#</code>.</em>  See
 * <a href="http://www.w3.org/2001/tag/group/track/issues/6">
 * http://www.w3.org/2001/tag/group/track/issues/6</a> for more information
 * on why this is necessary.</p>
 *
 * @author cwilper@cs.cornell.edu
 */
public class RDFXSDNamespace extends RDFNamespace {

    public final RDFName DATE_TIME;
    public final RDFName INT;
    public final RDFName LONG;
    public final RDFName FLOAT;
    public final RDFName DOUBLE;
    public final RDFName STRING;

    public RDFXSDNamespace() {

        this.uri = "http://www.w3.org/2001/XMLSchema#";
        this.prefix = "xsd";

        this.DATE_TIME = new RDFName(this, "dateTime");
        this.INT = new RDFName(this, "int");
        this.LONG = new RDFName(this, "long");
        this.FLOAT = new RDFName(this, "float");
        this.DOUBLE = new RDFName(this, "double");
        this.STRING = new RDFName(this, "string");
    }

}
