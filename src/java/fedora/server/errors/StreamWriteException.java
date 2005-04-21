package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> StreamWriteException.java</p>
 * <p><b>Description:</b> Signals that a low-level error occurred writing to a
 * stream.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class StreamWriteException
        extends StreamIOException {

    /**
     * Creates a StreamWriteException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public StreamWriteException(String message) {
        super(message);
    }

}