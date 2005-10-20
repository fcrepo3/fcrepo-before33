package fedora.server.management;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import javax.xml.transform.stream.*;
import javax.xml.transform.*;

import fedora.common.*;
import fedora.server.*;
import fedora.server.errors.*;
import fedora.server.search.*;
import fedora.server.storage.*;
import fedora.server.storage.types.*;
import fedora.server.security.*;
import fedora.server.utilities.*;

public class BESecurityServlet extends HttpServlet {
    
    private FieldSearch m_fieldSearch;
    private DOManager m_doManager;
    private File m_configFile;
    private File m_styleFile;

    /**
     * Respond to an HTTP GET request.
     *
     * Displays an html form for editing backend security configuration, 
     * or an xml document providing enough information to construct the form 
     * (if xml=true).
     */
    public void doGet(HttpServletRequest req,
                      HttpServletResponse res) throws ServletException {
        PrintWriter writer = null;
        try {

            // determine the caller's context
            Context context = ReadOnlyContext.getContext(Constants.HTTP_REQUEST.REST.uri, req);

            // load the current beSecurity.xml file
            BESecurityConfig config = null;
            synchronized (m_configFile) {
                FileInputStream in = new FileInputStream(m_configFile);
                config = BESecurityConfig.fromStream(in);
            }

            // in memory, add empty configs for all bMechs and methods not 
            // already explicitly configured via beSecurity.xml
            config.addEmptyConfigs(getAllBMechMethods(context));

            // respond to the request
            String xml = req.getParameter("xml");
            if (xml != null && xml.equals("true")) {
                // just provide the xml
                res.setContentType("text/xml; charset=UTF-8");
                writer = res.getWriter();
                config.write(false, writer);
            } else {
                // get the xml and transform it
                ByteArrayOutputStream memOut = new ByteArrayOutputStream();
                PrintWriter pw = new PrintWriter(memOut);
                config.write(false, pw);
                pw.flush();
                Reader xmlReader = new InputStreamReader(
                                       new ByteArrayInputStream(
                                           memOut.toByteArray()));
                Transformer transformer = TransformerFactory
                                              .newInstance()
                                              .newTemplates(new StreamSource(m_styleFile))
                                              .newTransformer();
//              transformer.setParameter("paramName", paramValue);
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
            res.setContentType("text/html");
            writer = res.getWriter();
            writer.println("<html><body><h2>Parameters</h2><table border=\"1\">");
            Iterator keys = req.getParameterMap().keySet().iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                writer.print("<tr><td><b>" + key + "</b></td><td>");
                String[] vals = (String[]) req.getParameterMap().get(key);
                for (int i = 0; i < vals.length; i++) {
                    if (i > 0) writer.println("<br/>");
                    writer.print("'" + vals[i] + "'");
                }
                writer.println("</td></tr>");
            }
            writer.println("</table></body></html>");
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

    private Map getAllBMechMethods(Context context) throws Exception {
        Map map = new HashMap();
        String[] resultFields = new String[] { "pid" };
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
                map.put(fields.getPid(), getMethods(fields.getPid(), context));
            }
            if (result.getToken() != null) {
                result = m_fieldSearch.resumeFindObjects(result.getToken());
                rows = result.objectFieldsList();
            } else {
                exhausted = true;
            }
        }
        return map;
    }

    private List getMethods(String bMechPID, Context context) throws Exception {
        List list = new ArrayList();
        BMechReader reader = m_doManager.getBMechReader(false, context, bMechPID);
        MethodDef[] defs = reader.getServiceMethods(null);
        for (int i = 0; i < defs.length; i++) {
            list.add(defs[i].methodName);
        }
        return list;
    }

    /**
     * Initialize the servlet by getting a reference to the FieldSearch module
     * and making sure the beSecurity.xml and stylesheet files exist.
     */
    public void init() throws ServletException {
        try {
            File fedoraHome = new File(System.getProperty("fedora.home"));
            Server server = Server.getInstance(fedoraHome, false);

            // fieldsearch module
            m_fieldSearch = (FieldSearch) server.getModule("fedora.server.search.FieldSearch");
            if (m_fieldSearch == null) {
                throw new ServletException("FieldSearch module not loaded");
            }

            // domanager module
            m_doManager = (DOManager) server.getModule("fedora.server.storage.DOManager");
            if (m_doManager == null) {
                throw new ServletException("DOManager module not loaded");
            }

            // config file
            m_configFile = new File(fedoraHome, 
                                   "server/config/beSecurity.xml");
            if (!m_configFile.exists()) {
                throw new ServletException("Required file missing: " 
                        + m_configFile.getPath());
            }

            // style file
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