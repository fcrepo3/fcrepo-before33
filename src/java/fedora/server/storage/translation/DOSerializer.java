package fedora.server.storage.translation;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamWriteException;
import fedora.server.storage.types.DigitalObject;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public interface DOSerializer {

    public DOSerializer getInstance() throws ServerException;

    public void serialize(DigitalObject obj, OutputStream out, String encoding)
            throws ObjectIntegrityException, StreamIOException, 
            UnsupportedEncodingException;

}