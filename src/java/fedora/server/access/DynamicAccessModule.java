package fedora.server.access;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.Map;

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
 * <p>Description: Module Wrapper for DynamicAccessImpl.java. </p>
 * processing.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette  payette@cs.cornell.edu
 * @version 1.0
 */
public class DynamicAccessModule extends Module implements Access
{

  /**
   * An instance of the core implementation class for DOValidator.
   * The DOValidatorModule acts as a wrapper to this class.
   */
  private DynamicAccessImpl da = null;;

  /** Current DOManager of the Fedora server. */
  private DOManager m_manager;

  /** IP Restriction for the Access subsystem. */
  private IPRestriction m_ipRestriction;

  private String reposBaseURL = null;


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
  public DynamicAccessModule(Map moduleParameters, Server server, String role)
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
    m_manager = (DOManager)
        getServer().getModule("fedora.server.storage.DOManager");
    if (m_manager == null)
    {
      throw new ModuleInitializationException("Can't get a DOManager "
          + "from Server.getModule", getRole());
    }
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
  }
  public void postInitModule()
      throws ModuleInitializationException
  {
      // Get the repository Base URL
      InetAddress hostIP = null;
      try
      {
        hostIP = InetAddress.getLocalHost();
      } catch (UnknownHostException uhe)
      {
        System.err.println("[DefaultBehaviorImpl] was unable to "
            + "resolve the IP address of the Fedora Server: "
            + " The underlying error was a "
            + uhe.getClass().getName() + "The message "
            + "was \"" + uhe.getMessage() + "\"");
      }
      String fedoraServerPort = getServer().getParameter("fedoraServerPort");
      reposBaseURL = "http://" + hostIP.getHostAddress() + ":" + fedoraServerPort;

      // get ref to the Dynamic Access implementation class
      da = new DynamicAccessImpl(reposBaseURL);
      da.dynamicBDefToMech = new Hashtable();
      try
      {
        da.dynamicBDefToMech.put("fedora-system:3",
          Class.forName(getParameter("fedora-system:4")));
      }
      catch(Exception e)
      {
        System.out.println("Unable to initialize module parameters: " + e.getMessage());
        throw new ModuleInitializationException(
            e.getMessage(),"fedora.server.validation.DOValidatorModule");
      }
  }

  public String[] getBehaviorDefinitions(Context context, String PID,
      Calendar asOfDateTime) throws ServerException
  {
    //m_ipRestriction.enforce(context);
    return da.getBehaviorDefinitions(context, PID, asOfDateTime);
  }


  /**
   */
  public MethodDef[] getBehaviorMethods(Context context, String PID,
      String bDefPID, Calendar asOfDateTime) throws ServerException
  {
    //m_ipRestriction.enforce(context);
    return da.getBehaviorMethods(context, PID, bDefPID, asOfDateTime);
  }

  /**
   */
  public MIMETypedStream getBehaviorMethodsXML(Context context, String PID,
      String bDefPID, Calendar asOfDateTime) throws ServerException
  {
    //m_ipRestriction.enforce(context);
    return da.getBehaviorMethodsXML(context, PID, bDefPID, asOfDateTime);
  }

  /**
   *
   */
  public MIMETypedStream getDissemination(Context context, String PID,
      String bDefPID, String methodName, Property[] userParms,
      Calendar asOfDateTime) throws ServerException
  {
    //m_ipRestriction.enforce(context);

    // We must reset the context to not used a cached reader
    // (e.g., FastDOReader) because we do not have inline xml datastream
    // available in FastDOReader yet.  We want to use DefinitiveDOReader
    // or SimpleDOReader, which are provided by DOManager when the context
    // is set to useCachedObject=false.

    HashMap h=new HashMap();
    h.put("application", "apia");
    h.put("useCachedObject", "false");
    h.put("userId", "fedoraAdmin");
    ReadOnlyContext newContext = new ReadOnlyContext(h);

    // CONTEXT CHANGE! Pass along the dynamic dissemination request,
    // with a reader appropriate to the dynamic context (non-cached).
    return da.getDissemination(context, PID, bDefPID, methodName, userParms,
      asOfDateTime, m_manager.getReader(newContext, PID));
  }

  /**
   */
  public ObjectMethodsDef[] getObjectMethods(Context context, String PID,
      Calendar asOfDateTime) throws ServerException
  {
    //m_ipRestriction.enforce(context);
    return da.getObjectMethods(context, PID, asOfDateTime);
  }

  // FIXIT! What do these serach methods mean in dynamic access context???
  // Maybe they can be taken out of the Access interface and put in a
  // Search interface and then only DefaultAccess will implement them!!
  public List search(Context context, String[] resultFields, String terms)
          throws ServerException
  {
    //m_ipRestriction.enforce(context);
    return da.search(context, resultFields, terms);
  }

  public List search(Context context, String[] resultFields,
          List conditions)
          throws ServerException
  {
    //m_ipRestriction.enforce(context);
    return da.search(context, resultFields, conditions);
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