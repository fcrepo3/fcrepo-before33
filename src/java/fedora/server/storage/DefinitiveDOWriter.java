package fedora.server.storage;

import java.io.InputStream;
import java.util.Date;

import fedora.server.errors.ServerException;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.*;

/**
 * A <code>DOWriter</code> for working with the definitive copy of a
 * digital object in the reference implementation.
 *
 * @author cwilper@cs.cornell.edu
 */
public class DefinitiveDOWriter
        implements DOWriter {
        
    private String m_pid;
//    private StreamStorage m_storage;

    /**
     * Constructs a DOWriter as a handle on an existing digital object.
     */
//    public DefinitiveDOWriter(String pid, Validator validator, 
//            StreamStorage storage) {
//    }
    
    /**
     * Constructs a DOWriter as a handle on a new digital object.
     */
//    public DefinitiveDOWriter(String pid, Validator validator, 
//            StreamStorage storage, InputStream template, String initialState) {
//    }

    public DefinitiveDOWriter() {
    }

    /**
     * Sets the content of the entire digital object.
     *
     * @param content A stream of encoded content of the digital object.
     * @throws ServerException If any type of error occurred fulfilling the 
     *         request.
     */
    public void set(InputStream content, String newState) {
        // write it to disk first, then validate it.  
        // If validation fails, tell the user
        // note that we do validation anytime writing is done
        //
        // m_storage.something...
    }

    /**
     * Sets the state of the entire digital object.
     *
     * @param state The state.
     * @throws ServerException If any type of error occurred fulfilling the 
     *         request.
     */
    public void setState(String state) {
           
    }

    /**
     * Removes the entire digital object.
     *
     * @throws ServerException If any type of error occurred fulfilling the 
     *         request.
     */    
    public void remove() {
    }

    /**
     * Adds a datastream to the object.
     *
     * @param datastream The datastream.
     * @return An internally-unique datastream id.
     * @throws ServerException If any type of error occurred fulfilling the 
     *         request.
     */
    public String addDatastream(Datastream datastream) {
        return null;
    }

    /**
     * Adds a disseminator to the object.
     *
     * @param disseminator The disseminator.
     * @return An internally-unique disseminator id.
     * @throws ServerException If any type of error occurred fulfilling the 
     *         request.
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
     * @throws ServerException If any type of error occurred fulfilling the 
     *         request.
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
     * @throws ServerException If any type of error occurred fulfilling the 
     *         request.
     */
    public void removeDisseminator(String id, Date start, Date end) {
    }

    /**
     * Saves the changes thus far to the permanent copy of the digital object.
     *
     * @param logMessage An explanation of the change(s).
     * @throws ServerException If any type of error occurred fulfilling the 
     *         request.
     */
    public void commit(String logMessage, String newState) {
    }

    /**
     * Clears the temporary storage area of changes to this object.
     * <p></p>
     * Subsequent calls will behave as if the changes made thus far never 
     * happened.
     *
     * @throws ServerException If any type of error occurred fulfilling the 
     *         request.
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
        return null;
    }

    public String GetObjectLabel() {
        return null;
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
}