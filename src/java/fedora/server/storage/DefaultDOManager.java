package fedora.server.storage;

import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.StorageException;
import fedora.server.Context;
import fedora.server.Module;
import fedora.server.ReadOnlyContext;
import fedora.server.Server;

import java.util.Map;

/**
 * Provides access to digital object readers and writers.
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

    /** pid will always be non-null, context will always be non-null */
    protected DOReader getReaderForContext(String pid, ReadOnlyContext context) {
        return null;
    }

    /** pid may be null, context will always be non-null */
    protected DOWriter getWriterForContext(String pid, ReadOnlyContext context) {
        return null;
    }
    
}