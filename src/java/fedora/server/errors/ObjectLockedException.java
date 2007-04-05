/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> ObjectLockedException.java</p>
 * <p><b>Description:</b> Signals that an object was locked.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ObjectLockedException
        extends StorageException {
	private static final long serialVersionUID = 1L;

    /**
     * Creates an ObjectLockedException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public ObjectLockedException(String message) {
        super(message);
    }

}