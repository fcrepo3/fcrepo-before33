package fedora.server.access;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import fedora.server.Context;
import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ServerException;
import fedora.server.storage.DisseminatingDOReader;
import fedora.server.storage.DOManager;
import fedora.server.storage.types.DisseminationBindingInfo;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.Property;
import fedora.server.utilities.DateUtility;

/**
 *
 * <p>Title: DefaultAccess.java</p>
 * <p>Description: The Access Module, providing support for the Fedora Access
 * subsystem.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class DefaultAccess extends Module implements Access
{
  /** Constant holding value of xml MIME type. */
  private final static String CONTENT_TYPE_XML = "text/xml";

  /** Current DOManager of the Fedora server. */
  private DOManager m_manager;

  /**
   * <p>Creates and initializes the Access Module. When the server is starting
   * up, this is invoked as part of the initialization process.</p>
   *
   * @param moduleParameters A pre-loaded Map of name-value pairs comprising
   *        the intended configuration of this Module.
   * @param server The <code>Server</code> instance.
   * @param role The role this module fulfills, a java class name.
   * @throws ModuleInitializationException If initilization values are
   *         invalid or initialization fails for some other reason.
   */
  public DefaultAccess(Map moduleParameters, Server server, String role)
          throws ModuleInitializationException
  {
    super(moduleParameters, server, role);
  }

  /**
   * <p>Initializes the module.</p>
   *
   * @throws ModuleInitializationException If the module cannot be initialized.
   */
  public void initModule() throws ModuleInitializationException
  {
    m_manager=(DOManager) getServer().getModule(
        "fedora.server.storage.DOManager");
    if (m_manager == null)
    {
      throw new ModuleInitializationException("Can't get a DOManager "
          + "from Server.getModule", getRole());
    }
  }

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
      Calendar asOfDateTime) throws ServerException
  {
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    String[] behaviorDefs = null;
    DisseminatingDOReader reader =
        m_manager.getDisseminatingReader(context, PID);
    behaviorDefs = reader.GetBehaviorDefs(versDateTime);
    return behaviorDefs;
  }

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
      String bDefPID, Calendar asOfDateTime) throws ServerException
  {
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    DisseminatingDOReader reader =
        m_manager.getDisseminatingReader(context, PID);
    MethodDef[] methodResults =
        reader.GetBMechMethods(bDefPID, versDateTime);
    return methodResults;
  }

  /**
   * <p>Gets the method definitions of the Behavior Mechanism object
   * associated with the specified Behavior Definition object in the form of
   * XML as defined in the WSDL.</p>
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
  public MIMETypedStream getBehaviorMethodsAsWSDL(Context context,
      String PID, String bDefPID, Calendar asOfDateTime) throws ServerException
  {
    try
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(4096);
      Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
      InputStream methodResults = null;
      DisseminatingDOReader reader =
          m_manager.getDisseminatingReader(context, PID);
      methodResults = reader.GetBMechMethodsWSDL(bDefPID, versDateTime);
      int byteStream = 0;
      while ((byteStream = methodResults.read()) >= 0)
      {
        baos.write(byteStream);
      }
      methodResults.close();
      if (methodResults != null)
      {
        MIMETypedStream methodDefs =
            new MIMETypedStream(CONTENT_TYPE_XML, baos.toByteArray());
        return methodDefs;
      }
    } catch (IOException ioe)
    {
      getServer().logWarning(ioe.getMessage());
      throw new GeneralException("DefaultAccess returned error. The "
                                 + "underlying error was a "
                                 + ioe.getClass().getName() + "The message "
                                 + "was \"" + ioe.getMessage() + "\"");
    }
    return null;
  }

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
      Calendar asOfDateTime) throws ServerException
  {
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    DisseminatingDOReader reader =
        m_manager.getDisseminatingReader(context, PID);

    // Get dissemination binding info.
    DisseminationBindingInfo[] dissBindInfo =
        reader.getDissemination(PID, bDefPID, methodName, versDateTime);

    // Assemble and execute the dissemination request from the binding info.
    DisseminationService dissService = new DisseminationService();
    MIMETypedStream dissemination =
        dissService.assembleDissemination(userParms, dissBindInfo);
    return dissemination;
  }

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
      Calendar asOfDateTime) throws ServerException
  {
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    DisseminatingDOReader reader =
        m_manager.getDisseminatingReader(context, PID);
    ObjectMethodsDef[] methodDefs =
        reader.getObjectMethods(PID, versDateTime);
    return methodDefs;
  }
}