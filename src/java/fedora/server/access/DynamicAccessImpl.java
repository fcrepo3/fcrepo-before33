package fedora.server.access;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Map;
import java.io.File;
import java.lang.Class;
import java.lang.reflect.*;

import fedora.server.Server;
import fedora.server.Context;
import fedora.server.access.internalservices.DefaultBehavior;
import fedora.server.access.internalservices.DefaultBehaviorImpl;
import fedora.server.access.internalservices.ServiceMethodDispatcher;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ServerException;
import fedora.server.security.IPRestriction;
import fedora.server.utilities.DateUtility;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.Property;

/**
 * <p>Title: DynamicAccessImpl.java</p>
 * <p>Description:
 * processing.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */
public class DynamicAccessImpl
{

  private Access m_access;
  private ServiceMethodDispatcher dispatcher;
  private String reposBaseURL = null;
  private File reposHomeDir = null;
  private Hashtable dynamicBDefToMech = null;

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
  public DynamicAccessImpl(Access m_access, String reposBaseURL,
    File reposHomeDir, Hashtable dynamicBDefToMech)
  {
    dispatcher = new ServiceMethodDispatcher();
    this.m_access = m_access;
    this.reposBaseURL = reposBaseURL;
    this.reposHomeDir = reposHomeDir;
    this.dynamicBDefToMech = dynamicBDefToMech;
  }

  /**
   */
  public String[] getBehaviorDefinitions(Context context, String PID,
      Calendar asOfDateTime) throws ServerException
  {

    // FIXIT! In FUTURE this method might consult some source that tells
    // what behavior definitions are appropriate to dynamically associate
    // with the object.  The rules for association might be based on the
    // context or based on something about the particular object (PID).
    // There is one rule that is always true - associate the Default
    // behavior definition with EVERY object.

    // For now we will just take the dynamic behavior definitions that were
    // loaded from the server configuration file (fedora.fcfg)
    // via DynamicAccessModule.
    // NOTE: AT THIS TIME THERE THERE IS JUST ONE LOADED, NAMELY,
    // THE DEFAULT DISSEMINATOR BDEF (bDefPID = fedora-system:3)

    ArrayList bdefs = new ArrayList();
    Iterator iter = dynamicBDefToMech.keySet().iterator();
    while (iter.hasNext())
    {
      bdefs.add(iter.next());
    }
    return (String[])bdefs.toArray(new String[0]);

    //String[] bdefs = new String[1];
    //bdefs[0] = "fedora-system:3";
    //return bdefs;
  }


  /**
   */
  public MethodDef[] getBehaviorMethods(Context context, String PID,
      String bDefPID, Calendar asOfDateTime) throws ServerException
  {
    Class mechClass = (Class) dynamicBDefToMech.get(bDefPID);
    if (mechClass != null)
    {
      try
      {
        Method method = mechClass.getMethod("reflectMethods", null);
        return (MethodDef[])method.invoke(null, null);
      }
      catch (Exception e)
      {
        throw new GeneralException("[DynamicAccessImpl] returned error when "
            + "attempting to get dynamic behavior method definitions. "
            + "The underlying error class was: "
            + e.getClass().getName() + ". The message "
            + "was \"" + e.getMessage() + "\"");
      }
    }
    // FIXIT!! Is this what we want to do or throw an error?
    // This means that the bDefPID is not listed in the dynamicBDefToMech
    // table as one of the supported dynamic behavior definitions.
    // We can quietly return no methods, but we need to look at this!!
    return new MethodDef[0];

    /*
    if (bDefPID.equalsIgnoreCase("fedora-system:3"))
    {
      return DefaultBehaviorImpl.reflectMethods();
    }
    return new MethodDef[0];
    */

  }

  /**
   */
  public MIMETypedStream getBehaviorMethodsXML(Context context, String PID,
      String bDefPID, Calendar asOfDateTime) throws ServerException
  {
    return null;
  }

  /**
   *
   */
  public MIMETypedStream getDissemination(Context context, String PID,
      String bDefPID, String methodName, Property[] userParms,
      Calendar asOfDateTime, DOReader reader) throws ServerException
  {
    if (bDefPID.equalsIgnoreCase("fedora-system:3"))
    {
      Object result = dispatcher.invokeMethod(
          new DefaultBehaviorImpl(context, asOfDateTime,
            reader, m_access, reposBaseURL, reposHomeDir), methodName, userParms);
      if (result.getClass().getName().equalsIgnoreCase(
        "fedora.server.storage.types.MIMETypedStream"))
      {
        return (MIMETypedStream)result;
      }
      else
      {
          throw new GeneralException("DynamicAccess returned error. "
            + "DefaultBehaviorImpl must return a MIME typed stream. "
            + "(see fedora.server.storage.types.MIMETypedStream)");
      }
    }
    else
    {
      // FIXIT! (FUTURE) Open up the possibility of there being other
      // kinds of dynamic behaviors.  Use the bDefPID to locate the
      // appropriate mechanism for the dynamic behavior.
      // Maybe lookup to some registry table:
      // bDefPID | bMechPID | internal/external

      /*
      String bMechPID = getBehaviorMechanismPID(bDefPID);
      if (isInternalService(bMechPID))
      {

      }
      else
      {
        // Figure out something brilliant in the future...
      }
      */
    }
    return null;
  }

  /**
   */
  public ObjectMethodsDef[] getObjectMethods(Context context, String PID,
      Calendar asOfDateTime) throws ServerException
  {
    String[] bDefPIDs = getBehaviorDefinitions(context, PID, asOfDateTime);
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    ArrayList objectMethods = new ArrayList();
    for (int i=0; i<bDefPIDs.length; i++)
    {
      MethodDef[] methodDefs =
        getBehaviorMethods(context, PID, bDefPIDs[i], asOfDateTime);
      for (int j=0; j<methodDefs.length; j++)
      {
        ObjectMethodsDef method = new ObjectMethodsDef();
        method.PID = PID;
        method.asOfDate = versDateTime;
        method.bDefPID = bDefPIDs[i];
        method.methodName = methodDefs[j].methodName;
        method.methodParmDefs = methodDefs[j].methodParms;
        objectMethods.add(method);
      }

    }
    return (ObjectMethodsDef[])objectMethods.toArray(new ObjectMethodsDef[0]);
  }

  public ObjectProfile getObjectProfile(Context context, String PID,
    Calendar asOfDateTime) throws ServerException
  {
    return null;
  }

  // FIXIT! What do these serach methods mean in dynamic access context???
  // Maybe they can be taken out of the Access interface and put in a
  // Search interface and then only DefaultAccess will implement them!!
  public List search(Context context, String[] resultFields,
          String terms)
          throws ServerException
  {
    return null;
  }

  public List search(Context context, String[] resultFields,
          List conditions)
          throws ServerException
  {
    return null;
  }

  /**
   */
  public boolean isDynamicBehaviorDefinition(Context context, String PID,
        String bDefPID)
        throws ServerException
  {
    // We want to have some registry to determine whether a particular
    // behavior definition should be dynamically associated with an object.
    // This determination would be based on the same rules embodied within
    // the getBehaviorDefinitions implementation of the DynamicAccess class.

    // FIXIT!! For now, HACK a return of true for the Default Disseminator
    // behavior definition.

    if (bDefPID.equalsIgnoreCase("fedora-system:3"))
    {
      System.out.println("DETECTED A DYNAMIC BEHAVIOR DEF: fedora-system:3");
      return true;
    }
    return false;
  }
}