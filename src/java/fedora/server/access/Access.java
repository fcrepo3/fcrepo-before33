package fedora.server.access;

import java.util.Calendar;
import java.util.List;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.Property;

/**
 * <p>Title: DefaultAccess.java</p>
 * <p>Description: Defines the Fedora Access subsystem interface.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public interface Access
{

  /**
   * <p>Gets the persistent identifiers or PIDs of all Behavior Definition
   * objects associated with the specified digital object.</p>
   *
   * @param context The context of this request.
   * @param PID The persistent identifier of the digitla object.
   * @param asOfDateTime The versioning datetime stamp.
   * @return An Array containing the list of Behavior Definition object PIDs.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public String[] getBehaviorDefinitions(Context context, String PID,
      Calendar asOfDateTime) throws ServerException;

  /**
   * <p>Gets the method definitions of the Behavior Mechanism object
   * associated with the specified Behavior Definition object in the form of
   * an array of method definitions.</p>
   *
   * @param context The context of this request.
   * @param PID The persistent identifier of the digital object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param asOfDateTime The versioning datetime stamp.
   * @return An Array containing the list of method definitions.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public MethodDef[] getBehaviorMethods(Context context, String PID,
      String bDefPID, Calendar asOfDateTime) throws ServerException;

  /**
   * <p>Gets the method definitions associated with the specified Behavior
   * Definition object in the form of XML as defined in the WSDL.</p>
   *
   * @param context The context of this request.
   * @param PID The persistent identifier of the digital object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param asOfDateTime The versioning datetime stamp.
   * @return A MIME-typed stream containing the method definitions in the form
   *         of an XML fragment obtained from the WSDL in the associated
   *         Behavior Mechanism object.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   */
  public MIMETypedStream getBehaviorMethodsXML(Context context, String PID,
      String bDefPID, Calendar asOfDateTime) throws ServerException;

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
      Calendar asOfDateTime) throws ServerException;

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
  public ObjectMethodsDef[] getObjectMethods(Context context, String PID,
      Calendar asOfDateTime) throws ServerException;

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
      Calendar asOfDateTime) throws ServerException;

  public List search(Context context, String[] resultFields,
          String terms)
          throws ServerException;

  public List search(Context context, String[] resultFields,
          List conditions)
          throws ServerException;

}