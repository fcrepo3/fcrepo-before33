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
 * Note that both instance methods throw StorageException and
 * ObjectNotFoundException (a subclass of the abstract
 * StorageException). Implementations of DOManager are expected 
 * to throw concrete subclasses of StorageException where needed.
 *
 * @author cwilper@cs.cornell.edu
 */
public abstract class DOManager 
        extends Module {
        
    private Context m_defaultContext;

    /**
     * Creates a new DOManager with an empty default context.
     */
    public DOManager(Map moduleParameters, Server server, String role)
            throws ModuleInitializationException {
        super(moduleParameters, server, role);
        m_defaultContext=null;
    }
    
    public void setDefaultContext(Context defaultContext) {
        m_defaultContext=defaultContext;
    }

    /**
     * Gets a digital object reader, using the default context.
     *
     * @param pid The PID of the object.
     * @returns DOReader A reader.
     * @throws StorageException If the request could not be fulfilled.
     * @throws ObjectNotFoundException If the object requested was not found.
     */
    public final DOReader getReader(String pid)
            throws StorageException, 
                   ObjectNotFoundException {
        return getReader(pid, null);
    }

    /**
     * Gets a digital object reader, using the default context with additional
     * or overridden values from the given context.
     */
    public final DOReader getReader(String pid, Context context)
            throws StorageException, 
                   ObjectNotFoundException {
        return getReaderForContext(pid, ReadOnlyContext.getUnion(m_defaultContext, context));
    }

    protected abstract DOReader getReaderForContext(String pid, ReadOnlyContext context)
            throws StorageException, 
                   ObjectNotFoundException;

    /**
     * Gets a digital object writer, using the default context.
     *
     * If the pid parameter is null, a new digital object will be
     * created (ObjectNotFound will never be thrown in this case).
     *
     * @param pid The PID of the object.
     * @returns DOWriter A writer.
     * @throws StorageException If the request could not be fulfilled.
     * @throws ObjectNotFoundException If the object requested was not found.
     */
    public final DOWriter getWriter(String pid)
            throws StorageException, 
                   ObjectNotFoundException {
        return getWriter(pid, null);
    }
    
    /**
     * Gets a digital object writer, using the default context with additional
     * or overridden values from the given context.
     */
    public final DOWriter getWriter(String pid, Context context)
            throws StorageException, 
                   ObjectNotFoundException {
        return getWriterForContext(pid, ReadOnlyContext.getUnion(m_defaultContext, context));
    }

    /**
     * Gets a digital object writer on a new digital object, using the default context.
     */
    public final DOWriter getWriter()
            throws StorageException {
        return getWriter(null, m_defaultContext);
    }
    
    /**
     * Gets a digital object writer on a new digital object, using the default 
     * context with additional or overridden values from the given context.
     */
    public final DOWriter getWriter(Context context)
            throws StorageException, ObjectNotFoundException {
        return getWriterForContext(null, ReadOnlyContext.getUnion(m_defaultContext, context));
    }

    protected abstract DOWriter getWriterForContext(String pid, ReadOnlyContext context)
            throws StorageException, 
                   ObjectNotFoundException;
        
}