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
   *<p> Gets the specified ConnectionPool.</p>
   *
   * @param poolName name of the specified ConnectionPool.
   */
  public ConnectionPool getPool(String poolName) throws
  ModuleInitializationException;
}