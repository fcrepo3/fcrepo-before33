package fedora.client;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * <p><b>Title:</b> BasisDataStream.java</p>
 * <p><b>Description:</b> </p>
 * <p>Basis datastreams can be:</p>
 * <p>custodianship:</p>
 * <ul>
 * <li>internally managed</li>
 * <li>externally managed</li>
 * </ul>
 * <p>storage:</p>
 * <ul>
 * <li>internally stored</li>
 * <li>externally stored</li>
 * <ul>
 * <li>must be externally </li>
 * </ul></ul>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class BasisDataStream
        extends DataStream {

    private HashSet m_descriptiveStreams=new HashSet();
    private boolean m_internallyStored=true;
    private String m_location;

    public BasisDataStream(File tempDir, String id) {
        super(tempDir, id);
    }

    public final int getType() {
        return DataStream.BASIS;
    }

    public void addDescriptiveStream(InlineDataStream inlineStream) {
        m_dirty=true;
        m_descriptiveStreams.add(inlineStream);
    }

    public void removeDescriptiveStream(InlineDataStream inlineStream) {
        m_dirty=true;
        m_descriptiveStreams.remove(inlineStream);
    }

    public Iterator descriptiveStreams() {
        return m_descriptiveStreams.iterator();
    }

    public boolean isInternallyStored() {
        return m_internallyStored;
    }

    public void setLocation(String location) {
        m_location=location;
        m_internallyStored=false;
        clearData();
    }

    public String getLocation() {
        return m_location;
    }

    public void setData(InputStream in)
            throws IOException {
        super.setData(in);
        m_location=null;
        m_internallyStored=true;
    }

}