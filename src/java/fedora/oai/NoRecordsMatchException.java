package fedora.oai;

/**
 * Signals that the combination of the values of the from, until, set and 
 * metadataPrefix arguments results in an empty list.
 *
 * This may occur while fulfilling a ListIdentifiers or ListRecords request.
 */
public class NoRecordsMatchException 
        extends OAIException {
        
    public NoRecordsMatchException() {
        super("noRecordsMatch", null);
    }

    public NoRecordsMatchException(String message) {
        super("noRecordsMatch", message);
    }
    
}