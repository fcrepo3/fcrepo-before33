package fedora.server.storage.service;

/**
 *
 * <p><b>Title:</b> MIMEContent.java</p>
 * <p><b>Description:</b> A data structure for holding input or output
 * specification for MIME content.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class MIMEContent
{
  public String elementType;  // content, mimeXml
  public String messagePartName;
  public String mimeType;
}