
package fedora.server.journal;

/**
 * <p>
 * <b>Title:</b> JournalException.java
 * </p>
 * <p>
 * <b>Description:</b> An Exception type for use by the Journaller and its
 * associated classes.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id: JournalException.java 5162 2006-10-25 00:49:06 +0000 (Wed, 25
 *          Oct 2006) eddie $
 */
public class JournalException
        extends Exception {

    private static final long serialVersionUID = 1L;

    public JournalException() {
        super();
    }

    public JournalException(String message) {
        super(message);
    }

    public JournalException(String message, Throwable cause) {
        super(message, cause);
    }

    public JournalException(Throwable cause) {
        super(cause);
    }

}
