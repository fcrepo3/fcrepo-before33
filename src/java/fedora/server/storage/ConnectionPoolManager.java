package fedora.server.storage;

import fedora.server.errors.ConnectionPoolNotFoundException;
import fedora.server.storage.ConnectionPool;

/**
 *
 * <p><b>Title:</b> ConnectionPoolManager.java</p>
 * <p><b>Description:</b> Interface that defines a <code>Module</code> to
 * facilitate the acquisition of JDBC connection pools for database access.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public interface ConnectionPoolManager
{
  /**
   *<p>Gets the specified connection pool.</p>
   *
   * @param poolName The name of the specified connection pool.
   * @return The named connection pool.
   * @throws ConnectionPoolNotFoundException If the specified connection pool
   *         cannot be found.
   */
  public ConnectionPool getPool(String poolName)
      throws ConnectionPoolNotFoundException;

  /**
   * <p>Gets the default Connection Pool. Overrides
   * <code>getPool(String poolName)</code> to return the default connection
   * pool when no specific pool name is provided as an argument.</p>
   *
   * @return The default connection pool.
   * @throws ConnectionPoolNotFoundException If the default connection pool
   *         cannot be found.
   */
  public ConnectionPool getPool()
      throws ConnectionPoolNotFoundException;
}