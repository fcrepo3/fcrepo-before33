package fedora.server.errors;

/**
 * <p>Title: ConnectionPoolNotFoundException.java</p>
 * <p>Description: Signals a database ConnectionPool could not be found.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class ConnectionPoolNotFoundException extends StorageException
{

  public ConnectionPoolNotFoundException(String message)
  {
    super(message);
  }

}
