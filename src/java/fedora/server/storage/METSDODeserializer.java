package fedora.server.storage;

import fedora.server.errors.StorageDeviceException;
import fedora.server.storage.types.DigitalObject;

import java.io.InputStream;

/**
 * Reads a METS-Fedora XML stream into a DigitalObject.
 */
public class METSDODeserializer 
        implements DODeserializer {
        
    private METSDODeserializer m_instance=new METSDODeserializer();

    private METSDODeserializer() { }
    
    public METSDODeserializer getInstance() {
        return m_instance;
    }
    
    public void deserialize(InputStream in, DigitalObject obj) 
            throws StorageDeviceException {
        // see sandy's code in definitivedoreader
    }

}