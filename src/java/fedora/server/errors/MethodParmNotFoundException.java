package fedora.server.errors;

/**
 * <p><b>Title: </b>MethodParmNotFoundException.java</p>
 * <p><b>Description: </b>Signals that a method parameter associated with a Behavior</p>
 * <p>Mechanism could not be found.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
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
