package edu.cornell.dlrg.oai;

/**
 * An indicator of the level of granularity in dates a repository supports.
 */
public class DateGranularitySupport {

    /**
     * Indicates that the repository supports timestamp granularity in days.
     */
    public static final DateGranularitySupport DAYS=new DateGranularitySupport("YYYY-MM-DD");
    
    /**
     * Indicates that the repository supports timestamp granularity in seconds.
     */
    public static final DateGranularitySupport SECONDS=new DateGranularitySupport("YYYY-MM-DDThh:mm:ssZ");
    
    private String m_stringValue;

    private DateGranularitySupport(String stringValue) {
        m_stringValue=stringValue;
    }
    
    public String toString() {
        return m_stringValue;
    }

}