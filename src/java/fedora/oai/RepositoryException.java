/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.oai;

/**
 *
 * <p><b>Title:</b> RepositoryException.java</p>
 * <p><b>Description:</b> An exception occuring as a result of a problem in
 * the underlying repository system.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class RepositoryException
        extends Exception {

	private static final long serialVersionUID = 1L;
	
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

}