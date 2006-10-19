package fedora.server.resourceIndex;

import java.util.Set;

import fedora.server.errors.ResourceIndexException;

/**
 * Stores and provides key information about behavior mechanism objects.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface BMechInfoStore extends BMechInfoProvider {

    /**
     * Set the <code>BMechMethodInfo</code> for each method of the given
     * behavior mechanism.
     *
     * @param bMechPID the behavior mechanism pid.
     * @param methodInfo the method information for all methods.
     *        A <code>null</code> or empty value will cause the information
     *        to be removed.
     * @throws ResourceIndexException if the information can't be written
     *         for any reason.
     */
    void setMethodInfo(String bMechPID, Set<BMechMethodInfo> methodInfo)
            throws ResourceIndexException;

}