package fedora.server.storage;

import fedora.server.errors.StorageDeviceException;
import fedora.server.storage.types.DigitalObject;

import java.io.IOException;
import java.io.OutputStream;

public class METSDOSerializer 
        implements DOSerializer {
        
    private METSDOSerializer m_instance=new METSDOSerializer();

    private METSDOSerializer() { }
    
    public METSDOSerializer getInstance() {
        return m_instance;
    }
    
    public void serialize(DigitalObject obj, OutputStream out) 
            throws StorageDeviceException {
        StringBuffer buf=new StringBuffer();
        buf.append("<?xml version=\"1.0\" ?>\n");
        buf.append("<mets xmlns=\"http://www.loc.gov/METS/\"\n");
        buf.append("      OBJID=\"" + obj.getPid() + "\"\n");
        buf.append("      LABEL=\"" + obj.getLabel() + "\">\n");
        buf.append("</mets>");
        try {
            out.write(buf.toString().getBytes());
            out.flush();
        } catch (IOException ioe) {
            throw new StorageDeviceException("Problem writing to outputstream while serializing to mets: " + ioe.getMessage());
        } finally {
            try {
            out.close();
            } catch (IOException ioe2) { }
        }
    }

}