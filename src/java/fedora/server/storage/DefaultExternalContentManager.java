package fedora.server.storage;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;
import java.util.Map;

import fedora.server.Context;
import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.errors.HttpServiceNotFoundException;

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
      throw new ModuleInitializationException("An external content manager "
          + "could not be instantiated. The underlying error was a "
          + th.getClass().getName() + "The message was \""
          + th.getMessage() + "\".", getRole());
    }
  }

  /**
   * A method that reads the contents of the specified URL and returns the
   * result as a MIMETypedStream
   *
   * @param urlString The URL of the content.
   * @return A MIME-typed stream.
   * @throws HttpServiceNotFoundException If the URL connection could not
   *         be established.
   */
  public MIMETypedStream getExternalContent(String URL)
      throws HttpServiceNotFoundException
  {
    try
    {
      MIMETypedStream httpContent = null;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      URL url = new URL(URL);
      URLConnection connection = (URLConnection)url.openConnection();
      String contentType = connection.getContentType();
      InputStream is = connection.getInputStream();
      int byteStream = 0;
      while((byteStream = is.read()) >=0 )
      {
        baos.write(byteStream);
      }
      System.err.println("beforeextContentManagerURL: "+URL);
      System.err.println("beforeextContentManagerMIME: "+contentType);
      if(contentType == null) contentType=connection.guessContentTypeFromStream(connection.getInputStream());
      System.err.println("afterextContentManagerURL: "+URL);
      System.err.println("afterextContentManagerMIME: "+contentType);
      httpContent = new MIMETypedStream(contentType, baos.toByteArray());
      return(httpContent);

    } catch (Throwable th)
    {
      throw new HttpServiceNotFoundException("ExternalContentManager "
          + "returned an error.\nThe underlying error was a "
          + th.getClass().getName() + "\nThe message "
          + "was \"" + th.getMessage() + "\" .");
    }
  }

}
