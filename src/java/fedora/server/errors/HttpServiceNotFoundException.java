package fedora.server.errors;

/**
 * <p>Title: HttpServiceNotFoundException.java</p>
 * <p>Description: Signals that a successful HTTP connection could NOT</p>
 * <p>be made to the designated URL.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class HttpServiceNotFoundException extends StorageException
{

  /**
   * Creates a HttpServiceNotFoundException.
   * @param message An informative message explaining what happened and
   *                (possibly) how to fix it.
   */
  public HttpServiceNotFoundException(String message)
  {
    super(message);
  }
}