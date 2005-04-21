package fedora.server.access;

import java.util.Date;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.search.FieldSearchQuery;
import fedora.server.search.FieldSearchResult;
import fedora.server.storage.types.DatastreamDef;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.Property;

/**
 * <p><b>Title: </b>Access.java</p>
 * <p><b>Description: </b>Defines the Fedora Access subsystem interface.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public interface Access
{

    public MIMETypedStream getDatastreamDissemination(Context context, String PID,
        String dsID, Date asOfDateTime) throws ServerException;

  /**
   * <p>Disseminates the content produced by executing the specified method
   * of the associated Behavior Mechanism object of the specified digital
   * object.</p>
   *
   * @param context The context of this request.
   * @param PID The persistent identifier of the digital object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param methodName The name of the method to be executed.
   * @param userParms An array of user-supplied method parameters consisting
   *        of name/value pairs.
   * @param asOfDateTime The versioning datetime stamp.
   * @return A MIME-typed stream containing the result of the dissemination.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public MIMETypedStream getDissemination(Context context, String PID,
      String bDefPID, String methodName, Property[] userParms,
      Date asOfDateTime) throws ServerException;

  /**
   * <p>Gets object profile</p>
   *
   * @param context The context of this request.
   * @param PID The persistent identifier of the digital object
   * @param asOfDateTime The versioning datetime stamp
   * @return An array of all methods associated with the specified
   *         digital object.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public ObjectProfile getObjectProfile(Context context, String PID,
      Date asOfDateTime) throws ServerException;

  /**
   * <p>Gets a list of all Behavior Definition object PIDs and method names
   * associated with the specified digital object.</p>
   *
   * @param context The context of this request.
   * @param PID The persistent identifier of the digital object
   * @param asOfDateTime The versioning datetime stamp
   * @return An array of all methods associated with the specified
   *         digital object.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  /*
  public ObjectMethodsDef[] getObjectMethods(Context context, String PID,
      Date asOfDateTime) throws ServerException;
  */

  public ObjectMethodsDef[] listMethods(Context context, String PID,
      Date asOfDateTime) throws ServerException;

  public DatastreamDef[] listDatastreams(Context context, String PID,
      Date asOfDateTime) throws ServerException;

  /**
   * <p>Lists the specified fields of each object matching the given
   * criteria.</p>
   *
   * @param context the context of this request
   * @param resultFields the names of the fields to return
   * @param maxResults the maximum number of results to return at a time
   * @param query the query
   * @return the specified fields of each object matching the given
   *         criteria.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public FieldSearchResult findObjects(Context context,
          String[] resultFields, int maxResults, FieldSearchQuery query)
          throws ServerException;

  /**
   * <p>Resumes an in-progress listing of object fields.</p>
   *
   * @param context the context of this request
   * @param sessionToken the token of the session in which the remaining
   *        results can be obtained
   * @return the remaining specified fields of each object matching the given
   *         criteria.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public FieldSearchResult resumeFindObjects(Context context,
          String sessionToken) throws ServerException;

  /**
   * <p>Gets information that describes the repository.</p>
   *
   * @param context the context of this request
   * @return information that describes the repository.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public RepositoryInfo describeRepository(Context context) throws ServerException;

  /**
   * <p>Gets the change history of an object by returning a list of timestamps
   * that correspond to modification dates of components. This currently includes
   * changes to datastreams and disseminators.</p>
   *
   * @param context The context of this request.
   * @param PID The persistent identifier of the digitla object.
   * @return An Array containing the list of timestamps indicating when changes
   *         were made to the object.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public String[] getObjectHistory(Context context, String PID) throws ServerException;

}