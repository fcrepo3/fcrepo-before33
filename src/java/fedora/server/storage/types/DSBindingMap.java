package fedora.server.storage.types;

/**
 *
 * <p><b>Title:</b> DSBindingMap.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class DSBindingMap
{
  public String dsBindMapID = null;

  public String dsBindMechanismPID = null;

  public String dsBindMapLabel = null;

  public String state = null;

  public DSBinding[] dsBindings = new DSBinding[0];

  public DSBindingMap()
  {
  }
}
