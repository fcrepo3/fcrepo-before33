//=====================================================================
//                         Mellon FEDORA
//   Flexible Extensible Digital Object Repository Architecture
//=====================================================================
package fedora.server.management;

/**
 * <p>Title: DatastreamInternal.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import java.io.InputStream;

public class DatastreamInternal extends Datastream
{

  private byte[] dsContent;


  public DatastreamInternal()
  {
  }

  public byte[] getContentBytes(int length, int offset)
  {
    // get content from internal storage location
    return(null);
  }

  public InputStream getContentStream()
  {
    // get content from internal storage location
    return(null);
  }
}