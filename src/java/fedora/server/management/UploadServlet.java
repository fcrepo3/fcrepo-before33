package fedora.server.management;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.Part;

import fedora.server.Logging;
import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ServerException;
import fedora.server.utilities.StreamUtility;

/**
 * Accepts and HTTP POST of a file from an authorized user, and if successful,
 * returns a text/plain response with a single line: OK {ID}
 * where {ID} is an opaque identifier that can be used to later submit to 
 * the appropriate API-M method. If it fails, it returns a single line: 
 * ERROR {WHY}.
 *
 * The submitted file must be named "file", must not be accompanied by any other
 * parameters, and cannot be over 2,047 MB in size (due to a trivially 
 * overcome cos.jar limitation).
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
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class UploadServlet 
        extends HttpServlet implements Logging {

    /** Content type for all responses. */
    private static final String CONTENT_TYPE_TEXT = "text/plain";

    /** Instance of the Fedora server. */
    private static Server s_server = null;

    /** Instance of Management subsystem (for storing uploaded files). */
    private static Management s_management = null;

    /**
     * The servlet entry point.  http://host:port/fedora/management/upload
     */
    public void doPost(HttpServletRequest request, 
            HttpServletResponse response) 
            throws IOException {
		try {
            MultipartParser parser=new MultipartParser(request, 
                Integer.MAX_VALUE, true, true);
			Part part=parser.readNextPart();
			if (part!=null && part.isFile()) {
			    if (part.getName().equals("file")) {
			        sendResponse("OK", saveAndGetId((FilePart) part), response);
				} else {
			        sendResponse("ERROR", "Client sent a file, but it wasn't named 'file'.", response);
				}
			} else {
			    if (part==null) {
			        sendResponse("ERROR", "Client didn't send anything!", response);
				} else {
			        sendResponse("ERROR", "Client should only send a file, not "
			                + "any parameters.", response);
				}
			}
		} catch (Exception e) {
		    e.printStackTrace();
		    sendResponse("ERROR", e.getClass().getName() + ": " 
		            + e.getMessage(), response);
		}
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
		sendResponse("FAILURE", "Client must use HTTP POST", response);
	}

    private String saveAndGetId(FilePart filePart)
	        throws ServerException, IOException {
		return s_management.putTempStream(filePart.getInputStream());
	}

    private void sendResponse(String status, String info, 
            HttpServletResponse response)
            throws IOException {
		response.setContentType(CONTENT_TYPE_TEXT);
		PrintWriter w=response.getWriter();
		w.println(status + " " + info);
	}

    /**
     * Initialize servlet.  Gets a reference to the fedora Server object.
     *
     * @throws ServletException If the servet cannot be initialized.
     */
    public void init() throws ServletException {
        try {
            s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
			s_management=(Management) s_server.getModule("fedora.server.management.Management");
			if (s_management==null) {
			    throw new ServletException("Unable to get Management module from server.");
			}
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