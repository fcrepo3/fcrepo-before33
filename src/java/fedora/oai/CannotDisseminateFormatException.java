package fedora.oai;

/**
 * Signals that the metadata format identified by the value given for the 
 * metadataPrefix argument is not supported by the item or by the repository.
 *
 * This may occur while fulfilling a GetRecord, ListIdentifiers, or ListRecords
 * request.
 */
public class CannotDisseminateFormatException 
        extends OAIException {
        
    public CannotDisseminateFormatException() {
        super("cannotDisseminateFormat", null);
    }

    public CannotDisseminateFormatException(String message) {
        super("cannotDisseminateFormat", message);
    }
    
}