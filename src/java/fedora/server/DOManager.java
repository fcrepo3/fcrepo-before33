package fedora.server;

/**
 * A Module that provides access to the registry and
 * digital object reader/writer factories.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface DOManager 
        extends Module {

    /** storage roles */
    public static int DEFINITIVE=1;
    public static int FAST=2;

    /**
     * Gets the digital object reader factory best fit for
     * the given role.
     *
     * @param role The role
     * @returns DOReaderFactory A reader factory well-fit to
     *          return DOReaders in the store with the given role.
     */
    public DOReaderFactory getReaderFactory(int role);


    /**
     * Same as above, but for writer..will comment better later.
     */    
    public DOWriterFactory getWriterFactory(int role);

}