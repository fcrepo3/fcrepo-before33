package fedora.server.access;

import fedora.server.utilities.DateUtility;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import fedora.server.Server;
import fedora.server.Logging;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ServerException;



/**
 * <p>Title: MethodParameterResolverServlet.java</p>
 *
 * <p>Description: <p>This servlet accepts the result of a posted web form
 * containing information about which method parameter values were selected
 * for a dissemination request. The information is read from the form and
 * translated into the corresponding API-A-LITE interface dissemination request
 * in the form of a URI. The initial request is then redirected to the
 * API-A-LITE interface to execute the dissemination request.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class MethodParameterResolverServlet
    extends HttpServlet implements Logging
{

  /** A string constant for the html MIME type */
  private static final String HTML_CONTENT_TYPE = "text/html";

  /** The Fedora API-A-Lite servlet path. */
  private static final String API_A_LITE_SERVLET_PATH = "/fedora/get/";

  /** An instance of the Fedora server. */
  private static Server s_server = null;

  public void init() throws ServletException
  {
    try
    {
      s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
    } catch (InitializationException ie)
    {
      throw new ServletException("Unable to get Fedora Server instance. -- "
          + ie.getMessage());
    }
  }

  /**
   * <p> Treat Get request identical to Post request.</p>
   *
   * @param request The servlet request.
   * @param response The servlet response.
   * @throws ServletException  If an error occurs that affects the servlet's
   *                           basic operation.
   * @throws IOException If an error occurs within an input or output operation.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    doPost(request, response);
  }

  /**
   * <p> Process Post request from web form.</p>
   *
   * @param request The servlet request.
   * @param response The servlet response.
   * @throws ServletException If an error occurs that affects the servlet's
   *                          basic operation.
   * @throws IOException If an error occurs within an input or output operation.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    String PID = null;
    String bDefPID = null;
    String methodName = null;
    String versDateTime = null;
    StringBuffer methodParms = new StringBuffer();
    response.setContentType(HTML_CONTENT_TYPE);
    PrintWriter out = response.getWriter();

    // Get parameters passed from web form.
    Enumeration parms = request.getParameterNames();
    while (parms.hasMoreElements())
    {
      String name = new String((String)parms.nextElement());
      if (name.equals("PID"))
      {
        PID = (String)request.getParameter(name);
      } else if (name.equals("bDefPID"))
      {
        bDefPID = (String)request.getParameter(name);
      } else if (name.equals("methodName"))
      {
        methodName = (String)request.getParameter(name);
      } else if (name.equals("asOfDateTime"))
      {
        versDateTime = (String)request.getParameter(name);
      } else if (name.equals("Submit")) {
        // Submit parameter is ignored.
      } else
      {
        // Any remaining parameters are assumed to be method parameters.
        methodParms.append(name+"="+(String)request.getParameter(name)+"&");
      }
    }

    // Check that all required parameters are present.
    if ((PID == null || PID.equalsIgnoreCase("")) ||
        (bDefPID == null || bDefPID.equalsIgnoreCase("")) ||
        (methodName == null || methodName.equalsIgnoreCase("")) )
    {
      String message = "[MethodParameterResolverServlet] Insufficient "
          + "information to construct dissemination request. Parameters "
          + "received from web form were: PID: " + PID
          + " -- bDefPID: " + bDefPID
          + " -- methodName: " + methodName
          + " -- methodParms: " + methodParms.toString() + "\".  ";
      logWarning(message);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.sendError(response.SC_INTERNAL_SERVER_ERROR, message);
    } else
    {
      // Translate web form parameters into dissemination request.
      StringBuffer redirectURL = new StringBuffer();
      URLDecoder decode = new URLDecoder();
      PID = decode.decode(PID, "UTF-8");
      bDefPID = decode.decode(bDefPID,"UTF-8");
      redirectURL.append(API_A_LITE_SERVLET_PATH
          + PID + "/"
          + bDefPID + "/"
          + methodName);
      if (methodParms.length() > 0)
      {
        if (versDateTime == null || versDateTime.equalsIgnoreCase(""))
        {
          redirectURL.append("?"+methodParms.toString());
        } else
        {
          redirectURL.append("/"+versDateTime+"?"+methodParms.toString());
        }
      } else
      {
        if (versDateTime == null || versDateTime.equalsIgnoreCase(""))
        {
          redirectURL.append("/");
        } else
        {
          redirectURL.append("/"+versDateTime+"/");
        }
      }

      // redirect request.
      response.sendRedirect(redirectURL.toString());
    }
  }

  //Clean up resources
  public void destroy()
  {
  }

  private Server getServer() {
      return s_server;
  }

  /**
   * Logs a SEVERE message, indicating that the server is inoperable or
   * unable to start.
   *
   * @param message The message.
   */
  public final void logSevere(String message) {
      StringBuffer m=new StringBuffer();
      m.append(getClass().getName());
      m.append(": ");
      m.append(message);
      getServer().logSevere(m.toString());
  }

  public final boolean loggingSevere() {
      return getServer().loggingSevere();
  }

  /**
   * Logs a WARNING message, indicating that an undesired (but non-fatal)
   * condition occured.
   *
   * @param message The message.
   */
  public final void logWarning(String message) {
      StringBuffer m=new StringBuffer();
      m.append(getClass().getName());
      m.append(": ");
      m.append(message);
      getServer().logWarning(m.toString());
  }

  public final boolean loggingWarning() {
      return getServer().loggingWarning();
  }

  /**
   * Logs an INFO message, indicating that something relatively uncommon and
   * interesting happened, like server or module startup or shutdown, or
   * a periodic job.
   *
   * @param message The message.
   */
  public final void logInfo(String message) {
      StringBuffer m=new StringBuffer();
      m.append(getClass().getName());
      m.append(": ");
      m.append(message);
      getServer().logInfo(m.toString());
  }

  public final boolean loggingInfo() {
      return getServer().loggingInfo();
  }

  /**
   * Logs a CONFIG message, indicating what occurred during the server's
   * (or a module's) configuration phase.
   *
   * @param message The message.
   */
  public final void logConfig(String message) {
      StringBuffer m=new StringBuffer();
      m.append(getClass().getName());
      m.append(": ");
      m.append(message);
      getServer().logConfig(m.toString());
  }

  public final boolean loggingConfig() {
      return getServer().loggingConfig();
  }

  /**
   * Logs a FINE message, indicating basic information about a request to
   * the server (like hostname, operation name, and success or failure).
   *
   * @param message The message.
   */
  public final void logFine(String message) {
      StringBuffer m=new StringBuffer();
      m.append(getClass().getName());
      m.append(": ");
      m.append(message);
      getServer().logFine(m.toString());
  }

  public final boolean loggingFine() {
      return getServer().loggingFine();
  }

  /**
   * Logs a FINER message, indicating detailed information about a request
   * to the server (like the full request, full response, and timing
   * information).
   *
   * @param message The message.
   */
  public final void logFiner(String message) {
      StringBuffer m=new StringBuffer();
      m.append(getClass().getName());
      m.append(": ");
      m.append(message);
      getServer().logFiner(m.toString());
  }

  public final boolean loggingFiner() {
      return getServer().loggingFiner();
  }

  /**
   * Logs a FINEST message, indicating method entry/exit or extremely
   * verbose information intended to aid in debugging.
   *
   * @param message The message.
   */
  public final void logFinest(String message) {
      StringBuffer m=new StringBuffer();
      m.append(getClass().getName());
      m.append(": ");
      m.append(message);
      getServer().logFinest(m.toString());
  }

  public final boolean loggingFinest() {
      return getServer().loggingFinest();
  }
}