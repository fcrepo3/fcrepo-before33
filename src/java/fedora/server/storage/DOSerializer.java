package fedora.server.storage;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamWriteException;
import fedora.server.storage.types.DigitalObject;

import java.io.OutputStream;

public interface DOSerializer {

    public void serialize(DigitalObject obj, OutputStream out)
            throws ObjectIntegrityException, StreamIOException,
            StreamWriteException;

}