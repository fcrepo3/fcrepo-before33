package fedora.server.storage;

/**
 * <p>Title: ConnectionPoolManager.java</p>
 * <p>Description: Interface that defines a <code>Module</code> to facilitate
 * the acquisition of JDBC ConnectionPools for database access.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */

import fedora.server.storage.ConnectionPool;
import fedora.server.errors.ModuleInitializationException;

public interface ConnectionPoolManager
{
  /**
   *<p>Gets the specified connection pool.</p>
   *
   * @param poolName The name of the specified connection pool.
   * @return The named connection pool
   * @throws ModuleInitializationException If initialization values are
   *         invalid or initialization fails for some other reason.
   */
  public ConnectionPool getPool(String poolName) throws
  ModuleInitializationException;

  /**
   * <p>Gets the default Connection Pool. Overrides
   * <code>getPool(String poolName)</code> to return the default connection
   * pool when no specific pool is provided.</p>
   *
   * @return The default connection pool.
   * @throws ModuleInitializationException If initialization values are
   *         invalid or initialization fails for some other reason.
   */
  public ConnectionPool getPool() throws
  ModuleInitializationException;
}