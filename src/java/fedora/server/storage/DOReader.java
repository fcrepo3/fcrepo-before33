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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public interface DOReader
{

    /**
     * Gets the type of fedora object (O=FEDORA_OBJECT, D=FEDORA_BDEF_OBJECT,
     * M=FEDORA_BMECH_OBJECT) this is a handle on.
     *
     * @return the type of Fedora object.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public String getFedoraObjectType() throws ServerException;

    /**
     * Gets the content model of the object.
     *
     * @return the content model of the object.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public String getContentModelId() throws ServerException;

    /**
     * Gets the date of creation of this object.
     *
     * @return the date of creation of this object.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public Date getCreateDate() throws ServerException;

    /**
     * Gets the date of the last modification of this object.
     *
     * @return the date of the last modification of this object.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public Date getLastModDate() throws ServerException;

    /**
     * Gets the userid of the user who owns the objects.
     *
     * @return the userid
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public String getOwnerId() throws ServerException;

    /**
     * Gets the entire list of audit records for the object.
     *
     * Changes to the list affect the underlying object if this is DOWriter.
     *
     * @return the entire list of audit records for the object.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public List getAuditRecords() throws ServerException;

    /**
     * Gets the content of the entire digital object as XML.  The object will
     * be returned exactly as it is stored in the repository.
     *
     * @return the content of the entire digital object as XML.
     * @throws ServerException If there object could not be found or there was
     *         was a failure in accessing the object for any reason.
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
     * @return the content of the entire digital object as XML, with datastream
     *         content included for those datastreams that are under the
     *         custodianship of the repository.
     * @throws ServerException If there object could not be found or there was
     *         was a failure in accessing the object for any reason.
     */
    public InputStream ExportObject() throws ServerException;

    /**
     * Gets the PID of the digital object.
     *
     * @return the PID of the digital object.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public String GetObjectPID() throws ServerException;

   /**
     * Gets the label of the digital object.
     *
     * @return the label of the digital object.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public String GetObjectLabel() throws ServerException;

    /**
     * Gets the state of the digital object.  The state indicates the status
     * of the digital object at any point in time.  Valid states are:
     * A=Active, I=Inactive, D=Deleted
     *
     * @return the state of the digital object.
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
     * @return a list of Datastream identifiers for all Datastreams in the
     *         digital object.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public String[] ListDatastreamIDs(String state) throws ServerException;

    /**
     * Gets the creation dates of all versions of a particular datastream,
     * in no particular order.
     *
     * @param datastreamID The datastream identifier
     * @return the creation dates.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public Date[] getDatastreamVersions(String datastreamID) throws ServerException;

    /**
     * Gets the creation dates of all versions of a particular disseminator,
     * in no particular order.
     *
     * @param dissID The disseminator identifier
     * @return the creation dates.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public Date[] getDisseminatorVersions(String dissID) throws ServerException;

    /**
     * Gets all datastreams as of a certain date and in a certain state.
	 *
     * This iterates through all datastreams in the object and
     * returns only those that existed at the given date/time,
     * and currently have a certain state.
	 *
     * If the date/time given is null, the most recent version of
     * each datastream is obtained.  If the state is null, all datastreams
	 * as of the given time will be returned, regardless of state.
     *
     * @param versDateTime The date-time stamp to get appropriate Datastream versions
     * @param state The state, null for any.
     * @return all datastreams as of a certain date and in a certain state.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public Datastream[] GetDatastreams(Date versDateTime, String state) throws ServerException;

   /**
     * Gets a particular Datastream in the digital object.
     * If the date given is null, the most recent version of the datastream is
     * given.  If the date is non-null, the closest version of the Datastream
     * to the specified date/time (without going over) is given.
     * If no datastreams match the given criteria, null is returned.
     *
     * @param datastreamID The Datastream identifier
     * @param versDateTime The date-time stamp to get appropriate Datastream version
     * @return a particular Datastream in the digital object.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public Datastream GetDatastream(String datastreamID, Date versDateTime) throws ServerException;

    /**
	 * Gets a particular datastream in the digital object.
	 * This is an alternative to retrieving a datastream if all that is known
	 * is the version id (and not the date).
	 * The datastream id and version id must match actual ids of an existing
	 * datastream in the object.  Otherwise, null will be returned.
	 *
	 * @param datastreamID The datastream identifier
	 * @param versionID The identifier of the particular version
	 * @return a particular Datastream in the digital object
	 * @throws ServerException If any time of error occurred fulfilling the
	 *         request.
	 */
    public Datastream getDatastream(String datastreamID, String versionID) throws ServerException;

   /**
     * Same as getDatastreams, but for disseminators.
     *
     * @param versDateTime The date-time stamp to get appropriate Disseminator version
     * @return all Disseminators as of a certain date.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public Disseminator[] GetDisseminators(Date versDateTime, String state) throws ServerException;

   /**
     * Same as listDatastreamIds, but for disseminators.
     *
     * @param state The state of the Disseminators to be listed.
     * @return a list of Disseminator identifiers for all Disseminators in the
     *         digital object.
     * @throws ServerException If any type of error occurred fulfilling the
     *         request.
     */
    public String[] ListDisseminatorIDs(String state) throws ServerException;

   /**
     * Same as getDatastream, but for disseminators.
     *
     * @param disseminatorID The Disseminator identifier
     * @param versDateTime The date-time stamp to get appropriate Disseminator version
     * @return a particular Disseminator in the digital object.
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
     * @return a list of PIDs of Behavior Definitions to which object subscribes.
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
     * @return a list of method definitions that are available on a particular
     *         Disseminator.
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
     * @return a list of ALL method definitions that are available on a particular
     *         digital object.
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
     * @return a particular method definition (filtered using bDefPID) as XML.
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
     * @return a list of method parameter definitions that are available on a
     *         particular method.
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
     * @return the dissemination binding info necessary to perform a particular
     *         dissemination.
     * @throws ServerException If anything went wrong
     */
    public DisseminationBindingInfo[] getDisseminationBindingInfo(String bDefPID,
          String methodName, Date versDateTime) throws ServerException;

}