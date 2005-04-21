package fedora.common.policy;

import java.net.*;

import org.jrdf.graph.*;

/**
 * A URIReference from a known namespace.
 *
 */
public class XacmlName implements URIReference {

    public XacmlNamespace parent;
    public String localName;
    public String datatype;
    public String uri;

    private URI m_uri;

    public XacmlName(XacmlNamespace parent, String localName, String datatype) {
        try {
            this.parent = parent;
            this.localName = localName;
            this.datatype = datatype;
            this.uri = parent.uri + ":" + localName;
            m_uri = new URI(this.uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Bad URI Syntax", e);
        }
    }
    
    public XacmlName(XacmlNamespace parent, String localName) {
    	this(parent, localName, "");
    }

    /**
     * Does the given string loosely match this name?
     *
     * Either: 1) It matches localName (case insensitive)
     *         2) It matches uri (case sensitive)
     * if (firstLocalNameChar == true):
     *         3) It is one character long, and that character
     *            matches the first character of localName (case insensitive)
     */
    public boolean looselyMatches(String in, boolean tryFirstLocalNameChar) {
        if (in == null || in.length() == 0) return false;
        if (in.equalsIgnoreCase(this.localName)) return true;
        if (in.equals(uri)) return true;
        if (tryFirstLocalNameChar
                && in.length() == 1 
                && in.toUpperCase().charAt(0) == 
                        localName.toUpperCase().charAt(0)) return true;
        return false;
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
        return this.uri + "\t" + this.datatype;
    }

}
