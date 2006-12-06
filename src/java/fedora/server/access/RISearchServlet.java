package fedora.server.access;

import java.io.File;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.trippi.TriplestoreReader;
import org.trippi.TriplestoreWriter;
import org.trippi.server.TrippiServer;
import org.trippi.server.http.TrippiServlet;

import fedora.common.Constants;

import fedora.server.Server;
import fedora.server.resourceIndex.ResourceIndex;

public class RISearchServlet extends TrippiServlet {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger =
        Logger.getLogger(ResourceIndex.class.getName());
	
    public TriplestoreReader getReader() throws ServletException {
        return getWriter();
    }
    
    public TriplestoreWriter getWriter() throws ServletException {
        TriplestoreWriter writer = null;
        try {
            Server server = Server.getInstance(new File(Constants.FEDORA_HOME), false);
            writer = (TriplestoreWriter) server.getModule("fedora.server.resourceIndex.ResourceIndex");
        } catch (Exception e) {
            throw new ServletException("Error initting RISearchServlet.", e);
        } 
        if (writer == null) {
            throw new ServletException("The Resource Index is not loaded.");
        } else {
            return writer;
        }
    }
    
    public void doGet(TrippiServer server, 
            HttpServletRequest request,
            HttpServletResponse response) 
    throws Exception {
    	if (logger.isDebugEnabled()) {
            logger.debug("doGet()\n" +
            			 "  type: " + request.getParameter("type") + "\n" +
						 "  template: " + request.getParameter("template") + "\n" +
						 "  lang: " + request.getParameter("lang") + "\n" +
						 "  query: " + request.getParameter("query") + "\n" +
						 "  limit: " + request.getParameter("limit") + "\n" +
						 "  distinct: " + request.getParameter("distinct") + "\n" +
						 "  format: " + request.getParameter("format") + "\n" +
						 "  flush: " + request.getParameter("flush") + "\n" +
						 "  dumbTypes: " + request.getParameter("dumbTypes") + "\n");
    	}
    	super.doGet(server, request, response);
    }

    public boolean closeOnDestroy() { return false; }
    public String getIndexStylesheetLocation() { return "/fedora/ri/index.xsl"; }
    public String getFormStylesheetLocation() { return "/fedora/ri/form.xsl"; }
    public String getErrorStylesheetLocation() { return "/fedora/ri/error.xsl"; }
    public String getContext(String origContext) { return "/fedora/ri"; }
}
