package fedora.server.storage;

import fedora.server.errors.ObjectExistsException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.StorageDeviceException;

import java.io.InputStream;

/**
 *
 * <p><b>Title:</b> TestStreamStorage.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
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
