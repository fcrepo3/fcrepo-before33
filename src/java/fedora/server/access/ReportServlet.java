package fedora.server.access;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import fedora.common.Constants;
import fedora.server.Context;
import fedora.server.Logging;
import fedora.server.ReadOnlyContext;
import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.QueryParseException;
import fedora.server.errors.ServerException;
import fedora.server.security.Authorization;



/**
 *
 * <p><b>Title:</b> ReportServlet.java</p>
 * <p><b>Description:</b> </p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author wdn5e@virginia.edu
 */
public class ReportServlet
        extends HttpServlet
        implements Logging {

	private Server s_server=null;

	private Server getServer() {
		return s_server;
	}
	
	public void init() throws ServletException {
		try {
			s_server=Server.getInstance(new File(System.getProperty("fedora.home")), false);
		} catch (InitializationException ie) {
			throw new ServletException("Error getting Fedora Server instance: "
					+ ie.getMessage());
		}        
	}

	/** Exactly the same behavior as doGet. */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	private static final String ADDR_EXCEPTION = "couldn't get remoteAddr";
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		String sessionToken = request.getParameter("sessionToken");
		
		String remoteAddr = remoteAddr = request.getRemoteAddr();
		
		String query = request.getParameter("query");

		//Hashtable parmshash = new Hashtable();
		String[] fieldsArray = null; {
			ArrayList fieldsList = new ArrayList();
			Enumeration enum = request.getParameterNames();
			while (enum.hasMoreElements()) {
				String name = (String) enum.nextElement();
				if (Report.allFields.contains(name)) {
					fieldsList.add(name);
				//} else if (Report.parms.contains(name)) {
					//parmshash.put(name,request.getParameter(name));
				}
			}
			if (fieldsList.size() > 0) {
				fieldsArray = (String[]) fieldsList.toArray(new String[] {});
			}
		}

		String maxResults = request.getParameter("maxResults");
		
		String newBase = request.getParameter("newBase");

		String xslt = request.getParameter("xslt");
		
		String reportName = request.getParameter("report");
		
		String prefix = request.getParameter("prefix");
		
		String dateRange = request.getParameter("dateRange");

		Report report;
		try {
		    Context context = ReadOnlyContext.getContext(Constants.POLICY_ENVIRONMENT.REST.uri, request, ReadOnlyContext.USE_CACHED_OBJECT);
			report = Report.getInstance(context, remoteAddr, sessionToken, reportName, fieldsArray, query, xslt, maxResults, newBase,
					prefix, dateRange);
		} catch (QueryParseException e1) {
			throw new ServletException("bad query parm", e1);
		} catch (ServerException e1) {
			throw new ServletException("server not available", e1);
		}

		String contentType = report.getContentType();
		response.setContentType(contentType + "; charset=UTF-8");
		OutputStream out = null; // PrintWriter
		if ("text/xml".equals(contentType)) { 
			out = response.getOutputStream();			
		} else if ("text/html".equals(contentType)) { 
			out = response.getOutputStream(); //response.getWriter();
		}
		
		try {
			report.writeOut(out);
		} catch (QueryParseException e) {
			throw new ServletException(e.getMessage(),e);
		} catch (ServerException e) {
			throw new ServletException(e.getMessage(), e);
		} catch (TransformerConfigurationException e) {
			throw new ServletException(e.getMessage(),e);
		} catch (IOException e) {
			throw new ServletException(e.getMessage(),e);
		} catch (TransformerException e) {
			throw new ServletException(e.getMessage(),e);
		}

		
	}


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
        getServer().logSevere(m.toString());
    }

    public final boolean loggingSevere() {
        return getServer().loggingSevere();
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
        getServer().logWarning(m.toString());
    }

    public final boolean loggingWarning() {
        return getServer().loggingWarning();
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
        getServer().logInfo(m.toString());
    }

    public final boolean loggingInfo() {
        return getServer().loggingInfo();
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
        getServer().logConfig(m.toString());
    }

    public final boolean loggingConfig() {
        return getServer().loggingConfig();
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
        getServer().logFine(m.toString());
    }

    public final boolean loggingFine() {
        return getServer().loggingFine();
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
        getServer().logFiner(m.toString());
    }

    public final boolean loggingFiner() {
        return getServer().loggingFiner();
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
        getServer().logFinest(m.toString());
    }

    public final boolean loggingFinest() {
        return getServer().loggingFinest();
    }


}
