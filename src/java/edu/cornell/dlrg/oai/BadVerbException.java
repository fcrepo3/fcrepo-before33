package edu.cornell.dlrg.oai;

/**
 * Signals that the value of the verb argument is not a legal OAI-PMH verb, 
 * the verb argument is missing, or the verb argument is repeated. 
 */
public class BadVerbException 
        extends OAIException {
        
    public BadVerbException() {
        super("badVerb", null);
    }

    public BadVerbException(String message) {
        super("badVerb", message);
    }
    
}