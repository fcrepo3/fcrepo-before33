package fedora.server.storage;

import fedora.server.errors.StorageDeviceException;
import fedora.server.storage.types.DigitalObject;

import java.io.InputStream;

/**
 * Reads a METS-Fedora XML stream into a DigitalObject.
 */
public class METSDODeserializer 
        implements DODeserializer {

    public METSDODeserializer() { 
    }
    
    public void deserialize(InputStream in, DigitalObject obj)  {
    //add appropriate throws clause, and
        // see sandy's code in definitivedoreader
    }

}