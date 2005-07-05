package fedora.server.access;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.trippi.*;
import org.trippi.server.TrippiServer;
import org.trippi.server.http.*;

import fedora.server.*;
import fedora.server.resourceIndex.ResourceIndex;

public class RISearchServlet extends TrippiServlet {
	private static final Logger logger =
        Logger.getLogger(ResourceIndex.class.getName());
	
    public TriplestoreReader getReader() throws ServletException {
        TriplestoreReader reader = null;
        try {
            Server server = Server.getInstance(new File(System.getProperty("fedora.home")), false);
            reader = (TriplestoreReader) server.getModule("fedora.server.resourceIndex.ResourceIndex");
        } catch (Exception e) {
            throw new ServletException("Error initting RISearchServlet.", e);
        } 
        if (reader == null) {
            throw new ServletException("The Resource Index is not loaded.");
        } else {
            return reader;
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
						 "  dumbTypes: " + request.getParameter("dumbTypes") + "\n");
    	}
    	super.doGet(server, request, response);
    }

    public boolean closeOnDestroy() { return false; }
    public String getIndexStylesheetLocation() { return "/ROOT/ri/index.xsl"; }
    public String getFormStylesheetLocation() { return "/ROOT/ri/form.xsl"; }
    public String getErrorStylesheetLocation() { return "/ROOT/ri/error.xsl"; }
    public String getContext(String origContext) { return "/ri"; }
}
