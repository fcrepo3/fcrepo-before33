//=====================================================================
//                         Mellon FEDORA
//   Flexible Extensible Digital Object Repository Architecture
//=====================================================================
package fedora.server.management;

/**
 * <p>Title: Datastream.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import java.util.Date;

public class Datastream
{

  public String parentPID;

  public String datastreamID;

  public String dsLabel;

  public String dsVersionID;

  public String dsControlGrp;

  public String dsMIME;

  public String dsInfoType;

  public Date dsCreateDT;


  public Datastream()
  {
  }
}