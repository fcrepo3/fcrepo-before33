package fedora.server.access;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;

import fedora.server.*;         // Server
import fedora.server.errors.*;  // ServerException

/**
 *
 * Resource Index web search interface.
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class RISearchServlet
        extends HttpServlet
        implements Logging {

    /** Instance of the Server */
    private static Server s_server=null;

    /** Instance of the resource index */
    //private static Access s_access=null;

    public void doGet(HttpServletRequest request, 
                      HttpServletResponse response)
            throws ServletException, 
                   IOException {
        try {
            String terms=request.getParameter("terms");
            String query=request.getParameter("query");

            String xmlOutput=request.getParameter("xml");
            StringBuffer html=new StringBuffer();

                response.setContentType("text/xml; charset=UTF-8");
                PrintWriter out=new PrintWriter(
                        new OutputStreamWriter(
                        response.getOutputStream(), "UTF-8"));
                out.flush();
                out.close();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/html");
            PrintWriter out=response.getWriter();
            out.print("<html><head><title>Fedora Error</title></head>");
            out.print("<body><h2>Fedora Error</h2>");
            out.print("<i>");
            out.print(e.getClass().getName());
            out.print("</i>: ");
            out.print(e.getMessage());
            out.print("</body>");
        }
    }

    /** Exactly the same behavior as doGet. */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /** Gets the Fedora Server instance. */
    public void init() throws ServletException {
        try {
            s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
     //       s_access=(Access) s_server.getModule("fedora.server.access.Access");
        } catch (InitializationException ie) {
            throw new ServletException("Error getting Fedora Server instance: "
                    + ie.getMessage());
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

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
        s_server.logSevere(m.toString());
    }

    public final boolean loggingSevere() {
        return s_server.loggingSevere();
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
        s_server.logWarning(m.toString());
    }

    public final boolean loggingWarning() {
        return s_server.loggingWarning();
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
        s_server.logInfo(m.toString());
    }

    public final boolean loggingInfo() {
        return s_server.loggingInfo();
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
        s_server.logConfig(m.toString());
    }

    public final boolean loggingConfig() {
        return s_server.loggingConfig();
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
        s_server.logFine(m.toString());
    }

    public final boolean loggingFine() {
        return s_server.loggingFine();
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
        s_server.logFiner(m.toString());
    }

    public final boolean loggingFiner() {
        return s_server.loggingFiner();
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
        s_server.logFinest(m.toString());
    }

    public final boolean loggingFinest() {
        return s_server.loggingFinest();
    }


}
