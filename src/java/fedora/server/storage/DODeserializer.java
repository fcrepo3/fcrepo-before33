package fedora.server.storage;

import fedora.server.errors.StorageException;
import fedora.server.storage.types.DigitalObject;

import java.io.InputStream;

/**
 * Reads an InputStream into a DigitalObject.
 */
public interface DODeserializer {

    public void deserialize(InputStream in, DigitalObject obj)
            throws StorageException;

}