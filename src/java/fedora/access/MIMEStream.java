package fedora.access;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public final class MIMEStream
{
    //	instance variables
    public String MIMEType;
    public byte[] data;

    //	constructors
    public MIMEStream() { }
    public MIMEStream(String MIMEType, byte[] data)
    {
	this.MIMEType = MIMEType;
	this.data = data;
    }
}