package edu.cornell.dlrg.oai;

/**
 * Signals that the value of the resumptionToken argument is invalid or expired.
 *
 * This may occur while fulfilling a ListIdentifiers, ListRecords, or ListSets
 * request.
 */
public class BadResumptionTokenException 
        extends OAIException {
        
    public BadResumptionTokenException() {
        super("badResumptionToken", null);
    }

    public BadResumptionTokenException(String message) {
        super("badResumptionToken", message);
    }
    
}