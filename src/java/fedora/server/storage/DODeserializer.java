package fedora.server.storage;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamReadException;
import fedora.server.storage.types.DigitalObject;

import java.io.InputStream;

/**
 * Reads an InputStream into a DigitalObject.
 */
public interface DODeserializer {

    public void deserialize(InputStream in, DigitalObject obj)
            throws ObjectIntegrityException, StreamIOException, 
            StreamReadException;

}