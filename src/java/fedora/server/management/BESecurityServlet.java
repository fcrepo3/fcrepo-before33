package fedora.server.management;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.xml.transform.stream.*;
import javax.xml.transform.*;

import fedora.server.*;
import fedora.server.errors.*;
import fedora.server.search.*;
import fedora.server.utilities.*;

public class BESecurityServlet extends HttpServlet {
    
    private FieldSearch m_fieldSearch;
    private File m_propsFile;
    private File m_styleFile;

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
        showConfig(req, res, null);
    }

    private void showConfig(HttpServletRequest req,
                            HttpServletResponse res,
                            String alertMessage) throws ServletException {
        PrintWriter writer = null;
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
            Date configLastModified = new Date(m_propsFile.lastModified());

            String xml = req.getParameter("xml");
            if (xml != null && xml.equals("true")) {
                // just provide the xml
                res.setContentType("text/xml; charset=UTF-8");
                writer = res.getWriter();
                writeXML(bMechLabels, 
                         beConfiguration, 
                         configLastModified,
                         writer);
            } else {
                // get the xml and transform it
                ByteArrayOutputStream memOut = new ByteArrayOutputStream();
                PrintWriter pw = new PrintWriter(memOut);
                writeXML(bMechLabels,
                         beConfiguration,
                         configLastModified,
                         pw);
                pw.flush();
                Reader xmlReader = new InputStreamReader(
                                       new ByteArrayInputStream(
                                           memOut.toByteArray()));
                Transformer transformer = TransformerFactory
                                              .newInstance()
                                              .newTemplates(new StreamSource(m_styleFile))
                                              .newTransformer();
                if (alertMessage != null) {
                    transformer.setParameter("alertMessage", alertMessage);
                }
                res.setContentType("text/html; charset=UTF-8");
                writer = res.getWriter();
                transformer.transform(new StreamSource(xmlReader),
                                      new StreamResult(res.getWriter()));
            }
        } catch (Exception e) {
            try {
                e.printStackTrace();
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                              e.getMessage());
            } catch (Exception ex) { }
        } finally {
            if (writer != null) {
                try { writer.flush(); } catch (Exception e) { }
                try { writer.close(); } catch (Exception e) { }
            }
        }
    }

    public void doPost(HttpServletRequest req,
                       HttpServletResponse res) throws ServletException {
        PrintWriter writer = null;
        try {
            writeConfigFile(req.getParameterMap());
            showConfig(req, res, "Success! Your changes have been saved.");
        } catch (Exception e) {
            try {
                e.printStackTrace();
                res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                              e.getMessage());
            } catch (Exception ex) { }
        } finally {
            if (writer != null) {
                try { writer.flush(); } catch (Exception e) { }
                try { writer.close(); } catch (Exception e) { }
            }
        }
    }

    private synchronized void writeConfigFile(Map params) throws Exception {
        String lastModifiedString = getParamValue("lastModified", params);
        if (lastModifiedString.equals("")) throw new IOException("Required parameter (lastModified) missing");
        long priorLastModified = DateUtility.parseDateAsUTC(lastModifiedString).getTime();
        long configLastModified = m_propsFile.lastModified();
        if (configLastModified > priorLastModified) throw new IOException("Up to date check failed - try again");
        PrintWriter configWriter = null;
        try {
            configWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(m_propsFile)));
            writeConfig(params, configWriter);
        } finally {
            if (configWriter != null) {
                configWriter.flush();
                configWriter.close();
            }
        }
    }

    private static String getParamValue(String name, Map params) {
        String[] values = (String[]) params.get(name);
        if (values == null || values.length == 0) return "";
        return values[0];
    }

    private static void writeConfig(Map params,
                                    PrintWriter out) {
        // do the header
        out.println("#");
        out.println("# Configuration for backend services.");
        out.println("#");
        out.println("# This information is used to generate proper callback URLs when ");
        out.println("# datastreams are passed by-reference to behavior mechanism ");
        out.println("# services.  It is also used to generate policies that dictate ");
        out.println("# whether a datastream callback request is honored.");
        out.println("#");
        String dateString = DateUtility.convertDateToString(new Date(System.currentTimeMillis()));
        out.println("# Last Generated: " + dateString);
        out.println("#");
        out.println();

        // then the defaults
        out.println("# Defaults for unconfigured values");
        String defaultBasicAuth = getParamValue("all.basicAuth", params);
        if (!defaultBasicAuth.equals("true")) defaultBasicAuth = "false";
        String defaultSSL = getParamValue("all.ssl", params);
        if (!defaultSSL.equals("true")) defaultSSL = "false";
        String defaultIPList = getParamValue("all.ipList", params);
        out.println("all.basicAuth = " + defaultBasicAuth);
        out.println("all.ssl       = " + defaultSSL);
        out.println("# These are space-delimited regular expressions.  Here, an empty value allows any IP address.");
        out.println("all.iplist    = " + defaultIPList);
        out.println();

        // construct the list of roles
        List roles = new ArrayList();
        Iterator iter = params.keySet().iterator();
        while (iter.hasNext()) {
            String name = (String) iter.next();
            String[] parts = name.split("\\.");
            if (parts.length == 2) {
                String role = parts[0];
                if (!role.equals("all") && !roles.contains(role)) roles.add(role);
            }
        }

        // then output all the info for each, in sorted order
        Collections.sort(roles);
        iter = roles.iterator();
        while (iter.hasNext()) {
            String role = (String) iter.next();
            String label = getParamValue(role + ".label", params);
            String basicAuth = getParamValue(role + ".basicAuth", params);
            String ssl = getParamValue(role + ".ssl", params);
            String ipList = getParamValue(role + ".ipList", params);

            String roleKey = role.replaceAll(":", "\\\\:");

            // write the label as a comment
            if (label.trim().length() == 0) label = role;
            out.println("# " + label);

            // write the basicAuth value (as a comment if default)
            if (basicAuth.length() == 0 || basicAuth.equals("default")) {
                basicAuth = defaultBasicAuth;
                out.print("# (Using Default) ");
            }
            out.println(roleKey + ".basicAuth = " + basicAuth);

            // write the ssl value (as a comment if default)
            if (ssl.length() == 0 || ssl.equals("default")) {
                ssl = defaultSSL;
                out.print("# (Using Default) ");
            }
            out.println(roleKey + ".ssl       = " + ssl);

            // write the iplist value (as a comment if default)
            if (ipList.length() == 0 || ipList.equals("default")) {
                ipList = defaultIPList;
                out.print("# (Using Default) ");
            }
            out.println(roleKey + ".iplist    = " + ipList);

            out.println();


        }

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
        Enumeration enm = props.propertyNames();
        while (enm.hasMoreElements()) {
            String propName = (String) enm.nextElement();
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
        if (ipList.trim().length() == 0) ipList = ".*";
        out.print(" ipList=\"" + StreamUtility.enc(ipList) + "\"");
        out.println("/>");
    }

    /**
     * Initialize by getting a reference to the FieldSearch module
     * and making sure the beSecurity.properties and stylesheet files exist.
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
            m_styleFile = new File(fedoraHome,
                                   "server/management/backendSecurityConfig.xslt");
            if (!m_styleFile.exists()) {
                throw new ServletException("Required file missing: " 
                        + m_styleFile.getPath());
            }
        } catch (InitializationException e) {
            throw new ServletException("Unable to get server instance", e);
        }
    }
    
}