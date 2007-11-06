/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.common.rdf;

/**
 * The Fedora RDF namespace.
 * 
 * <pre>
 * Namespace URI    : info:fedora/
 * Preferred Prefix : fedora
 * </pre>
 *
 * @see <a href="http://info-uri.info/registry/OAIHandler?verb=GetRecord&metadataPrefix=reg&identifier=info:fedora/">
 *      "info" URI Scheme Registry page</a>
 * @author cwilper@fedora-commons.org
 */
public class FedoraNamespace extends RDFNamespace {

    public FedoraNamespace() {
        this.uri = "info:fedora/";
        this.prefix = "fedora";
    }

}
