package fedora.oai;

import java.util.Date;

/**
 * A simple implementation of ResumptionToken that provides getters on the 
 * values passed in the constructor.
 */
public class SimpleResumptionToken
        implements ResumptionToken {
        
    private String m_value;
    private Date m_expirationDate;
    private long m_completeListSize;
    private long m_cursor;
        
    public SimpleResumptionToken(String value, Date expirationDate,
            long completeListSize, long cursor) {
        m_value=value;
        m_expirationDate=expirationDate;
        m_completeListSize=completeListSize;
        m_cursor=cursor;
    }
    
    public String getValue() {
        return m_value;
    }
    
    public Date getExpirationDate() {
        return m_expirationDate;
    }
    
    public long getCompleteListSize() {
        return m_completeListSize;
    }
    
    public long getCursor() {
        return m_cursor;
    }
    
}