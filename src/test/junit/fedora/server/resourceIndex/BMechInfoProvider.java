package fedora.server.resourceIndex;

import java.util.Set;

import fedora.server.errors.ResourceIndexException;

/**
 * Provides key information about behavior mechanism objects.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface BMechInfoProvider {

    /**
     * Get the <code>BMechMethodInfo</code> for each method of the given 
     * behavior mechanism.
     *
     * @param bMechPID the behavior mechanism pid.
     * @return A set with one or more items, each containing information for 
     *         a method of the behavior mechanism.
     * @throws ResourceIndexException if no such behavior mechanism exists
     *         or the information can't be read for any reason.
     */
    Set<BMechMethodInfo> getMethodInfo(String bMechPID)
            throws ResourceIndexException;

}