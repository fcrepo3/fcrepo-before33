package fedora.server.storage;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.storage.types.MIMETypedStream;

/**
 * <p>Title: DatastreamResolverServlet.java</p>
 * <p>Description: This servlet acts as a proxy to resolve the physical location
 * of datastreams. It requires a single parameter named <code>id</code> that
 * denotes the temporary id of the requested datastresm. The servlet will
 * perform a database lookup on the Fedora server using the temporary id to
 * obtain the actual * physical location of the datastream and then return the
 * contents of the datastream as a MIME-typed stream.
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class DatastreamResolverServlet extends HttpServlet
{

  private static Server s_server;
  private static ConnectionPool connectionPool;

  static
  {
    try
    {
      String fedoraHome = System.getProperty("fedora.home");
      if (fedoraHome == null) {
          throw new ServerInitializationException(
              "Server failed to initialize: The 'fedora.home' "
              + "system property was not set.");
      } else {
          s_server = Server.getInstance(new File(fedoraHome));
      }
    } catch (InitializationException ie) {
        System.err.println(ie.getMessage());
    }
  }

  /**
   * <p>Initialize servlet.</p>
   *
   * @throws ServletException If the servet cannot be initialized.
   */
  public void init() throws ServletException
  {
    try
    {
      ConnectionPoolManager poolManager = (ConnectionPoolManager)
          s_server.getModule("fedora.server.storage.ConnectionPoolManager");
      connectionPool = poolManager.getPool();
    } catch (Throwable th)
    {
      System.err.println("Unable to init DatastreamREsolverServlet. The "
                                 + "underlying error was a "
                                 + th.getClass().getName() + "The message "
                                 + "was \"" + th.getMessage() + "\"");
    }
  }

  /**
   * <p>Processes the servlet request and resolves the physical location of
   * the specified datastream.</p>
   *
   * @param request  The servlet request.
   * @param response servlet The servlet response.
   * @throws ServletException If an error occurs that effects the servlet's
   *         basic operation.
   * @throws IOException If an error occurrs with an input or output operation.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    String id = request.getParameter("id");
    String dsPhysicalLocation = null;
    String dsControlGroupType = null;
    Connection connection = null;

    // check for required id parameter
    if (id == null || id.equalsIgnoreCase(""))
    {
      PrintWriter out = response.getWriter();
      response.setContentType("text/html");
      out.println("<br>DatastreamResolverServlet: No datastream id specified "
                  + "in servlet request: " + request.getRequestURI() + "</br>");
      return;
    }

    try
    {
      connection = connectionPool.getConnection();
      String query = "SELECT DS_Physical_Location, DS_Control_Group_Type "
          + "FROM DatastreamLocation WHERE DS_Temp_ID = '" + id + "';";
      System.err.println("DatastreamResolverQuery: " + query);
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(query);
      ResultSetMetaData rsMeta = rs.getMetaData();
      int cols = rsMeta.getColumnCount();

      while (rs.next())
      {
        String[] results = new String[cols];
        for (int i=1; i<=cols; i++)
        {
          results[i-1] = rs.getString(i);
        }
        dsPhysicalLocation = results[0];
        dsControlGroupType = results[1];
      }

      if (dsControlGroupType.equalsIgnoreCase("M"))
      {
        // Not yet implemented
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<br>DatastreamResolverServlet: "
                    + "Repository Managed Datastreams not yet "
                   + "supported</br>");
        s_server.logWarning("DatastreamResolverServlet: "
            + "Repository Managed Datastreams not yet "
                   + "supported");
        out.close();
      } else if (dsControlGroupType.equalsIgnoreCase("P"))
        {
          ExternalContentManager externalContentManager = (ExternalContentManager)
              s_server.getModule("fedora.server.storage.ExternalContentManager");
          System.err.println("dsLocationRealURL: "+dsPhysicalLocation);
          MIMETypedStream mimeTypedStream =
              externalContentManager.getExternalContent(dsPhysicalLocation);
          System.err.println("dsLocationMIME: "+mimeTypedStream.MIMEType);
          ServletOutputStream out = response.getOutputStream();
          response.setContentType(mimeTypedStream.MIMEType);
          s_server.logFinest("dsLocationMIME: "+mimeTypedStream.MIMEType);
          out.write(mimeTypedStream.stream);
        out.close();
      } else if (dsControlGroupType.equalsIgnoreCase("X"))
      {
        // Not yet implemented
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<br>DatastreamResolverServlet: "
                    + "Repository-Defined XML Metadata Datastreams not yet "
                    + "supported</br>");
        s_server.logWarning("DatastreamResolverServlet: "
                            + "Repository-Defined XML Metadata Datastreams "
                            + "not yet supported</br>");
        out.close();
      } else
      {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        out.println("<br>DatastreamResolverServlet: Unknown "
                    + "dsControlGroupType: " + dsControlGroupType + "</br>");
        out.close();
        s_server.logWarning("DatastreamResolverServlet: Unknown "
                            + "dsControlGroupType: " + dsControlGroupType);
      }
    } catch (Throwable th)
    {
      System.err.println("DatastreamResolverServlet returned an error. The "
                                 + "underlying error was a "
                                 + th.getClass().getName() + "The message "
                                 + "was \"" + th.getMessage() + "\"");
      s_server.logWarning("DatastreamResolverServlet returned an error. The "
                                 + "underlying error was a "
                                 + th.getClass().getName() + "The message "
                                 + "was \"" + th.getMessage() + "\"");
    } finally
    {
      if (connection != null)
      {
        connectionPool.free(connection);
      }
    }

  }

  //Clean up resources
  public void destroy()
  {}
}