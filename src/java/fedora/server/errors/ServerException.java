package fedora.server.errors;

import java.util.Locale;

/**
 * Signifies that an error occurred in the server.
 *
 * @author cwilper@cs.cornell.edu
 */
public abstract class ServerException 
        extends Exception {

    /**
     * Creates a ServerException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public ServerException(String message) {
        super(message);
    }

    public String getMessage(Locale locale) {
        return getMessage();        
    }

    public String getCode() {
        return "UNKNOWN";
    }

}