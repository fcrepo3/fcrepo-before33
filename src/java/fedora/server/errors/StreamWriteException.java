package fedora.server.errors;

/**
 * Signals that a low-level error occurred writing to a stream.
 *
 * @author cwilper@cs.cornell.edu
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