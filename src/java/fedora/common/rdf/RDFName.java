package fedora.common.rdf;

import java.net.*;

import org.jrdf.graph.*;

/**
 * A URIReference from a known namespace.
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
        return this.uri;
    }

}
