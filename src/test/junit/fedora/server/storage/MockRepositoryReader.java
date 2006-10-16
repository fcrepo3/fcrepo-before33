package fedora.server.storage;

import java.util.HashMap;
import java.util.Map;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.storage.types.DigitalObject;

/**
 * Mock implementation of <code>RepositoryReader</code> for testing.
 *
 * This holds <code>DigitalObject</code> instances in memory.
 */
public class MockRepositoryReader implements RepositoryReader {

    /**
     * The <code>DigitalObject</code>s in the "repository", keyed by PID.
     */
    private Map _objects = new HashMap();

    public MockRepositoryReader() {
    }

    /**
     * Adds/replaces the object into the "repository".
     */
    public synchronized void putObject(DigitalObject obj) {
        _objects.put(obj.getPid(), obj);
    }

    /**
     * Removes the object from the "repository" and returns it (or null
     * if it didn't exist in the first place).
     */
    public synchronized DigitalObject deleteObject(String pid) {
        return (DigitalObject) _objects.remove(pid);
    }

    // Mock methods from RepositoryReader interface.

    /**
     * {@inheritDoc}
     */
    public synchronized DOReader getReader(boolean cachedObjectRequired, 
                              Context context, 
                              String pid)
            throws ServerException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized BMechReader getBMechReader(boolean cachedObjectRequired,
                                      Context context,
                                      String pid)
            throws ServerException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized BDefReader getBDefReader(boolean cachedObjectRequired, 
                                    Context context, 
                                    String pid)
            throws ServerException {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized String[] listObjectPIDs(Context context)
            throws ServerException {
        return null;
    }

}