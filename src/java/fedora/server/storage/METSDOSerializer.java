package fedora.server.storage;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamWriteException;
import fedora.server.storage.types.DigitalObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class METSDOSerializer 
        implements DOSerializer {
        
    private String m_characterEncoding;

    // java always supports UTF-8 and UTF-16 btw.
    public METSDOSerializer(String characterEncoding) 
            throws UnsupportedEncodingException {
        m_characterEncoding=characterEncoding;
        StringBuffer buf=new StringBuffer();
        buf.append("test");
        byte[] temp=buf.toString().getBytes(m_characterEncoding);
    }
    
    public String getEncoding() {
        return m_characterEncoding;
    }

    // subclasses should override this
    public static String getVersion() {
        return "1.0";
    }
    
    public void serialize(DigitalObject obj, OutputStream out) 
            throws ObjectIntegrityException, StreamIOException, 
            StreamWriteException {
        StringBuffer buf=new StringBuffer();
        buf.append("<?xml version=\"1.0\" ");
        buf.append("encoding=\"");
        buf.append(m_characterEncoding);
        buf.append("\" ?>\n");
        buf.append("<mets xmlns=\"http://www.loc.gov/METS/\"\n");
        buf.append("      OBJID=\"" + obj.getPid() + "\"\n");
        buf.append("      LABEL=\"" + obj.getLabel() + "\">\n");
        buf.append("</mets>");
        try {
            out.write(buf.toString().getBytes(m_characterEncoding));
            out.flush();
        } catch (IOException ioe) {
            // this could be an unsupportedencodingexception, but it won't be 
            // because we already checked for that in the constructor
            throw new StreamWriteException("Problem writing to outputstream while serializing to mets: " + ioe.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException ioe2) { 
                throw new StreamIOException("Problem closing outputstream after attempting to serialize to mets: " + ioe2.getMessage());
            }
        }
        if (1==2) throw new ObjectIntegrityException("bad object");
    }
    
    public boolean equals(Object o) {
        if (this==o) { return true; }
        try {
            return equals((METSDOSerializer) o);
        } catch (ClassCastException cce) {
            return false;
        }
    }
    
    public boolean equals(METSDOSerializer o) {
        return (o.getEncoding().equals(getEncoding())
                && o.getVersion().equals(getVersion()));
    }

}