package fedora.server.storage;

import java.io.InputStream;

import fedora.server.errors.ServerException;
import fedora.server.storage.types.MIMETypedStream;

/**
 * <p>Title: ExternalContentManager.java</p>
 * <p>Description: Interface that provides a mechanism for retrieving external
 * content via HTTP.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public interface ExternalContentManager
{
  public MIMETypedStream getExternalContent(String URL)
      throws ServerException;

}