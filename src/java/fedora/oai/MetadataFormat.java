package fedora.oai;

/**
 * Describes a metadata format.
 * 
 * @see http://www.openarchives.org/OAI/openarchivesprotocol.html#ListMetadataFormats
 */
public interface MetadataFormat {

    /**
     * Get the prefix of the format.
     */
    public abstract String getPrefix();
    
    /**
     * Get the URL of the schema.
     */
    public abstract String getSchemaLocation();
    
    /**
     * Get the URI of the namespace.
     */
    public abstract String getNamespaceURI();
    
}