package fedora.server.storage;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import fedora.server.Context;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ObjectExistsException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.ObjectNotInLowlevelStorageException;
import fedora.server.errors.ObjectAlreadyInLowlevelStorageException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StorageException;
import fedora.server.errors.StorageDeviceException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamReadException;
import fedora.server.errors.StreamWriteException;
import fedora.server.errors.ValidationException;
import fedora.server.storage.lowlevel.ILowlevelStorage;
import fedora.server.storage.replication.DOReplicator;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.*;
import fedora.server.utilities.MethodInvokerThread;

/**
 * <p></p>
 * @author cwilper@cs.cornell.edu
 */
public class DefinitiveDOWriter
        implements DOWriter {

    private DigitalObject m_obj;
    private boolean m_pendingRemoval;
    private boolean m_pendingSave;
    private boolean m_pendingCommit;
    private boolean m_removed;
    private boolean m_invalidated;

    private static ObjectIntegrityException ERROR_PENDING_REMOVAL =
            new ObjectIntegrityException("That can't be done because you said "
                    + "i should remove the object and i assume that's what you "
                    + "want unless you call rollback()");

    private static ObjectIntegrityException ERROR_REMOVED =
            new ObjectIntegrityException("The handle is no longer valid "
                    + "because the object has been removed and the change "
                    + "has been committed.");

    private static ObjectIntegrityException ERROR_INVALIDATED =
            new ObjectIntegrityException("The handle is no longer valid "
                    + "because it was explicitly invalidated, probably as "
                    + "a result of having been release()d to the DOManager.");

    private ILowlevelStorage m_storage;
    private StreamValidator m_validator;
    private DODeserializer m_importDeserializer;
    private DOSerializer m_storageSerializer;
    private DODeserializer m_storageDeserializer;
    private DOSerializer m_exportSerializer;


    private DefaultDOManager m_mgr;
    private ConnectionPool m_pool;
    private Context m_context;

    public DefinitiveDOWriter(Context context, DefaultDOManager mgr, DigitalObject obj) {
        m_context=context;
        m_obj=obj;
        m_mgr=mgr;
        m_pool=mgr.getConnectionPool();
    }

    /**
     * Constructs a DOWriter as a handle on an existing digital object.
     * If workingCopy==true, the working copy area is examined for
     * a copy of the object pending commit or removal, and if it doesn't
     * find one, it works from the definitive copy (acts as if workingCopy was
     * false)
     */
    public DefinitiveDOWriter(String pid, ILowlevelStorage storage, StreamValidator validator,
            DODeserializer importDeserializer, DOSerializer storageSerializer,
            DODeserializer storageDeserializer, DOSerializer exportSerializer,
            boolean workingCopy)
            throws ServerException {
        m_obj=new BasicDigitalObject();
        m_storage=storage;
        m_validator=validator;
        m_importDeserializer=importDeserializer;
        m_storageSerializer=storageSerializer;
        m_storageDeserializer=storageDeserializer;
        m_exportSerializer=exportSerializer;
        boolean initialized=false;
        if (workingCopy) {
            try {
                m_storageDeserializer.deserialize(m_storage.retrieve(pid + "-pendingCommit"), m_obj, "UTF-8");
                // it was found, and it's pending commit...init it as such
                m_removed=false;
                m_pendingRemoval=false;
                makeDirty();
                initialized=true;
            } catch (UnsupportedEncodingException uee) {
            } catch (ObjectNotInLowlevelStorageException onfe) {
                try {
                    InputStream in=m_storage.retrieve(pid + "-pendingRemoval");
                    // it was found, and it's pending removal...init it as such
                    try {
                        in.close();
                    } catch (IOException dontCare) { }
                    m_removed=false;
                    m_pendingRemoval=true;
                    makeDirty();
                    initialized=true;
                } catch (ObjectNotInLowlevelStorageException onfe2) {
                    // it wasnt found... so we should load from permanent
                    // source (this will happen after exit from this block
                    // because initialized is false
                }
            }
        }
        if (!initialized) {
            try {
            m_storageDeserializer.deserialize(m_storage.retrieve(pid), m_obj, "UTF-8");
            } catch (UnsupportedEncodingException uee) { }
            m_pendingCommit=false;
            m_pendingSave=false;
            m_pendingRemoval=false;
            m_removed=false;
        }
        if (!m_pendingRemoval) {
            if (!pid.equals(m_obj.getPid())) {
                throw new ObjectIntegrityException("While getting a DOWriter for the "
                        + "pre-existing object '" + pid + "', it was found but "
                        + "after deserializing it, it has a different PID, '"
                        + m_obj.getPid() + "'.");
            }
        }
    }

    /**
     * Constructs a DOWriter as a handle on a new digital object.
     */
    public DefinitiveDOWriter(String pid, ILowlevelStorage storage, StreamValidator validator,
            DODeserializer importDeserializer, DOSerializer storageSerializer,
            DODeserializer storageDeserializer, DOSerializer exportSerializer,
            InputStream initialContent, boolean useContentPid)
            throws ServerException {
        m_obj=new BasicDigitalObject();
        m_storage=storage;
        m_validator=validator;
        m_importDeserializer=importDeserializer;
        m_storageSerializer=storageSerializer;
        m_storageDeserializer=storageDeserializer;
        m_exportSerializer=exportSerializer;
        m_pendingRemoval=false;
        m_removed=false;
        set(initialContent);
        if (!useContentPid) {
            m_obj.setPid(pid);
        }
    }

    /**
     * Sets the content of the entire digital object.
     *
     * @param content A stream of encoded content of the digital object.
     */
    public void set(InputStream content)
            throws ObjectIntegrityException, StreamIOException,
            StreamReadException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        try {
        m_importDeserializer.deserialize(content, m_obj, "UTF-8");
        } catch (UnsupportedEncodingException uee) { }
        makeDirty();
    }

    /**
     * Sets the state of the entire digital object.
     *
     * @param state The state.
     */
    public void setState(String state)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        m_obj.setState(state);
        makeDirty();
    }

    /**
     * Sets the label of the digital object.
     *
     * @param label The label.
     */
    public void setLabel(String label)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        m_obj.setLabel(label);
        makeDirty();
    }

    /**
     * Removes the entire digital object.
     *
     */
    public void remove()
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        m_pendingRemoval=true;
        makeDirty();
    }

    /**
     * Adds a datastream to the object.
     *
     * @param datastream The datastream.
     * @return An internally-unique datastream id.
     */
    public String addDatastream(Datastream datastream)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        makeDirty();
        return null;
    }

    /**
     * Adds a disseminator to the object.
     *
     * @param disseminator The disseminator.
     * @return An internally-unique disseminator id.
     */
    public String addDisseminator(Disseminator disseminator)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        makeDirty();
        return null;
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
     */
    public void removeDatastream(String id, Date start, Date end)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        makeDirty();
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
     */
    public void removeDisseminator(String id, Date start, Date end)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        makeDirty();
    }

    /**
     * Saves the changes thus far to the permanent copy of the digital object.
     *
     * @param logMessage An explanation of the change(s).
     */
    public void commit(String logMessage)
            throws ServerException {
        assertNotRemoved();
        assertNotInvalidated();
        //if (save()) {
        m_mgr.doCommit(m_context, m_obj, logMessage, m_pendingRemoval);
        //}
        if (m_pendingRemoval) {
            invalidate();
        }
        m_pendingCommit=false;
        m_removed=true;
    }

    /**
     * Clears the temporary storage area of changes to this object.
     * <p></p>
     * Subsequent calls will behave as if the changes made thus far never
     * happened.
     *
     */
    public void rollBack()
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        m_pendingCommit=false;
        m_pendingSave=false; // i think
    }

    public InputStream GetObjectXML()
            throws ObjectIntegrityException, StreamIOException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        try {
        m_exportSerializer.serialize(m_obj, bytes, "UTF-8");
        } catch (UnsupportedEncodingException uee) { }
        return new ByteArrayInputStream(bytes.toByteArray());
    }

    public String GetObjectState()
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        return m_obj.getState();
    }

    public InputStream ExportObject()
            throws ObjectIntegrityException, StreamIOException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        return GetObjectXML();
    }

    public String GetObjectPID()
            throws ObjectIntegrityException {
        assertNotRemoved(); // be a little forgiving of pending removal
        assertNotInvalidated();
        return m_obj.getPid();
    }

    public String GetObjectLabel()
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        return m_obj.getLabel();
    }

    public String[] ListDatastreamIDs(String state)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        Iterator iter=m_obj.datastreamIdIterator();
        ArrayList al=new ArrayList();
        while (iter.hasNext()) {
            al.add((String) iter.next());
        }
        iter=al.iterator();
        String[] out=new String[al.size()];
        int i=0;
        while (iter.hasNext()) {
            out[i]=(String) iter.next();
            i++;
        }
        return out;
    }

    public Datastream[] GetDatastreams(Date versDateTime)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        return new Datastream[0];
    }

    public Datastream GetDatastream(String datastreamID, Date versDateTime)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        return new Datastream();
    }

    public Disseminator[] GetDisseminators(Date versDateTime)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        return new Disseminator[0];
    }

    public String[] ListDisseminatorIDs(String state)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        Iterator iter=m_obj.disseminatorIdIterator();
        ArrayList al=new ArrayList();
        while (iter.hasNext()) {
            al.add((String) iter.next());
        }
        iter=al.iterator();
        String[] out=new String[al.size()];
        int i=0;
        while (iter.hasNext()) {
            out[i]=(String) iter.next();
            i++;
        }
        return out;
    }

    public Disseminator GetDisseminator(String disseminatorID, Date versDateTime)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        return new Disseminator();
    }

    // Returns PIDs of Behavior Definitions to which object subscribes
    public String[] GetBehaviorDefs(Date versDateTime)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        return new String[0];
    }

    // Returns list of methods that Behavior Mechanism implements for a BDef
    public MethodDef[] GetBMechMethods(String bDefPID, Date versDateTime)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        return new MethodDef[0];
    }

    // Overloaded method: returns InputStream as alternative
    public InputStream GetBMechMethodsWSDL(String bDefPID, Date versDateTime)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        return new ByteArrayInputStream(new byte[0]);
    }

    // Returns list of method parameters that Behavior Mechanism implements
    // for a BDef
    public MethodParmDef[] GetBMechMethodParms(String bDefPID,
            String methodName, Date versDateTime)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        return new MethodParmDef[0];
    }

    // Returns list of default method parameters that Behavior Mechanism
    // implements for a BDef
    public MethodParmDef[] GetBMechDefaultMethodParms(String bDefPID,
            String methodName, Date versDateTime)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        return new MethodParmDef[0];
    }

    public DSBindingMapAugmented[] GetDSBindingMaps(Date versDateTime)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
        return new DSBindingMapAugmented[0];
    }

    /** this should go in DOReader, methinks */
    public void validate(String validationType)
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        assertNotPendingRemoval();
    }

    // i don't think this needs to be public as long as save() is,
    // but making it public might be nice
    public boolean pendingCommit()
            throws ObjectIntegrityException {
        assertNotRemoved();
        assertNotInvalidated();
        return m_pendingCommit;
    }

    private void makeDirty() {
        m_pendingCommit=true;
        m_pendingSave=true;
    }

    // saves to temporary area if changes occurred in memory
    public boolean save()
            throws StorageException {
        assertNotRemoved();
        assertNotInvalidated();
        // FIXME: update as per new logic..
        return false;

        /*
        if (m_pendingSave) {
           if (m_pendingRemoval) {
               // flag that removal is needed by removing the temp copy
               // and creating a 0000-pendingRemoval item
               try {
                   m_storage.remove(m_obj.getPid() + "-pendingCommit");
               } catch (ObjectNotInLowlevelStorageException onilse) {
                   // don't care... it may not have been saved yet...
               }
               String removeMeString="pending removal";
               ByteArrayInputStream removeMeStream=new ByteArrayInputStream(removeMeString.getBytes());
               m_storage.add(m_obj.getPid() + "-pendingRemoval", removeMeStream);
           } else {
               // serialize to temp copy as 0000-pendingCommit...
               Method m=null;
               PipedInputStream in=null;
               PipedOutputStream out=null;
               try {
                   try {
                       in=new PipedInputStream();
                       out=new PipedOutputStream(in);
                       m=m_storageSerializer.getClass().getMethod("serialize", new Class[] {m_obj.getClass(), Class.forName("java.io.OutputStream"), Class.forName("java.lang.String")});
                   } catch (Throwable wontHappen) { }
                   MethodInvokerThread serThread=new MethodInvokerThread(m_storageSerializer, m, new Object[] {m_obj, out});
                   serThread.start();
                   m_storage.add(m_obj.getPid() + "-pendingCommit", in);
                   if (serThread.getThrown()!=null) {
                       try {
                           throw serThread.getThrown();
                       } catch (StorageException se) {
                           throw se;
                       } catch (Throwable th) {
                           System.out.println("[Do something better here] non-storageException during save (add): " + th.getClass().getName() + ": " + th.getMessage());
                       }
                   }
               } catch (ObjectAlreadyInLowlevelStorageException oailse) {
                   try {
                       in=new PipedInputStream();
                       out=new PipedOutputStream(in);
                       m=m_storageSerializer.getClass().getMethod("serialize", new Class[] {m_obj.getClass(), Class.forName("java.io.OutputStream"), Class.forName("java.lang.String")});
                   } catch (Throwable wontHappen) { }
                   MethodInvokerThread serThread=new MethodInvokerThread(m_storageSerializer, m, new Object[] {m_obj, out});
                   serThread.start();
                   m_storage.replace(m_obj.getPid() + "-pendingCommit", in);
                   if (serThread.getThrown()!=null) {
                       try {
                           throw serThread.getThrown();
                       } catch (StorageException se) {
                           throw se;
                       } catch (Throwable th) {
                           System.out.println("[Do something better here] non-storageException during save (replace): " + th.getClass().getName() + ": " + th.getMessage());
                       }
                   }
               }
           }
           m_pendingSave=false;
           return true;
        }
        return false;
        */
    }

    public void invalidate() {
        m_invalidated=true;
    }

    public void cancel() {
        // cleanup temp if exists, release lock, and invalidate
    }

    private void assertNotPendingRemoval()
            throws ObjectIntegrityException {
        if (m_pendingRemoval)
            throw ERROR_PENDING_REMOVAL;
    }

    private void assertNotRemoved()
            throws ObjectIntegrityException {
        if (m_removed)
            throw ERROR_REMOVED;
    }

    private void assertNotInvalidated()
            throws ObjectIntegrityException {
        if (m_invalidated)
            throw ERROR_INVALIDATED;
    }

    public void finalize() throws StorageException {
        save();
    }
}