package fedora.server.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

/**
 * <p>Title: ConnectionPool.java</p>
 * <p>Description: A class for preallocating, recycling, and managing</p>
 * <p>JDBC connections.</p>
 * <p>Copyright: Taken/adapted from Core Servlets and JavaServer Pages</p>
 * <p>from Prentice Hall and Sun Microsystems Press,</p>
 * <p>http://www.coreservlets.com/</p>
 * <p>&copy; 2000 Marty Hall; may be freely used or adapted</p>
 * <p>Company: </p>
 * @author Marty Hall
 * @version 1.0
 */
public class ConnectionPool implements Runnable
{
  private String driver, url, username, password;
  private int maxConnections;
  private boolean waitIfBusy;
  private Vector availableConnections, busyConnections;
  private boolean connectionPending = false;

  /**
   * <p>Constructs a ConnectionPool based on the calling arguments.</p>
   *
   * @param driver The JDBC driver class name.
   * @param url The JDBC connection URL.
   * @param username The database user name.
   * @param password The he database password.
   * @param initialConnections The minimum number of connections possible.
   * @param maxConnections The maximum number of connections possible.
   * @param waitIfBusy Boolean flag that determines whether to wait if there
   * are no more available connections. If set to true, it will wait until a
   * connection becomes available. If set to false, it will throw
   * <code>SQLException</code> when a connection is requested and there are no
   * more available connections.
   * @throws SQLException If the connection pool cannot be established for
   * any reason.
   */
  public ConnectionPool(String driver, String url,
                        String username, String password,
                        int initialConnections,
                        int maxConnections,
                        boolean waitIfBusy)
      throws SQLException
  {
    this.driver = driver;
    this.url = url;
    this.username = username;
    this.password = password;
    this.maxConnections = maxConnections;
    this.waitIfBusy = waitIfBusy;
    if (initialConnections > maxConnections)
    {
      initialConnections = maxConnections;
    }
    availableConnections = new Vector(initialConnections);
    busyConnections = new Vector();
    for(int i=0; i<initialConnections; i++)
    {
      availableConnections.addElement(makeNewConnection());
    }
  }

  /**
   * <p>Gets the next available connection.</p>
   *
   * @return The next available connection.
   * @throws SQLException If the maximum number of connections has been reached
   * or there is some other problem in obtaining the connection.
   */
  public synchronized Connection getConnection()
      throws SQLException
  {
    if (!availableConnections.isEmpty())
    {
      Connection existingConnection =
        (Connection)availableConnections.lastElement();
      int lastIndex = availableConnections.size() - 1;
      availableConnections.removeElementAt(lastIndex);
      // If connection on available list is closed (e.g.,
      // it timed out), then remove it from available list
      // and repeat the process of obtaining a connection.
      // Also wake up threads that were waiting for a
      // connection because maxConnection limit was reached.
      if (existingConnection.isClosed())
      {
        notifyAll(); // Freed up a spot for anybody waiting
        return(getConnection());
      } else {
        busyConnections.addElement(existingConnection);
        return(existingConnection);
      }
    } else {

      // Three possible cases:
      // 1) You haven't reached maxConnections limit. So
      //    establish one in the background if there isn't
      //    already one pending, then wait for
      //    the next available connection (whether or not
      //    it was the newly established one).
      // 2) You reached maxConnections limit and waitIfBusy
      //    flag is false. Throw SQLException in such a case.
      // 3) You reached maxConnections limit and waitIfBusy
      //    flag is true. Then do the same thing as in second
      //    part of step 1: wait for next available connection.

      if ((totalConnections() < maxConnections) && !connectionPending)
      {
        makeBackgroundConnection();
      } else if (!waitIfBusy)
      {
        throw new SQLException("Connection limit reached");
      }
      // Wait for either a new connection to be established
      // (if you called makeBackgroundConnection) or for
      // an existing connection to be freed up.
      try
      {
        wait();
      } catch(InterruptedException ie) {}
      // Someone freed up a connection, so try again.
      return(getConnection());
    }
  }

  /**
   * <p>Makes a background connection. You can't just make a new connection
   * in the foreground when none are available, since this can take several
   * seconds with a slow network connection. Instead, start a thread that
   * establishes a new connection, then wait. You get woken up either when
   * the new connection is established or if someone finishes with an existing
   * connection.
   */
  private void makeBackgroundConnection()
  {
    connectionPending = true;
    try
    {
      Thread connectThread = new Thread(this);
      connectThread.start();
    } catch(OutOfMemoryError oome)
    {
      // Give up on new connection
    }
  }

  public void run()
  {
    try
    {
      Connection connection = makeNewConnection();
      synchronized(this)
      {
        availableConnections.addElement(connection);
        connectionPending = false;
        notifyAll();
      }
    } catch(Exception e)
    { // SQLException or OutOfMemory
      // Give up on new connection and wait for existing one
      // to free up.
    }
  }


  /**
   * <p>Explicitly makes a new connection. Called in
   * the foreground when initializing the ConnectionPool,
   * and called in the background when running.</p>
   *
   * @return A JDBC connection.
   * @throws SQLException If the connection cannot be established.
   */
  private Connection makeNewConnection()
      throws SQLException
  {
    try
    {
      // Load database driver if not already loaded
      Class.forName(driver);
      // Establish network connection to database
      Connection connection =
        DriverManager.getConnection(url, username, password);
      return(connection);
    } catch(ClassNotFoundException cnfe)
    {
      // Simplify try/catch blocks of people using this by
      // throwing only one exception type.
      throw new SQLException("Can't find class for driver: " +
                             driver);
    }
  }

  /**
   * <p>Releases the specified connection and returns it to the active pool.</p>
   *
   * @param connection A JDBC connection.
   */
  public synchronized void free(Connection connection)
  {
    busyConnections.removeElement(connection);
    availableConnections.addElement(connection);
    // Wake up threads that are waiting for a connection
    notifyAll();
  }

  /**
   * <p>Provides the total number of connections present including those that
   * are busy and those that are available.</p>
   *
   * @return The number of total connections present.
   */
  public synchronized int totalConnections()
  {
    return(availableConnections.size() +
           busyConnections.size());
  }

  /**
   * <p>Closes all the connections. Use with caution:
   *  be sure no connections are in use before
   *  calling. Note that you are not <i>required</i> to
   *  call this when done with a ConnectionPool, since
   *  connections are guaranteed to be closed when
   *  garbage collected. But this method gives more control
   *  regarding when the connections are closed.
   */
  public synchronized void closeAllConnections()
  {
    closeConnections(availableConnections);
    availableConnections = new Vector();
    closeConnections(busyConnections);
    busyConnections = new Vector();
  }

  /**
   * <p>Closes connections in the specified list.</p>
   *
   * @param connections A list of connections to be closed.
   */
  private void closeConnections(Vector connections)
  {
    try
    {
      for(int i=0; i<connections.size(); i++)
      {
        Connection connection =
          (Connection)connections.elementAt(i);
        if (!connection.isClosed())
        {
          connection.close();
        }
      }
    } catch(SQLException sqle)
    {
      // Ignore errors; garbage collect anyhow
    }
  }

  /**
   * <p>Converts this class object into a meaningful string.</p>
   *
   * @return A string describing the connection pool.
   */
  public synchronized String toString()
  {
    String info =
      "ConnectionPool(" + url + "," + username + ")" +
      ", available=" + availableConnections.size() +
      ", busy=" + busyConnections.size() +
      ", max=" + maxConnections;
    return(info);
  }
}