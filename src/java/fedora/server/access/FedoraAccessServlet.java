package fedora.server.access;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.net.URLDecoder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.util.Properties;

import com.icl.saxon.expr.StringValue;

import fedora.server.access.dissemination.DisseminationService;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ServerException;
import fedora.server.Context;
import fedora.server.ReadOnlyContext;
import fedora.server.Server;
import fedora.server.storage.DOManager;
import fedora.server.storage.types.DisseminationBindingInfo;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.Property;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.utilities.DateUtility;

/**
 * <p>Title: FedoraAccessServlet.java</p>
 * <p>Description: Implements Fedora Access LITE (API-A-LITE) interface via a
 * java Servlet front end. The syntax defined by API-A-LITE has two bindings:
 * <ol>
 * <li>http://hostname:port/PID/bDefPID/methodName[/dateTime][?parmArray] - this
 *     syntax requests a dissemination of the specified object using the
 *     specified method of the associated behavior definition object. The result
 *     is returned as a MIME-typed stream.</li>
 * <ul>
 * <li>hostname - required hostname of the Fedora server.</li>
 * <li>port - required port number on which the Fedora server is running.</li>
 * <li>PID - required persistent idenitifer of the digital object.</li>
 * <li>bDefPID - required persistent identifier of the digital object's associated
 *     behavior definition object.</li>
 * <li>methodName - required name of the method to be executed</li>
 * <li>dateTime - optional dateTime value indicating dissemination of a
 *     version of the digital object at the specified point in time. (NOT
 *     implemented in current version.)
 * <li>parmArray - optional array of method parameters consisting of
 *     name/value pairs in the form parm1=value1&parm2=value2...</li>
 * <li>clearCache_ - optional parameter that signals whether the dissemination
 *     cache is to be cleared; value of "yes" clears cache; value of "no" or
 *     omission does not clear cache.</li>
 * </ul>
 * <li>http://hostname:port/PID[/dateTime][?xmlEncode_=BOOLEAN] - this syntax
 *     requests a list all methods associated with the specified digital
 *     object. The xmlEncode parameter determines the type of output returned.
 *     If the parameter is onitted or has a value of "no", a MIME-typed stream
 *     consisting of html is returned providing a browser-savvy means of
 *     browsing the object's methods. If the value specified is "yes", then
 *     a MIME-typed stream consisting of XML is returned.</li>
 * <ul>
 * <li>hostname - required hostname of the Fedora server.</li>
 * <li>port - required port number on which the Fedora server is running.</li>
 * <li>PID - required persistent identifier of the digital object.</li>
 * <li>dateTime - optional dateTime value indicating dissemination of a
 *     version of the digital object at the specified point in time. (NOT
 *     implemented in current version.)
 * <li>xmlEncode_ - an optional parameter indicating the requested output format.
 *     Value of "yes" indicates raw xml; the absence of the xmlEncode parameter
 *     or a value of "no" indicates format is to be html.</li>
 * </ul>
 * <i><b>Note that the clearCache_ parameter name ends with the underscore
 * character ("_"). This is done to avoid possible name clashes with
 * user-supplied method parameter names that may be in the dissemination
 * request. As a general rule, user-supplied parameters should never contain
 * names that end with the underscore character to prevent possible name
 * clashes with the servlet.</b></i>
 * <p>If a dissemination request is successful, it is placed into the
 * dissemination cache which has a default size of 100. This default can be
 * changed by setting the <code>disseminationCacheSize</code> parameter in
 * the <code>fedora.fcfg</code> configuration file. If this parameter is not
 * present or cannot be parsed, the cache size will default to 100.</p><p>This
 * is a crude form of caching. A more robust cache will be needed to enhance
 * performance as the number of dissemination requests becomes very large.</p>
 * </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class FedoraAccessServlet extends HttpServlet
{
  /** Content type for html. */
  private static final String CONTENT_TYPE_HTML = "text/html";

  /** Content type for xml. */
  private static final String CONTENT_TYPE_XML  = "text/xml";

  /** Debug toggle for testing */
  private static boolean debug = false;

  /** Dissemination cache size. */
  private static int DISS_CACHE_SIZE = 100;

  /** Dissemination cache. */
  private Hashtable disseminationCache = new Hashtable();

  /** The Fedora Server instance */
  private static Server s_server = null;

  /** Instance of the access subsystem */
  private static Access s_access = null;

  /** Constant indicating value of the string "yes". */
  private static final String YES = "yes";

  /** Instance of DOManager. */
  private static DOManager m_manager = null;

  /** Full URL for the parameter resolver servlet. Hostname and port
   *  are determined dynamically and are assumed to be the same as
   *  for this servlet.
   */
  private static String PARAMETER_RESOLVER_URL = null;

  /** Servlet path for the parameter resolver servlet */
  private static final String PARAMETER_RESOLVER_SERVLET_PATH =
      "/fedora/getAccessParmResolver?";

  private HttpSession session = null;
  private Hashtable h_userParms = new Hashtable();
  private String requestURL = null;
  private String requestURI = null;
  private URLDecoder decoder = new URLDecoder();

  /** Make sure we have a server instance. */
  static
  {
    try
    {
      //FIXME!! - need to think about most appropriate place for dissemination
      // cache size parameter in config file; for now, put at top level.
      s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
      Integer I1 = new Integer(s_server.getParameter("disseminationCacheSize"));
      DISS_CACHE_SIZE = I1.intValue();
      s_server.logInfo("Dissemination cache size: " + DISS_CACHE_SIZE);
      Boolean B1 = new Boolean(s_server.getParameter("debug"));
      debug = B1.booleanValue();
      m_manager=(DOManager) s_server.getModule(
              "fedora.server.storage.DOManager");
      s_access =
              (Access) s_server.getModule("fedora.server.access.Access");
    } catch (InitializationException ie)
    {
      System.err.println(ie.getMessage());
    } catch (NumberFormatException nfe)
    {
      System.err.println("disseminationCacheSize parameter not found. Cache" +
                         "size set to 100." + nfe.getMessage());
    }
  }

  /**
   * <p>Process Fedora Access Request. Parse and validate the servlet input
   * parameters and then execute the specified request.</p>
   *
   * @param request  The servlet request.
   * @param response servlet The servlet response.
   * @throws ServletException If an error occurs that effects the servlet's
   *         basic operation.
   * @throws IOException If an error occurrs with an input or output operation.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    String PID = null;
    String bDefPID = null;
    String methodName = null;
    Calendar asOfDate = null;
    Date versDateTime = null;
    String action = null;
    String clearCache = null;
    Property[] userParms = null;
    long servletStartTime = new Date().getTime();
    boolean isGetObjectMethodsRequest = false;
    boolean isGetDisseminationRequest = false;
    PARAMETER_RESOLVER_URL = "http://" + request.getServerName()
        + ":" + request.getServerPort() + PARAMETER_RESOLVER_SERVLET_PATH;

    HashMap h=new HashMap();
    h.put("application", "apia");
    h.put("useCachedObject", "true");
    h.put("userId", "fedoraAdmin");
    h.put("host", request.getRemoteAddr());
    ReadOnlyContext context = new ReadOnlyContext(h);

    requestURL = request.getRequestURL().toString()+"?";
    requestURI = requestURL+request.getQueryString();

    // Parse servlet URL.
    StringBuffer servletURL = request.getRequestURL();
    String[] URIArray = servletURL.toString().split("/");
    if (URIArray.length == 6 || URIArray.length == 7)
    {
      PID = decoder.decode(URIArray[5], "UTF-8");
      if (URIArray.length > 6)
      {
        versDateTime = DateUtility.convertStringToDate(URIArray[6]);
      }
      s_server.logFinest("[FedoraAccessServlet] GetObjectMethods Syntax "
          + "Encountered: "+ requestURI);
      s_server.logFinest("PID: " + PID + "\nbDefPID: "
          + "\nasOfDate: " + versDateTime);
      isGetObjectMethodsRequest = true;

    } else if (URIArray.length > 7)
    {
      PID = decoder.decode(URIArray[5],"UTF-8");
      bDefPID = decoder.decode(URIArray[6],"UTF-8");
      methodName = URIArray[7];
      if (URIArray.length > 8)
      {
        versDateTime = DateUtility.convertStringToDate(URIArray[8]);
      }
      s_server.logFinest("[FedoraAccessServlet] Dissemination Syntax "
          + "Ecountered");
      s_server.logFinest("PID: " + PID + "\nbDefPID: " + bDefPID
          + "\nmethodName: " + methodName + "\nasOfDate: " + versDateTime);
      isGetDisseminationRequest = true;

    } else
    {
      PrintWriter out = response.getWriter();
      out.println("<br>Dissemination Request Syntax Error: Required fields "
          + "are missing in the Dissemination Request. The expected syntax "
          + "is:<br>"+URIArray[0]+"//"+URIArray[2]+URIArray[3]+URIArray[4]
          + "/PID/bDefPID/methodName[/dateTime][?ParmArray]<br>"
          + "Submitted request was: " + requestURI);
      return;
    }

    // Separate out servlet parameters from method parameters
    Hashtable h_userParms = new Hashtable();
    String xmlEncode = "no";
    for ( Enumeration e = request.getParameterNames(); e.hasMoreElements();)
    {
      String name = (String)e.nextElement();
      if (name.equalsIgnoreCase("xmlEncode"))
      {
        xmlEncode = request.getParameter(name);
      } else if (name.equalsIgnoreCase("clearCache_"))
      {
        clearCache = request.getParameter(name);
      } else
      {
        String value = request.getParameter(name);
        h_userParms.put(name,value);
      }
    }

    // API-A interface requires user-supplied parameters to be of type
    // Property[] so create Property[] from hashtable of user parameters.
    int userParmCounter = 0;
    userParms = new Property[h_userParms.size()];
    for ( Enumeration e = h_userParms.keys(); e.hasMoreElements();)
    {
      Property userParm = new Property();
      userParm.name = (String)e.nextElement();
      userParm.value = (String)h_userParms.get(userParm.name);
      userParms[userParmCounter] = userParm;
      userParmCounter++;
    }

    if (isGetObjectMethodsRequest)
    {
      getObjectMethods(context, PID, asOfDate, xmlEncode, request,
              response);
      long stopTime = new Date().getTime();
      long interval = stopTime - servletStartTime;
      System.out.println("[FedoraAccessServlet] Servlet Roundtrip "
          + "GetObjectMethods: " + interval + " milliseconds.");
    } else if (isGetDisseminationRequest)
    {
      getDissemination(context, PID, bDefPID, methodName, userParms, asOfDate,
                       clearCache, response);
      long stopTime = new Date().getTime();
      long interval = stopTime - servletStartTime;
      System.out.println("[FedoraAccessServlet] Servlet Roundtrip "
          + "GetDissemination: " + interval + " milliseconds.");
    }

  }

  /**
   * <p>This method calls the Fedora Access Subsystem to retrieve an array
   * of object method definitions for the digital object with the specified PID.
   * The array is then deserialized into XML. If the value of the xmlEncode
   * variable is set to "yes", the raw XML is returned. If the value of the
   * xmlEncode variable is   * set to "no" or is omitted in the URL request,
   * the XML is transformed by an XLT stylesheet into a web form.</p>
   *
   * @param context The read only context of the request.
   * @param PID The persistent identifier of the Digital Object.
   * @param asOfDateTime The version datetime stamp of the digital object.
   * @param xmlEncode The control flag that signals output format; Value of
   *        "yes" signals output is of MIME type text/xml; Value of "no" or
   *        omitted signals output as MIME type text/html.
   * @param request The servlet request.
   * @param response The servlet response.
   * @throws IOException If an error occurrs with an input or output operation.
   */
  public void getObjectMethods(Context context, String PID, Calendar asOfDateTime,
      String xmlEncode, HttpServletRequest request,
      HttpServletResponse response) throws IOException
  {

    PrintWriter out = response.getWriter();
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    ObjectMethodsDef[] objMethDefArray = null;
    String serverURI = request.getRequestURL().toString()+"?";

    try
    {
      objMethDefArray = s_access.getObjectMethods(context, PID, asOfDateTime);
      if (objMethDefArray != null)
      {
        // Object Methods found.
        // Deserialize ObjectmethodsDef datastructure into XML
        PipedWriter pw = new PipedWriter();
        PipedReader pr = new PipedReader(pw);
        pw.write("<?xml version=\"1.0\"?>");
        if (versDateTime == null || DateUtility.
            convertDateToString(versDateTime).equalsIgnoreCase(""))
        {
          pw.write("<object "
              + " targetNamespace=\"http://www.fedora.info/definitions/1/0/access/\""
              + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
              + " pid=\"" + PID + "\" >");
	  pw.write("<import namespace=\"http://www.fedora.info/definitions/1/0/access/\""
              + " location=\"objectmethods.xsd\"/>");
        } else
        {
          pw.write("<object "
              + " targetNamespace=\"http://www.fedora.info/definitions/1/0/access/\""
              + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
              + " pid=\"" + PID + "\""
              + " dateTime=\"" + DateUtility.convertDateToString(versDateTime)
              + "\" >");
          pw.write("<import namespace=\"http://www.fedora.info/definitions/1/0/access/\""
              + " location=\"objectmethods.xsd\"/>");

        }
        String nextBdef = "null";
        String currentBdef = "";
        for (int i=0; i<objMethDefArray.length; i++)
        {
          currentBdef = objMethDefArray[i].bDefPID;
          if (!currentBdef.equalsIgnoreCase(nextBdef))
          {
            if (i != 0) pw.write("</bdef>");
            pw.write("<bdef pid=\"" + objMethDefArray[i].bDefPID + "\" >");
          }
          pw.write("<method name=\"" + objMethDefArray[i].methodName + "\" >");
          MethodParmDef[] methodParms = objMethDefArray[i].methodParmDefs;
          for (int j=0; j<methodParms.length; j++)
          {
            pw.write("<parm parmName=\"" + methodParms[j].parmName
                + "\" parmDefaultValue=\"" + methodParms[j].parmDefaultValue
                + "\" parmRequired=\"" + methodParms[j].parmRequired
                + "\" parmType=\"" + methodParms[j].parmType
                + "\" parmLabel=\"" + methodParms[j].parmLabel + "\" >");
            if (!methodParms[j].parmDomainValues[0].equalsIgnoreCase("null"))
            {
              pw.write("<parmDomainValues>");
              for (int k=0; k<methodParms[j].parmDomainValues.length; k++)
              {
                pw.write("<value>" + methodParms[j].parmDomainValues[k]
                    + "</value>");
              }
              pw.write("</parmDomainValues>");
            }
            pw.write("</parm>");
          }

          pw.write("</method>");
          nextBdef = currentBdef;
        }
        pw.write("</bdef>");
        pw.write("</object>");
        pw.close();

        if (xmlEncode.equalsIgnoreCase(YES))
        {
          // Return results as raw XML
          response.setContentType(CONTENT_TYPE_XML);
          int c = 0;
          while ( (c = pr.read()) >= 0)
          {
            out.write(c);
          }
          pr.close();
        } else
        {
          // Transform results into an html table
          response.setContentType(CONTENT_TYPE_HTML);
          File xslFile = new File(s_server.getHomeDir(), "access/objectmethods.xslt");
          TransformerFactory factory = TransformerFactory.newInstance();
          Templates template = factory.newTemplates(new StreamSource(xslFile));
          Transformer transformer = template.newTransformer();
          Properties details = template.getOutputProperties();
          transformer.setParameter("serverURI", new StringValue(serverURI));
          transformer.setParameter("dummy", new StringValue("dummy"));
          transformer.transform(new StreamSource(pr), new StreamResult(out));
          pr.close();
        }

      } else
      {
        // Object Methods Definition request returned nothing.
        String message = "[FedoraAccessServlet] No Object Method Definitions "
           + "returned.";
        s_server.logFinest(message);
        System.err.println(message);
        showURLParms(PID, "", "", asOfDateTime,
                     new Property[0], "", response, message);
      }
    } catch (TransformerException e)
    {
      // FIXME!! Needs more refined Exception handling
      String message = "[FedoraAccessServlet] An error has occured in "
                     + "transforming the deserialized XML from getObjectMethods"
                     + " into html. The "
                     + "error was \" "
                     + e.getClass().getName()
                     + " \". Reason: "  + e.getMessage();
      s_server.logWarning(message);
      e.printStackTrace();
      showURLParms(PID, "", "", asOfDateTime,
                   new Property[0], "", response, message);
    } catch (ServerException se)
    {
      String message = "[FedoraAccessServlet] An error has occured in accessing"
                     + " the Fedora Access Subsystem. The error was \" "
                     + se.getClass().getName()
                     + " \". Reason: "  + se.getMessage();
      s_server.logWarning(message);
      se.printStackTrace();
      showURLParms(PID, "", "", asOfDateTime,
                   new Property[0], "", response, message);
    }
  }

  /**
   * <p>This method calls the Fedora Access Subsystem to retrieve a MIME-typed
   * stream corresponding to the dissemination request.</p>
   *
   * @param context The read only context of the request.
   * @param PID The persistent identifier of the Digital Object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param methodName The method name.
   * @param userParms An array of user-supplied method parameters.
   * @param asOfDateTime The version datetime stamp of the digital object.
   * @param clearCache The dissemination cache flag.
   * @param response The servlet response.
   * @throws IOException If an error occurrs with an input or output operation.
   */
  public void getDissemination(Context context, String PID, String bDefPID, String methodName,
      Property[] userParms, Calendar asOfDateTime, String clearCache,
      HttpServletResponse response) throws IOException
  {
    PrintWriter out = response.getWriter();
    try
    {
      // See if dissemination request is in local cache
      MIMETypedStream dissemination = null;
      dissemination = getDisseminationFromCache(context, PID, bDefPID,
          methodName, userParms, asOfDateTime, clearCache, response);
      if (dissemination != null)
      {
        // Dissemination was successful;
        // Return MIMETypedStream back to browser client
        response.setContentType(dissemination.MIMEType);
        int byteStream = 0;
        ByteArrayInputStream dissemResult =
            new ByteArrayInputStream(dissemination.stream);
        while ((byteStream = dissemResult.read()) >= 0)
        {
          out.write(byteStream);
        }
    } else
    {
      // Dissemination request failed; echo back request parameter.
      String message = "[FedoraAccessServlet] No Dissemination Result "
          + " was returned.";
      showURLParms(PID, bDefPID, methodName, asOfDateTime, userParms,
                  clearCache, response, message);
      System.out.println(message);
      s_server.logWarning(message);
    }
    // FIXME!! Decide on exception handling
    } catch (Exception e)
    {
      String message = "[FedoraAccessServlet] An error has occured in "
                     + "getting a dissemination from cache. The error was \" "
                     + e.getClass().getName()
                     + " \". Reason: "  + e.getMessage();
      s_server.logWarning(message);
      e.printStackTrace();
      showURLParms(PID, bDefPID, methodName, asOfDateTime,
                   userParms, clearCache, response, message);
    }
  }

  /**
   * <p>For now, treat a HTTP POST request just like a GET request.</p>
   *
   * @param request The servet request.
   * @param response The servlet response.
   * @throws ServletException If thrown by <code>doGet</code>.
   * @throws IOException If thrown by <code>doGet</code>.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    doGet(request, response);
  }

  /**
   * <p>Initialize servlet.</p>
   *
   * @throws ServletException If the servet cannot be initialized.
   */
  public void init() throws ServletException
  {}

  /**
   * <p>Cleans up servlet resources.</p>
   */
  public void destroy()
  {}

  /**
   * <p>Instantiates a new dissemination cache.</p>
   */
  private synchronized void clearDisseminationCache()
  {
    disseminationCache = new Hashtable();
  }

  /**
   * <p>Gets dissemination from cache. This method attempts to retrieve
   * a dissemination from the cache. If found, the dissemination is
   * returned. If not found, this method calls <code>GetDissemination</code>
   * to get the dissemination. If the retrieval is successful, the
   * dissemination is added to the cache. The cache may be cleared by
   * setting the URL servlet parameter <code>clearCache</code> to a value
   * of "yes". The cache is also flushed when it reaches the limit
   * specified by <code>DISS_CACHE_SIZE</code>.</p>
   *
   * @param context The read only context of the request.
   * @param PID The persistent identifier of the Digital Object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param methodName The method name.
   * @param userParms An array of user-supplied method parameters.
   * @param asOfDateTime The version datetime stamp of the digital object.
   * @param clearCache The dissemination cache flag.
   * @param response The servlet response.
   * @return The MIME-typed stream containing dissemination result.
   * @throws IOException If an error occurrs with an input or output operation.
   */
  private synchronized MIMETypedStream getDisseminationFromCache(
      Context context, String PID, String bDefPID, String methodName,
      Property[] userParms, Calendar asOfDateTime, String clearCache,
      HttpServletResponse response) throws IOException
  {
    // Clear cache if size gets larger than DISS_CACHE_SIZE
    if ( (disseminationCache.size() > DISS_CACHE_SIZE) ||
         (clearCache != null && clearCache.equalsIgnoreCase(YES)) )
    {
      clearDisseminationCache();
    }
    MIMETypedStream disseminationResult = null;
    // See if dissemination request is in local cache
    disseminationResult = (MIMETypedStream)disseminationCache.get(requestURI);
    if (disseminationResult == null)
    {
      // Dissemination request NOT in local cache.
      // Try reading from relational database
      try
      {
        disseminationResult =
            s_access.getDissemination(context, PID, bDefPID, methodName,
                                      userParms, asOfDateTime);
      } catch (ServerException se)
      {

        response.setStatus(404);
        String message = "[FedoraAccessServlet] An error has occured in "
            + "accessing the Fedora Access Subsystem. The error was \" "
            + se.getClass().getName()
            + " \". Reason: "  + se.getMessage();
        response.sendError(404, message);
        s_server.logWarning(message);
        se.printStackTrace();
        showURLParms(PID, bDefPID, methodName, asOfDateTime,
                   userParms, clearCache, response, message);
      }
      if (disseminationResult != null)
      {
        // Dissemination request succeeded, so add to local cache
        // FIXME!! This is a crude cache. More robust caching will be needed
        // to achieve optimum performance as the number of requests gets large.
        disseminationCache.put(requestURI, disseminationResult);
        if (debug) s_server.logFinest("ADDED to CACHE: "+requestURI);
      }
      if (debug) s_server.logFinest("CACHE SIZE: "+disseminationCache.size());
    }
    return disseminationResult;
  }

  /**
   * <p>Displays a list of the servlet input parameters. This method is
   * generally called when a service request returns no data. Usually
   * this is a result of an incorrect spelling of either a required
   * URL parameter or in one of the user-supplied parameters. The output
   * from this method can be used to help verify the URL parameters
   * sent to the servlet and hopefully fix the problem.</p>
   *
   * @param PID The persistent identifier of the digital object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param methodName the name of the method.
   * @param asOfDateTime The version datetime stamp of the digital object.
   * @param userParms An array of user-supplied method parameters and values.
   * @param clearCache The dissemination cache flag.
   * @param response The servlet response.
   * @param message The message text to include at the top of the output page.
   * @throws IOException If an error occurrs with an input or output operation.
   */
  private void showURLParms(String PID, String bDefPID,
                           String methodName, Calendar asOfDateTime,
                           Property[] userParms, String clearCache,
                           HttpServletResponse response,
                           String message)
      throws IOException
  {

    String versDate = DateUtility.convertCalendarToString(asOfDateTime);
    if (debug) System.err.println("versdate: "+versDate);
    PrintWriter out = response.getWriter();
    response.setContentType(CONTENT_TYPE_HTML);

    // Display servlet input parameters
    out.println("<html>");
    out.println("<head>");
    out.println("<title>FedoraServlet</title>");
    out.println("</head>");
    out.println("<body>");
    out.println("<br></br><font size='+2'>" + message + "</font>");
    out.println("<br></br><font color='red'>Request Parameters</font>");
    out.println("<br></br>");
    out.println("<table cellpadding='5'>");
    out.println("<tr>");
    out.println("<td><font color='red'>PID_</td>");
    out.println("<td> = <td>" + PID + "</td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("<td><font color='red'>bDefPID_</td>");
    out.println("<td> = </td>");
    out.println("<td>" + bDefPID + "</td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("<td><font color='red'>methodName_</td>");
    out.println("<td> = </td>");
    out.println("<td>" + methodName + "</td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("<td><font color='red'>asOfDateTime_</td>");
    out.println("<td> = </td>");
    out.println("<td>" + versDate + "</td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("<td><font color='red'>clearCache_</td>");
    out.println("<td> = </td>");
    out.println("<td>" + clearCache + "</td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("<td colspan='5'><font size='+1' color='blue'>"+
                "Other Parameters Found:</font></td>");
    out.println("</tr>");
    out.println("<tr>");
    out.println("</tr>");

    // List user-supplied parameters if any
    if (userParms != null)
    {
    for (int i=0; i<userParms.length; i++)
    {
      out.println("<tr>");
      out.println("<td><font color='red'>" + userParms[i].name
                  + "</font></td>");
      out.println("<td> = </td>");
      out.println("<td>" + userParms[i].value + "</td>");
        out.println("</tr>");
    }
    }
    out.println("</table></center></font>");
    out.println("</body></html>");

    if (debug)
    {
      System.err.println("PID: " + PID + "\nbDefPID: " + bDefPID
                         + "\nmethodName: " + methodName);
      if (userParms != null)
      {
        for (int i=0; i<userParms.length; i++)
        {
          s_server.logFinest("userParm: " + userParms[i].name
              + "\nuserValue: "+userParms[i].value);
        }
      }
    }
  }
}