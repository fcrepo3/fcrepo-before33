package fedora.server.journal;

import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;
import fedora.server.management.Management;
import fedora.server.management.ManagementDelegate;

/**
 * <p>
 * <b>Title:</b> JournalWorker.java
 * </p>
 * <p>
 * <b>Description:</b> A common interface for the <code>JournalConsumer</code>
 * and <code>JournalCreator</code> classes. These classes form the
 * implementation layer between the <code>Journaller</code> and the
 * <code>ManagementDelegate</code>.
 * </p>
 * 
 * @author jblake@cs.cornell.edu
 * @version $Id$
 */

public interface JournalWorker extends Management {
    /**
     * Called by the Journaller during post-initialization, with a reference to
     * the ManagementDelegate module.
     */
    public void setManagementDelegate(ManagementDelegate delegate)
            throws ModuleInitializationException;

    /**
     * Called when the Journaller module receives a shutdown() from the server.
     */
    public void shutdown() throws ModuleShutdownException;
}
