/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.utility.validate;

import java.util.Iterator;

import fedora.server.search.FieldSearchQuery;

/**
 * Provides an abstract wrapper around the repository of digital objects.
 * 
 * @author Jim Blake
 */
public interface ObjectSource {

    /**
     * Get a series of PIDs, representing all digital objects in the repository
     * that satisfy the specified query.
     */
    Iterator<String> findObjectPids(FieldSearchQuery query)
            throws ObjectSourceException;

    /**
     * Get the object that has this PID, or <code>null</code> if there is no
     * such object.
     */
    ValidationObject getValidationObject(String pid)
            throws ObjectSourceException;
}
