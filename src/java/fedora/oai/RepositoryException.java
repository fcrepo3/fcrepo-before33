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

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

}