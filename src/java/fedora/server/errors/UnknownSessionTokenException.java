package fedora.server.errors;

/**
 * Signals that the requested session was not found.
 */
public class UnknownSessionTokenException 
        extends ServerException {

    /**
     * Creates an UnknownSessionTokenException
     *
     * @param msg Description of the exception.
     */
    public UnknownSessionTokenException(String msg) {
           super(null, msg, null, null, null);
    }
}
