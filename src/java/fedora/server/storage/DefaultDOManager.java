package fedora.server.storage;

import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.StorageException;
import fedora.server.Module;
import fedora.server.Server;

import java.util.Map;

/**
 * Provides access to digital object readers and writers.
 *
 * Note that both instance methods throw StorageException and
 * ObjectNotFoundException (a subclass of the abstract
 * StorageException). Implementations of DOManager are expected 
 * to throw concrete subclasses of StorageException where needed.
 *
 * @author cwilper@cs.cornell.edu
 */
public class DefaultDOManager 
        extends DOManager {

    /**
     * Creates a new DefaultDOManager.
     */
    public DefaultDOManager(Map moduleParameters, Server server, String role)
            throws ModuleInitializationException {
        super(moduleParameters, server, role);
    }
    
    /**
     * Gets a digital object reader.
     *
     * @param pid The PID of the object.
     * @returns DOReader A reader.
     * @throws StorageException If the request could not be fulfilled.
     * @throws ObjectNotFoundException If the object requested was not found.
     */
    public DOReader getReader(String pid) {
      //      throws StorageException, 
      //             ObjectNotFoundException {
        return null;
    }

    /**
     * Gets a digital object writer.
     *
     * If the pid parameter is null, a new digital object will be
     * created (ObjectNotFound will never be thrown in this case).
     *
     * @param pid The PID of the object.
     * @returns DOWriter A writer.
     * @throws StorageException If the request could not be fulfilled.
     * @throws ObjectNotFoundException If the object requested was not found.
     */
    public DOWriter getWriter(String pid) {
           // throws StorageException, 
           //        ObjectNotFoundException {
        return null;
    }

}