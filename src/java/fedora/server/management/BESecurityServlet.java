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
     *
     * The xml document looks like this:
     *
     * <backendSecurityConfig lastModified="2005-10-10T10:10:10.123Z">
     *   <default basicAuth="false" ssl="false" ipList="127\.0\.0\.1"/>
     *   <service role="bDef:1" label="My BDef" basicAuth="true" ssl="false" ipList="127.0.0.1"/>
     *   <service role="bDef:2" basicAuth="default" ssl="default" ipList="default"/>
     * </backendSecurityConfig>
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
            beConfiguration.load(new FileInputStream(m_propsFile));

            String xml = req.getParameter("xml");
            if (xml != null && xml.equals("true")) {
                // just provide the xml
                res.setContentType("text/xml; charset=UTF-8");
                PrintWriter writer = res.getWriter();
                writeXML(bMechLabels, 
                         beConfiguration, 
                         new Date(m_propsFile.lastModified()), 
                         writer);
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
        return new Date(file.lastModified());
    }
    
    private void writeXML(Map bMechLabels,
                          Properties props,
                          Date lastModifiedUTC,
                          PrintWriter out) {
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<backendSecurityConfig lastModified=\"" 
                + DateUtility.convertDateToString(lastModifiedUTC) + "\">");
        writeXMLLine("default", 
                     null, 
                     null, 
                     props.getProperty("all.basicAuth", "false"),
                     props.getProperty("all.ssl", "false"),
                     props.getProperty("all.iplist", ""),
                     out);
        //
        // do all from properties, getting labels if available
        //

        // first determine the list of roles, ignoring "all"
        List configuredRoles = new ArrayList();
        Enumeration enum = props.propertyNames();
        while (enum.hasMoreElements()) {
            String propName = (String) enum.nextElement();
            if (propName.indexOf(":") != -1) {
                String roleName = propName.split("\\.")[0];
                if (!configuredRoles.contains(roleName)) {
                    configuredRoles.add(roleName);
                }
            }
        }

        // then write the configuration for each
        for (int i = 0; i < configuredRoles.size(); i++) {
            String role = (String) configuredRoles.get(i);
            String label = (String) bMechLabels.get(role); // ok if null
            String basicAuth = getValueOrDefault(props, role + ".basicAuth");
            String ssl = getValueOrDefault(props, role + ".ssl");
            String ipList = getValueOrDefault(props, role + ".iplist");
            writeXMLLine("service", role, label, basicAuth, ssl, ipList, out);
        }

        // 
        // do all remaining from existing map, except those in configuredRoles,
        // giving them defaults for everything
        // 
        Iterator iter = bMechLabels.keySet().iterator();
        while (iter.hasNext()) {
            String role = (String) iter.next();
            if (!configuredRoles.contains(role)) {
                writeXMLLine("service", 
                             role, 
                             (String) bMechLabels.get(role), 
                             "default", 
                             "default", 
                             "default", 
                             out);
            }
        }
        out.println("</backendSecurityConfig>");
    }

    private String getValueOrDefault(Properties props, String propName) {
        String val = props.getProperty(propName, "default");
        if (val.trim().length() == 0) val = "default";
        return val;
    }

    private void writeXMLLine(String elementName,
                              String role,
                              String label,
                              String basicAuth,
                              String ssl,
                              String ipList,
                              PrintWriter out) {
        out.print("  <" + elementName);
        if (role != null) out.print(" role=\"" + role + "\"");
        if (label != null) out.print(" label=\"" + StreamUtility.enc(label) + "\"");
        out.print(" basicAuth=\"" + basicAuth + "\"");
        out.print(" ssl=\"" + ssl + "\"");
        out.print(" ipList=\"" + StreamUtility.enc(ipList) + "\"");
        out.println("/>");
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