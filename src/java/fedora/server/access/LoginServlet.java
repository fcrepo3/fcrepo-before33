package fedora.server.access;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import fedora.server.Logging;
import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.utilities.StreamUtility;

/**
 * Prompts user for name and password, using HTTP basic authentication.
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class LoginServlet 
        extends HttpServlet implements Logging {

    /** Content type for html. */
    private static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";

    /** Instance of the Fedora server. */
    private static Server s_server = null;

    /**
     * The servlet entry point.  http://host:port/fedora/login
     *
     * @param request The servlet request.
     * @param response servlet The servlet response.
     * @throws ServletException If anything really bad happens.
     * @throws IOException If an error occurrs with an input or output operation.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId=request.getRemoteUser();
        response.setHeader("Cache-control", "no-cache, must-revalidate");
        if (userId==null) { // || userId.equals(notUser)) {
            // send challenge
			response.setHeader("WWW-Authenticate", "Basic realm=\"" 
			        + s_server.getParameter("repositoryName") + "\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            // tell who they're logged in as
            response.setContentType(CONTENT_TYPE_HTML);
            PrintWriter w=response.getWriter();
            w.println("<html><body>");
            w.println("You are logged in as <b>" + userId + "</b><br/>");
   			StringBuffer endOfUrl=new StringBuffer();
   			endOfUrl.append(s_server.getParameter("fedoraServerHost"));
   			if (!s_server.getParameter("fedoraServerPort").equals("80")) {
   			    endOfUrl.append(":" + s_server.getParameter("fedoraServerPort"));
   			}
   			/* 
   			endOfUrl.append("/fedora/login?prevUser=" + userId);
			if (!userId.equals("guest")) {
                w.println("Login <a href=\"http://guest:guest@" + endOfUrl.toString() + "\"/>as a guest</a>.<br/>");
			}
			*/
            w.println("Login <a href=\"http://" + endOfUrl.toString() + "\"/>as a different user</a>.");
            w.println("</body></html>");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Initialize servlet.  Gets a reference to the fedora Server object.
     *
     * @throws ServletException If the servet cannot be initialized.
     */
    public void init() throws ServletException {
        try {
            s_server=Server.getInstance(new File(System.getProperty("fedora.home")), false);
        } catch (InitializationException ie) {
            throw new ServletException("Unable to get Fedora Server instance."
                + ie.getMessage());
        }
    }

    public final Server getServer() {
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