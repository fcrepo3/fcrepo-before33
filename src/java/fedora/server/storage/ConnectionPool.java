package fedora.server.storage;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

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

  private String driver;
  private String url;
  private String username;
  private String password;
  private int maxActive = 0;
  private int maxIdle = 0;
  private long maxWait = 0;
  private long minEvictableIdleTimeMillis =0;
  private int minIdle = 0;
  private int numTestsPerEvictionRun = 0;
  private boolean testOnBorrow = false;
  private boolean testOnReturn = false;
  private boolean testWhileIdle = false;
  private long timeBetweenEvictionRunsMillis = 0;
  private byte whenExhaustedAction = 0;
  private DDLConverter ddlConverter;
  
  private PoolingDataSource dataSource;
  private GenericObjectPool connectionPool;


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
    this.driver = driver;
    this.url = url;
    this.username = username;
    this.password = password;
    this.maxActive = maxActive;
    this.maxIdle = maxIdle;
    this.maxWait = maxWait;
    this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    this.minIdle = minIdle;
    this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    this.testOnBorrow = testOnBorrow;
    this.testOnReturn = testOnReturn;
    this.testWhileIdle = testWhileIdle;
    this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    this.whenExhaustedAction = whenExhaustedAction;
        
    connectionPool = new GenericObjectPool(null);
    if (LOG.isDebugEnabled()) {
        LOG.debug("default_max_active: "+GenericObjectPool.DEFAULT_MAX_ACTIVE);
        LOG.debug("default_max_idle: "+GenericObjectPool.DEFAULT_MAX_IDLE);
        LOG.debug("default_max_wait_time: "+GenericObjectPool.DEFAULT_MAX_WAIT);
        LOG.debug("default_min_evict_idle_time: "+GenericObjectPool.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
        LOG.debug("default_min_idle: "+GenericObjectPool.DEFAULT_MIN_IDLE);
        LOG.debug("default_num_tests_per_evict_run: "+GenericObjectPool.DEFAULT_NUM_TESTS_PER_EVICTION_RUN);
        LOG.debug("default_time_between_evict_runs: "+GenericObjectPool.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS);
        LOG.debug("default_when_exhausted_action: "+GenericObjectPool.DEFAULT_WHEN_EXHAUSTED_ACTION);
        LOG.debug("default_when_exhausted_block: "+GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
        LOG.debug("default_when_exhausted_fail: "+GenericObjectPool.WHEN_EXHAUSTED_FAIL);
        LOG.debug("default_when_exhausted_grow: "+GenericObjectPool.WHEN_EXHAUSTED_GROW);
        LOG.debug("default_test_on_borrow: "+GenericObjectPool.DEFAULT_TEST_ON_BORROW);
        LOG.debug("default_test_on_return: "+GenericObjectPool.DEFAULT_TEST_ON_RETURN);
        LOG.debug("default_test_while_idle: "+GenericObjectPool.DEFAULT_TEST_WHILE_IDLE);
    }
        
    connectionPool.setMaxActive(maxActive);
    connectionPool.setMaxIdle(maxIdle);
    connectionPool.setMaxWait(maxWait);
    connectionPool.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    connectionPool.setMinIdle(minIdle);
    connectionPool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
    connectionPool.setTestOnBorrow(testOnBorrow);
    connectionPool.setTestOnReturn(testOnReturn);
    connectionPool.setTestWhileIdle(testWhileIdle);
    connectionPool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    connectionPool.setWhenExhaustedAction(whenExhaustedAction);
    if (LOG.isDebugEnabled()) {
        LOG.debug("set_max_active: "+connectionPool.getMaxActive());
        LOG.debug("set_max_idle: "+connectionPool.getMaxIdle());
        LOG.debug("set_max_wait_time: "+connectionPool.getMaxWait());
        LOG.debug("set_min_evict_idle_time: "+connectionPool.getMinEvictableIdleTimeMillis());
        LOG.debug("set_min_idle: "+connectionPool.getMinIdle());
        LOG.debug("set_num_tests_per_evict_run: "+connectionPool.getNumTestsPerEvictionRun());
        LOG.debug("set_time_between_evict_runs: "+connectionPool.getTimeBetweenEvictionRunsMillis());
        LOG.debug("set_num_active: "+connectionPool.getNumActive());
        LOG.debug("set_num_idle: "+connectionPool.getNumIdle());
        LOG.debug("set_test_on_borrow: "+connectionPool.getTestOnBorrow());
        LOG.debug("set_test_on_return: "+connectionPool.getTestOnReturn());
        LOG.debug("set_test_while_idle: "+connectionPool.getTestWhileIdle());
        LOG.debug("set_when_exhausted_action: "+connectionPool.getWhenExhaustedAction());
    }
    
    // Load class for jdbc driver
    try {
        Class.forName(driver);
    } catch(ClassNotFoundException cnfe)
    {
        cnfe.printStackTrace();
        throw new SQLException("Can't find class for driver: " + driver);
    }                
    

    ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(url, username, password);
    PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
    dataSource = new PoolingDataSource(connectionPool);

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
      LOG.debug("connectionPool: "+this.toString());
      return dataSource.getConnection();
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
    }
  }


  /**
   * <p>Converts this class object into a meaningful string.</p>
   *
   * @return A string describing the connection pool.
   */
  public String toString()
  {
    String info =
      "ConnectionPool(" + url + "," + username + "," + password + ")" +
      ", numIdle=" + connectionPool.getNumIdle() +
      ", numActive=" + connectionPool.getNumActive() +
      ", maxIdle=" + connectionPool.getMaxIdle() +
      ", maxActive=" + connectionPool.getMaxActive();
    return(info);
  }
}
