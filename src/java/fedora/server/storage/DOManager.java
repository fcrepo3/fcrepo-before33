// FIXME: this is going to change so that 1) it takes a storageType on
// construction, 2) the config stuff needs to be smoothed out given new
// config thoughts, 3) it provides readers and writers based on the
// storageType order given in the constructor... or something? hmm.
// 4) the createObject and deleteObject things can be taken care of
// in terms of the PID generator and DORegistry.  The DORegistry is
// going to be reader/writer pair-specific, and won't live at this
// level, btw.

package fedora.server.storage;

import fedora.server.errors.ModuleInitializationException;
import fedora.server.Module;

import java.util.Map;

/**
 * Provides access to digital object reader/writer factories,
 * which ultimately provide DOReader and DOWriter objects on
 * a per-digital-object basis.
 *
 * @author cwilper@cs.cornell.edu
 */
public abstract class DOManager 
        extends Module {

    /**
     * Creates a new DOManager.
     */
    public DOManager(Map moduleParameters)
            throws ModuleInitializationException {
        super(moduleParameters);
    }

    /**
     * Gets the digital object reader factory best fit for
     * the given storage type.
     *
     * @param storageType The storage type.
     * @returns DOReaderFactory An appropriate provider of DOReaders.
     */
    public abstract DOReaderFactory getReaderFactory(String storageType);

    /**
     * Gets the digital object writer factory best fit for
     * the given storage type.
     *
     * @param storageType The storage type.
     * @returns DOWriterFactory An appropriate provider of DOWriters.
     */
    public abstract DOWriterFactory getWriterFactory(String storageType);
    
    /**
     * Gets a Registry for the given storage type.
     *
     * @param storageType The storage type.
     * @returns Registry A registry appropriate for the storage role
     */
    public abstract DORegistry getRegistry(String storageType);

}