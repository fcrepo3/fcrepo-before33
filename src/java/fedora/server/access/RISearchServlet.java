package fedora.server.access;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;

import com.hp.hpl.jena.rdql.Value;

import fedora.server.*;
import fedora.server.errors.*;
import fedora.server.resourceIndex.*;

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
    private static Server s_server;

    /** Instance of the resource index */
    private static ResourceIndex s_ri;


    public void doGet(HttpServletRequest request, 
                      HttpServletResponse response) 
              throws ServletException {
        PrintWriter out = null;
        RIResultIterator results = null;
        try {
            // Get parameters
            String query = request.getParameter("query");
            String lang = request.getParameter("lang");
            if (lang == null || lang == "") {
                lang = "rdql";
            }

            // Respond depending on whether they passed a query
            if (query == null || query.equals("")) {
                // No query entered, so prompt for one.
                response.setContentType("text/html; charset=UTF-8");
                out = new PrintWriter(new OutputStreamWriter(
                        response.getOutputStream(), "UTF-8"));
                out.println("<html><body>");
                out.println("<h2>Resource Index Query</h2>\n");
                out.println("<form method=\"GET\">\n");
                out.println("Query language: <select name=\"lang\"><option value=\"itql\">itql</option><option value=\"rdql\">rdql</option></select><br>\n");
                out.println("<textarea rows=\"8\" cols=\"60\" name=\"query\">Enter query here</textarea><br>\n");
                out.println("<input type=\"submit\"/>\n");
                out.println("</form>\n");
                out.println("</body></html>");
            } else {
                // Query entered, stream entire response as xml
                response.setContentType("text/xml; charset=UTF-8");
                long startMillis = System.currentTimeMillis();
                boolean queryOk = true;
                try {
                    // Execute the query
                    RIQuery riQuery;
                    if (lang.equals("rdql")) {
                        riQuery = new RDQLQuery(query);
                    } else {
                        riQuery = new ITQLQuery(query);
                    }
                    results = s_ri.executeQuery(riQuery);
                    queryOk = true;
                } catch (Exception e) {
                    // Error while querying: send HTTP500+xml and log it
                    StringBuffer msg = new StringBuffer();
                    msg.append("Error while querying: ");
                    msg.append(e.getClass().getName());
                    if (e.getMessage() != null) msg.append(": " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out = new PrintWriter(
                            new OutputStreamWriter(
                            response.getOutputStream(), "UTF-8"));
                    startXML(out, true, query, lang);
                    out.println("<error>" + enc(msg.toString()) + "</error>");
                    out.println("</response>");
                    logFine(msg.toString());
                    e.printStackTrace();
                    queryOk = false;
                }
                if ( queryOk ) {
                    // Query seems ok... stream the response
                    out = new PrintWriter(
                            new OutputStreamWriter(
                            response.getOutputStream(), "UTF-8"));
                    startXML(out, false, query, lang);
                    // put the binding names into an array (faster than List)
                    String[] names = new String[results.names().size()];
                    Iterator iter = results.names().iterator();
                    int i = 0;
                    while (iter.hasNext()) {
                        names[i] = (String) iter.next();
                        i++;
                    }
                    // output each result
                    long firstMillis = System.currentTimeMillis();
                    int count = 0;
                    while (results.hasNext()) {
                        count++;
                        Map map = results.next();
                        out.println("<result>");
                        // output each value
                        for (i = 0; i < names.length; i++) {
                            out.print("<" + names[i]);
                            Value value = (Value) map.get(names[i]);
                            if ( value == null ) {
                                out.println(" null=\"true\"/>");
                            } else {
                                if (value.isRDFLiteral()) {
                                    out.print(" literal=\"true\">");
                                    out.print(enc(value.getRDFLiteral().getLexicalForm()));
                                } else {
                                    out.print(">" + enc(value.getRDFResource().getURI()));
                                }
                                out.println("</" + names[i] + ">");
                            }
                        }
                        out.println("</result>");
                    }
                    long lastMillis = System.currentTimeMillis();
                    // .. then a summary
                    long latency = firstMillis - startMillis;
                    long total = lastMillis - startMillis;
                    out.println("<summary results=\"" + count 
                                    + "\" latency=\"" + latency
                                    + "\" total=\"" + total + "\"/>");
                    out.println("</response>");
                }
            }
        } catch (Exception e) {
            // Unexpected error, print trace, log, and throw ServletException
            StringBuffer msg = new StringBuffer();
            msg.append("Unexpected error: ");
            msg.append(e.getClass().getName());
            if (e.getMessage() != null) msg.append(": " + e.getMessage());
            e.printStackTrace();
            logWarning(msg.toString());
            throw new ServletException(msg.toString(), e);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception e) {
                    logWarning("Error closing result iterator: " 
                            + e.getClass().getName() + ":" + e.getMessage());
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    logWarning("Error flushing/closing output stream: " 
                            + e.getClass().getName() + ":" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /** Exactly the same behavior as doGet. */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private static void startXML(PrintWriter out,
                                 boolean error,
                                 String query,
                                 String lang) throws IOException {
        String status;
        if (error) {
            status = "error";
        } else {
            status = "ok";
        }
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<response status=\"" + status + "\">");
        out.println("<query lang=\"" + lang + "\">" + enc(query) + "</query>");
    }

    private static String enc(String in) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == '>') {
                out.append("&gt;");
            } else if (c == '<') {
                out.append("&lt;");
            } else if (c == '&') {
                out.append("&amp;");
            } else if (c == '\'') {
                out.append("&apos;");
            } else if (c == '"') {
                out.append("&quot;");
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }

    /** 
     * Init this servlet by getting Fedora server and resource index module 
     * instances. 
     */
    public void init() throws ServletException {
        try {
            s_server = Server.getInstance(new File(System.getProperty("fedora.home")));
            s_ri = (ResourceIndex) s_server.getModule("fedora.server.resourceIndex.ResourceIndex");
        } catch (Exception e) {
            throw new ServletException("Error during initialization: " 
                                       + e.getClass().getName() + ":"
                                       + e.getMessage());
        }
        if (s_ri == null) {
            throw new ServletException("Resource index module not loaded.");
        }
    }

    ///////////////////////////////////////////////////////////////////////////

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
