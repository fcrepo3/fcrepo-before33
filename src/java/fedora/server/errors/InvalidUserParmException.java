package fedora.server.errors;

/**
 * <p><b>Title: </b>InvalidUserParmException.java</p>
 * <p><b>Description: </b>Signals that one or more user-supplied method paramters
 * do not validate against the method paramter definitions in the associated
 * Behavior Mechanism object.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
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