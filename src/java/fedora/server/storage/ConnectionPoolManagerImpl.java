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
import fedora.server.utilities.DDLConverter;

/**
 *
 * <p><b>Title:</b> ConnectionPoolManagerImpl.java</p>
 * <p><b>Description:</b> Implements ConnectionPoolManager to facilitate obtaining
 * database connection pools. This class initializes the connection pools
 * specified by parameters in the Fedora <code>fedora.fcfg</code> configuration
 * file. The Fedora server must be instantiated in order for this class to
 * function properly.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author rlw@virginia.edu
 * @version $Id$
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
        throw new ModuleInitializationException("Default Connection Pool "
            + "Name Not Specified", getRole());
      }
      s_server.logInfo("DefaultPoolName: "+defaultPoolName);
      String poolList = this.getParameter("poolNames");

      // Pool names should be comma delimited
      String[] poolNames = poolList.split(",");

      // Initialize each connection pool
      for (int i=0; i<poolNames.length; i++)
      {
        s_server.logInfo("poolName["+i+"] = "+poolNames[i]);
        jdbcDriverClass = s_server.getDatastoreConfig(poolNames[i]).
                 getParameter("jdbcDriverClass");
        s_server.logInfo("JDBC driver: "+jdbcDriverClass);
        dbUsername = s_server.getDatastoreConfig(poolNames[i]).
                   getParameter("dbUsername");
        s_server.logInfo("Database username: "+dbUsername);
        dbPassword = s_server.getDatastoreConfig(poolNames[i]).
                   getParameter("dbPassword");
        s_server.logInfo("Database password: "+dbPassword);
        jdbcURL = s_server.getDatastoreConfig(poolNames[i]).
                  getParameter("jdbcURL");
        s_server.logInfo("JDBC connection URL: "+jdbcURL);
        Integer i1 = new Integer(s_server.getDatastoreConfig(poolNames[i]).
                  getParameter("minPoolSize"));
        int minConnections = i1.intValue();
        s_server.logInfo("Minimum connections: "+minConnections);
        Integer i2 = new Integer(s_server.getDatastoreConfig(poolNames[i]).
                  getParameter("maxPoolSize"));
        int maxConnections = i2.intValue();
        s_server.logInfo("Maximum connections: "+maxConnections);

        // If a ddlConverter has been specified for the pool,
        // try to instantiate it so the ConnectionPool can use
        // it when it provides a TableCreatingConnection.
        // If a ddlConverter has been specified, it is assumed
        // that a failure to initialize (construct) it should
        // trigger a ModuleInitializationException (a fatal startup error).
        DDLConverter ddlConverter=null;
        String ddlConverterClassName=getServer().
                  getDatastoreConfig(poolNames[i]).
                  getParameter("ddlConverter");
        if (ddlConverterClassName!=null)
        {
          try
          {
            ddlConverter=(DDLConverter)
                    Class.forName(ddlConverterClassName).newInstance();
          } catch (Throwable th) {
            throw new ModuleInitializationException("A DDLConverter was "
                    + "specified for the pool \"" + poolNames[i]
                    + "\", but it couldn't be instantiated.  The underlying "
                    + "error was a " + th.getClass().getName()
                    + "The message was \"" + th.getMessage() + "\".",
                    getRole());
          }
        }

        // Create connection pool
        try
        {
          ConnectionPool connectionPool = new ConnectionPool(jdbcDriverClass,
              jdbcURL, dbUsername, dbPassword, minConnections,
              maxConnections, true, ddlConverter);
          s_server.logInfo("Initialized Pool: "+connectionPool);
          h_ConnectionPools.put(poolNames[i],connectionPool);
          s_server.logInfo("putPoolInHash: "+h_ConnectionPools.size());
        } catch (SQLException sqle)
        {
          s_server.logWarning("Unable to initialize connection pool: "
                              + poolNames[i] + ": " + sqle.getMessage());
        }
      }

    } catch (Throwable th)
    {
      throw new ModuleInitializationException("A connection pool could "
          + "not be instantiated. The underlying error was a "
          + th.getClass().getName() + "The message was \""
          + th.getMessage() + "\".", getRole());
    }
  }

  /**
   * <p>Gets a named connection pool.</p>
   *
   * @param poolName The name of the connection pool.
   * @return The named connection pool.
   * @throws ConnectionPoolNotFoundException If the specified connection pool
   *         cannot be found.
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
        this.getServer().logInfo("PoolFound: "+connectionPool);
      } else
      {
        // Error: pool was never initialized or name could not be found
        throw new ConnectionPoolNotFoundException("Connection pool "
            + "not found: " + poolName);
      }
    } catch (Throwable th) {
      throw new ConnectionPoolNotFoundException("The specified connection "
          + "pool \"" + poolName + "\" could not be found. The underlying "
          + "error was a " + th.getClass().getName()
          + "The message was \"" + th.getMessage() + "\".");
    }

    return connectionPool;
  }

  /**
   * <p>Gets the default Connection Pool. This method overrides <code>
   * getPool(String poolName)</code>.</p>
   *
   * @return The default connection pool.
   * @throws ConnectionPoolNotFoundException If the default connection pool
   *         cannot be found.
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
        this.getServer().logInfo("PoolFound: "+connectionPool);
      } else
      {
        // Error: default pool was never initialized or could not be found
        throw new ConnectionPoolNotFoundException("Default connection pool " +
            "not found: " + defaultPoolName);
      }

    } catch (Throwable th) {
      throw new ConnectionPoolNotFoundException("The default connection "
          + "pool \"" + defaultPoolName + "\" could not be found. The "
          + "underlying error was a " + th.getClass().getName()
          + "The message was \"" + th.getMessage() + "\".");
    }

    return connectionPool;
  }
}