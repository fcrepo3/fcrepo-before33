package fedora.server.storage;

import java.io.InputStream;
import java.util.Date;

import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamReadException;
import fedora.server.errors.StreamWriteException;
import fedora.server.errors.StorageDeviceException;
import fedora.server.errors.ObjectExistsException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.ValidationException;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Disseminator;

/**
 * The standard interface for write operations on a digital object.
 * <p></p>
 * A <code>DOWriter</code> instance is a handle on a Fedora digital object,
 * and is obtained via a <code>getWriter(String)</code> call on a 
 * <code>DOManager</code>.
 * <p></p>
 * Call save() to save changes while working with a DOWriter, where the
 * DOWriter handle may be lost but the changes need to be remembered.
 * <p></p>
 * Work with a DOWriter ends with either commit() or cancel().
 *
 * @author cwilper@cs.cornell.edu
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
     * @return An internally-unique datastream id.
     * @throws ServerException If any type of error occurred fulfilling the 
     *         request.
     */
    public String addDatastream(Datastream datastream) throws ServerException;

    /**
     * Adds a disseminator to the object.
     *
     * @param disseminator The disseminator.
     * @return An internally-unique disseminator id.
     * @throws ServerException If any type of error occurred fulfilling the 
     *         request.
     */
    public String addDisseminator(Disseminator disseminator) 
            throws ServerException;

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
            throws ServerException;

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
            throws ServerException;

    /**
     * Saves the changes thus far to the permanent copy of the digital object.
     *
     * @param logMessage An explanation of the change(s).
     * @throws ServerException If any type of error occurred fulfilling the 
     *         request.
     */
    public void commit(String logMessage) throws ServerException;

    public void save() throws ServerException;
            
    public void cancel() throws ServerException;

    /**
     * Marks this DOWriter handle invalid (unusable).
     */
    public void invalidate();
}