package fedora.server.storage;

import fedora.server.errors.ConnectionPoolNotFoundException;
import fedora.server.storage.ConnectionPool;

/**
 *
 * <p><b>Title:</b> ConnectionPoolManager.java</p>
 * <p><b>Description:</b> Interface that defines a <code>Module</code> to
 * facilitate the acquisition of JDBC connection pools for database access.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
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