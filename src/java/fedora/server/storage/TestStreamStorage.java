package fedora.server.storage;

import fedora.server.errors.ObjectExistsException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.StorageDeviceException;

import java.io.InputStream;

public interface TestStreamStorage {

    public void add(String id, InputStream in) 
            throws ObjectExistsException, StorageDeviceException;

    public void replace(String id, InputStream in) 
            throws ObjectNotFoundException, StorageDeviceException;

    public InputStream retrieve(String id)
            throws ObjectNotFoundException, StorageDeviceException;

    public void delete(String id)
            throws ObjectNotFoundException, StorageDeviceException;

}
