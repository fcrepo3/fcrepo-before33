package fedora.server.access.dissemination;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

import fedora.server.Server;
import fedora.server.access.dissemination.DisseminationService;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.ConnectionPoolManager;
import fedora.server.Context;
import fedora.server.ReadOnlyContext;
import fedora.server.storage.DOManager;
import fedora.server.storage.DOReader;
import fedora.server.storage.ExternalContentManager;
import fedora.server.storage.types.DatastreamManagedContent;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamMediation;

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
  private static DOManager m_manager;
  private static Context m_context;
  private static Hashtable dsRegistry;
  private static int datastreamMediationLimit;
  private static final String HTML_CONTENT_TYPE = "text/html";

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
          m_manager = (DOManager) s_server.getModule(
              "fedora.server.storage.DOManager");
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
      String expireLimit = s_server.getParameter("datastreamMediationLimit");
      if (expireLimit == null || expireLimit.equalsIgnoreCase(""))
      {
        s_server.logWarning("DatastreamResolverServlet was unable to "
            + "resolve the datastream expiration limit from the configuration "
            + "file. The expiration limit has been set to 5000 milliseconds.");
        datastreamMediationLimit = 5000;
      } else
      {
        Integer I1 =
            new Integer(expireLimit);
        datastreamMediationLimit = I1.intValue();
        s_server.logFinest("datastreamMediationLimit: "
            + datastreamMediationLimit);
      }
    } catch (Throwable th)
    {
      String message = "Unable to init DatastreamResolverServlet. The "
          + "underlying error was a " + th.getClass().getName()
          + "The message " + "was \"" + th.getMessage() + "\"";
      th.printStackTrace();
      s_server.logWarning(message);
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
    String id = null;
    String dsPhysicalLocation = null;
    String dsControlGroupType = null;
    MIMETypedStream mimeTypedStream = null;
    DisseminationService ds = null;
    Timestamp keyTimestamp = null;
    Timestamp currentTimestamp = null;
    PrintWriter out = null;
    ServletOutputStream outStream = null;

    HashMap h = new HashMap();
    h.put("application", "apia");
    h.put("useCachedObject", "false");
    h.put("userId", "fedoraAdmin");
    m_context = new ReadOnlyContext(h);

    id = request.getParameter("id").replaceAll("T"," ");
    s_server.logFinest("[DatastreamResolverServlet] datastream tempID: " + id);

    try
    {
      // Check for required id parameter.
      if (id == null || id.equalsIgnoreCase(""))
      {
        out = response.getWriter();
        response.setContentType(HTML_CONTENT_TYPE);
        out.println("<br>[DatastreamResolverServlet] No datastream ID specified"
            + " in servlet request: " + request.getRequestURI() + "</br>");
        out.close();
        return;
      }

      // Get in-memory hashtable of mappings from Fedora server.
      ds = new DisseminationService();
      dsRegistry = ds.dsRegistry;
      DatastreamMediation dm = (DatastreamMediation)dsRegistry.get(id);
      dsPhysicalLocation = dm.dsLocation;
      dsControlGroupType = dm.dsControlGroupType;
      keyTimestamp = keyTimestamp.valueOf(ds.extractTimestamp(id));
      currentTimestamp = new Timestamp(new Date().getTime());
      s_server.logFinest("[DatastreamResolverServlet] dsPhysicalLocation: "
          + dsPhysicalLocation);
      s_server.logFinest("[DatastreamResolverServlet] dsControlGroupType: "
          + dsControlGroupType);

      // Deny mechanism requests that fall outside the specified time interval.
      // The expiration limit can be adjusted using the Fedora config parameter
      // named "datastreamMediationLimit" which is in milliseconds.
      s_server.logFinest("[DatastreamResolverServlet] TimeStamp differential "
          + "for Mechanism's response: " + ((long)currentTimestamp.getTime() -
          (long)keyTimestamp.getTime()) + " milliseconds");
      System.out.println("[DatastreamResolverServlet] TimeStamp differential "
          + "for Mechanism's response: " + ((long)currentTimestamp.getTime() -
          (long)keyTimestamp.getTime()) + " milliseconds");
      if (currentTimestamp.getTime() - keyTimestamp.getTime() >
          (long) datastreamMediationLimit)
      {
        out = response.getWriter();
        response.setContentType(HTML_CONTENT_TYPE);
        out.println("<br><b>[DatastreamResolverServlet] Error:</b>"
            + "<font color=\"red\"> Mechanism has failed to respond "
            + "to the DatastreamResolverServlet within the specified "
            + "time limit of \"" + datastreamMediationLimit + "\""
            + "seconds. Datastream access denied.");
        s_server.logWarning("[DatastreamResolverServlet] Error: "
            + "Mechanism has failed to respond "
            + "to the DatastreamResolverServlet within the specified "
            + "time limit of \"" + datastreamMediationLimit + "\""
            + "seconds. Datastream access denied.");
        out.close();
        return;
      }

      if (dsControlGroupType.equalsIgnoreCase("E"))
      {
        // Datastream is ReferencedExternalContent so dsLocation is a URL string
        ExternalContentManager externalContentManager =
            (ExternalContentManager)s_server.getModule(
            "fedora.server.storage.ExternalContentManager");
        mimeTypedStream =
            externalContentManager.getExternalContent(dsPhysicalLocation);
        outStream = response.getOutputStream();
        response.setContentType(mimeTypedStream.MIMEType);
        outStream.write(mimeTypedStream.stream);
      } else if (dsControlGroupType.equalsIgnoreCase("M") ||
                 dsControlGroupType.equalsIgnoreCase("X"))
      {
        // Datastream is either XMLMetadata or ManagedContent so dsLocation
        // is in the form of an internal Fedora ID using the syntax:
        // PID+DSID+DSVersID; parse the ID and get the datastream content.
        String PID = null;
        String dsVersionID = null;
        String dsID = null;
        String[] s = dsPhysicalLocation.split("\\+");
        if (s.length != 3)
        {
          String message = "[DatastreamResolverServlet]  The "
              + "internal Fedora datastream id: \"" + dsPhysicalLocation
              + "\" is invalid.";
          s_server.logWarning(message);
          throw new ServletException(message);
        }
        PID = s[0];
        dsID = s[1];
        dsVersionID = s[2];
        s_server.logFinest("[DatastreamResolverServlet] PID: " + PID
            + "\n dsID: " + dsID + "\n dsVersionID: " + dsVersionID);
        DOReader doReader =  m_manager.getReader(m_context, PID);
        // FIXME!! Need to deal with datastream versioning; ignored for now.
        Datastream d =
            (Datastream) doReader.GetDatastream(dsID, null);
        s_server.logInfo("[DatastreamResolverServlet] Got datastream: "
            + d.DatastreamID);
        InputStream is =
            d.getContentStream();
        int bytestream = 0;
        response.setContentType(d.DSMIME);
        outStream = response.getOutputStream();
        while ((bytestream = is.read()) != -1)
        {
          outStream.write(bytestream);
        }
        is.close();
      } else
      {
        out = response.getWriter();
        response.setContentType(HTML_CONTENT_TYPE);
        out.println("<br>[DatastreamResolverServlet] Unknown "
            + "dsControlGroupType: " + dsControlGroupType + "</br>");
        s_server.logWarning("[DatastreamResolverServlet] Unknown "
            + "dsControlGroupType: " + dsControlGroupType);
      }
    } catch (Throwable th)
    {
      String message = "[DatastreamResolverServlet] returned an error. The "
          + "underlying error was a " + th.getClass().getName()
          + "The message " + "was \"" + th.getMessage() + "\"";
      th.printStackTrace();
      s_server.logWarning(message);
      throw new ServletException(message);
    } finally
    {
      if (out != null) out.close();
      if (outStream != null) outStream.close();
    }

  }

  //Clean up resources
  public void destroy()
  {}
}