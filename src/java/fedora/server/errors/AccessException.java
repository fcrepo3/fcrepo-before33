package fedora.server.errors;

/**
 * <p>Title: AccessException.java</p>
 * <p>Description: Abstract superclass for access-related exceptions.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */

public abstract class AccessException extends Exception
{

  /**
   * Creates an AccessException
   *
   * @param message An informative message explaining what happened and
   *                (possibly) how to fix it.
   */
  public AccessException(String message)
  {
    super(message);
  }

}