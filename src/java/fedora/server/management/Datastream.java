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
import java.io.InputStream;

public abstract class Datastream
{

  public String parentPID;

  public String datastreamID;

  /** Control Group: INTERNAL=0/INLINEMETA=1/EXTERNAL=2/EXREQUEST=3  */
  public int dsControlGrp;

  public String dsLabel;

  public String dsVersionID;

  public String dsMIME;

  public String dsInfoType;

  public Date dsCreateDT;

  public int dsSize;


  public Datastream()
  {
  }

}