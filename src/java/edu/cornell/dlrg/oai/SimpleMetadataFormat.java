package edu.cornell.dlrg.oai;

/**
 * A simple implementation of MetadataFormat that provides getters on the values
 * passed in the constructor.
 */
public class SimpleMetadataFormat
        implements MetadataFormat {
        
    private String m_prefix;
    private String m_schemaLocation;
    private String m_namespaceURI;
        
    public SimpleMetadataFormat(String prefix, String schemaLocation,
            String namespaceURI) {
        m_prefix=prefix;
        m_schemaLocation=schemaLocation;
        m_namespaceURI=namespaceURI;
    }

    public String getPrefix() {
        return m_prefix;
    }
    
    public String getSchemaLocation() {
        return m_schemaLocation;
    }
    
    public String getNamespaceURI() {
        return m_namespaceURI;
    }    
}