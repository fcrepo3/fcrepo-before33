package fedora.server.access;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private String[] getFieldsArray(HttpServletRequest req) {
        ArrayList l=new ArrayList();
        if ( (req.getParameter("pid")!=null) && (req.getParameter("pid").equalsIgnoreCase("true")) ) l.add("pid");
        if ( (req.getParameter("label")!=null) && (req.getParameter("label").equalsIgnoreCase("true")) ) l.add("label");
        if ( (req.getParameter("fType")!=null) && (req.getParameter("fType").equalsIgnoreCase("true")) ) l.add("fType");
        if ( (req.getParameter("cModel")!=null) && (req.getParameter("cModel").equalsIgnoreCase("true")) ) l.add("cModel");
        if ( (req.getParameter("state")!=null) && (req.getParameter("state").equalsIgnoreCase("true")) ) l.add("state");
        if ( (req.getParameter("locker")!=null) && (req.getParameter("locker").equalsIgnoreCase("true")) ) l.add("locker");
        if ( (req.getParameter("cDate")!=null) && (req.getParameter("cDate").equalsIgnoreCase("true")) ) l.add("cDate");
        if ( (req.getParameter("mDate")!=null) && (req.getParameter("mDate").equalsIgnoreCase("true")) ) l.add("mDate");
        if ( (req.getParameter("dcmDate")!=null) && (req.getParameter("dcmDate").equalsIgnoreCase("true")) ) l.add("dcmDate");
        if ( (req.getParameter("title")!=null) && (req.getParameter("title").equalsIgnoreCase("true")) ) l.add("title");
        if ( (req.getParameter("creator")!=null) && (req.getParameter("creator").equalsIgnoreCase("true")) ) l.add("creator");
        if ( (req.getParameter("subject")!=null) && (req.getParameter("subject").equalsIgnoreCase("true")) ) l.add("subject");
        if ( (req.getParameter("description")!=null) && (req.getParameter("description").equalsIgnoreCase("true")) ) l.add("description");
        if ( (req.getParameter("publisher")!=null) && (req.getParameter("publisher").equalsIgnoreCase("true")) ) l.add("publisher");
        if ( (req.getParameter("contributor")!=null) && (req.getParameter("contributor").equalsIgnoreCase("true")) ) l.add("contributor");
        if ( (req.getParameter("date")!=null) && (req.getParameter("date").equalsIgnoreCase("true")) ) l.add("date");
        if ( (req.getParameter("type")!=null) && (req.getParameter("type").equalsIgnoreCase("true")) ) l.add("type");
        if ( (req.getParameter("format")!=null) && (req.getParameter("format").equalsIgnoreCase("true")) ) l.add("format");
        if ( (req.getParameter("identifier")!=null) && (req.getParameter("identifier").equalsIgnoreCase("true")) ) l.add("identifier");
        if ( (req.getParameter("source")!=null) && (req.getParameter("source").equalsIgnoreCase("true")) ) l.add("source");
        if ( (req.getParameter("language")!=null) && (req.getParameter("language").equalsIgnoreCase("true")) ) l.add("language");
        if ( (req.getParameter("relation")!=null) && (req.getParameter("relation").equalsIgnoreCase("true")) ) l.add("relation");
        if ( (req.getParameter("coverage")!=null) && (req.getParameter("coverage").equalsIgnoreCase("true")) ) l.add("coverage");
        if ( (req.getParameter("rights")!=null) && (req.getParameter("rights").equalsIgnoreCase("true")) ) l.add("rights");
        String[] ret=new String[l.size()];
        for (int i=0; i<l.size(); i++) 
           ret[i]=(String) l.get(i);
        return ret;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HashMap h=new HashMap();
            h.put("application", "apia");
            h.put("useCachedObject", "true");
            h.put("userId", "fedoraAdmin");
            h.put("host", request.getRemoteAddr());
            ReadOnlyContext context = new ReadOnlyContext(h);

            String[] fieldsArray=getFieldsArray(request);
            HashSet fieldHash=new HashSet();
            if (fieldsArray!=null) {
                for (int i=0; i<fieldsArray.length; i++) {
                    fieldHash.add(fieldsArray[i]);
                }
            }
            String terms=request.getParameter("terms");
            String query=request.getParameter("query");
            
            String sessionToken=request.getParameter("sessionToken");

            // default to 25 if not specified or specified incorrectly
            int maxResults=25;
            if (request.getParameter("maxResults")!=null) {
                try {
                    maxResults=Integer.parseInt(request.getParameter("maxResults"));
                } catch (NumberFormatException nfe) {
                }
            }
            
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
                html.append("<input type=\"checkbox\" name=\"pid\" value=\"true\"" + (fieldHash.contains("pid") ? " checked" : "") + "> Pid<br>");
                html.append("<input type=\"checkbox\" name=\"label\" value=\"true\"" + (fieldHash.contains("label") ? " checked" : "") + "> Label<br>");
                html.append("<input type=\"checkbox\" name=\"fType\" value=\"true\"" + (fieldHash.contains("fType") ? " checked" : "") + "> Fedora Object Type<br>");
                html.append("<input type=\"checkbox\" name=\"cModel\" value=\"true\"" + (fieldHash.contains("cModel") ? " checked" : "") + "> Content Model<br>");
                html.append("<input type=\"checkbox\" name=\"state\" value=\"true\"" + (fieldHash.contains("state") ? " checked" : "") + "> State<br>");
                html.append("<input type=\"checkbox\" name=\"locker\" value=\"true\"" + (fieldHash.contains("locker") ? " checked" : "") + "> Locking User<br>");
                html.append("<input type=\"checkbox\" name=\"cDate\" value=\"true\"" + (fieldHash.contains("cDate") ? " checked" : "") + "> Creation Date<br>");
                html.append("<input type=\"checkbox\" name=\"mDate\" value=\"true\"" + (fieldHash.contains("mDate") ? " checked" : "") + "> Last Modified Date<br>");
                html.append("<input type=\"checkbox\" name=\"dcmDate\" value=\"true\"" + (fieldHash.contains("dcmDate") ? " checked" : "") + "> Dublin Core Modified Date<br>");
                html.append("<input type=\"checkbox\" name=\"bDef\" value=\"true\"" + (fieldHash.contains("bDef") ? " checked" : "") + "> Behavior Definition PID<br>");
                html.append("<input type=\"checkbox\" name=\"bMech\" value=\"true\"" + (fieldHash.contains("bMech") ? " checked" : "") + "> Behavior Mechanism PID<br>");
                html.append("</td><td width=18% valign=top>");
                html.append("<input type=\"checkbox\" name=\"title\" value=\"true\"" + (fieldHash.contains("title") ? " checked" : "") + "> Title<br>");
                html.append("<input type=\"checkbox\" name=\"creator\" value=\"true\"" + (fieldHash.contains("creator") ? " checked" : "") + "> Creator<br>");
                html.append("<input type=\"checkbox\" name=\"subject\" value=\"true\"" + (fieldHash.contains("subject") ? " checked" : "") + "> Subject<br>");
                html.append("<input type=\"checkbox\" name=\"description\" value=\"true\"" + (fieldHash.contains("description") ? " checked" : "") + "> Description<br>");
                html.append("<input type=\"checkbox\" name=\"publisher\" value=\"true\"" + (fieldHash.contains("publisher") ? " checked" : "") + "> Publisher<br>");
                html.append("<input type=\"checkbox\" name=\"contributor\" value=\"true\"" + (fieldHash.contains("contributor") ? " checked" : "") + "> Contributor<br>");
                html.append("<input type=\"checkbox\" name=\"date\" value=\"true\"" + (fieldHash.contains("date") ? " checked" : "") + "> Date<br>");
                html.append("<input type=\"checkbox\" name=\"type\" value=\"true\"" + (fieldHash.contains("type") ? " checked" : "") + "> Type<br>");
                html.append("<input type=\"checkbox\" name=\"format\" value=\"true\"" + (fieldHash.contains("format") ? " checked" : "") + "> Format<br>");
                html.append("<input type=\"checkbox\" name=\"identifier\" value=\"true\"" + (fieldHash.contains("identifier") ? " checked" : "") + "> Identifier<br>");
                html.append("<input type=\"checkbox\" name=\"source\" value=\"true\"" + (fieldHash.contains("source") ? " checked" : "") + "> Source<br>");
                html.append("<input type=\"checkbox\" name=\"language\" value=\"true\"" + (fieldHash.contains("language") ? " checked" : "") + "> Language<br>");
                html.append("<input type=\"checkbox\" name=\"relation\" value=\"true\"" + (fieldHash.contains("relation") ? " checked" : "") + "> Relation<br>");
                html.append("<input type=\"checkbox\" name=\"coverage\" value=\"true\"" + (fieldHash.contains("coverage") ? " checked" : "") + "> Coverage<br>");
                html.append("<input type=\"checkbox\" name=\"rights\" value=\"true\"" + (fieldHash.contains("rights") ? " checked" : "") + "> Rights<br>");
                html.append("</td><td width=64% valign=top>");
                html.append("Simple Query: <input type=\"text\" name=\"terms\" size=\"15\" value=\"" + (terms==null ? "" : StreamUtility.enc(terms)) + "\"><p> ");
                html.append("Advanced Query: <input type=\"text\" name=\"query\" size=\"15\" value=\"" + (query==null ? "" : StreamUtility.enc(query)) + "\"><p> ");
                html.append("Maximum Results: <select name=\"maxResults\"><option value=\"20\">20</option><option value=\"40\">40</option><option value=\"60\">60</option><option value=\"80\">80</option></select><p>&nbsp;</p> ");
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
            if ((fieldsArray!=null && fieldsArray.length>0) || (sessionToken!=null)) {
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
                        appendXML("pid", f.getPid(), xmlBuf);
                        appendXML("label", f.getLabel(), xmlBuf);
                        appendXML("fType", f.getFType(), xmlBuf);
                        appendXML("cModel", f.getCModel(), xmlBuf);
                        appendXML("state", f.getState(), xmlBuf);
                        appendXML("locker", f.getLocker(), xmlBuf);
                        appendXML("cDate", f.getCDate(), formatter, xmlBuf);
                        appendXML("mDate", f.getMDate(), formatter, xmlBuf);
                        appendXML("dcmDate", f.getDCMDate(), formatter, xmlBuf);
                        appendXML("bDef", f.bDefs(), xmlBuf);
                        appendXML("bMech", f.bMechs(), xmlBuf);
                        appendXML("title", f.titles(), xmlBuf);
                        appendXML("creator", f.creators(), xmlBuf);
                        appendXML("subject", f.subjects(), xmlBuf);
                        appendXML("description", f.descriptions(), xmlBuf);
                        appendXML("publisher", f.publishers(), xmlBuf);
                        appendXML("contributor", f.contributors(), xmlBuf); 
                        appendXML("date", f.dates(), xmlBuf); 
                        appendXML("type", f.types(), xmlBuf);
                        appendXML("format", f.formats(), xmlBuf);
                        appendXML("identifier", f.identifiers(), xmlBuf);
                        appendXML("source", f.sources(), xmlBuf);
                        appendXML("language", f.languages(), xmlBuf);
                        appendXML("relation", f.relations(), xmlBuf);
                        appendXML("coverage", f.coverages(), xmlBuf);
                        appendXML("rights", f.rights(), xmlBuf);
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
                                    html.append(StreamUtility.enc(f.getLabel()));
                                }
                            } else if (l.equalsIgnoreCase("fType")) {
                                html.append(f.getFType());
                            } else if (l.equalsIgnoreCase("cModel")) {
                                if (f.getCModel()!=null) {
                                    html.append(StreamUtility.enc(f.getCModel()));
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
                        if (fieldHash.contains("pid")) html.append("<input type=\"hidden\" name=\"pid\" value=\"true\">");
                        if (fieldHash.contains("label")) html.append("<input type=\"hidden\" name=\"label\" value=\"true\">");
                        if (fieldHash.contains("fType")) html.append("<input type=\"hidden\" name=\"fType\" value=\"true\">");
                        if (fieldHash.contains("cModel")) html.append("<input type=\"hidden\" name=\"cModel\" value=\"true\">");
                        if (fieldHash.contains("state")) html.append("<input type=\"hidden\" name=\"state\" value=\"true\">");
                        if (fieldHash.contains("locker")) html.append("<input type=\"hidden\" name=\"locker\" value=\"true\">");
                        if (fieldHash.contains("cDate")) html.append("<input type=\"hidden\" name=\"cDate\" value=\"true\">");
                        if (fieldHash.contains("mDate")) html.append("<input type=\"hidden\" name=\"mDate\" value=\"true\">");
                        if (fieldHash.contains("dcmDate")) html.append("<input type=\"hidden\" name=\"dcmDate\" value=\"true\">");
                        if (fieldHash.contains("bDef")) html.append("<input type=\"hidden\" name=\"bDef\" value=\"true\">");
                        if (fieldHash.contains("bMech")) html.append("<input type=\"hidden\" name=\"bMech\" value=\"true\">");
                        if (fieldHash.contains("title")) html.append("<input type=\"hidden\" name=\"title\" value=\"true\">");
                        if (fieldHash.contains("creator")) html.append("<input type=\"hidden\" name=\"creator\" value=\"true\">");
                        if (fieldHash.contains("subject")) html.append("<input type=\"hidden\" name=\"subject\" value=\"true\">");
                        if (fieldHash.contains("description")) html.append("<input type=\"hidden\" name=\"description\" value=\"true\">");
                        if (fieldHash.contains("publisher")) html.append("<input type=\"hidden\" name=\"publisher\" value=\"true\">");
                        if (fieldHash.contains("contributor")) html.append("<input type=\"hidden\" name=\"contributor\" value=\"true\">");
                        if (fieldHash.contains("date")) html.append("<input type=\"hidden\" name=\"date\" value=\"true\">");
                        if (fieldHash.contains("type")) html.append("<input type=\"hidden\" name=\"type\" value=\"true\">");
                        if (fieldHash.contains("format")) html.append("<input type=\"hidden\" name=\"format\" value=\"true\">");
                        if (fieldHash.contains("identifier")) html.append("<input type=\"hidden\" name=\"identifier\" value=\"true\">");
                        if (fieldHash.contains("source")) html.append("<input type=\"hidden\" name=\"source\" value=\"true\">");
                        if (fieldHash.contains("language")) html.append("<input type=\"hidden\" name=\"language\" value=\"true\">");
                        if (fieldHash.contains("relation")) html.append("<input type=\"hidden\" name=\"relation\" value=\"true\">");
                        if (fieldHash.contains("coverage")) html.append("<input type=\"hidden\" name=\"coverage\" value=\"true\">");
                        if (fieldHash.contains("rights")) html.append("<input type=\"hidden\" name=\"rights\" value=\"true\">");
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
		out.println("<result xmlns=\"http://www.fedora.info/definitions/1/0/types/\">");
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
            out.append("      <" + name + ">" + StreamUtility.enc(value) + "</" + name + ">\n");
        }
    }
    
    private void appendXML(String name, List values, StringBuffer out) {
        for (int i=0; i<values.size(); i++) {
	    appendXML(name, (String) values.get(i), out);
	}
    }

    private void appendXML(String name, Date dt, SimpleDateFormat formatter, 
            StringBuffer out) {
        if(dt!=null) appendXML(name, formatter.format(dt), out);
    }
    
    private String getList(List l) {
        StringBuffer ret=new StringBuffer();
        for (int i=0; i<l.size(); i++) {
            if (i>0) {
                ret.append(", ");
            }
            ret.append(StreamUtility.enc((String) l.get(i)));
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
