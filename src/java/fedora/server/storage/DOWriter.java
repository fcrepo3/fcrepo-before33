package fedora.server.storage;

import java.util.Date;

import fedora.server.errors.ServerException;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Disseminator;

/**
 *
 * <p><b>Title:</b> DOWriter.java</p>
 * <p><b>Description:</b> The standard interface for write operations on a
 * digital object.</p>
 *
 * <p>A <code>DOWriter</code> instance is a handle on a Fedora digital object,
 * and is obtained via a <code>getWriter(String)</code> call on a
 * <code>DOManager</code>.</p>
 *
 * <p>Call save() to save changes while working with a DOWriter, where the
 * DOWriter handle may be lost but the changes need to be remembered.</p>
 *
 * <p>Work with a DOWriter ends with either commit() or cancel().</p>
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
public interface DOWriter
        extends DOReader {

    /**
     * Sets the state of the entire digital object.
     *
     * @param state The state.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public void setState(String state) throws ServerException;

    /**
     * Sets the state for all versions of the specified datastream.
     *
     * @param id The datastream id.
     * @param state The state.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public void setDatastreamState(String id, String state) throws ServerException;

    /**
     * Sets the mime type for all versions of the specified datastream.
     *
     * @param id The datastream id.
     * @param mimeType The new mime type.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public void setDatastreamMimeType(String id, String mimeType) throws ServerException;

    public void setDatastreamAltIDs(String id, String[] altIDs) throws ServerException;
    public void setDatastreamFormatURI(String id, String formatURI) throws ServerException;
    public void setDatastreamVersionable(String id, boolean versionable) throws ServerException;    

    /**
     * Sets the state for all versions of the specified disseminator.
     *
     * @param id The disseminator id.
     * @param state The state.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public void setDisseminatorState(String id, String state) throws ServerException;

    /**
     * Sets the label of the digital object.
     *
     * @param label The label.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public void setLabel(String label) throws ServerException;

    /**
     * Removes the entire digital object.
     *
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public void remove() throws ServerException;

    /**
     * Adds a datastream to the object.
     *
     * @param datastream The datastream.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public void addDatastream(Datastream datastream) throws ServerException;

    /**
     * Adds a disseminator to the object.
     *
     * @param disseminator The disseminator.
     * @return An internally-unique disseminator id.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public void addDisseminator(Disseminator disseminator)
            throws ServerException;

    /**
     * Removes a range of datastream versions from an object without leaving
     * anything behind.  If any integrity checks need to be done, they should
     * be done outside of this code.
     *
     * @param id The id of the datastream.
     * @param start The start date (inclusive) of versions to remove.  If
     *        <code>null</code>, this is taken to be the smallest possible
     *        value.
     * @param end The end date (inclusive) of versions to remove.  If
     *        <code>null</code>, this is taken to be the greatest possible
     *        value.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public Date[] removeDatastream(String id, Date start, Date end)
            throws ServerException;

    /**
     * Removes a range of disseminator versions from an object without leaving
     * anything behind.  If any integrity checks need to be done, they should
     * be done outside of this code.
     *
     * @param id The id of the datastream.
     * @param start The start date (inclusive) of versions to remove.  If
     *        <code>null</code>, this is taken to be the smallest possible
     *        value.
     * @param end The end date (inclusive) of versions to remove.  If
     *        <code>null</code>, this is taken to be the greatest possible
     *        value.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public Date[] removeDisseminator(String id, Date start, Date end)
            throws ServerException;

    /**
     * Saves the changes thus far to the permanent copy of the digital object.
     *
     * @param logMessage An explanation of the change(s).
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public void commit(String logMessage) throws ServerException;

    /**
     * Marks this DOWriter handle invalid (unusable).
     */
    public void invalidate();

    /**
     * Generate a unique id for a datastream.
     */
    public String newDatastreamID();

    /**
     * Generate a unique id for a datastream version.
     */
    public String newDatastreamID(String dsID);

    /**
     * Generate a unique id for a disseminator.
     */
    public String newDisseminatorID();

    /**
     * Generate a unique id for a disseminator version.
     */
    public String newDisseminatorID(String dissID);

    /**
     * Generate a unique id for a datastreamBindingMap.
     */
    public String newDatastreamBindingMapID();

    /**
     * Generate a unique id for an audit record.
     */
    public String newAuditRecordID();

}