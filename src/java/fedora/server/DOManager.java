package fedora.server;

/**
 * Provides access to digital object reader/writer factories,
 * which ultimately provide DOReader and DOWriter objects on
 * a per-digital-object basis.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface DOManager 
        extends Module {

    /**
     * Gets the digital object reader factory best fit for
     * the given storage type.
     *
     * @param storageType The storage type.
     * @returns DOReaderFactory An appropriate provider of DOReaders.
     */
    public DOReaderFactory getReaderFactory(String storageType);

    /**
     * Gets the digital object writer factory best fit for
     * the given storage type.
     *
     * @param storageType The storage type.
     * @returns DOWriterFactory An appropriate provider of DOWriters.
     */
    public DOWriterFactory getWriterFactory(String storageType);
    
    /**
     * Gets a Registry for the given storage type.
     *
     * @param storageType The storage type.
     * @returns Registry A registry appropriate for the storage role
     */
    public DORegistry getRegistry(String storageType);

}