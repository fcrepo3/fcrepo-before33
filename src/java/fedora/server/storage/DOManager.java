package fedora.server.storage;

import java.io.InputStream;

import fedora.server.errors.ServerException;
import fedora.server.Context;

/**
 * Provides context-appropriate digital object readers and writers
 * and repository query facilities.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface DOManager {
        
    /**
     * Gets a digital object reader.
     *
     * @param context The context of this request.
     * @param pid The PID of the object.
     * @return A reader.
     * @throws ServerException If anything went wrong.
     */
    public abstract DOReader getReader(Context context, String pid)
            throws ServerException;


    /**
     * Gets a DOWriter for an existing digital object.
     *
     * @param context The context of this request.
     * @param pid The PID of the object.
     * @return A writer, or null if the pid didn't point to an accessible object.
     * @throws ServerException If anything went wrong.
     */
    public abstract DOWriter getWriter(Context context, String pid)
            throws ServerException;

    /**
     * Creates a digital object with a newly-allocated pid, and returns 
     * a DOWriter on it.  The initial state will be "L" (locked).
     *
     * @param context The context of this request.
     * @param pid The PID of the object.
     * @return A writer.
     * @throws ServerException If anything went wrong.
     */
    public abstract DOWriter newWriter(Context context)
            throws ServerException;
    
    /**
     * Creates a copy of the digital object given by the InputStream,
     * with either a new PID or the PID indicated by the InputStream.
     *
     * @param context The context of this request.
     * @param in A serialization of the digital object.
     * @param format The format of the serialization.
     * @param newPid Whether a new PID should be generated or the one indicated
     *        by the InputStream should be used.
     * @throws ServerException If anything went wrong.
     */
    public abstract DOWriter newWriter(Context context, InputStream in, String format, String encoding, boolean newPid)
            throws ServerException;

    /**
     * Gets a list of object PIDs in the given state.
     * If state is given as null, all accessible PIDs for the context
     * are returned.
     */
    public abstract String[] listObjectPIDs(Context context, String state)
            throws ServerException;
    
}