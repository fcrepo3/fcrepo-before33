package fedora.server.journal;

/**
 * 
 * <p>
 * <b>Title:</b> JournalException.java
 * </p>
 * <p>
 * <b>Description:</b> An Exception type for use by the Journaller and its
 * associated classes.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */
public class JournalException extends Exception {

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
