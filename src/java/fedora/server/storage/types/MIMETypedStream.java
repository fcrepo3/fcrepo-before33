package fedora.server.storage.types;

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
    public byte[] stream;

    /**
     * <p>Constructs a MIMETypedStream.</p>
     *
     * @param MIMEType The MIME type of the byte stream.
     * @param stream The byte stream.
     */
    public MIMETypedStream(String MIMEType, byte[] stream)
    {
        this.MIMEType = MIMEType;
        this.stream = stream;
    }
}