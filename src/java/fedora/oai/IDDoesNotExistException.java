package fedora.oai;

/**
 * Signals that the value of the identifier argument is unknown or illegal in 
 * this repository.
 *
 * This may occur while fulfilling a GetRecord or ListMetadataFormats request.
 */
public class IDDoesNotExistException 
        extends OAIException {
        
    public IDDoesNotExistException() {
        super("idDoesNotExist", null);
    }

    public IDDoesNotExistException(String message) {
        super("idDoesNotExist", message);
    }
    
}