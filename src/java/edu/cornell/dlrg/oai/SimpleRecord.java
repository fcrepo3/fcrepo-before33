package edu.cornell.dlrg.oai;

import java.util.Date;
import java.util.Set;

/**
 * A simple implementation of Record that provides getters on the values
 * passed in the constructor.
 */
public class SimpleRecord
        implements Record {
        
    private Header m_header;
    private String m_metadata;
    private Set m_abouts;

    public SimpleRecord(Header header, String metadata, Set abouts) {
        m_header=header;
        m_metadata=metadata;
        m_abouts=abouts;
    }

    public Header getHeader() {
        return m_header;
    }
    
    public String getMetadata() {
        return m_metadata;
    }
    
    public Set getAbouts() {
        return m_abouts;
    }

}
