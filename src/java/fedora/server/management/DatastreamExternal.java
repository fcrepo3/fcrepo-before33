//=====================================================================
//                         Mellon FEDORA
//   Flexible Extensible Digital Object Repository Architecture
//=====================================================================
package fedora.server.management;

/**
 * <p>Title: DatastreamExternal.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import java.io.InputStream;

public class DatastreamExternal extends Datastream
{

  private String dsLocationURL;


  public DatastreamExternal()
  {
  }


  public String getLocationURL()
  {
    return(dsLocationURL);
  }

  public byte[] getContentBytes(int length, int offset)
  {
    // run the external content retriever
    return(null);
  }

  public InputStream getContentStream()
  {
    // run the external content retriever
    return(null);
  }
}