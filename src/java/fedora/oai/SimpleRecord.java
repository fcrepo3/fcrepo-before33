package fedora.oai;

import java.util.Set;

/**
 *
 * <p><b>Title:</b> SimpleRecord.java</p>
 * <p><b>Description:</b> A simple implementation of Record that provides
 * getters on the values passed in the constructor.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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
