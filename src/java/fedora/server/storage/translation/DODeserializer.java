package fedora.server.storage.translation;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamReadException;
import fedora.server.storage.types.DigitalObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * Reads an InputStream into a DigitalObject.
 */
public interface DODeserializer {

    public DODeserializer getInstance() throws ServerException;

    public void deserialize(InputStream in, DigitalObject obj, String encoding)
            throws ObjectIntegrityException, StreamIOException, 
            UnsupportedEncodingException;

}