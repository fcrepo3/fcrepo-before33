package fedora.server.access;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;

import fedora.server.Server;
import fedora.server.Logging;
import fedora.server.errors.InitializationException;


/**
 * <p><b>Title: </b>MethodParameterResolverServlet.java</p>
 *
 * <p><b>Description: </b>This servlet accepts the result of a posted web form
 * containing information about which method parameter values were selected
 * for a dissemination request. The information is read from the form and
 * translated into the corresponding API-A-LITE interface dissemination request
 * in the form of a URI. The initial request is then redirected to the
 * API-A-LITE interface to execute the dissemination request.</p>
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
 * @version 1.0
 */
public class MethodParameterResolverServlet
    extends HttpServlet implements Logging
{

  /** A string constant for the html MIME type */
  private static final String HTML_CONTENT_TYPE = "text/html; charset=UTF-8";

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
    URLDecoder decoder = new URLDecoder();
    Hashtable h_methodParms = new Hashtable();

    // Get parameters passed from web form.
    Enumeration parms = request.getParameterNames();
    while (parms.hasMoreElements())
    {
      String name = new String((String)parms.nextElement());
      if (name.equals("PID"))
      {
        PID = decoder.decode((String)request.getParameter(name), "UTF-8");
      } else if (name.equals("bDefPID"))
      {
        bDefPID = decoder.decode((String)request.getParameter(name), "UTF-8");
      } else if (name.equals("methodName"))
      {
        methodName = decoder.decode((String)request.getParameter(name), "UTF-8");
      } else if (name.equals("asOfDateTime"))
      {
        versDateTime = (String)request.getParameter(name);
      } else if (name.equals("Submit")) {
        // Submit parameter is ignored.
      } else
      {
        // Any remaining parameters are assumed to be method parameters so
        // decode and place in hashtable.
        h_methodParms.put(decoder.decode(name, "UTF-8"),
            decoder.decode((String)request.getParameter(name), "UTF-8"));
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
      redirectURL.append(API_A_LITE_SERVLET_PATH
          + PID + "/"
          + bDefPID + "/"
          + methodName);

      // Add method parameters.
      int i = 0;
      for (Enumeration e = h_methodParms.keys() ; e.hasMoreElements(); )
      {
        String name = URLEncoder.encode((String) e.nextElement(), "UTF-8");
        String value = URLEncoder.encode((String) h_methodParms.get(name), "UTF-8");
        i++;
        if (i == h_methodParms.size())
        {
          methodParms.append(name + "=" + value);
        } else
        {
          methodParms.append(name + "=" + value + "&");
        }

      }
      if (h_methodParms.size() > 0)
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

      // Redirect request back to FedoraAccessServlet.
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