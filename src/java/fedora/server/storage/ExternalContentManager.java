/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.storage.types.MIMETypedStream;

/**
 *
 * <p><b>Title:</b> ExternalContentManager.java</p>
 * <p><b>Description:</b> Interface that provides a mechanism for retrieving
 * external content via HTTP.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public interface ExternalContentManager
{
  public MIMETypedStream getExternalContent(String URL, Context context)
      throws ServerException;

}