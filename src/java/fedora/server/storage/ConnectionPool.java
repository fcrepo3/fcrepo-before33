/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import org.apache.log4j.Logger;

import fedora.server.utilities.DDLConverter;
import fedora.server.utilities.TableCreatingConnection;

/**
 * Provides a dispenser for database Connection Pools.
 *
 * @author rlw@virginia.edu, cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ConnectionPool
{

  /** Logger for this class. */
  private static final Logger LOG = Logger.getLogger(
        ConnectionPool.class.getName());

  private DDLConverter ddlConverter;

  private BasicDataSource dataSource;

  /**
   * <p>Constructs a ConnectionPool based on the calling arguments.</p>
   *
   * @param driver The JDBC driver class name.
   * @param url The JDBC connection URL.
   * @param username The database user name.
   * @param password The database password.
   * @param maxActive Maximum number of active instances in pool.
   * @param maxIdle Maximum number of idle instances in pool.
   * @param maxWait Maximum amount of time in milliseconds the borrowObject()
   *                method should wait when whenExhaustedAction is set to
   *                WHEN_EXHAUSTED_BLOCK.
   * @param minIdle Minimum of idle instances in pool.
   * @param minEvictableIdleTimeMillis Minimum amount of time in milliseconds
   *                                   an object can be idle in pool before
   *                                   eligible for eviction (if applicable).
   * @param numTestsPerEvictionRun Number of objects to be examined on each run of
   *                               idle evictor thread (if applicable).
   * @param timeBetweenEvictionRunsMillis Time in milliseconds to sleep between runs
   *                                      of the idle object evictor thread.
   * @param testOnBorrow When true objects are validated before borrowed from the pool.
   * @param testOnReturn When true, objects are validated before returned to hte pool.
   * @param testWhileIdle When true, objects are validated by the idle object evictor thread.
   * @param whenExhaustedAction Action to take when a new object is requested and the
   *                            the pool has reached maximum number of active objects.
   * @throws SQLException If the connection pool cannot be established for
   * any reason.
   */
  public ConnectionPool(String driver, 
						String url,
                        String username, 
                        String password,
                        int maxActive, 
                        int maxIdle,
                        long maxWait, 
                        int minIdle,
                        long minEvictableIdleTimeMillis,
                        int numTestsPerEvictionRun,
                        long timeBetweenEvictionRunsMillis,
                        boolean testOnBorrow,
                        boolean testOnReturn,
                        boolean testWhileIdle,
                        byte whenExhaustedAction)
      throws SQLException
  {

    try {
        Class.forName(driver);
    } catch (ClassNotFoundException e) {
        throw new SQLException("JDBC class not found: " + driver + "; make sure "
              + "the JDBC driver is in the classpath");
    }

    // // http://jakarta.apache.org/commons/dbcp/configuration.html
    Properties props = new Properties();
    props.setProperty("url", url);
    props.setProperty("username", username);
    props.setProperty("password", password);
    props.setProperty("maxActive", "" + maxActive);
    props.setProperty("maxIdle", "" + maxIdle);
    props.setProperty("maxWait", "" + maxWait);
    props.setProperty("minIdle", "" + minIdle);
    props.setProperty("minEvictableIdleTimeMillis", "" + minEvictableIdleTimeMillis);
    props.setProperty("numTestsPerEvictionRun", "" + numTestsPerEvictionRun);
    props.setProperty("timeBetweenEvictionRunsMillis", "" + timeBetweenEvictionRunsMillis);
    props.setProperty("testOnBorrow", "" + testOnBorrow);
    props.setProperty("testOnReturn", "" + testOnReturn);
    props.setProperty("testWhileIdle", "" + testWhileIdle);

    if (whenExhaustedAction == 0) {
        // fail (don't wait, just fail)
        props.setProperty("maxWait", "0");
    } else if (whenExhaustedAction == 1) {
        // block (wait indefinitely)
        props.setProperty("maxWait", "-1");
    } else if (whenExhaustedAction == 2) {
        // grow (override the maxActive value with -1, unlimited)
        props.setProperty("maxActive", "-1");
    }

    try {
        dataSource = (BasicDataSource) 
            BasicDataSourceFactory.createDataSource(props);
        dataSource.setDriverClassName(driver);
    } catch (Exception e) {
        SQLException se = new SQLException("Error initializing connection pool");
        se.initCause(se);
        throw se;
    }
  }

  protected void setConnectionProperties(Map props) {
    for (String name : (Set<String>) props.keySet()) {
      dataSource.addConnectionProperty(name, (String) props.get(name));
    }
  }

  /**
   * Constructs a ConnectionPool that can provide TableCreatingConnections.
   *
   * @param driver The JDBC driver class name.
   * @param url The JDBC connection URL.
   * @param username The database user name.
   * @param password The he database password.
   * @param ddlConverter The DDLConverter that the TableCreatingConnections
   *                     should use when createTable(TableSpec) is called.
   * @param maxActive Maximum number of active instances in pool.
   * @param maxIdle Maximum number of idle instances in pool.
   * @param maxWait Maximum amount of time in milliseconds the borrowObject()
   *                method should wait when whenExhaustedAction is set to
   *                WHEN_EXHAUSTED_BLOCK.
   * @param minIdle Minimum of idle instances in pool.
   * @param minEvictableIdleTimeMillis Minimum amount of time in milliseconds
   *                                   an object can be idle in pool before
   *                                   eligible for eviction (if applicable).
   * @param numTestsPerEvictionRun Number of objects to be examined on each run of
   *                               idle evictor thread (if applicable).
   * @param timeBetweenEvictionRunsMillis Time in milliseconds to sleep between runs
   *                                      of the idle object evictor thread.
   * @param testOnBorrow When true objects are validated before borrowed from the pool.
   * @param testOnReturn When true, objects are validated before returned to hte pool.
   * @param testWhileIdle When true, objects are validated by the idle object evictor thread.
   * @param whenExhaustedAction Action to take when a new object is requested and the
   *                            the pool has reached maximum number of active objects.
   * @throws SQLException If the connection pool cannot be established for
   *         any reason.
   */
  public ConnectionPool(String driver, 
						String url,
                        String username, 
                        String password,
                        DDLConverter ddlConverter,
                        int maxActive, 
                        int maxIdle,
                        long maxWait, 
                        int minIdle,
                        long minEvictableIdleTimeMillis,
                        int numTestsPerEvictionRun,
                        long timeBetweenEvictionRunsMillis,
                        boolean testOnBorrow,
                        boolean testOnReturn,
                        boolean testWhileIdle,
                        byte whenExhaustedAction)
      throws SQLException
  {
    this(driver, 
         url, 
         username, 
         password,
         maxActive, 
         maxIdle,
         maxWait, 
         minIdle,
         minEvictableIdleTimeMillis,
         numTestsPerEvictionRun,
         timeBetweenEvictionRunsMillis,
         testOnBorrow,
         testOnReturn,
         testWhileIdle,
         whenExhaustedAction);
    
    this.ddlConverter=ddlConverter;
  }

  /**
   * Gets a TableCreatingConnection.
   * <p></p>
   * This derives from the same pool, but wraps the Connection in
   * an appropriate TableCreatingConnection before returning it.
   *
   * @return The next available Connection from the pool, wrapped as
   *         a TableCreatingException, or null if this ConnectionPool
   *         hasn't been configured with a DDLConverter (see constructor).
   * @throws SQLException If there is any propblem in getting the SQL
   *         connection.
   */
  public TableCreatingConnection getTableCreatingConnection()
      throws SQLException
  {
    if (ddlConverter==null)
    {
      return null;
    } else {
      Connection c=getConnection();
      return new TableCreatingConnection(c, ddlConverter);
    }
  }

  /**
   * <p>Gets the next available connection.</p>
   *
   * @return The next available connection.
   * @throws SQLException If the maximum number of connections has been reached
   *         or there is some other problem in obtaining the connection.
   */
  public Connection getConnection()
      throws SQLException
  {
      try {
        return dataSource.getConnection();
      } finally {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Got connection from pool (" + toString() + ")");
        }
      }
  }

  /**
   * <p>Releases the specified connection and returns it to the pool.</p>
   *
   * @param connection A JDBC connection.
   */
  public void free(Connection connection)
  {
    try {
      connection.close();
    } catch (SQLException sqle) {
        LOG.warn("Unable to close connection", sqle);
      } finally {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Returned connection to pool (" + toString() + ")");
        }
    }
  }

  public String toString() {
    return dataSource.getUsername() + "@" + dataSource.getUrl()
        + ", numIdle=" + dataSource.getNumIdle()
        + ", numActive=" + dataSource.getNumActive()
        + ", maxActive=" + dataSource.getMaxActive();
  }

}
