package fedora.server.access;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import fedora.server.Context;
import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.InvalidUserParmException;
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
    Hashtable h_userParms = new Hashtable();
    MIMETypedStream dissemination = null;
    MethodParmDef[] defaultMethodParms = null;
    DisseminatingDOReader reader =
        m_manager.getDisseminatingReader(context, PID);

    // Put any user-supplied method parameters into hash table
    if (userParms != null)
    {
      for (int i=0; i<userParms.length; i++)
      {
        h_userParms.put(userParms[i].name, userParms[i].value);
      }
    }

    // Validate user-supplied parameters
    validateUserParms(context, PID, bDefPID, methodName,
                      h_userParms, versDateTime);

    // Add any default method parameters to validated user parm list
    defaultMethodParms = reader.GetBMechDefaultMethodParms(bDefPID,
        methodName, versDateTime);
    for (int i=0; i<defaultMethodParms.length; i++)
    {
      System.out.println("addedName: "+defaultMethodParms[i].parmName);
      System.out.println("addedValue: "+defaultMethodParms[i].parmDefaultValue);
      h_userParms.put(defaultMethodParms[i].parmName,
                      defaultMethodParms[i].parmDefaultValue);
    }

    // Get dissemination binding info.
    DisseminationBindingInfo[] dissBindInfo =
        reader.getDissemination(PID, bDefPID, methodName, versDateTime);

    // Assemble and execute the dissemination request from the binding info.
    DisseminationService dissService = new DisseminationService();
    dissemination =
        dissService.assembleDissemination(PID, h_userParms, dissBindInfo);
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

  /**
   * <p>Validates user-supplied method parameters against values
   * in the corresponding Behavior Definition object. The method will validate
   * for:</p>
   * <ol>
   * <li> Valid name - each name must match a valid method parameter name</li>
   * <li> DefaultValue - any specified parameters with valid default values
   * will have the default value substituted if the user-supplied value is null
   * </li>
   * <li> Required name - each required method parameter name must be present
   * </ol>
   *
   * @param context The context of this request.
   * @param PID The persistent identifier of the digital object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param methodName The name of the method.
   * @param h_userParms A hashtable of user-supplied method parameter
   *        name/value pairs.
   * @param versDateTime The version datetime stamp of the digital object.
   * @throws ServerException If any type of error occurred fulfilling the
   *         request.
   *
   */
  private void validateUserParms(Context context, String PID, String bDefPID,
      String methodName, Hashtable h_userParms, Date versDateTime)
      throws ServerException
  {
    DisseminatingDOReader fdor = null;
    MethodParmDef[] methodParms = null;
    MethodParmDef methodParm = null;
    StringBuffer sb = new StringBuffer();
    Hashtable h_validParms = new Hashtable();
    boolean isValid = true;

    DisseminatingDOReader reader =
      m_manager.getDisseminatingReader(context, PID);
    methodParms = reader.GetBMechMethodParms(bDefPID,
        methodName, versDateTime);

    // Put valid method parameters and their attributes into hashtable
    if (methodParms != null)
    {
      for (int i=0; i<methodParms.length; i++)
      {
        methodParm = methodParms[i];
        h_validParms.put(methodParm.parmName,methodParm);
        System.out.println("methodParms[" + i + "]: " + methodParms[i].parmName
            + "label: " + methodParms[i].parmLabel
            + "default: " + methodParms[i].parmDefaultValue
            + "required: " + methodParms[i].parmRequired
            + "type: " + methodParms[i].parmType);
        for (int j=0; j<methodParms[i].parmDomainValues.length; j++)
        {
          System.out.println("domain: " + methodParms[i].parmDomainValues[j]);
        }
      }
    }

    // Check if there are any user supplied parameters
    if (!h_userParms.isEmpty())
    {
      // Iterate over each user supplied parameter name
      Enumeration parmNames = h_userParms.keys();
      while (parmNames.hasMoreElements())
      {
        String parmName = (String)parmNames.nextElement();
        if (h_validParms.isEmpty())
        {
          // This is an error. There are no method parameters defined for
          // this method and user parameters are specified in the
          // dissemination request.
          sb.append("The method parameter \"" + parmName
                    + "\" is not valid for the method \""
                    + methodName + "\"."
                    + "The method \"" + methodName
                    + "\" defines no method parameters.");
          throw new InvalidUserParmException("[Invalid User Parameters] "
              + sb.toString());
        }
        methodParm = (MethodParmDef)h_validParms.get(parmName);
        if (methodParm != null && methodParm.parmName != null)
        {
          // Method has one or more parameters defined
          if (methodParm.parmRequired)
          {
            // Method parm is required
            if (h_userParms.get(methodParm.parmName) == null)
            {
              // This is a fatal error. A required method parameter does not
              // appear in the list of user supplied parameters.
              sb.append("The required parameter \""
                  + methodParm.parmName + "\" was not found in the "
                  + "user-supplied parameter list.");
              isValid = false;
            }
          }

          // Check for default value if user-supplied value is null or empty
          String value = (String)h_userParms.get(methodParm.parmName);
          if (value == null && value.equalsIgnoreCase(""))
          {
            // Value of user-supplied parameter is  null or empty
            if(methodParm.parmDefaultValue != null)
            {
              // Default value is specified for this parameter.
              // Substitute default value.
              h_userParms.put(methodParm.parmName, methodParm.parmDefaultValue);
              System.out.println("SET DEFAULT VALUE: "+
                                 methodParm.parmDefaultValue);
            } else
            {
              // This is a non-fatal error. There is no default specified
              // for this parameter and the user has supplied no value for
              // the parameter. The value of the empty string will be used
              // as the value of the parameter.
              this.getServer().logWarning("The method parameter \""
                  + methodParm.parmName
                  + "\" has no default value and no "
                  + "value was specified by the user.  "
                  + "The value of the empty string has "
                  + "been assigned to this parameter.");
            }
          } else
          {
            // Value of user-supplied parameter contains a value.
            // Validate the supplied value against the parmDomainValues list.
            String[] parmDomainValues = methodParm.parmDomainValues;
            if (!parmDomainValues[0].equalsIgnoreCase(""))
            {
              boolean isValidValue = false;
              String userValue = (String)h_userParms.get(methodParm.parmName);
              System.out.println("userValue: "+userValue);
              for (int i=0; i<parmDomainValues.length; i++)
              {
                System.out.println("parmDomain: "+parmDomainValues[i]);
                if (userValue.equalsIgnoreCase(parmDomainValues[i]))
                {
                  isValidValue = true;
                }
              }
              if (!isValidValue)
              {
                // This is a fatal error. The value supplied for this method
                // parameter does not match any of the values specified by
                // this method.
                sb.append("The method parameter \""
                          + methodParm.parmName
                          + "\" with a value of \""
                          + (String)h_userParms.get(methodParm.parmName)
                          + "\" is not allowed for the method \""
                          + methodName + "\".");
                isValid = false;
              }
            }
          }
        } else
        {
          // This is a fatal error. A user-supplied parameter name does
          // not match any valid parameter names for this method.
          sb.append("The method parameter \"" + parmName
                    + "\" is not valid for the method \""
                    + methodName + "\".");
          isValid = false;
        }
      }
    } else
    {
      // There were no user supplied parameters.
      // Check if this method has any required parameters.
      if (methodParms != null)
      {
        for (int i=0; i<methodParms.length; i++)
        {
          methodParm = methodParms[i];
          if (methodParm.parmRequired)
          {
            // This is a fatal error. A required method parameter was not
            // supplied.
            sb.append("The method parameter \""
                + methodParm.parmName
                + "\" is required by the method \""
                + methodName + "\".");
            isValid = false;
          }
        }
      }
    }

    if (!isValid)
    {
      throw new InvalidUserParmException("[Invalid User Parameter] "
          + sb.toString());
    }
    return;
  }
}