package fedora.server.errors;

/**
 * <p>Title: MethodParmNotFoundException.java</p>
 * <p>Description: Signals that a method parameter associated with a Behavior</p>
 * <p>Mechanism could not be found.
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */

public class MethodParmNotFoundException extends StorageException
{

  /**
   * Creates a MethodParmNotFoundException.
   * @param message An informative message explaining what happened and
   *                (possibly) how to fix it.
   */
  public MethodParmNotFoundException(String message)
  {
    super(message);
  }
}
