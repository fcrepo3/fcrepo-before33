package fedora.server.storage;

/**
 * <p>Title: ConnectionPoolManagerImpl.java</p>
 * <p>Description: Implements ConnectionPoolManager to facilitate obtaining
 * ConnectionPools. This class initializes the ConnectionPools specified
 * by parameters in the Fedora <code>fedora.fcfg</code> configuration file.
 * The Fedora server must be instantiated in order for this class to
 * function properly.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */

// java imports
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import java.sql.SQLException;

// Fedora imports
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.InitializationException;
import fedora.server.Module;
import fedora.server.Server;

public class ConnectionPoolManagerImpl extends Module
    implements ConnectionPoolManager
{

  private static Hashtable h_ConnectionPools = null;
  private String jdbcDriverClass = null;
  private String dbUsername = null;
  private String dbPassword = null;
  private String jdbcURL = null;
  private int initConnections = 0;
  private int maxConnections = 0;
  private static String defaultPoolName = null;

  /**
   * <p>Creates a new ConnectionPoolManager</p>
   *
   * @param moduleParameters The name and value pair map of module parameters
   * @param server The server instance
   * @param role The module role name
   * @throws ModuleInitializationException If initialization values are
   *         invalid or initialization fails for some other reason.
   */
  public ConnectionPoolManagerImpl(Map moduleParameters,
                                   Server server, String role)
      throws ModuleInitializationException
  {
    super(moduleParameters, server, role);
  }

  /**
   * Initializes the Module based on configuration parameters. The
   * implementation of this method is dependent on the schema used to define
   * the parameter names for the role of
   * <code>fedora.server.storage.ConnectionPoolManager</code>.
   *
   * @throws ModuleInitializationException If initialization values are
   *         invalid or initialization fails for some other reason.
   */
  public void initModule() throws ModuleInitializationException
  {
    try
    {
      h_ConnectionPools = new Hashtable();
      Server s_server = this.getServer();
      // Get default pool name
      defaultPoolName = this.getParameter("defaultPoolName");
      if (defaultPoolName == null || defaultPoolName.equalsIgnoreCase(""))
      {
        String message = "Default Connection Pool Name Not Specified";
        throw new ModuleInitializationException(message,
            "fedora.server.storage.ConnectionPoolManagerImpl");
      }
      System.out.println("DefaultPoolName: "+defaultPoolName);
      // Get list of pool names from fedora.fcfg config file
      String poolList = this.getParameter("poolNames");
      // Names should be comma delimted so parse out names
      String[] poolNames = poolList.split(",");
      // Initialize each pool found
      for (int i=0; i<poolNames.length; i++)
      {
        System.out.println("poolName["+i+"] = "+poolNames[i]);
        jdbcDriverClass = s_server.getDatastoreConfig(poolNames[i]).
                 getParameter("jdbcDriverClass");
        System.out.println("driver: "+jdbcDriverClass);
        dbUsername = s_server.getDatastoreConfig(poolNames[i]).
                   getParameter("dbUsername");
        System.out.println("user: "+dbUsername);
        dbPassword = s_server.getDatastoreConfig(poolNames[i]).
                   getParameter("dbPassword");
        System.out.println("pass: "+dbPassword);
        jdbcURL = s_server.getDatastoreConfig(poolNames[i]).
                  getParameter("jdbcURL");
        System.out.println("URL: "+jdbcURL);
        Integer i1 = new Integer(s_server.getDatastoreConfig(poolNames[i]).
                                 getParameter("minPoolSize"));
        int initConnections = i1.intValue();
        System.out.println("min: "+initConnections);
        Integer i2 = new Integer(s_server.getDatastoreConfig(poolNames[i]).
                                 getParameter("maxPoolSize"));
        int maxConnections = i2.intValue();
        System.out.println("max: "+maxConnections);
        System.out.flush();
        // Create connection pool
        ConnectionPool connectionPool = new ConnectionPool(jdbcDriverClass,
            jdbcURL, dbUsername, dbPassword, initConnections, maxConnections,
            true);
        // Add ConnectionPool to hashtable
        System.out.println("Initialized Pool: "+connectionPool);
        h_ConnectionPools.put(poolNames[i],connectionPool);
        System.out.println("putPoolInHash: "+h_ConnectionPools.size());
      }
    } catch (SQLException sqe)
    {
      throw new ModuleInitializationException(
          sqe.getMessage(),"fedora.server.storage.ConnectionPoolManagerImpl");
    } catch (PatternSyntaxException pse)
    {
      throw new ModuleInitializationException(
          pse.getMessage(),"fedora.server.storage.ConnectionPoolManagerImpl");
    } catch (NullPointerException npe)
    {
      throw new ModuleInitializationException(
          npe.getMessage(),"fedora.server.storage.ConnectionPoolManagerImpl");
    } catch (Exception e)
    {
      throw new ModuleInitializationException(
          e.getMessage(),"fedora.server.storage.ConnectionPoolManagerImpl");
    }
  }

  /**
   * <p>Gets a named connection pool.</p>
   *
   * @param poolName The name of the connection pool.
   * @return The named connection pool.
   */
  public ConnectionPool getPool(String poolName)
      throws ModuleInitializationException
  {
    ConnectionPool connectionPool = null;
    try
    {
      if (h_ConnectionPools.containsKey(poolName))
      {
        // Pool exists
        connectionPool = (ConnectionPool)h_ConnectionPools.get(poolName);
        System.out.println("PoolFound: "+connectionPool);
      } else
      {
        // Error pool was never initialized or name could not be found
        String message = "CONNECTION POOL NOT INITIALIZED: "+poolName;
        throw new ModuleInitializationException(message,
            "fedora.server.storage.ConnectionPoolManagerImpl");
      }
    } catch (Exception e)
    {
      throw new ModuleInitializationException(
          e.getMessage(),"fedora.server.storage.ConnectionPoolManagerImpl");
    }
    return connectionPool;
  }

  /**
   * <p>Gets the default Connection Pool. This method overrides <code>
   * getPool(String poolName)</code>.</p>
   *
   * @return The default connection pool.
   */
  public ConnectionPool getPool()
      throws ModuleInitializationException
  {
    ConnectionPool connectionPool = null;
    try
    {
      if (h_ConnectionPools.containsKey(defaultPoolName))
      {
        // Pool exists
        connectionPool = (ConnectionPool)h_ConnectionPools.get(defaultPoolName);
        System.out.println("PoolFound: "+connectionPool);
      } else
      {
        // Error pool was never initialized or name could not be found
        String message = "CONNECTION POOL NOT INITIALIZED: "+defaultPoolName;
        throw new ModuleInitializationException(message,
            "fedora.server.storage.ConnectionPoolManagerImpl");
      }
    } catch (Exception e)
    {
      throw new ModuleInitializationException(
          e.getMessage(),"fedora.server.storage.ConnectionPoolManagerImpl");
    }
    return connectionPool;
  }
}