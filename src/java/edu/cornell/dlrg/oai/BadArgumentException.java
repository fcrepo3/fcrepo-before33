package edu.cornell.dlrg.oai;

/**
 * Signals that a request includes illegal arguments, is missing required 
 * arguments, includes a repeated argument, or values for arguments have an 
 * illegal syntax.
 *
 * This may occur while fulfilling any request.
 */
public class BadArgumentException 
        extends OAIException {
        
    public BadArgumentException() {
        super("badArgument", null);
    }

    public BadArgumentException(String message) {
        super("badArgument", message);
    }
    
}