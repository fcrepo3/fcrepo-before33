package fedora.server.access;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class MIMETypedStream
{
    public String MIMEType;
    public byte[] stream;

    public MIMETypedStream() { }

    public MIMETypedStream(String MIMEType, byte[] stream)
    {
	this.MIMEType = MIMEType;
	this.stream = stream;
    }

}