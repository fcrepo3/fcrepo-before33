package edu.cornell.dlrg.oai;

/**
 * Signals that there are no metadata formats available for the specified item.
 *
 * This may occur while fulfilling a ListMetadataFormats request.
 */
public class NoMetadataFormatsException 
        extends OAIException {
        
    public NoMetadataFormatsException() {
        super("noMetadataFormats", null);
    }

    public NoMetadataFormatsException(String message) {
        super("noMetadataFormats", message);
    }
    
}