package fedora.server.storage;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import fedora.server.errors.ConnectionPoolNotFoundException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.InitializationException;
import fedora.server.Module;
import fedora.server.Server;

/**
 * <p>Title: ConnectionPoolManagerImpl.java</p>
 * <p>Description: Implements ConnectionPoolManager to facilitate obtaining
 * database connection pools. This class initializes the connection pools
 * specified by parameters in the Fedora <code>fedora.fcfg</code> configuration
 * file. The Fedora server must be instantiated in order for this class to
 * function properly.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class ConnectionPoolManagerImpl extends Module
    implements ConnectionPoolManager
{

  private static Hashtable h_ConnectionPools = new Hashtable();
  private static String defaultPoolName = null;
  private int minConnections = 0;
  private int maxConnections = 0;
  private String jdbcDriverClass = null;
  private String dbUsername = null;
  private String dbPassword = null;
  private String jdbcURL = null;

  /**
   * <p>Constructs a new ConnectionPoolManagerImpl</p>
   *
   * @param moduleParameters The name/value pair map of module parameters.
   * @param server The server instance.
   * @param role The module role name.
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
      Server s_server = this.getServer();
      defaultPoolName = this.getParameter("defaultPoolName");
      if (defaultPoolName == null || defaultPoolName.equalsIgnoreCase(""))
      {
        throw new ModuleInitializationException("Default Connection Pool " +
            "Name Not Specified",
            "fedora.server.storage.ConnectionPoolManagerImpl");
      }
      System.out.println("DefaultPoolName: "+defaultPoolName);
      String poolList = this.getParameter("poolNames");

      // Pool names should be comma delimited
      String[] poolNames = poolList.split(",");

      // Initialize each connection pool
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
        int minConnections = i1.intValue();
        System.out.println("min: "+minConnections);
        Integer i2 = new Integer(s_server.getDatastoreConfig(poolNames[i]).
                  getParameter("maxPoolSize"));
        int maxConnections = i2.intValue();
        System.out.println("max: "+maxConnections);

        // Create connection pool
        try
        {
          ConnectionPool connectionPool = new ConnectionPool(jdbcDriverClass,
              jdbcURL, dbUsername, dbPassword, minConnections,
              maxConnections, true);
          System.out.println("Initialized Pool: "+connectionPool);
          h_ConnectionPools.put(poolNames[i],connectionPool);
          System.out.println("putPoolInHash: "+h_ConnectionPools.size());
        } catch (SQLException sqle)
        {
          System.out.println("Unable to initialize connection pool: " +
                             poolNames[i]);
          s_server.logWarning("Unable to initialize connection pool: " +
                              poolNames[i]);
        }
      }

    } catch (PatternSyntaxException pse)
    {
      throw new ModuleInitializationException(
          pse.getMessage(),"fedora.server.storage.ConnectionPoolManager");
    } catch (NullPointerException npe)
    {
      throw new ModuleInitializationException(
          npe.getMessage(),"fedora.server.storage.ConnectionPoolManager");
    }
  }

  /**
   * <p>Gets a named connection pool.</p>
   *
   * @param poolName The name of the connection pool.
   * @return The named connection pool.
   * @throws ConnectionPoolNotFoundException If the specified connection pool
   * cannot be found.
   */
  public ConnectionPool getPool(String poolName)
      throws ConnectionPoolNotFoundException
  {
    ConnectionPool connectionPool = null;

    try
    {
      if (h_ConnectionPools.containsKey(poolName))
      {
        connectionPool = (ConnectionPool)h_ConnectionPools.get(poolName);
        System.out.println("PoolFound: "+connectionPool);
      } else
      {
        // Error: pool was never initialized or name could not be found
        throw new ConnectionPoolNotFoundException("Connection pool " +
            "not found: " + poolName);
      }
    } catch (Exception e)
    {
      throw new ConnectionPoolNotFoundException("Connection pool " +
          "not found: " + poolName + "\n" + e.getMessage());
    }

    return connectionPool;
  }

  /**
   * <p>Gets the default Connection Pool. This method overrides <code>
   * getPool(String poolName)</code>.</p>
   *
   * @return The default connection pool.
   * @throws ConnectionPoolNotfoundException If the default connection pool
   * cannot be found.
   */
  public ConnectionPool getPool()
      throws ConnectionPoolNotFoundException
  {
    ConnectionPool connectionPool = null;

    try
    {
      if (h_ConnectionPools.containsKey(defaultPoolName))
      {
        connectionPool = (ConnectionPool)h_ConnectionPools.get(defaultPoolName);
        System.out.println("PoolFound: "+connectionPool);
      } else
      {
        // Error: default pool was never initialized or could not be found
        throw new ConnectionPoolNotFoundException("Default connection pool " +
            "not found: " + defaultPoolName);
      }

    } catch (Exception e)
    {
      throw new ConnectionPoolNotFoundException("Default connection pool " +
          "not found: "+defaultPoolName + "\n" + e.getMessage());
    }

    return connectionPool;
  }
}