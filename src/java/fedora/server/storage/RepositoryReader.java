package fedora.server.storage;

import fedora.server.errors.ServerException;
import fedora.server.Context;

/**
 *
 * <p><b>Title:</b> RepositoryReader.java</p>
 * <p><b>Description:</b> Provides context-appropriate digital object readers and
 * the ability to list all objects (accessible in the given
 * context) within the repository.</p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface RepositoryReader {

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

    public abstract BMechReader getBMechReader(Context context, String pid)
            throws ServerException;

    public abstract BDefReader getBDefReader(Context context, String pid)
            throws ServerException;

    /**
     * Gets a list of PIDs (accessible in the given context)
     * of all objects in the repository.
     */
    public String[] listObjectPIDs(Context context)
            throws ServerException;

}