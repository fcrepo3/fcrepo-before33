/*
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Educational Community License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.opensource.org/licenses/ecl1.txt">
 * http://www.opensource.org/licenses/ecl1.txt.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2007 by 
 * The Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */

package melcoe.fedora.util;

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
        }
        
        return writer;
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
