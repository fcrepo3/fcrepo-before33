package fedora.server.errors;

/**
 * Signals that a low-level error occurred reading from a stream.
 *
 * @author cwilper@cs.cornell.edu
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