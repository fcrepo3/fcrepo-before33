package fedora.server.storage;

/**
 * A provider of DOReaders.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface DOReaderFactory {

    /**
     * Gets a DOReader for reading the object with the given PID.
     *
     * @param pid The PID of the object.
     * @returns DOReader The reader for the object.
     */
    public DOReader getReader(String pid);
    
}