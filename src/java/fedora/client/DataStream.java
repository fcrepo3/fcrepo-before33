package fedora.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * <p><b>Title:</b> DataStream.java</p>
 * <p><b>Description:</b> </p>
 * <p>The model of a datastream as it exists inside the editor.</p>
 *
 * <p>This class has getters and setters for the fields and bytes
 * of a datastream while it is being edited.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public abstract class DataStream {

    /** Empty stream */
    public final static ByteArrayInputStream
            EMPTY=new ByteArrayInputStream(new byte[0]);

    /** Identifier for INLINE datastreams */
    public final static int INLINE = 0;

    /** Identifier for BASIS datastreams */
    public final static int BASIS = 1;

    /** The file where the bytes are temporarily stored during editing */
    private File m_dataFile;

    /** The mime type of the datastream */
    private String m_mimeType;

    /** The identified for the datastream */
    private String m_id;

    /** The size of the datastream, in bytes */
    private long m_size;

    /** Whether this datastream is dirty */
    protected boolean m_dirty=true;

    /**
     * Constructs a datastream with a given temporary directory to
     * write itself to, and an identifier.
     */
    public DataStream(File tempDir, String id) {
        m_id=id;
        File m_dataFile=new File(tempDir, id);
        clearData();
    }

    /**
     * Returns INLINE or BASIS.
     */
    public abstract int getType();

    /**
     * Gets the id of the datastream inside the object.
     */
    public String getId() {
        return m_id;
    }

    /**
     * Gets the mime type.
     */
    public String getMimeType() {
        return m_mimeType;
    }

    /**
     * Sets the mime type.
     */
    public void setMimeType(String mimeType) {
        m_dirty=true;
        m_mimeType=mimeType;
    }

    /**
     * Gets the size, in bytes.
     */
    public long getSize() {
        return m_size;
    }

    /**
     * Gets an <code>InputStream</code> to the local copy of the datastream.
     */
    public InputStream getData()
            throws IOException {
        if (m_size==0) { return EMPTY; }
        return new FileInputStream(m_dataFile);
    }

    /**
     * Reads the bytes from the given <code>InputStream</code> as the data
     * for this digital object.  When finished, the <code>InputStream</code>
     * is closed.
     */
    public void setData(InputStream in)
            throws IOException {
        m_dirty=true;
        FileOutputStream out=new FileOutputStream(m_dataFile);
        byte[] buf=new byte[4096];
        int i=0;
        m_size=0;
        while((i=in.read(buf))!=-1) {
            m_size+=i;
            out.write(buf, 0, i);
        }
        in.close();
        out.close();
    }

    public boolean isDirty() {
        return m_dirty;
    }

    public void setClean() {
        m_dirty=false;
    }

    public void clearData() {
        m_size=0;
        m_dirty=true;
        m_dataFile.delete();
    }

}