/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

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