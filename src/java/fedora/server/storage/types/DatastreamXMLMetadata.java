package fedora.server.storage.types;

/**
 * <p>Title: DatastreamXMLMetadata.java </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */

import java.io.InputStream;
import java.util.Date;

public class DatastreamXMLMetadata extends Datastream
{

  public byte[] xmlContent;


  public DatastreamXMLMetadata()
  {
  }

  public InputStream getContentStream()
  {
    // run the external content retriever
    return(null);
  }
}