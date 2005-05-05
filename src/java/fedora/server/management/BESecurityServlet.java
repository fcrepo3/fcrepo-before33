package fedora.server.management;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import fedora.server.*;
import fedora.server.errors.*;
import fedora.server.search.*;

public class BESecurityServlet extends HttpServlet {
    
    private FieldSearch m_fieldSearch;
    private File m_propsFile;

    /**
     * Display the form.
     */
    public void doGet(HttpServletRequest req,
                      HttpServletResponse res) throws ServletException {
        // get info on all bmechs from fedora/search
        // read the properties file
        // if xml = true, serialize xml with all the info
        // else transform it to html
        try {
            res.setContentType("text/xml; charset=UTF=8");
            PrintWriter writer = res.getWriter();
            writeXML(null, null, writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            try {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                              e.getMessage());
            } catch (Exception ex) { }
        }
    }
    
    private void writeXML(Map allBMechLabels,
                          Properties props,
                          PrintWriter out) {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<beSecurity>");
        out.println("</beSecurity>");
    }
   
    /**
     * Initialize by getting a reference to the FieldSearch module
     * and making sure the beSecurity.properties file exists.
     */
    public void init() throws ServletException {
        try {
            File fedoraHome = new File(System.getProperty("fedora.home"));
            Server server = Server.getInstance(fedoraHome, false);
            m_fieldSearch = (FieldSearch) server.getModule("fedora.server.search.FieldSearch");
            if (m_fieldSearch == null) {
                throw new ServletException("FieldSearch module not loaded");
            }
            m_propsFile = new File(fedoraHome, 
                                   "server/config/beSecurity.properties");
            if (!m_propsFile.exists()) {
                throw new ServletException("Required file missing: " 
                        + m_propsFile.getPath());
            }
        } catch (InitializationException e) {
            throw new ServletException("Unable to get server instance", e);
        }
    }
    
}