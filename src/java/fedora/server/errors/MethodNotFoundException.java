package fedora.server.errors;

/**
 * <p>Title: MethodNotFoundException.java</p>
 * <p>Description: Signals that a method associated with a Behavior</p>
 * <p>Mechanism could not be found.
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class MethodNotFoundException extends StorageException
{

  /**
   * Creates a MethodNotFoundException.
   * @param message An informative message explaining what happened and
   *                (possibly) how to fix it.
   */
  public MethodNotFoundException(String message)
  {
    super(message);
  }
}
