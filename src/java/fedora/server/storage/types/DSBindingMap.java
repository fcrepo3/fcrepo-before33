package fedora.server.storage.types;

/**
 * <p>Title: DSBindingMap.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
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
