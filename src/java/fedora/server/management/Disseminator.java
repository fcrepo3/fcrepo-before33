//=====================================================================
//                         Mellon FEDORA
//   Flexible Extensible Digital Object Repository Architecture
//=====================================================================
package fedora.server.management;

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

  public String dissVersionGrp;

  public String bDefID;

  public String bMechID;

  public DSBindingMap dsBindMap;

  public Date dissCreateDT;


  public Disseminator()
  {
  }
}