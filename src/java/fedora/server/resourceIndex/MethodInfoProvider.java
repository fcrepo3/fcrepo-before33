/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.resourceIndex;

import java.util.Set;

import fedora.server.errors.ResourceIndexException;

/**
 * Provides key information about known service method implementations.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface MethodInfoProvider {

    /**
     * Get the <code>MethodInfo</code> for each method of the given 
     * behavior mechanism.
     *
     * @param bMechPID the behavior mechanism pid.
     * @return A set with one item for each implemented method.
     * @throws ResourceIndexException if no such behavior mechanism exists
     *         or the information can't be read for any other reason.
     */
    Set<MethodInfo> getMethodInfo(String bMechPID)
            throws ResourceIndexException;

}