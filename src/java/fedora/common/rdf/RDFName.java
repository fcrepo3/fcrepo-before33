package fedora.common.rdf;

import java.net.*;

import org.jrdf.graph.*;

/**
 * A URIReference from a known namespace.
 */
public class RDFName implements URIReference {

    public RDFNamespace namespace;
    public String localName;
    public String uri;

    private URI m_uri;

    public RDFName(RDFNamespace namespace, 
                   String localName) {
        try {
            this.namespace = namespace;
            this.localName = localName;
            this.uri = namespace.uri + localName;
            m_uri = new URI(this.uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad URI Syntax", e);
        }
    }

    //
    // Implementation of the URIReference interface
    //

    public void accept(TypedNodeVisitor visitor) {
        visitor.visitURIReference(this);
    }

    public URI getURI() {
        return m_uri;
    }

    public String toString() {
        return this.uri;
    }

}
