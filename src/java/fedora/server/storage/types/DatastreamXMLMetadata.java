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
import java.io.ByteArrayInputStream;
import java.util.Date;

public class DatastreamXMLMetadata extends Datastream
{
 
  // metadata classes, from METS:
  //
  // techMD (technical metadata), 
  // sourceMD (analog/digital source metadata), 
  // rightsMD (intellectual property rights metadata), 
  // digiprovMD (digital provenance metadata).
  // dmdSec (descriptive metadata).
  public final static int TECHNICAL=1;
  public final static int SOURCE=2;
  public final static int RIGHTS=3;
  public final static int DIGIPROV=3;
  public final static int DESCRIPTIVE=4;
  
  // FIXME:not sure if this is used publicly, but getContentStream
  // should preclude its public use.  Also not sure how this affect
  // character encoding/decoding and what the dependencies are.
  public byte[] xmlContent;

  /**
   * The metadata class. See above.
   */
  public int DSMDClass;
  
  /** 
   * The namespace prefixes used in the XML encoding of the object.
   * This can be used to fully qualify the elements in an XML datastream
   * in conjunction with DigitalObject.getNamespaceMapping, without
   * requiring that the XML datastream is re-parsed to find these names.
   */
  public String[] namespacePrefixes;

  public DatastreamXMLMetadata()
  {
  }
  
  public InputStream getContentStream()
  {
    return(new ByteArrayInputStream(xmlContent));
  }
}