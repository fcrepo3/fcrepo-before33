package fedora.server.errors;

/**
 * <p>Title: InvalidUserParmException.java</p>
 * <p>Description: Signals that one or more user-supplied method paramters
 * do not validate against the method paramter definitions in the associated
 * Behavior Mechanism object.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class InvalidUserParmException extends DisseminationException
{

  /**
   * Creates an InvalidUserParmException.
   *
   * @param message An informative message explaining what happened and
   *                (possibly) how to fix it.
   */
  public InvalidUserParmException(String message)
  {
      super(message);
  }

}