package fedora.server.storage;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import fedora.server.Debug;
import fedora.server.utilities.DDLConverter;
import fedora.server.utilities.TableCreatingConnection;

/**
 * <p>Title: ConnectionPool.java</p>
 * <p>Description: Provides a dispenser for database Connection Pools.</p>
 *
 *
 * @author rlw@virginia.edu, cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ConnectionPool
{
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
    if (Debug.DEBUG) {
        System.out.println("default_max_active: "+GenericObjectPool.DEFAULT_MAX_ACTIVE);
        System.out.println("default_max_idle: "+GenericObjectPool.DEFAULT_MAX_IDLE);
        System.out.println("default_max_wait_time: "+GenericObjectPool.DEFAULT_MAX_WAIT);
        System.out.println("default_min_evict_idle_time: "+GenericObjectPool.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS);
        System.out.println("default_min_idle: "+GenericObjectPool.DEFAULT_MIN_IDLE);
        System.out.println("default_num_tests_per_evict_run: "+GenericObjectPool.DEFAULT_NUM_TESTS_PER_EVICTION_RUN);
        System.out.println("default_time_between_evict_runs: "+GenericObjectPool.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS);
        System.out.println("default_when_exhausted_action: "+GenericObjectPool.DEFAULT_WHEN_EXHAUSTED_ACTION);
        System.out.println("default_when_exhausted_block: "+GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
        System.out.println("default_when_exhausted_fail: "+GenericObjectPool.WHEN_EXHAUSTED_FAIL);
        System.out.println("default_when_exhausted_grow: "+GenericObjectPool.WHEN_EXHAUSTED_GROW);
        System.out.println("default_test_on_borrow: "+GenericObjectPool.DEFAULT_TEST_ON_BORROW);
        System.out.println("default_test_on_return: "+GenericObjectPool.DEFAULT_TEST_ON_RETURN);
        System.out.println("default_test_while_idle: "+GenericObjectPool.DEFAULT_TEST_WHILE_IDLE);
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
    if (Debug.DEBUG) {
        System.out.println("set_max_active: "+connectionPool.getMaxActive());
        System.out.println("set_max_idle: "+connectionPool.getMaxIdle());
        System.out.println("set_max_wait_time: "+connectionPool.getMaxWait());
        System.out.println("set_min_evict_idle_time: "+connectionPool.getMinEvictableIdleTimeMillis());
        System.out.println("set_min_idle: "+connectionPool.getMinIdle());
        System.out.println("set_num_tests_per_evict_run: "+connectionPool.getNumTestsPerEvictionRun());
        System.out.println("set_time_between_evict_runs: "+connectionPool.getTimeBetweenEvictionRunsMillis());
        System.out.println("set_num_active: "+connectionPool.getNumActive());
        System.out.println("set_num_idle: "+connectionPool.getNumIdle());
        System.out.println("set_test_on_borrow: "+connectionPool.getTestOnBorrow());
        System.out.println("set_test_on_return: "+connectionPool.getTestOnReturn());
        System.out.println("set_test_while_idle: "+connectionPool.getTestWhileIdle());
        System.out.println("set_when_exhausted_action: "+connectionPool.getWhenExhaustedAction());
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
      if (Debug.DEBUG) 
          System.out.println("connectionPool: "+this.toString());
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
        System.out.println("Unable to close connection");
        sqle.printStackTrace();
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
