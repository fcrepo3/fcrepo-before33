package fedora.server.storage.types;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 *
 * <p><b>Title:</b> DatastreamXMLMetadata.java</p>
 * <p><b>Description:</b> </p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version 1.0
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
  public final static int DIGIPROV=3;

  /** Digital provenance XML metadata */
  public final static int DESCRIPTIVE=4;

  // FIXME:xml datastream contents are held in memory...this could be expensive.
  public byte[] xmlContent;

  /**
   * The class of XML metadata (TECHNICAL, SOURCE, RIGHTS,
   * DIGIPROV, or DESCRIPTIVE)
   */
  public int DSMDClass;

  /**
   * The namespace prefixes used in the XML encoding of the object.
   * This can be used to fully qualify the elements in an XML datastream
   * in conjunction with DigitalObject.getNamespaceMapping, without
   * requiring that the XML datastream is re-parsed to find these names.
   */
  public String[] namespacePrefixes;

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
