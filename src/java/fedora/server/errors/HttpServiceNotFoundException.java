package fedora.server.errors;

/**
 * <p><b>Title: </b>HttpServiceNotFoundException.java</p>
 * <p><b>Description: </b>Signals that a successful HTTP connection could NOT</p>
 * <p>be made to the designated URL.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
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