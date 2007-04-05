/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ObjectNotFoundException.java</p>
 * <p><b>Description:</b> Signals that an object could not be found.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ObjectNotFoundException
        extends StorageException {
	private static final long serialVersionUID = 1L;

    /**
     * Creates an ObjectNotFoundException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public ObjectNotFoundException(String message) {
        super(message);
    }

}