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

  /** ControlGrp "Fedora Content" (internal) datastreams */
  public final static int INTERNAL = 0;

  /** ControlGrp "Fedora User Metadata" (internal/inline) datastreams */
  public final static int INTERNAL_INLINE = 1;

  /** ControlGrp for "External Referenced Content" (external) datastreams */
  public final static int EXTERNAL = 2;

  /** Future(?): ControlGrp "External Service Request" (external/request) datastreams */
  public final static int EXTERNAL_REQUEST = 3;

  public String parentPID;

  public String datastreamID;

  /** Control Group: INTERNAL/INLINEMETA/EXTERNAL/EXREQUEST  */
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

  public abstract InputStream getContentStream();

  public abstract byte[] getContentBytes(int length, int offset);


}