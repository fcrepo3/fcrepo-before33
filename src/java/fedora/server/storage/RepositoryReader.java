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