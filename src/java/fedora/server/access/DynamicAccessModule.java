package fedora.server.access;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

// Fedora imports
import fedora.server.errors.ServerException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ObjectValidityException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.InitializationException;
import fedora.server.security.IPRestriction;
import fedora.server.Context;
import fedora.server.Module;
import fedora.server.Server;
import fedora.server.ReadOnlyContext;
import fedora.server.search.FieldSearchQuery;
import fedora.server.search.FieldSearchResult;
import fedora.server.storage.DOManager;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.Property;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>Title: DynamicAccessModule.java</p>
 * <p>Description: Module Wrapper for DynamicAccessImpl.java.
 * The Dynamic Access module will associate dynamic disseminators with
 * the a digital object.  It will look to the Fedora repository configuration
 * file to obtain a list of dynamic disseminators.  Currently, the system
 * supports two types of dynamic disseminators:
 *  - Default (BDefPID=fedora-system:3 and BMechPID=fedora-system:4)
 *  - Bootstrap (BDefPID=fedora-system:1 and BMechPID=fedora-system:2).
 * The Default disseminator that is associated with every object
 * in the repository.  The Default Disseminator endows the objects with a
 * set of basic generic behaviors that enable a simplistic view of the object
 * contents (the Item Index) and a list of all disseminations available on
 * the object (the Dissemination Index).
 * The Bootstrap disseminator is associated with every behavior definition and
 * behavior mechanism object.  It defines methods to get the special metadata
 * datastreams out of them, and some other methods.  (NOTE: The Bootstrap
 * Disseminator functionality is NOT YET IMPLEMENTED.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette  payette@cs.cornell.edu
 * @version 1.0
 */
public class DynamicAccessModule extends Module implements Access
{
  /**
   * An instance of the core implementation class for DynamicAccess.
   * The DynamicAccessModule acts as a wrapper to this class.
   */
  private DynamicAccessImpl da = null;;

  /** Current DOManager of the Fedora server. */
  private DOManager m_manager;

  /** Main Access module of the Fedora server. */
  private Access m_access;

  /** IP Restriction for the Access subsystem. */
  private IPRestriction m_ipRestriction;

  private Hashtable dynamicBDefToMech = null;
  private String reposBaseURL = null;
  private File reposHomeDir = null;


  /**
   * <p>Creates and initializes the Dynmamic Access Module.
   * When the server is starting up, this is invoked as part of the
   * initialization process.</p>
   *
   * @param moduleParameters A pre-loaded Map of name-value pairs comprising
   *        the intended configuration of this Module.
   * @param server The <code>Server</code> instance.
   * @param role The role this module fulfills, a java class name.
   * @throws ModuleInitializationException If initilization values are
   *         invalid or initialization fails for some other reason.
   */
  public DynamicAccessModule(Map moduleParameters, Server server, String role)
          throws ModuleInitializationException
  {
    super(moduleParameters, server, role);
  }

  public void postInitModule()
      throws ModuleInitializationException
  {
    // FIXIT! NOT DOING IP restriction in DynamicAccess because it is done
    // already by DefaultAccess.  Consider possible cases where we
    // may want to do it here.
    /*
    String allowHosts=getParameter("allowHosts");
    String denyHosts=getParameter("denyHosts");
    try {
        m_ipRestriction = new IPRestriction(allowHosts, denyHosts);
    } catch (ServerException se) {
        throw new ModuleInitializationException("Error setting IP restriction "
                + "for DynamicAccess " + se.getClass().getName() + ": "
                + se.getMessage(), getRole());
    }
    */
      m_manager = (DOManager)
          getServer().getModule("fedora.server.storage.DOManager");
      if (m_manager == null)
      {
        throw new ModuleInitializationException("[DynamicAccessModule] " +
            "Can't get a DOManager from Server.getModule", getRole());
      }
      m_access = (Access)
          getServer().getModule("fedora.server.access.Access");
      if (m_access == null)
      {
        throw new ModuleInitializationException("[DynamicAccessModule] " +
            "Can't get a ref to Access from Server.getModule", getRole());
      }
      // Get the repository Base URL
      InetAddress hostIP = null;
      try
      {
        hostIP = InetAddress.getLocalHost();
      } catch (UnknownHostException uhe)
      {
        System.err.println("[DynamicAccessModule] was unable to "
            + "resolve the IP address of the Fedora Server: "
            + " The underlying error was a "
            + uhe.getClass().getName() + "The message "
            + "was \"" + uhe.getMessage() + "\"");
      }
      String fedoraServerPort = getServer().getParameter("fedoraServerPort");
      reposBaseURL = "http://" + hostIP.getHostAddress() + ":" + fedoraServerPort;
      reposHomeDir = getServer().getHomeDir();

      // FIXIT!! In the future, we want to read the repository configuration
      // file for the list of dynamic behavior definitions and their
      // associated internal service classes.  For now, we are explicitly
      // loading up the Default behavior def/mech since this is the only
      // thing supported in the system right now.
      dynamicBDefToMech = new Hashtable();
      try
      {
        dynamicBDefToMech.put("fedora-system:3",
          Class.forName(getParameter("fedora-system:4")));
      }
      catch(Exception e)
      {
        System.out.println("Unable to initialize module parameters: " + e.getMessage());
        throw new ModuleInitializationException(
            e.getMessage(),"fedora.server.validation.DOValidatorModule");
      }

      // get ref to the Dynamic Access implementation class
      da = new DynamicAccessImpl(m_access, reposBaseURL, reposHomeDir, dynamicBDefToMech);
  }

  /**
   * Get a list of behavior definition identifiers for dynamic disseminators
   * associated with the digital object.
   * @param context
   * @param PID   identifier of digital object being reflected upon
   * @param asOfDateTime
   * @return
   * @throws ServerException
   */
  public String[] getBehaviorDefinitions(Context context, String PID,
      Calendar asOfDateTime) throws ServerException
  {
    //m_ipRestriction.enforce(context);
    return da.getBehaviorDefinitions(context, PID, asOfDateTime);
  }

  /**
   * Get the behavior method defintions for a given dynamic disseminator that
   * is associated with the digital object. The dynamic disseminator is
   * identified by the bDefPID.
   * @param context
   * @param PID   identifier of digital object being reflected upon
   * @param bDefPID identifier of dynamic behavior definition
   * @param asOfDateTime
   * @return
   * @throws ServerException
   */
  public MethodDef[] getBehaviorMethods(Context context, String PID,
      String bDefPID, Calendar asOfDateTime) throws ServerException
  {
    //m_ipRestriction.enforce(context);
    return da.getBehaviorMethods(context, PID, bDefPID, asOfDateTime);
  }

  /**
   * Get an XML encoding of the behavior defintions for a given dynamic
   * disseminator that is associated with the digital object.  The dynamic
   * disseminator is identified by the bDefPID.
   * @param context
   * @param PID  identifier of digital object being reflected upon
   * @param bDefPID  identifier of dynamic behavior definition
   * @param asOfDateTime
   * @return
   * @throws ServerException
   */
  public MIMETypedStream getBehaviorMethodsXML(Context context, String PID,
      String bDefPID, Calendar asOfDateTime) throws ServerException
  {
    //m_ipRestriction.enforce(context);
    return da.getBehaviorMethodsXML(context, PID, bDefPID, asOfDateTime);
  }

  /**
   * Perform a dissemination for a behavior method that belongs to a
   * dynamic disseminator that is associate with the digital object.  The
   * method belongs to the dynamic behavior definition and is implemented
   * by a dynamic behavior mechanism (which is an internal service in the
   * repository access subsystem).
   * @param context
   * @param PID  identifier of the digital object being disseminated
   * @param bDefPID  identifier of dynamic behavior definition
   * @param methodName
   * @param userParms
   * @param asOfDateTime
   * @return
   * @throws ServerException
   */
  public MIMETypedStream getDissemination(Context context, String PID,
      String bDefPID, String methodName, Property[] userParms,
      Calendar asOfDateTime) throws ServerException
  {
    //m_ipRestriction.enforce(context);

    // NOTE!! We must reset the context to NOT use a cached reader
    // (e.g., FastDOReader) because the current implementation of the cached
    // reader does not support dissemination of inline xml datastreams.
    // We thus set the context to useCachedObject-false, so that DOManager
    // will provide a reader on the XML object storage.  In this case,
    // SimpleDOReader will be instantiated and the dissemination will be
    // performed by reading information directly from the XML objects.

    HashMap h=new HashMap();
    h.put("application", "apia");
    h.put("useCachedObject", "false");
    h.put("userId", "fedoraAdmin");
    ReadOnlyContext newContext = new ReadOnlyContext(h);
    return da.getDissemination(context, PID, bDefPID, methodName, userParms,
      asOfDateTime, m_manager.getReader(newContext, PID));
  }

  /**
   * Get the definitions for all dynamic disseminations on the object. This will
   * return the method definitions for all methods for all of the dynamic
   * disseminators associated with the object.
   * @param context
   * @param PID  identifier of digital object being reflected upon
   * @param asOfDateTime
   * @return
   * @throws ServerException
   */
  public ObjectMethodsDef[] getObjectMethods(Context context, String PID,
      Calendar asOfDateTime) throws ServerException
  {
    //m_ipRestriction.enforce(context);
    return da.getObjectMethods(context, PID, asOfDateTime);
  }

  /**
   * Get the profile information for the digital object.  This contain key
   * metadata and URLs for the Dissemination Index and Item Index of the
   * object.
   * @param context
   * @param PID  identifier of digital object being reflected upon
   * @param asOfDateTime
   * @return
   * @throws ServerException
   */
  public ObjectProfile getObjectProfile(Context context, String PID,
    Calendar asOfDateTime) throws ServerException
  {
    return null;
  }

  // FIXIT: What do these mean in this context...anything?
  // Maybe these methods' exposure needs to be re-thought?
  public FieldSearchResult listObjectFields(Context context, 
          String[] resultFields, int maxResults, FieldSearchQuery query) 
          throws ServerException {
      return null;
  }

  // FIXIT: What do these mean in this context...anything?
  // Maybe these methods' exposure needs to be re-thought?
  public FieldSearchResult resumeListObjectFields(Context context,
          String sessionToken) throws ServerException {
      return null;
  }
  
  /**
   */
  protected boolean isDynamicBehaviorDefinition(Context context, String PID,
        String bDefPID)
        throws ServerException
  {
    //m_ipRestriction.enforce(context);
    return da.isDynamicBehaviorDefinition(context, PID, bDefPID);
  }
}