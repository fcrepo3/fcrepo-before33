package fedora.server.access;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import fedora.server.Logging;
import fedora.server.ReadOnlyContext;
import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ServerException;
import fedora.server.search.Condition;
import fedora.server.search.FieldSearchQuery;
import fedora.server.search.FieldSearchResult;
import fedora.server.search.ObjectFields;
import fedora.server.utilities.StreamUtility;

public class FieldSearchServlet 
        extends HttpServlet 
        implements Logging {
        
    /** Instance of the Server */
    private static Server s_server=null;

    /** Instance of the access subsystem */
    private static Access s_access=null;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HashMap h=new HashMap();
            h.put("application", "apia");
            h.put("useCachedObject", "true");
            h.put("userId", "fedoraAdmin");
            h.put("host", request.getRemoteAddr());
            ReadOnlyContext context = new ReadOnlyContext(h);

            String[] fieldsArray=request.getParameterValues("fields");
            HashSet fieldHash=new HashSet();
            if (fieldsArray!=null) {
                for (int i=0; i<fieldsArray.length; i++) {
                    fieldHash.add(fieldsArray[i]);
                }
            }
            String terms=request.getParameter("terms");
            String query=request.getParameter("query");
            
            String sessionToken=request.getParameter("sessionToken");
            
            // FIXME: It would be nice if this were user-controlled..
            // but for now you get 25 or less at a time.
            int maxResults=25;
            
            String xmlOutput=request.getParameter("xml");
            boolean xml=false;
            if ( (xmlOutput!=null) 
                    && (xmlOutput.toLowerCase().startsWith("t")
                    || xmlOutput.toLowerCase().startsWith("y")) ) {
                xml=true;
            }
            StringBuffer xmlBuf=new StringBuffer(); 
            StringBuffer html=new StringBuffer();
            if (!xml) {
                html.append("<form method=\"post\" action=\"/fedora/search\">");
                html.append("<center><table border=0 cellpadding=8>\n");
                html.append("<tr><td width=18% valign=top>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"pid\"" + (fieldHash.contains("pid") ? " checked" : "") + "> Pid<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"label\"" + (fieldHash.contains("label") ? " checked" : "") + "> Label<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"fType\"" + (fieldHash.contains("fType") ? " checked" : "") + "> Fedora Object Type<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"cModel\"" + (fieldHash.contains("cModel") ? " checked" : "") + "> Content Model<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"state\"" + (fieldHash.contains("state") ? " checked" : "") + "> State<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"locker\"" + (fieldHash.contains("locker") ? " checked" : "") + "> Locking User<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"cDate\"" + (fieldHash.contains("cDate") ? " checked" : "") + "> Creation Date<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"mDate\"" + (fieldHash.contains("mDate") ? " checked" : "") + "> Last Modified Date<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"dcmDate\"" + (fieldHash.contains("dcmDate") ? " checked" : "") + "> Dublin Core Modified Date<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"bDef\"" + (fieldHash.contains("bDef") ? " checked" : "") + "> Behavior Definition PID<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"bMech\"" + (fieldHash.contains("bMech") ? " checked" : "") + "> Behavior Mechanism PID<br>");
                html.append("</td><td width=18% valign=top>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"title\"" + (fieldHash.contains("title") ? " checked" : "") + "> Title<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"creator\"" + (fieldHash.contains("creator") ? " checked" : "") + "> Creator<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"subject\"" + (fieldHash.contains("subject") ? " checked" : "") + "> Subject<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"description\"" + (fieldHash.contains("description") ? " checked" : "") + "> Description<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"publisher\"" + (fieldHash.contains("publisher") ? " checked" : "") + "> Publisher<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"contributor\"" + (fieldHash.contains("contributor") ? " checked" : "") + "> Contributor<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"date\"" + (fieldHash.contains("date") ? " checked" : "") + "> Date<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"type\"" + (fieldHash.contains("type") ? " checked" : "") + "> Type<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"format\"" + (fieldHash.contains("format") ? " checked" : "") + "> Format<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"identifier\"" + (fieldHash.contains("identifier") ? " checked" : "") + "> Identifier<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"source\"" + (fieldHash.contains("source") ? " checked" : "") + "> Source<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"language\"" + (fieldHash.contains("language") ? " checked" : "") + "> Language<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"relation\"" + (fieldHash.contains("relation") ? " checked" : "") + "> Relation<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"coverage\"" + (fieldHash.contains("coverage") ? " checked" : "") + "> Coverage<br>");
                html.append("<input type=\"checkbox\" name=\"fields\" value=\"rights\"" + (fieldHash.contains("rights") ? " checked" : "") + "> Rights<br>");
                //html.append("Fields: <input type=\"text\" name=\"fields\" size=\"15\"> ");
                html.append("</td><td width=64% valign=top>");
                html.append("Simple Query: <input type=\"text\" name=\"terms\" size=\"15\" value=\"" + (terms==null ? "" : StreamUtility.enc(terms)) + "\"><p> ");
                html.append("Advanced Query: <input type=\"text\" name=\"query\" size=\"15\" value=\"" + (query==null ? "" : StreamUtility.enc(query)) + "\"><p>&nbsp;<p> ");
                html.append("<input type=\"submit\" value=\"Search\"><p> ");
                html.append("<font size=-1>Choose the fields you want returned by checking the appropriate boxes.");
                html.append(" For simple queries, fill in the Simple Query box and press Search.  ");
                html.append("A simple query consists of one or more words comprising a phrase, where keywords can contain the * and ? wildcards.");
                html.append("For advanced queries, fill in the Advanced Query box and press Search.");
                html.append(" An advanced query is a space-delimited set of conditions.  A condition is of the form <i>propertyOPERATORvalue</i>.");
                html.append("Valid operators are =, ~ (meaning 'contains' -- this can take wildcards), <, <=, >, or >=.  Conditions may not contain spaces, ");
                html.append("unless the value is enclosed in single quotes.  For date-based fields, you may use the ");
                html.append("<, <=, >, or >= operators, and you must format your date like: <i>yyyy-DD-MM[THH:MM:SS[Z]]</i>, where square brackets indicate optional parts.  Note that the ");
                html.append("letter T acts as a delimiter between the date and time parts of the string.</font>");
                html.append("</td></tr></table></center>");
                html.append("</form>");
                html.append("<hr size=\"1\">");
            }
            FieldSearchResult fsr=null;
            if ((fieldsArray!=null && fieldsArray.length>0)) {
                if (sessionToken!=null) {
                    fsr=s_access.resumeFindObjects(context, sessionToken);
                } else {
                    if ((terms!=null) && (terms.length()!=0)) {
                        fsr=s_access.findObjects(context, fieldsArray, 
                                maxResults, new FieldSearchQuery(terms));
                    } else {
                        fsr=s_access.findObjects(context, fieldsArray, 
                                maxResults, new FieldSearchQuery(
                                Condition.getConditions(query)));
                    }
                }
                List searchResults=fsr.objectFieldsList();
                if (!xml) {
                    html.append("<center><table width=\"90%\" border=\"0\" cellpadding=\"4\">\n");
                    html.append("<tr>");
                    for (int i=0; i<fieldsArray.length; i++) {
                        html.append("<td bgcolor=\"#333399\" valign=\"top\"><font color=\"#ffffff\"><b>");
                        html.append(fieldsArray[i]);
                        html.append("</b></font></td>");
                    }
                    html.append("</tr>");
                }
                SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
                for (int i=0; i<searchResults.size(); i++) {
                    ObjectFields f=(ObjectFields) searchResults.get(i);
                    if (xml) {
		        xmlBuf.append("  <objectFields>\n");
                        for (int j=0; j<fieldsArray.length; j++) {
                            String l=fieldsArray[j];
                            if (l.equalsIgnoreCase("pid")) {
                                appendXML(l, f.getPid(), xmlBuf);
                            } else if (l.equalsIgnoreCase("label")) {
                                appendXML(l, f.getLabel(), xmlBuf);
                            } else if (l.equalsIgnoreCase("fType")) {
			        appendXML(l, f.getFType(), xmlBuf);
                            } else if (l.equalsIgnoreCase("cModel")) {
			        appendXML(l, f.getCModel(), xmlBuf);
                            } else if (l.equalsIgnoreCase("state")) {
			        appendXML(l, f.getState(), xmlBuf);
                            } else if (l.equalsIgnoreCase("locker")) {
			        appendXML(l, f.getLocker(), xmlBuf);
                            } else if (l.equalsIgnoreCase("cDate")) {
			        appendXML(l, f.getCDate(), formatter, xmlBuf);
                            } else if (l.equalsIgnoreCase("mDate")) {
			        appendXML(l, f.getMDate(), formatter, xmlBuf);
                            } else if (l.equalsIgnoreCase("dcmDate")) {
			        appendXML(l, f.getDCMDate(), formatter, xmlBuf);
                            } else if (l.equalsIgnoreCase("bDef")) {
			        appendXML(l, f.bDefs(), xmlBuf);
                            } else if (l.equalsIgnoreCase("bMech")) {
			        appendXML(l, f.bMechs(), xmlBuf);
                            } else if (l.equalsIgnoreCase("title")) {
			        appendXML(l, f.titles(), xmlBuf);
                            } else if (l.equalsIgnoreCase("creator")) {
			        appendXML(l, f.creators(), xmlBuf);
                            } else if (l.equalsIgnoreCase("subject")) {
			        appendXML(l, f.subjects(), xmlBuf);
                            } else if (l.equalsIgnoreCase("description")) {
			        appendXML(l, f.descriptions(), xmlBuf);
                            } else if (l.equalsIgnoreCase("publisher")) {
			        appendXML(l, f.publishers(), xmlBuf);
                            } else if (l.equalsIgnoreCase("contributor")) {
			        appendXML(l, f.contributors(), xmlBuf); 
			    } else if (l.equalsIgnoreCase("date")) {
			        appendXML(l, f.dates(), xmlBuf); 
                            } else if (l.equalsIgnoreCase("type")) {
			        appendXML(l, f.types(), xmlBuf);
                            } else if (l.equalsIgnoreCase("format")) {
			        appendXML(l, f.formats(), xmlBuf);
                            } else if (l.equalsIgnoreCase("identifier")) {
			        appendXML(l, f.identifiers(), xmlBuf);
                            } else if (l.equalsIgnoreCase("source")) {
			        appendXML(l, f.sources(), xmlBuf);
                            } else if (l.equalsIgnoreCase("language")) {
			        appendXML(l, f.languages(), xmlBuf);
                            } else if (l.equalsIgnoreCase("relation")) {
			        appendXML(l, f.relations(), xmlBuf);
                            } else if (l.equalsIgnoreCase("coverage")) {
			        appendXML(l, f.coverages(), xmlBuf);
                            } else if (l.equalsIgnoreCase("rights")) {
			        appendXML(l, f.rights(), xmlBuf);
                            }                        
			 }
		         xmlBuf.append("  </objectFields>\n");
		    } else {
                        html.append("<tr>");
                        for (int j=0; j<fieldsArray.length; j++) {
                            String l=fieldsArray[j];
                            html.append("<td valign=\"top\">");
                            if (l.equalsIgnoreCase("pid")) {
                                html.append("<a href=\"/fedora/get/");
                                html.append(f.getPid());
                                html.append("\">");
                                html.append(f.getPid());
                                html.append("</a>");
                            } else if (l.equalsIgnoreCase("label")) {
                                if (f.getLabel()!=null) {
                                    html.append(f.getLabel());
                                }
                            } else if (l.equalsIgnoreCase("fType")) {
                                html.append(f.getFType());
                            } else if (l.equalsIgnoreCase("cModel")) {
                                if (f.getCModel()!=null) {
                                    html.append(f.getCModel());
                                }
                            } else if (l.equalsIgnoreCase("state")) {
                                html.append(f.getState());
                            } else if (l.equalsIgnoreCase("locker")) {
                                if (f.getLocker()!=null) {
                                    html.append(f.getLocker());
                                }
                            } else if (l.equalsIgnoreCase("cDate")) {
                                html.append(formatter.format(f.getCDate()));
                            } else if (l.equalsIgnoreCase("mDate")) {
                                html.append(formatter.format(f.getMDate()));
                            } else if (l.equalsIgnoreCase("dcmDate")) {
                                html.append(formatter.format(f.getDCMDate()));
                            } else if (l.equalsIgnoreCase("bDef")) {
                                html.append(getList(f.bDefs()));
                            } else if (l.equalsIgnoreCase("bMech")) {
                                html.append(getList(f.bMechs()));
                            } else if (l.equalsIgnoreCase("title")) {
                                html.append(getList(f.titles()));
                            } else if (l.equalsIgnoreCase("creator")) {
                                html.append(getList(f.creators()));
                            } else if (l.equalsIgnoreCase("subject")) {
                                html.append(getList(f.subjects()));
                            } else if (l.equalsIgnoreCase("description")) {
                                html.append(getList(f.descriptions()));
                            } else if (l.equalsIgnoreCase("publisher")) {
                                html.append(getList(f.publishers()));
                            } else if (l.equalsIgnoreCase("contributor")) {
                                html.append(getList(f.contributors()));
                            } else if (l.equalsIgnoreCase("date")) {
                                html.append(getList(f.dates()));
                            } else if (l.equalsIgnoreCase("type")) {
                                html.append(getList(f.types()));
                            } else if (l.equalsIgnoreCase("format")) {
                                html.append(getList(f.formats()));
                            } else if (l.equalsIgnoreCase("identifier")) {
                                html.append(getList(f.identifiers()));
                            } else if (l.equalsIgnoreCase("source")) {
                                html.append(getList(f.sources()));
                            } else if (l.equalsIgnoreCase("language")) {
                                html.append(getList(f.languages()));
                            } else if (l.equalsIgnoreCase("relation")) {
                                html.append(getList(f.relations()));
                            } else if (l.equalsIgnoreCase("coverage")) {
                                html.append(getList(f.coverages()));
                            } else if (l.equalsIgnoreCase("rights")) {
                                html.append(getList(f.rights()));
                            }                        
                            html.append("</td>");
                        }
                        html.append("</tr>");
                        html.append("<tr><td colspan=\"");
                        html.append(fieldsArray.length);
                        html.append("\"><hr size=\"1\"></td></tr>");
                    }
                }
                if (!xml) {
                    html.append("</table>");
                    if (fsr!=null && fsr.getToken()!=null) {
                        if (fsr.getCursor()!=-1) {
                            long viewingStart=fsr.getCursor()+1;
                            long viewingEnd=fsr.objectFieldsList().size() + viewingStart - 1;
                            html.append("<p>Viewing results " + viewingStart + " to " + viewingEnd);
                            if (fsr.getCompleteListSize()!=-1) {
                                html.append(" of " + fsr.getCompleteListSize());
                            }
                            html.append("</p>\n");
                        }
                        html.append("<form method=\"post\" action=\"/fedora/search\">");
                        if (fieldHash.contains("pid")) html.append("<input type=\"hidden\" name=\"fields\" value=\"pid\">");
                        if (fieldHash.contains("label")) html.append("<input type=\"hidden\" name=\"fields\" value=\"label\">");
                        if (fieldHash.contains("fType")) html.append("<input type=\"hidden\" name=\"fields\" value=\"fType\">");
                        if (fieldHash.contains("cModel")) html.append("<input type=\"hidden\" name=\"fields\" value=\"cModel\">");
                        if (fieldHash.contains("state")) html.append("<input type=\"hidden\" name=\"fields\" value=\"state\">");
                        if (fieldHash.contains("locker")) html.append("<input type=\"hidden\" name=\"fields\" value=\"locker\">");
                        if (fieldHash.contains("cDate")) html.append("<input type=\"hidden\" name=\"fields\" value=\"cDate\">");
                        if (fieldHash.contains("mDate")) html.append("<input type=\"hidden\" name=\"fields\" value=\"mDate\">");
                        if (fieldHash.contains("dcmDate")) html.append("<input type=\"hidden\" name=\"fields\" value=\"dcmDate\">");
                        if (fieldHash.contains("bDef")) html.append("<input type=\"hidden\" name=\"fields\" value=\"bDef\">");
                        if (fieldHash.contains("bMech")) html.append("<input type=\"hidden\" name=\"fields\" value=\"bMech\">");
                        if (fieldHash.contains("title")) html.append("<input type=\"hidden\" name=\"fields\" value=\"title\">");
                        if (fieldHash.contains("creator")) html.append("<input type=\"hidden\" name=\"fields\" value=\"creator\">");
                        if (fieldHash.contains("subject")) html.append("<input type=\"hidden\" name=\"fields\" value=\"subject\">");
                        if (fieldHash.contains("description")) html.append("<input type=\"hidden\" name=\"fields\" value=\"description\">");
                        if (fieldHash.contains("publisher")) html.append("<input type=\"hidden\" name=\"fields\" value=\"publisher\">");
                        if (fieldHash.contains("contributor")) html.append("<input type=\"hidden\" name=\"fields\" value=\"contributor\">");
                        if (fieldHash.contains("date")) html.append("<input type=\"hidden\" name=\"fields\" value=\"date\">");
                        if (fieldHash.contains("type")) html.append("<input type=\"hidden\" name=\"fields\" value=\"type\">");
                        if (fieldHash.contains("format")) html.append("<input type=\"hidden\" name=\"fields\" value=\"format\">");
                        if (fieldHash.contains("identifier")) html.append("<input type=\"hidden\" name=\"fields\" value=\"identifier\">");
                        if (fieldHash.contains("source")) html.append("<input type=\"hidden\" name=\"fields\" value=\"source\">");
                        if (fieldHash.contains("language")) html.append("<input type=\"hidden\" name=\"fields\" value=\"language\">");
                        if (fieldHash.contains("relation")) html.append("<input type=\"hidden\" name=\"fields\" value=\"relation\">");
                        if (fieldHash.contains("coverage")) html.append("<input type=\"hidden\" name=\"fields\" value=\"coverage\">");
                        if (fieldHash.contains("rights")) html.append("<input type=\"hidden\" name=\"fields\" value=\"rights\">");
                        html.append("\n<input type=\"hidden\" name=\"sessionToken\" value=\"" + fsr.getToken() + "\">\n");
                        html.append("<input type=\"submit\" value=\"More Results &gt;\"></form>");
                    }
                    html.append("</center>\n");
                }
            }
            if (!xml) {
                response.setContentType("text/html");
                PrintWriter out=response.getWriter();
                out.print("<html><head><title>Search Repository</title></head>");
                out.print("<body><h2>Search Repository</h2>");
                out.print(html.toString());
                out.print("</body>");
            } else {
                response.setContentType("text/xml");
                PrintWriter out=response.getWriter();
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<result>");
       if ((fsr!=null) && (fsr.getToken()!=null)) {
           out.println("  <listSession>");
           out.println("    <token>" + fsr.getToken() + "</token>");
           if (fsr.getCursor()!=-1) {
               out.println("    <cursor>" + fsr.getCursor() + "</cursor>");
           }
           if (fsr.getCompleteListSize()!=-1) {
               out.println("    <completeListSize>" + fsr.getCompleteListSize() + "</completeListSize>");
           }
           if (fsr.getExpirationDate()!=null) {
               out.println("    <expirationDate>" + new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").format(fsr.getExpirationDate()) + "</expirationDate>");
           }
           out.println("  </listSession>");
       }
		out.println("<resultList>");
		out.println(xmlBuf.toString());
		out.println("</resultList>");
		out.println("</result>");
            }
        } catch (ServerException se) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("text/html");
            PrintWriter out=response.getWriter();
            out.print("<html><head><title>Fedora Error</title></head>");
            out.print("<body><h2>Fedora Error</h2>");
            out.print("<i>");
            out.print(se.getClass().getName());
            out.print("</i>: ");
            out.print(se.getMessage());
            out.print("</body>");
        }
    }

    private void appendXML(String name, String value, StringBuffer out) {
        if (value!=null) {
            out.append("      <" + name + ">" + value + "</" + name + ">\n");
        }
    }
    
    private void appendXML(String name, List values, StringBuffer out) {
        for (int i=0; i<values.size(); i++) {
	    appendXML(name, (String) values.get(i), out);
	}
    }

    private void appendXML(String name, Date dt, SimpleDateFormat formatter, 
            StringBuffer out) {
        appendXML(name, formatter.format(dt), out);
    }
    
    private String getList(List l) {
        StringBuffer ret=new StringBuffer();
        for (int i=0; i<l.size(); i++) {
            if (i>0) {
                ret.append(", ");
            }
            ret.append((String) l.get(i));
        }
        return ret.toString();
    }

    /** Exactly the same behavior as doGet. */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /** Gets the Fedora Server instance. */
    public void init() throws ServletException {
        try {
            s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
            s_access=(Access) s_server.getModule("fedora.server.access.Access");
        } catch (InitializationException ie) {
            throw new ServletException("Error getting Fedora Server instance: "
                    + ie.getMessage());
        }
    }
    
    private Server getServer() {
        return s_server;
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
