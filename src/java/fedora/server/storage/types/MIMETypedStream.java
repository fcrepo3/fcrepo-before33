package fedora.server.storage.types;

import java.io.InputStream;

/**
 *
 * <p><b>Title:</b> MIMETypedStream.java</p>
 * <p><b>Description:</b> Data structure for holding a MIME-typed stream.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class MIMETypedStream
{
    public String MIMEType;
    private InputStream stream;
    public Property[] header;

    /**
     * <p>Constructs a MIMETypedStream.</p>
     *
     * @param MIMEType The MIME type of the byte stream.
     * @param stream The byte stream.
     */
    public MIMETypedStream(String MIMEType, InputStream stream, Property[] header)
    {
        this.MIMEType = MIMEType;
        this.header = header;
        this.setStream(stream);
    }

    public InputStream getStream()
    {
      return stream;
    }

    public void setStream(InputStream stream)
    {
      this.stream = stream;
    }
}