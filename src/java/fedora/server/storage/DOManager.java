package fedora.server.storage;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.search.FieldSearchQuery;
import fedora.server.search.FieldSearchResult;

/**
 *
 * <p><b>Title:</b> DOManager.java</p>
 * <p><b>Description:</b> A RepositoryReader that provides facilities for creating
 * and modifying objects within the repository, as well as
 * a query facility.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version 1.0
 */
public interface DOManager
        extends RepositoryReader {

    /**
     * Relinquishes control of a DOWriter back to the DOManager.
     * <p></p>
     * This is not the same as releasing the lock on a digital object.
     * To do that, use DOWriter.cancel() or DOWriter.commit(...).
     * <p></p>
     * When a DOManager provides a DOWriter, it creates two kinds of
     * locks.  One, the object lock, is created permanently.  This
     * is the lock that guarantees no other user can make a modification
     * to the object until the locking user is done making a change.  This is
     * a persistent lock in that it remains even after the system is
     * shutdown or the DOWriter object is garbage collected.
     * <p></p>
     * The other kind of lock is more temporary.  It's called a session lock.
     * This is used to guarantee that the same user can't get more than one
     * handle (DOWriter) on the same underlying object.  To release the
     * session lock, a DOWriter user calls this method.
     */
    public abstract void releaseWriter(DOWriter writer)
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
     * Gets a list of object PIDs (accessible in the given context) with the
     * given criteria.  Any parameter whose name ends with "Pattern" may
     * use the * and ? wildcards.  A parameter given as null means "any".
     */
    public String[] listObjectPIDs(Context context, String pidPattern,
            String foType, String lockedByPattern, String state,
            String labelPattern, String contentModelIdPattern,
            Calendar createDateMin, Calendar createDateMax,
            Calendar lastModDateMin, Calendar lastModDateMax)
            throws ServerException;

    public FieldSearchResult findObjects(Context context,
            String[] resultFields, int maxResults, FieldSearchQuery query)
            throws ServerException;

    public FieldSearchResult resumeFindObjects(Context context,
            String sessionToken)
            throws ServerException;

}