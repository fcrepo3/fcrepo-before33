package fedora.server.errors;

/**
 * <p>Title: DisseminationBindingInfoNotFoundException.java</p>
 * <p>Description: Signals that an instance of DisseminationBindingInfo
 * could not be found or was null.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class DisseminationBindingInfoNotFoundException extends StorageException
{

  /**
   * <p>Creates a DisseminationBindingInfoNotFoundException.</p>
   *
   * @param message An informative message explaining what happened and
   *                (possibly) how to fix it.
   */
  public DisseminationBindingInfoNotFoundException(String message)
  {
    super(message);
  }
}