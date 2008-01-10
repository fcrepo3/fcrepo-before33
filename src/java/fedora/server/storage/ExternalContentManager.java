/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage;

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.storage.types.MIMETypedStream;

/**
 * Interface that provides a mechanism for retrieving external content via HTTP.
 * 
 * @author Ross Wayland
 */
public interface ExternalContentManager {

    public MIMETypedStream getExternalContent(String URL, Context context)
            throws ServerException;

}
