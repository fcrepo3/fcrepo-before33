package fedora.server.management;

import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import fedora.server.*;
import fedora.server.errors.*;
import fedora.server.search.*;
import fedora.server.utilities.*;

public class BESecurityServlet extends HttpServlet {
    
    private FieldSearch m_fieldSearch;
    private File m_propsFile;

    /**
     * Display the form or an xml document providing enough information to
     * construct the form (if xml=true).
     */
    public void doGet(HttpServletRequest req,
                      HttpServletResponse res) throws ServletException {
        try {
            // get the pids and labels of all bMechs in the repository
            Map bMechLabels = new HashMap();
            String[] resultFields = new String[] { "pid", "label" };
            List conditions = new ArrayList();
            FieldSearchQuery query = new FieldSearchQuery(
                                         Condition.getConditions("fType=M"));
            FieldSearchResult result = m_fieldSearch.findObjects(resultFields,
                                                                 100,
                                                                 query);
            List rows = result.objectFieldsList();
            boolean exhausted = false;
            while (!exhausted) {
                Iterator iter = rows.iterator();
                while (iter.hasNext()) {
                    ObjectFields fields = (ObjectFields) iter.next();
                    bMechLabels.put(fields.getPid(), fields.getLabel());
                }
                if (result.getToken() != null) {
                    result = m_fieldSearch.resumeFindObjects(result.getToken());
                    rows = result.objectFieldsList();
                } else {
                    exhausted = true;
                }
            }

            // load the current backend security configuration
            Properties beConfiguration = new Properties();
            Date lastModifiedUTC = getLastModifiedUTCDate(m_propsFile);
            beConfiguration.load(new FileInputStream(m_propsFile));

            String xml = req.getParameter("xml");
            if (xml != null && xml.equals("true")) {
                // just provide the xml
                res.setContentType("text/xml; charset=UTF-8");
                PrintWriter writer = res.getWriter();
                writeXML(bMechLabels, beConfiguration, lastModifiedUTC, writer);
                writer.flush();
                writer.close();
            } else {
                // get the xml and transform it
                res.setContentType("text/html; charset=UTF-8");
                PrintWriter writer = res.getWriter();
                writer.println("transformation of xml not implemented yet");
                writer.flush();
                writer.close();
            }
        } catch (Exception e) {
            try {
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                              e.getMessage());
            } catch (Exception ex) { }
        }
    }

    private static Date getLastModifiedUTCDate(File file) {
        return DateUtility.convertLocalDateToUTCDate(new Date(file.lastModified()));
    }
    
    private void writeXML(Map bMechLabels,
                          Properties props,
                          Date lastModifiedUTC,
                          PrintWriter out) {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<beSecurityConfig lastModified=\"" 
                + DateUtility.convertDateToString(lastModifiedUTC) + "\">");
        Iterator pids = bMechLabels.keySet().iterator();
        while (pids.hasNext()) {
            String pid = (String) pids.next();
            String label = (String) bMechLabels.get(pid);
            out.println("  <bMech pid=\"" + StreamUtility.enc(pid) + "\""
                    + " label=\"" + StreamUtility.enc(label) + "\"/>");
        }
        props.list(out);
        out.println("</beSecurityConfig>");
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