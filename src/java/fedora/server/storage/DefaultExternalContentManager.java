package fedora.server.storage;

import java.io.ByteArrayOutputStream;
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
 * <p>Title: DefaultExternalContentManager.java</p>
 * <p>Description: Provides a mechanism to obtain external HTTP-accessible
 * content.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class DefaultExternalContentManager extends Module
    implements ExternalContentManager
{

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

      // Currently there are no parameters for this module.
      // This is where any future parameters in the config file would be read.

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
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    MIMETypedStream httpContent = null;
    try
    {
      URL url = new URL(URL);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
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
      // RLW: change required by conversion fom byte[] to InputStream
      //int byteStream = 0;
      //while((byteStream = inStream.read()) >=0 )
      //{
      //  baos.write(byteStream);
      //}
      // RLW: change required by conversion fom byte[] to InputStream
      if(contentType == null) contentType =
          connection.guessContentTypeFromStream(connection.getInputStream());
      // RLW: change required by conversion fom byte[] to InputStream
      httpContent = new MIMETypedStream(contentType, inStream);
      //httpContent = new MIMETypedStream(contentType, baos.toByteArray());
      //baos = null;
      // RLW: change required by conversion fom byte[] to InputStream
      return(httpContent);

    } catch (Throwable th)
    {
      throw new HttpServiceNotFoundException("[DefaultExternalContentManager] "
          + "returned an error.  The underlying error was a "
          + th.getClass().getName() + "  The message "
          + "was  \"" + th.getMessage() + "\"  .  ");
    } /*finally
    {
      try
      {
        if (inStream != null) inStream.close();
      } catch (IOException ioe)
      {
        throw new GeneralException("[DefaultExternalContentManager]"
            + " unable to close IO stream.  The underlying error was a "
            + ioe.getClass().getName() + "  The message "
          + "was  \"" + ioe.getMessage() + "\"  .  ");
      }
    }*/
  }
}