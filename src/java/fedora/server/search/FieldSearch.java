package fedora.server.search;

import java.util.List;

import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;

/**
 *
 * <p><b>Title:</b> FieldSearch.java</p>
 * <p><b>Description:</b> A provider of a simple field-based search service
 * across all objects in the repository</p>
 *
 * <p>Key object metadata and dublin core fields are searchable from via
 * implementations of this interface.</p>
 *
 * <p>Key fields include:<dir>
 * <i>pid, label, cModel, state, ownerId, cDate, mDate, dcmDate</i></dir></p>
 *
 * <p>Dublin core fields include:<dir>
 * <i>title, creator, subject, description, publisher, contributor, date, type,
 * format, identifier, source, language, relation, coverage, rights</i></dir></p>
 * <p></p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface FieldSearch {

    /**
     * Update the search indexes with information from the provided DOReader.
     *
     * @param reader the DOReader containing all the field information
     *        for the object
     * @throws ServerException if anything went wrong
     */
    public void update(DOReader reader)
            throws ServerException;

    /**
     * Remove an object from the search indexes.
     *
     * @param pid the unique id of the object whose info should be removed
     * @return true if object successfully removed; false otherwise.
     * @throws ServerException if anything went wrong
     */
    public boolean delete(String pid)
            throws ServerException;

    /**
     * Search across specific fields and return the desired fields.
     *
     * @param resultFields the desired fields
     * @param maxResults the maximum number of results the client wants
     * @param query the query
     * @return FieldSearchResult the results
     * @throws ServerException if anything went wrong
     */
    public FieldSearchResult findObjects(String[] resultFields,
            int maxResults, FieldSearchQuery query)
            throws ServerException;

    /**
     * Resume an in-progress search across specific fields and return the
     * desired fields.
     *
     * @param sessionToken the token of the session in which the remaining
     *        results can be found
     * @return FieldSearchResult the results
     * @throws ServerException if anything went wrong
     */
    public FieldSearchResult resumeFindObjects(String sessionToken)
            throws ServerException;

}