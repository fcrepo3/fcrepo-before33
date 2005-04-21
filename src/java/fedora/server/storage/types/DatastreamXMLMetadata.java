package fedora.server.storage.types;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

/**
 *
 * <p><b>Title:</b> DatastreamXMLMetadata.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class DatastreamXMLMetadata extends Datastream
{

  // techMD (technical metadata),
  // sourceMD (analog/digital source metadata),
  // rightsMD (intellectual property rights metadata),
  // digiprovMD (digital provenance metadata).
  // dmdSec (descriptive metadata).

  /** Technical XML metadata */
  public final static int TECHNICAL=1;

  /** Source XML metatdata */
  public final static int SOURCE=2;

  /** Rights XML metatdata */
  public final static int RIGHTS=3;

  /** Digital provenance XML metadata */
  public final static int DIGIPROV=4;

  /** Descriptive XML metadata */
  public final static int DESCRIPTIVE=5;

  // FIXME:xml datastream contents are held in memory...this could be expensive.
  public byte[] xmlContent;

  /**
   * The class of XML metadata (TECHNICAL, SOURCE, RIGHTS,
   * DIGIPROV, or DESCRIPTIVE)
   */
  public int DSMDClass = 0;

  private String m_encoding;

  public DatastreamXMLMetadata()
  {
      m_encoding="UTF-8";
  }

  public DatastreamXMLMetadata(String encoding)
  {
      m_encoding=encoding;
  }

  public InputStream getContentStream()
  {
    return(new ByteArrayInputStream(xmlContent));
  }

  public InputStream getContentStreamAsDocument() throws UnsupportedEncodingException {
      // *with* the <?xml version="1.0" encoding="m_encoding" ?> line
      String firstLine="<?xml version=\"1.0\" encoding=\"" + m_encoding + "\" ?>\n";
      byte[] firstLineBytes=firstLine.getBytes(m_encoding);
      byte[] out=new byte[xmlContent.length+firstLineBytes.length];
      for (int i=0; i<firstLineBytes.length; i++) {
          out[i]=firstLineBytes[i];
      }
      for (int i=firstLineBytes.length; i<firstLineBytes.length+xmlContent.length; i++) {
          out[i]=xmlContent[i-firstLineBytes.length];
      }
      return new ByteArrayInputStream(out);
  }
}
