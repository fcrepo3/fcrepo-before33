package fedora.server.storage;

import fedora.server.storage.types.*;
import fedora.server.errors.ServerException;
import java.util.Date;
import java.util.List;
import java.io.InputStream;

/**
 *
 * <p><b>Title:</b> DOReader.java</p>
 * <p><b>Description:</b> Interface for reading Fedora digital objects from
 * within the storage sub system.</p>
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
 * @author payette@cs.cornell.edu
 * @version 1.0
 */
public interface DOReader
{

    /**
     * Gets the type of fedora object (O=FEDORA_OBJECT, D=FEDORA_BDEF_OBJECT,
     * M=FEDORA_BMECH_OBJECT) this is a handle on.
     */
    public String getFedoraObjectType() throws ServerException;

    /**
     * Gets the content model of the object.
     */
    public String getContentModelId() throws ServerException;

    /**
     * Gets the date of creation of this object.
     */
    public Date getCreateDate() throws ServerException;

    /**
     * Gets the date of the last modification of this object.
     */
    public Date getLastModDate() throws ServerException;

    /**
     * Gets the userid of the user with a write lock on this object.
     */
    public String getLockingUser() throws ServerException;

    /**
     * Gets the entire list of audit records for the object.
     *
     * Changes to the list affect the underlying object if this is DOWriter.
     */
    public List getAuditRecords() throws ServerException;

    /**
     * Gets the content of the entire digital object as XML.  The object will
     * be returned exactly as it is stored in the repository.
     *
     * @throws ServerException If there object could not be found or there was
     *        was a failure in accessing the object for any reason.
     */
    public InputStream GetObjectXML() throws ServerException;

    /**
     * Gets the content of the entire digital object as XML, with datastream
     * content included for those datastreams that are under the custodianship
     * of the repository.  The XML will contain XMLMetadata (inline XML) and
     * Managed Content datastreams (base64 encoded).  The content of
     * External Referenced datastreams will not be included inline, but the URL
     * for those datastreams will be returned.
     * <p></p>
     * The intent of this method is to return the digital object along with
     * its datastream content, except in cases where the datastream content is
     * not actually stored within the repository system.
     *
     * @throws ServerException If there object could not be found or there was
     *        was a failure in accessing the object for any reason.
     */
    public InputStream ExportObject() throws ServerException;

    /**
     * Gets the PID of the digital object.
     *
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public String GetObjectPID() throws ServerException;

   /**
     * Gets the label of the digital object.
     *
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public String GetObjectLabel() throws ServerException;

    /**
     * Gets the state of the digital object.  The state indicates the status
     * of the digital object at any point in time.  Valid states are:
     * A=Active, W=Withdrawn, C=Marked for Deletion, D=Pending Deletion.
     * New states may be defined in the future.
     *
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public String GetObjectState() throws ServerException;

   /**
     * Gets a list of Datastream identifiers for all Datastreams in the digital
     * object.  Will take a state parameter to specify that only Datastreams
     * that are in a particular state should be listed (e.g., only active
     * Datastreams with a state value of "A").  If state is given
     * as null, all datastream ids will be returned, regardless of state.
     *
     * @param state The state of the Datastreams to be listed.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public String[] ListDatastreamIDs(String state) throws ServerException;

    /**
     * Gets all datastreams as of a certain date.
     * This iterates through all datastreams in the object and
     * returns only those that existed at the given date/time,
     * regardless of state.
     * If the date/time given is null, the most recent version of
     * each datastream is obtained.
     *
     * @param state The date-time stamp to get appropriate Datastream versions
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public Datastream[] GetDatastreams(Date versDateTime) throws ServerException;

   /**
     * Gets a particular Datastream in the digital object.
     * If the date given is null, the most recent version of the datastream is
     * given.  If the date is non-null, the closest version of the Datastream
     * to the specified date/time (without going over) is given.
     * If no datastreams match the given criteria, null is returned.
     *
     * @param datastreamID The Datastream identifier
     * @param state The date-time stamp to get appropriate Datastream version
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public Datastream GetDatastream(String datastreamID, Date versDateTime) throws ServerException;

   /**
     * Same as getDatastreams, but for disseminators.
     *
     * @param state The date-time stamp to get appropriate Disseminator version
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public Disseminator[] GetDisseminators(Date versDateTime) throws ServerException;

   /**
     * Same as listDatastreamIds, but for disseminators.
     *
     * @param state The state of the Disseminators to be listed.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public String[] ListDisseminatorIDs(String state) throws ServerException;

   /**
     * Same as getDatastream, but for disseminators.
     *
     * @param disseminatorID The Disseminator identifier
     * @param state The date-time stamp to get appropriate Disseminator version
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public Disseminator GetDisseminator(String disseminatorID, Date versDateTime) throws ServerException;

   /**
     * Gets PIDs of Behavior Definitions to which object subscribes.  This is
     * done by looking at all the Disseminators for the object, and reflecting
     * on what Behavior Definitions objects the Disseminators refer to.
     * The given date is used to query for disseminators.  The disseminators
     * as they existed during the given date are used.  If the date
     * is given as null, the most recent version of each disseminator
     * is used.
     *
     * @param versDateTime The date-time stamp to get appropriate version
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public String[] GetBehaviorDefs(Date versDateTime) throws ServerException;

    /**
     * Gets list of method definitions that are available on a particular
     * Disseminator. This is done by reflecting on the Disseminator
     * that subscribes to the Behavior Definition that is specified in the
     * method input parameter.  Then, by reflecting on that Disseminator,
     * the PID of the Behavior Mechanism object can be obtained.
     * Finally, method implementation information can be found in the
     * Behavior Mechanism object to which that Disseminator refers.
     *
     * @param bDefPID The PID of a Behavior Definition to which the object
     *        subscribes.  If this is the special bootstrap bdef,
     *        this method returns null.
     * @param versDateTime The date-time stamp to get appropriate version.
     *        If this is given as null, the most recent version is used.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public MethodDef[] getObjectMethods(String bDefPID, Date versDateTime) throws ServerException;

    /**
     * Gets list of ALL method definitions that are available on a particular
     * digital object. This is done by reflecting on EACH Disseminator
     * and getting the PID of the behavior mechanism object for that disseminator.
     * The methods are reflected via the behavior mechanism object, which is
     * implementing the methods defined in a particular by a behavior definition.
     *
     * @param versDateTime The date-time stamp to get appropriate version.
     *        If this is given as null, the most recent version is used.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public ObjectMethodsDef[] getObjectMethods(Date versDateTime)
          throws ServerException;

    /**
     * Same as getObjectMethods (filtered with bDefPID as argument), except that the method
     * definitions are returned as an Inputstream which contains XML encoded
     * to the Fedora Method Map schema.
     *
     * @param bDefPID The PID of a Behavior Definition to which the object
     *        subscribes.  If this is the special bootstrap bdef,
     *        this method returns null.
     * @param versDateTime The date-time stamp to get appropriate version
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public InputStream getObjectMethodsXML(String bDefPID, Date versDateTime) throws ServerException;

    /**
     * Gets list of method parameter definitions that are available on a
     * particular method. This is done by reflecting on the Disseminator
     * that subscribes to the Behavior Definition that is specified in the
     * method input parameter.  Then, by reflecting on that Disseminator,
     * the PID of the Behavior Mechanism object can be obtained.
     * Finally, method implementation information can be found in the
     * Behavior Mechanism object to which that Disseminator refers.
     *
     * @param bDefPID The PID of a Behavior Definition to which the object
     *        subscribes.  If this is the special bootstrap bdef,
     *        this method returns null.
     * @param methodName The name of the method.
     * @param versDateTime The date-time stamp to get appropriate version
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public MethodParmDef[] getObjectMethodParms(String bDefPID, String methodName,
        Date versDateTime) throws ServerException;

    /**
     * Gets datastream binding map.
     *
     * @param versDateTime versioning datetime stamp
     * @return DSBindingMapAugmented[] array of datastream binding maps
     * @throws ServerException If anything went wrong
     */
    public DSBindingMapAugmented[] GetDSBindingMaps(Date versDateTime)
          throws ServerException;

    /**
     * Gets the dissemination binding info necessary to perform a particular
     * dissemination.
     *
     * @param bDefPID the behavior definition pid
     * @param methodName the method name
     * @param versDateTime versioning datetime stamp
     * @throws ServerException If anything went wrong
     */
    public DisseminationBindingInfo[] getDisseminationBindingInfo(String bDefPID,
          String methodName, Date versDateTime) throws ServerException;

}