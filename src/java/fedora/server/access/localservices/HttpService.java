package fedora.server.access.localservices;

/**
 * <p>Title: HttpService.java</p>
 * <p>Description: Provides a local Http Behavior Mechanism service that</p>
 * <p>handles mechanisms invoking services via Http ( simple HTTP GETs, </p>
 * <p>cgi-scripts, java servlets, JSPs, etc.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */

// fedora imports
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.errors.HttpServiceNotFoundException;

// java imports
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpService
{

  private String url = null;

  public HttpService()
  {
  }

  public HttpService(String url)
  {
    this.url = url;
  }

  /**
   * A method that reads the contents of the specified URL and returns the
   * result as a MIMETypedStream
   *
   * @param urlString URL of the content
   * @return MIMETypedStream
   * @throws HttpServiceNotFoundException
   */
  public MIMETypedStream getHttpContent(String urlString) throws HttpServiceNotFoundException
  {
    MIMETypedStream httpContent = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try
    {
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      String contentType = connection.getContentType();
      InputStream is = connection.getInputStream();
      int byteStream = 0;
      while((byteStream = is.read()) >=0 )
      {
        baos.write(byteStream);
      }
      httpContent = new MIMETypedStream(contentType, baos.toByteArray());
    } catch (MalformedURLException murle)
    {
      System.out.println(murle);
      throw new HttpServiceNotFoundException(murle.getMessage());
    } catch (IOException ioe)
    {
      System.out.println(ioe);
      throw new HttpServiceNotFoundException(ioe.getMessage());
    }

    return(httpContent);
  }

  public static void main(String[] args)
  {
    HttpService hs = new HttpService();
    String url = "http://icarus.lib.virginia.edu/test/dummy.html";
    try
    {
      MIMETypedStream content = hs.getHttpContent(url);
      System.out.println("MIME: "+content.MIMEType);
      System.out.write(content.stream);
    } catch (IOException ioe)
    {
      System.out.println(ioe.getMessage());
    } catch (HttpServiceNotFoundException lsnf)
    {
      System.out.println(lsnf.getMessage());
    }
  }
}