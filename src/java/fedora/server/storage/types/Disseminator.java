package fedora.server.storage.types;

/**
 * <p>Title: Disseminator.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import java.util.Date;

public class Disseminator
{

  public String parentPID;

  public String dissID;

  public String dissLabel;

  public String dissVersionID;

  public String bDefID;

  public String bMechID;

  public String bDefLabel;

  public String bMechLabel;

  public String dsBindMapID;

  public DSBindingMap dsBindMap;

  public Date dissCreateDT;

  public String dissState;


  public Disseminator()
  {
  }
}