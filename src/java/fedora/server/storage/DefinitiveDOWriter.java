package fedora.server.storage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Date;

import fedora.server.errors.ObjectExistsException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.StorageDeviceException;
import fedora.server.errors.ValidationException;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.DigitialObject;
import fedora.server.storage.types.*;

/**
 * A <code>DOWriter</code> for working with the definitive copy of a
 * digital object in the reference implementation.
 *
 * This implementation stores the objects in <code>StreamStorage</code> using 
 * METS XML encoding.
 *
 * @author cwilper@cs.cornell.edu
 */
public class DefinitiveDOWriter
        implements DOWriter {

    private DigitalObject m_obj;
/** remove and replace these with a DigitalObject instance  */
    private String m_pid;
    private String m_label;
    private String m_state;
    
    private boolean m_isDirty;
    
/**        */
    
    private TestStreamStorage m_storage;
    private TestStreamStorage m_tempStorage;
    private StreamValidator m_validator;

    /**
     * Constructs a DOWriter as a handle on an existing digital object.
     */
    public DefinitiveDOWriter(String pid, StreamValidator validator, 
            TestStreamStorage storage, TestStreamStorage tempStorage) 
            throws StorageDeviceException, ObjectNotFoundException {
            
        m_pid=pid;
        m_validator=validator;
        m_storage=storage;
        m_tempStorage=tempStorage;
        
        m_isDirty=false;
    }
    
    /**
     * Constructs a DOWriter as a handle on a new digital object.
     */
    public DefinitiveDOWriter(String pid, StreamValidator validator, 
            TestStreamStorage storage, TestStreamStorage tempStorage, 
            InputStream template, String initialState, 
            String initialValidationType) 
            throws ValidationException, ObjectNotFoundException,
            ObjectExistsException, StorageDeviceException {
        m_pid=pid;
        m_validator=validator;
        m_storage=storage;
        m_tempStorage=tempStorage;
        set(template, initialValidationType, false);
        setState(initialState);
    }

    /**
     * Sets the content of the entire digital object.
     *
     * @param content A stream of encoded content of the digital object.
     */
    public void set(InputStream content, String validationType, boolean shouldExist) 
            throws ValidationException, ObjectNotFoundException, 
            ObjectExistsException, StorageDeviceException {
        String tempId=m_pid + "-toValidate";
        m_tempStorage.add(tempId, content);
        m_validator.validate(m_tempStorage.retrieve(tempId), validationType);
        if (shouldExist) {
            m_storage.replace(tempId, m_tempStorage.retrieve(tempId));
        } else {
            m_storage.add(tempId, m_tempStorage.retrieve(tempId));
        }
        m_tempStorage.delete(tempId);
    }

    /**
     * Sets the state of the entire digital object.
     *
     * @param state The state.
     */
    public void setState(String state) {
        m_state=state;
        m_isDirty=true;
    }

    /**
     * Sets the label of the digital object.
     *
     * @param label The label.
     */
    public void setLabel(String label) {
        m_label=label;
        m_isDirty=true;
    }

    /**
     * Removes the entire digital object.
     *
     */    
    public void remove() {
        m_isDirty=true;
    }

    /**
     * Adds a datastream to the object.
     *
     * @param datastream The datastream.
     * @return An internally-unique datastream id.
     */
    public String addDatastream(Datastream datastream) {
        return null;
    }

    /**
     * Adds a disseminator to the object.
     *
     * @param disseminator The disseminator.
     * @return An internally-unique disseminator id.
     */
    public String addDisseminator(Disseminator disseminator) {
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
    public void removeDatastream(String id, Date start, Date end) {
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
    public void removeDisseminator(String id, Date start, Date end) {
    }

    /**
     * Saves the changes thus far to the permanent copy of the digital object.
     *
     * @param logMessage An explanation of the change(s).
     */
    public void commit(String logMessage) {
        save();
        ByteArrayOutputStream out=new ByteArrayOutputStream();
  //      METSDOSerializer.getInstance().serialize(this, out);
        ByteArrayInputStream in=new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * Clears the temporary storage area of changes to this object.
     * <p></p>
     * Subsequent calls will behave as if the changes made thus far never 
     * happened.
     *
     */
    public void rollBack() {
    }
    
    public String GetObjectXML() {
        return null;
    }

    public String ExportObject() {
        return null;
    }

    public String GetObjectPID() {
        return m_pid;
    }

    public String GetObjectLabel() {
        return m_label;
    }

    public String[] ListDatastreamIDs(String state) {
        return null;
    }

    public Datastream[] GetDatastreams(Date versDateTime) {
        return null;
    }

    public Datastream GetDatastream(String datastreamID, Date versDateTime) {
        return null;
    }

    public Disseminator[] GetDisseminators(Date versDateTime) {
        return null;
    }

    public String[] ListDisseminatorIDs(String state) {
        return null;
    }

    public Disseminator GetDisseminator(String disseminatorID, Date versDateTime) {
        return null;
    }

    // Returns PIDs of Behavior Definitions to which object subscribes
    public String[] GetBehaviorDefs(Date versDateTime) {
        return null;
    }

    // Returns list of methods that Behavior Mechanism implements for a BDef
    public MethodDef[] GetBMechMethods(String bDefPID, Date versDateTime) {
        return null;
    }

    // Overloaded method: returns InputStream as alternative
    public InputStream GetBMechMethodsWSDL(String bDefPID, Date versDateTime) {
        return null;
    }

    public DSBindingMapAugmented[] GetDSBindingMaps(Date versDateTime) {
        return null;
    }

    /** this should go in DOReader, methinks */
    public void validate(String validationType) {
    }
    
    public boolean isDirty() {
        return m_isDirty;
    }
    
    public void save() {
    }
}