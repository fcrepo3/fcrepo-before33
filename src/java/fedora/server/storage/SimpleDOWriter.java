package fedora.server.storage;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fedora.server.Context;
import fedora.server.Logging;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamReadException;
import fedora.server.errors.StreamWriteException;
import fedora.server.errors.StorageDeviceException;
import fedora.server.errors.ObjectExistsException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.ValidationException;
import fedora.server.storage.DefaultDOManager;
import fedora.server.storage.translation.DOTranslator;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Disseminator;

/**
 *
 * <p><b>Title:</b> SimpleDOWriter.java</p>
 * <p><b>Description:</b> A <code>DOWriter</code> that uses a DigitalObject
 * under the hood.</p>
 *
 * <p>This interface supports transaction behavior with the commit(String) and
 * rollBack() methods.  When a DOWriter is instantiated, there is an implicit
 * transaction.  Write methods may be called, but they won't affect the
 * the underlying data store until commit(String) is invoked.  This also has
 * the effect of creating another implicit transaction.  If temporary
 * changes are no longer wanted, rollBack() may be called to return the object
 * to it's original form.  rollBack() is only valid for the current transaction.</p>
 *
 * <p>The read methods of DOWriter reflect on the composition of the object in
 * the context of the current transaction.</p>
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
public class SimpleDOWriter
        extends SimpleDOReader
        implements DOWriter {

    private static ObjectIntegrityException ERROR_PENDING_REMOVAL =
            new ObjectIntegrityException("That can't be done because you said "
                    + "I should remove the object and i assume that's what you "
                    + "want unless you call rollback()");

    private static ObjectIntegrityException ERROR_INVALIDATED =
            new ObjectIntegrityException("The handle is no longer valid "
                    + "... this object has already been committed or explicitly"
                    + " invalidated.");

    private DigitalObject m_obj;
    private Context m_context;
    private DefaultDOManager m_mgr;

    private boolean m_pendingRemoval=false;
    private boolean m_invalidated=false;

    public SimpleDOWriter(Context context, DefaultDOManager mgr,
            DOTranslator translator, String shortExportFormat,
            String longExportFormat, String encoding, DigitalObject obj,
            Logging logTarget) {
        super(context, mgr, translator, shortExportFormat, longExportFormat,
                encoding, obj, logTarget);
        m_context=context;
        m_obj=obj;
        m_mgr=mgr;
    }

    public void setState(String state)
            throws ObjectIntegrityException {
        assertNotInvalidated();
        assertNotPendingRemoval();
        m_obj.setState(state);
    }

    public void setLabel(String label)
            throws ObjectIntegrityException {
        assertNotInvalidated();
        assertNotPendingRemoval();
        m_obj.setLabel(label);
    }

    /**
     * Removes the entire digital object.
     *
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public void remove()
            throws ObjectIntegrityException {
        assertNotInvalidated();
        assertNotPendingRemoval();
        m_pendingRemoval=true;
    }

    /**
     * Adds a datastream to the object.
     *
     * @param datastream The datastream.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public void addDatastream(Datastream datastream)
            throws ServerException {
        assertNotInvalidated();
        assertNotPendingRemoval();
        m_obj.datastreams(datastream.DatastreamID).add(datastream);
    }

    /**
     * Adds a disseminator to the object.
     *
     * @param disseminator The disseminator.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public void addDisseminator(Disseminator disseminator)
            throws ServerException {
        assertNotInvalidated();
        assertNotPendingRemoval();
        m_obj.disseminators(disseminator.dissID).add(disseminator);
    }

    /**
     * Removes a datastream from the object.
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
    public void removeDatastream(String id, Date start, Date end)
            throws ServerException {
        assertNotInvalidated();
        assertNotPendingRemoval();
        List versions=m_obj.datastreams(id);
        ArrayList removeList=new ArrayList();
        for (int i=0; i<versions.size(); i++) {
            Datastream ds=(Datastream) versions.get(i);
            if (start!=null) {
                if (end!=null) {
                    if ( (ds.DSCreateDT.compareTo(start)>=0)
                            && (ds.DSCreateDT.compareTo(end)<=0) ) {
                        removeList.add(ds);
                    }
                } else {
                    if (ds.DSCreateDT.compareTo(start)>=0) {
                        removeList.add(ds);
                    }
                }
            } else {
                if (end!=null) {
                    if (ds.DSCreateDT.compareTo(end)<=0) {
                        removeList.add(ds);
                    }
                } else {
                    removeList.add(ds);
                }
            }
        }
        versions.removeAll(removeList);
    }

    /**
     * Removes a disseminator from the object.
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
    public void removeDisseminator(String id, Date start, Date end)
            throws ServerException {
        assertNotInvalidated();
        assertNotPendingRemoval();
        List versions=m_obj.disseminators(id);
        ArrayList removeList=new ArrayList();
        for (int i=0; i<versions.size(); i++) {
            Disseminator diss=(Disseminator) versions.get(i);
            if (start!=null) {
                if (end!=null) {
                    if ( (diss.dissCreateDT.compareTo(start)>=0)
                            && (diss.dissCreateDT.compareTo(end)<=0) ) {
                        removeList.add(diss);
                    }
                } else {
                    if (diss.dissCreateDT.compareTo(start)>=0) {
                        removeList.add(diss);
                    }
                }
            } else {
                if (end!=null) {
                    if (diss.dissCreateDT.compareTo(end)<=0) {
                        removeList.add(diss);
                    }
                } else {
                    removeList.add(diss);
                }
            }
        }
        versions.removeAll(removeList);
    }

    /**
     * Saves the changes thus far to the permanent copy of the digital object.
     *
     * @param logMessage An explanation of the change(s).
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public void commit(String logMessage)
            throws ServerException {
        assertNotInvalidated();
        m_mgr.doCommit(m_context, m_obj, logMessage, m_pendingRemoval);
        invalidate();
    }

    public void save() {
    }

    public void cancel() {
        // cleanup temp, release lock, and invalidate
    }

    public void invalidate() {
        m_invalidated=true;
    }

    private void assertNotPendingRemoval()
            throws ObjectIntegrityException {
        if (m_pendingRemoval)
            throw ERROR_PENDING_REMOVAL;
    }

    private void assertNotInvalidated()
            throws ObjectIntegrityException {
        if (m_invalidated)
            throw ERROR_INVALIDATED;
    }
}