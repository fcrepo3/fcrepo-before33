package fedora.server.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.errors.HttpServiceNotFoundException;
import fedora.server.errors.StreamIOException;

/**
 *
 * <p><b>Title:</b> DefaultExternalContentManager.java</p>
 * <p><b>Description:</b> Provides a mechanism to obtain external HTTP-accessible
 * content.</p>
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
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class DefaultExternalContentManager extends Module
    implements ExternalContentManager
{

  private String m_userAgent;
  private String fedoraServerHost;
  private String fedoraServerPort;

  /**
   * <p> Creates a new DefaultExternalContentManager.</p>
   *
   * @param moduleParameters The name/value pair map of module parameters.
   * @param server The server instance.
   * @param role The module role name.
   * @throws ModuleInitializationException If initialization values are
   *         invalid or initialization fails for some other reason.
   */
  public DefaultExternalContentManager(Map moduleParameters,
                                       Server server, String role)
      throws ModuleInitializationException
  {
    super(moduleParameters, server, role);
  }

  /**
   * Initializes the Module based on configuration parameters. The
   * implementation of this method is dependent on the schema used to define
   * the parameter names for the role of
   * <code>fedora.server.storage.DefaultExternalContentManager</code>.
   *
   * @throws ModuleInitializationException If initialization values are
   *         invalid or initialization fails for some other reason.
   */
  public void initModule() throws ModuleInitializationException
  {
    try
    {
      Server s_server = this.getServer();
      s_server.logInfo("DefaultExternalContentManager initialized");
      m_userAgent=getParameter("userAgent");
      if (m_userAgent==null) {
        m_userAgent="Fedora";
      }

      fedoraServerPort = s_server.getParameter("fedoraServerPort");
      fedoraServerHost = s_server.getParameter("fedoraServerHost");


    } catch (Throwable th)
    {
      throw new ModuleInitializationException("[DefaultExternalContentManager] "
          + "An external content manager "
          + "could not be instantiated. The underlying error was a "
          + th.getClass().getName() + "The message was \""
          + th.getMessage() + "\".", getRole());
    }
  }

  /**
   * A method that reads the contents of the specified URL and returns the
   * result as a MIMETypedStream
   *
   * @param URL The URL of the external content.
   * @return A MIME-typed stream.
   * @throws HttpServiceNotFoundException If the URL connection could not
   *         be established.
   */
  public MIMETypedStream getExternalContent(String URL)
      throws GeneralException, HttpServiceNotFoundException
  {
    InputStream inStream = null;
    MIMETypedStream httpContent = null;
    try
    {
      //URL url = new URL(java.net.URLDecoder.decode(URL, "utf-8"));
      URL url = new URL(URL);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setRequestProperty("User-Agent", m_userAgent);
      if (connection.getResponseCode()!=HttpURLConnection.HTTP_OK)
      {
          throw new StreamIOException(
                  "Server returned a non-200 response code ("
                  + connection.getResponseCode() + ") from GET request of URL: "
                  + URL);
      }
      connection.setInstanceFollowRedirects(true);
      String contentType = connection.getContentType();
      inStream = connection.getInputStream();
      if(contentType == null)
      {
        contentType =
          connection.guessContentTypeFromStream(connection.getInputStream());
        if (contentType == null) contentType = "text/plain";
      }
      httpContent = new MIMETypedStream(contentType, inStream);
      return(httpContent);

    } catch (Throwable th)
    {
      throw new HttpServiceNotFoundException("[DefaultExternalContentManager] "
          + "returned an error.  The underlying error was a "
          + th.getClass().getName() + "  The message "
          + "was  \"" + th.getMessage() + "\"  .  ");
    }
  }
}
