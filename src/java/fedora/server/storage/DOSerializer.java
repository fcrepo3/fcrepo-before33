package fedora.server.storage;

import fedora.server.errors.StorageException;
import fedora.server.storage.types.DigitalObject;

import java.io.OutputStream;

public interface DOSerializer {

    public void serialize(DigitalObject obj, OutputStream out)
            throws StorageException;

}