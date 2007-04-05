/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.resourceIndex;

import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;

/**
 * Stores and provides key information about known service method 
 * implementations.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface MethodInfoStore extends MethodInfoProvider {

    /**
     * Add or replace method information about the given behavior definition.
     *
     * A behavior definition defines a set of method names and the runtime
     * parameters for each.
     *
     * @param reader the behavior definition.
     * @throws ResourceIndexException if any error occurs.
     */
    void putBDefInfo(BDefReader reader) throws ResourceIndexException;

    /**
     * Add or replace method information about the given behavior mechanism.
     *
     * For each method it implements, a behavior mechanism defines a
     * set of datastream binding keys and a set of possible return types.
     *
     * @param reader the behavior mechanism.
     * @throws ResourceIndexException if any error occurs.
     */
    void putBMechInfo(BMechReader reader) throws ResourceIndexException;

    /**
     * Delete method information about the given behavior definition.
     *
     * @param bDefPID the pid of the behavior definition whose information
     *                should be deleted.
     * @throws ResourceIndexException if any error occurs.
     */
    void deleteBDefInfo(String bDefPID) throws ResourceIndexException;

    /**
     * Delete method information about the given behavior mechanism.
     *
     * @param bDefPID the pid of the behavior mechanism whose information
     *                should be deleted.
     * @throws ResourceIndexException if any error occurs.
     */
    void deleteBMechInfo(String bMechPID) throws ResourceIndexException;

}