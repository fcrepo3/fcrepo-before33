package fedora.server.storage;

import java.io.InputStream;
import java.util.Date;

import fedora.server.errors.ServerException;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Disseminator;

/**
 * The standard interface for write operations on digital objects.
 *
 * A <code>DOWriter</code> instance is a handle on a Fedora digital object,
 * and is obtained via a <code>getWriter(String)</code> call on a 
 * <code>DOManager</code>.
 *
 * Through a few simple methods, transaction-like behavior is supported
 * by this class.   When methods are called to make changes to the digital 
 * object, those changes are made temporarily.  If the object is finalized, 
 * those changes will be lost.  To prevent this from happening, the <code>save()</code>
 * method can be used.  This saves the changes to a holding area that the
 * <code>DOManager</code> is aware of so that another DOWriter instance 
 * can be provided as a handle on this working copy of the object.  A change
 * to a digital object will only be written to permanent storage when
 * <code>commitChanges()</code> is called.  The object can be reverted to the 
 * last commit-point by calling <code>abandonChanges()</code>.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface DOWriter {

    /**
     * Sets the content of the entire digital object.
     *
     * @param content A stream of encoded content of the digital object.
     * @throw ServerException If any type of error occurred fulfilling the request.
     */
    public void set(InputStream content) throws ServerException;

    /**
     * Sets the state of the entire digital object.
     *
     * @param state The state.
     * @throw ServerException If any type of error occurred fulfilling the request.
     */
    public void setState(String state) throws ServerException;

    /**
     * Removes the entire digital object.
     *
     * @throw ServerException If any type of error occurred fulfilling the request.
     */    
    public void remove() throws ServerException;


    /**
     * Saves the changes thus far to a temporary area, but does not
     * commit them.
     *
     * @throw ServerException If any type of error occurred fulfilling the request.
     */
    public void save() throws ServerException;

    /**
     * Adds a datastream to the object.
     *
     * @param datastream The datastream.
     * @throw ServerException If any type of error occurred fulfilling the request.
     */
    public String addDatastream(Datastream datastream) throws ServerException;

    /**
     * Adds a disseminator to the object.
     *
     * @param disseminator The disseminator.
     * @throw ServerException If any type of error occurred fulfilling the request.
     */
    public String addDisseminator(Disseminator disseminator) throws ServerException;

    /**
     * Removes a datastream from the object.
     *
     * @param id The id of the datastream.
     * @param start The start date (inclusive) of versions to remove.  If null, this
     *        is taken to be the smallest possible value.
     * @param end The end date (inclusive) of versions to remove.  If null, this is
     *        taken to be the greatest possible value.
     * @throw ServerException If any type of error occurred fulfilling the request.
     */
    public String removeDatastream(String id, Date start, Date end) throws ServerException;

    /**
     * Removes a disseminator from the object.
     *
     * @param id The id of the datastream.
     * @param start The start date (inclusive) of versions to remove.  If null, this
     *        is taken to be the smallest possible value.
     * @param end The end date (inclusive) of versions to remove.  If null, this is
     *        taken to be the greatest possible value.
     * @throw ServerException If any type of error occurred fulfilling the request.
     */
    public String removeDisseminator(String id, Date start, Date end) throws ServerException;

    /**
     * Saves the changes thus far to the permanent storage area.
     *
     * @param logMessage An explanation of the change(s).
     * @throw ServerException If any type of error occurred fulfilling the request.
     */
    public void commitChanges(String logMessage) throws ServerException;

    /**
     * Clears the temporary storage area of changes to this object.
     * <p></p>
     * Subsequent calls will behave as if the changes made thus far never happened.
     *
     * @throw ServerException If any type of error occurred fulfilling the request.
     */
    public void abandonChanges() throws ServerException;
}