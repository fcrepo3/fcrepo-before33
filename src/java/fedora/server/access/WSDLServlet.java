package fedora.server.access;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Dynamically provides complete and accurate WSDL files for Fedora APIs.
 *
 * This servlet directly includes the common XSD type definitions
 * in each WSDL file and ensures that the binding address reflects the
 * base URL of the Fedora instance, based on the request URI.
 */
public class WSDLServlet extends HttpServlet {

    /** This servlet's path, relative to the Fedora webapp. */
    public static final String SERVLET_PATH = "wsdl";

    /** Relative path to our XSD source file. */
    private static final String _XSD_PATH = "xsd/fedora-types.xsd";

    /** Source WSDL file relative paths, mapped by name */
    private static final Map _WSDL_PATHS = new HashMap();

    /** $FEDORA_HOME/server */
    private File _serverDir;

    static {
        _WSDL_PATHS.put("API-A",      "access/Fedora-API-A.wsdl");
        _WSDL_PATHS.put("API-A-LITE", "access/Fedora-API-A-LITE.wsdl");
        _WSDL_PATHS.put("API-M",      "management/Fedora-API-A.wsdl");
        _WSDL_PATHS.put("API-M-LITE", "management/Fedora-API-A-LITE.wsdl");
    }

    /**
     * Respond to an HTTP GET request.
     *
     * The single parameter, "api", indicates which WSDL file to provide.
     * If no parameters are given, a simple HTML index is given instead.
     */
    public void doGet(HttpServletRequest request, 
                      HttpServletResponse response) 
              throws IOException, ServletException {

        String body;
        String api = request.getParameter("api");

        if (api == null || api.length() == 0) {
            response.setContentType("text/html; charset=UTF-8");
            body = getIndex();
        } else {
            response.setContentType("text/xml; charset=UTF-8");
            body = getWSDL(api, request.getRequestURI());
        }

        PrintWriter writer = response.getWriter();
        writer.print(body);
        writer.flush();
        writer.close();
    }

    /**
     * Get a simple HTML index pointing to each WSDL file 
     * provided by the servlet.
     */
    private String getIndex() {
        StringBuffer out = new StringBuffer();
        out.append("<html><body><ul>\n");
        Iterator names = _WSDL_PATHS.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            out.append("<li> <a href=\"?api=" + name + "\">" 
                    + name + "</a></li>\n");
        }
        out.append("</ul></body></html>");
        return out.toString();
    }

    /**
     * Get the self-contained WSDL given the api name and request URI.
     */
    private String getWSDL(String api, String requestURI) 
            throws IOException, ServletException {
        return null;
    }

    /**
     * Initialize by setting serverDir based on fedora.home system property.
     */
    public void init() throws ServletException {

        String fedoraHome = System.getProperty("fedora.home");

        if (fedoraHome == null || fedoraHome.length() == 0) {
            throw new ServletException("fedora.home is not defined");
        } else {
            _serverDir = new File(new File(fedoraHome), "server");
            if (!_serverDir.isDirectory()) {
                throw new ServletException("No such directory: " 
                        + _serverDir.getPath());
            }
        }

    }

}
