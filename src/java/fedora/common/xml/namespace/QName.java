/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.common.xml.namespace;

/**
 * A namespace-qualified name in XML.
 *
 * @author cwilper@cs.cornell.edu
 */
public class QName {

    /** The namespace to which this name belongs. */
    public final XMLNamespace namespace;

    /** The local part of the qualified name. */
    public final String localName;

    /**
     * A string of the form: <code>prefix:localName</code>, acceptable for
     * use in an instance document.  The prefix used will be the preferred
     * prefix of the namespace.
     */
    public final String qName;

    /**
     * Constructs an instance.
     *
     * @param namespace the namespace to which this name belongs.
     * @param localName the local part of the qualified name.
     * @throws IllegalArgumentException if either parameter is null.
     */
    public QName(XMLNamespace namespace, String localName) {
        if (namespace == null) {
            throw new IllegalArgumentException("namespace cannot be null");
        }
        if (localName == null) {
            throw new IllegalArgumentException("localName cannot be null");
        }
        this.namespace = namespace;
        this.localName = localName;
        this.qName = namespace.prefix + ":" + localName;
    }

    //---
    // Object overrides
    //---

    /**
     * Returns a string of the form: <code>{namespace-uri}localName</code>.
     *
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "{" + namespace.uri + "}" + localName;
    }

    /**
     * Returns true iff the given object is an instance of this class
     * and has the same namespace and localName values.
     *
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof QName) {
            QName q = (QName) o;
            return namespace.equals(q.namespace)
                    && localName.equals(q.localName);
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return namespace.hashCode() + localName.hashCode();
    }
}
