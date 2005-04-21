package fedora.server.errors;

/**
 *
 * <p><b>Title:</b> StreamReadException.java</p>
 * <p><b>Description:</b> Signals that a low-level error occurred reading from
 * a stream.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class StreamReadException
        extends StreamIOException {

    /**
     * Creates a StreamReadException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public StreamReadException(String message) {
        super(message);
    }

}