package fedora.oai;

/**
 *
 * <p><b>Title:</b> SimpleMetadataFormat.java</p>
 * <p><b>Description:</b> A simple implementation of MetadataFormat that
 * provides getters on the values passed in the constructor.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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