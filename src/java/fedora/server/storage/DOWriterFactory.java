package fedora.server.storage;

/**
 * A provider of DOWriters.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface DOWriterFactory {

    /**
     * Gets a DOWriter for reading the object with the given PID.
     *
     * @param pid The PID of the object.
     * @returns DOWriter The writer for the object.
     */
    public DOWriter getWriter(String pid);
    
}