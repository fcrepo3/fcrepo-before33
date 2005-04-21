package fedora.oai;

/**
 *
 * <p><b>Title:</b> DateGranularitySupport.java</p>
 * <p><b>Description:</b> An indicator of the level of granularity in dates a
 * repository supports.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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