/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.common.xml.namespace;

/**
 * The Fedora BESecurity XML namespace.
 *
 * <pre>
 * Namespace URI    : info:fedora/fedora-system:def/beSecurity#
 * Preferred Prefix : besecurity
 * </pre>
 *
 * @author cwilper@cs.cornell.edu
 */
public class FedoraBESecurityNamespace
        extends XMLNamespace {

    //---
    // Singleton instantiation
    //---

    /** The only instance of this class. */
    private static final FedoraBESecurityNamespace ONLY_INSTANCE
            = new FedoraBESecurityNamespace();

    /**
     * Constructs the instance.
     */
    private FedoraBESecurityNamespace() {
        super("info:fedora/fedora-system:def/beSecurity#", "besecurity");
    }

    /**
     * Gets the only instance of this class.
     *
     * @return the instance.
     */
    public static FedoraBESecurityNamespace getInstance() {
        return ONLY_INSTANCE;
    }

}
