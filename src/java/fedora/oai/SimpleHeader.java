package fedora.oai;

import java.util.Date;
import java.util.Set;

/**
 * A simple implementation of Header that provides getters on the values
 * passed in the constructor.
 */
public class SimpleHeader
        implements Header {
        
    private String m_identifier;
    private Date m_datestamp;
    private Set m_setSpecs;
    private boolean m_isAvailable;

    public SimpleHeader(String identifier, Date datestamp, Set setSpecs,
            boolean isAvailable) {
        m_identifier=identifier;
        m_datestamp=datestamp;
        m_setSpecs=setSpecs;
        m_isAvailable=isAvailable;
    }
    
    public String getIdentifier() {
        return m_identifier;
    }
    
    public Date getDatestamp() {
        return m_datestamp;
    }
    
    public Set getSetSpecs() {
        return m_setSpecs;
    }
            
    public boolean isAvailable() {
        return m_isAvailable;
    }
    
}