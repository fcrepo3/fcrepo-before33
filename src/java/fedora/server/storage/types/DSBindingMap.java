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
  public String dsBindMapID;

  public String dsBindMechanismPID;

  public String dsBindMapLabel;

  // TODO: Does binding map really have state, by design?
  public String state;

  public DSBinding[] dsBindings;

  public DSBindingMap()
  {
  }
}
