package fedora.server.errors;

/**
 * <p><b>Title: </b>ConnectionPoolNotFoundException.java</p>
 * <p><b>Description: </b>Signals a database ConnectionPool could not be found.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class ConnectionPoolNotFoundException extends StorageException
{

  public ConnectionPoolNotFoundException(String message)
  {
    super(message);
  }

}
