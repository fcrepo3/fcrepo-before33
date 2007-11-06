/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.common.xml.namespace;

/**
 * An XML namespace.
 *
 * @author cwilper@cs.cornell.edu
 */
public class XMLNamespace {

    /** The URI of this namespace. */
    public final String uri;

    /**
     * The preferred prefix for this namespace when used in instance documents.
     */
    public final String prefix;

    /**
     * Constructs an instance.
     *
     * @param uri the URI of the namespace.
     * @param prefix the preferred prefix.
     * @throws IllegalArgumentException if either parameter is null.
     */
    public XMLNamespace(String uri, String prefix) {
        if (uri == null) {
            throw new IllegalArgumentException("uri cannot be null");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        this.uri = uri;
        this.prefix = prefix;
    }

    //---
    // Object overrides
    //---

    /**
     * Returns the URI of the namespace.
     *
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return uri;
    }

    /**
     * Returns true iff the given object is an instance of this class
     * and has the same URI.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof XMLNamespace && o.getClass().getName().equals(
                this.getClass().getName())) {
            XMLNamespace n = (XMLNamespace) o;
            return uri.equals(n.uri);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return uri.hashCode();
    }

}
