package fedora.server.storage.types;

import java.io.InputStream;
/**
 * <p>Title: MIMETypedStream.java</p>
 * <p>Description: Data structure for holding a MIME-typed stream.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class MIMETypedStream
{
    public String MIMEType;
    // RLW: change required by conversion fom byte[] to InputStream
    private InputStream stream;

    /**
     * <p>Constructs a MIMETypedStream.</p>
     *
     * @param MIMEType The MIME type of the byte stream.
     * @param stream The byte stream.
     */
    public MIMETypedStream(String MIMEType, InputStream stream)
    {
        this.MIMEType = MIMEType;
        this.setStream(stream);
    }

    // RLW: change required by conversion fom byte[] to InputStream
    public InputStream getStream()
    {
      return stream;
    }

    public void setStream(InputStream stream)
    {
      this.stream = stream;
    }
    // RLW: change required by conversion fom byte[] to InputStream
}