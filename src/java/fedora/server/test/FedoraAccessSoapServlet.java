package fedora.server.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.xml.namespace.QName;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import fedora.server.types.gen.MethodDef;
import fedora.server.types.gen.MethodParmDef;
import fedora.server.types.gen.MIMETypedStream;
import fedora.server.types.gen.ObjectMethodsDef;
import fedora.server.types.gen.Property;
import fedora.server.utilities.DateUtility;

import org.apache.axis.client.Service;
import org.apache.axis.client.Call;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;

/**
 * <p>Title: FedoraAccessSoapServlet.java</p>
 * <p>Description: An example of a web-based client that provides a front end
 * to the Fedora Access SOAP service. This servlet is designed to provide a
 * "browser centric" view of the Fedora Access interface. Return types from
 * the Fedora Access SOAP service are translated into a form suitable for
 * viewing with a web browser; in other words MIME-typed streams. Applications
 * that can readily handle SOAP requests and responses would most likely
 * communicate directly with the Fedora SOAP service rather than use a java
 * servlet as an intermediary. This servlet serves as an example of how to
 * construct a client that uses the Fedora Access API via SOAP.</p>
 *
 * <p>Input parameters for the servlet include:</p>
 * <ul>
 * <li>action_ name of Fedora service which must be one of the following:
 * <ol>
 * <li>GetBehaviorDefinitions - Gets list of Behavior Defintions</li>
 * <li>GetBehaviorMethods - Gets list of Behavior Methods</li>
 * <li>GetBehaviorMethodsAsWSDL - Gets Behavior Methods as XML</li>
 * <li>GetDissemination - Gets a dissemination result</li>
 * <li>GetObjectmethods - Gets a list of all Behavior Methods of an object.</li>
 * </ol>
 * <li>PID_ - persistent identifier of the digital object</li>
 * <li>bDefPID_ - persistent identifier of the Behavior Definiton object</li>
 * <li>methodName_ - name of the method</li>
 * <li>asOfDateTime_ - versioning datetime stamp</li>
 * <li>clearCache_ - signal to flush the dissemination cache; value of "yes"
 * will clear the cache.</li>
 * <li>userParms - behavior methods may require or provide optional parameters
 * that may be input as arguments to the method; these method parameters are
 * entered as name/value pairs like the other serlvet parameters. (optional)</li>
 * </ul>
 * <p><i><b>Note that all servlet parameter names that are implementation specific
 * end with the underscore character ("_"). This is done to avoid possible
 * name clashes with user-supplied method parameter names. As a general rule,
 * user-supplied parameters should never contain names that end with the
 * underscore character to prevent possible name conflicts.</b></i>
 * <p>If a dissemination request is successful, it is placed into the
 * dissemination cache which has a default size of 100.</p>
 * </ul>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class FedoraAccessSoapServlet extends HttpServlet
{

  /** Content type for html. */
  private static final String CONTENT_TYPE_HTML = "text/html";

  /** Content type for xml. */
  private static final String CONTENT_TYPE_XML  = "text/xml";

  /** Debug toggle for testing */
  private static final boolean debug = true;

  /** Dissemination cache size. */
  private static final int DISS_CACHE_SIZE = 100;

  /** Dissemination cache. */
  private Hashtable disseminationCache = new Hashtable();

  /** URI of Fedora API definitions. */
  private static final String FEDORA_API_URI =
      "http://www.fedora.info/definitions/1/0/api/";

  /** URI of Fedora Type definitions. */
  private static final String FEDORA_TYPE_URI =
      "http://www.fedora.info/definitions/1/0/types/";

  /** URI of Fedora Access SOAP service. */
  private static final String FEDORA_ACCESS_ENDPOINT =
      "http://localhost:8080/fedora/access/soap";

  /** GetBehaviorDefinitions service name. */
  private static final String GET_BEHAVIOR_DEFINITIONS =
      "GetBehaviorDefinitions";

  /** GetBehaviorMethods service name. */
  private static final String GET_BEHAVIOR_METHODS =
      "GetBehaviorMethods";

  /** GetBehaviorMethodsAsWSDL service name. */
  private static final String GET_BEHAVIOR_METHODS_AS_WSDL =
      "GetBehaviorMethodsAsWSDL";

  /** GetDissemination service name. */
  private static final String GET_DISSEMINATION =
      "GetDissemination";

  /** GetObjectMethods service name. */
  private static final String GET_OBJECT_METHODS =
      "GetObjectMethods";

  /** User-supplied method parameters from servlet URL. */
  private Hashtable h_userParms = null;

  /** The incoming request URL. */
  private String requestURL = null;

  /** The incoming request complete URI. */
  private String requestURI = null;

  /** Servlet session. */
  private HttpSession session = null;

  /** Constant indicating value of the string "yes". */
  private static final String YES = "yes";

  /**
   * <p>Process Fedora Access Request. Parse and validate the servlet input
   * parameters and then execute the specified request by calling the
   * appropriate Fedora Access SOAP service.</p>
   *
   * @param request  The servlet request.
   * @param response servlet The servlet response.
   * @throws ServletException If an error occurs that effects the servlet's
   *         basic operation.
   * @throws IOException If an error occurs with an input or output operation.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    Calendar asOfDateTime = null;
    Date versDateTime = null;
    String action = null;
    String bDefPID = null;
    String clearCache = null;
    String methodName = null;
    String PID = null;
    Property[] userParms = null;
    h_userParms = new Hashtable();

    // getRequestURL only available in Servlet API 2.3.
    // Use following for earlier releases servlet API:
    // requestURL = new String("http://" + request.getServerName()
    //                        + ":" + request.getServerPort()
    //                        + request.getRequestURI() + "?");
    requestURL = request.getRequestURL().toString()+"?";
    requestURI = new String(requestURL + request.getQueryString());
    if (debug) System.err.println("RequestURL: " + requestURL
                                  + "RequestURI: " + requestURI
                                  + "Session: " + session);

    //FIXME!! session management not yet implemented.
    session = request.getSession(true);
    PrintWriter out = response.getWriter();

    // Get servlet input parameters.
    Enumeration URLParms = request.getParameterNames();
    while ( URLParms.hasMoreElements())
    {
      String parm = (String) URLParms.nextElement();
      if (parm.equals("action_"))
      {
        action = request.getParameter(parm);
      } else if (parm.equals("PID_"))
      {
        PID = request.getParameter(parm);
      } else if (parm.equals("bDefPID_"))
      {
        bDefPID = request.getParameter(parm);
      } else if (parm.equals("methodName_"))
      {
        methodName = request.getParameter(parm);
      } else if (parm.equals("asOfDateTime_"))
      {
        asOfDateTime = DateUtility.
                   convertStringToCalendar(request.getParameter(parm));
      } else if (parm.equals("clearCache_"))
      {
        clearCache = request.getParameter(parm);
      } else
      {
        // Any remaining parameters are assumed to be user-supplied method
        // parameters. Place user-supplied parameters in hashtable for
        // easier access.
        h_userParms.put(parm, request.getParameter(parm));
      }
    }

    // API-A interface requires user-supplied parameters to be of type
    // Property[]; create Property[] from hashtable of user parameters.
    int userParmCounter = 0;
    if ( !h_userParms.isEmpty() )
    {
      userParms = new Property[h_userParms.size()];
      for ( Enumeration e = h_userParms.keys(); e.hasMoreElements();)
      {
        Property userParm = new Property();
        userParm.setName((String)e.nextElement());
        userParm.setValue((String)h_userParms.get(userParm.getName()));
        userParms[userParmCounter] = userParm;
        userParmCounter++;
      }
    }

    // Validate servlet URL parameters to verify that all parameters required
    // by the servlet are present and to verify that any other user-supplied
    // parameters are valid for the request.
    if (isValidURLParms(action, PID, bDefPID, methodName, versDateTime,
                      h_userParms, clearCache, response))
    {

      // Have valid request.
      if (action.equals(GET_BEHAVIOR_DEFINITIONS))
      {
        String[] behaviorDefs = null;
        try
        {
          // Call Fedora Access SOAP service to request Behavior Definitons.
          behaviorDefs = getBehaviorDefinitions(PID, asOfDateTime);
          if (behaviorDefs != null)
          {
            response.setContentType(CONTENT_TYPE_HTML);

            // Behavior Definitions found. Return HTML table containing results;
            // include links to digital object PID to further reflect on object.
            //
            // Note that what is returned by the Fedora Access SOAP service is
            // a data structure. In a browser-based environment, it makes more
            // sense to return something that is "browser-friendly" so the
            // returned datastructure is transformed into an html table. In a
            // nonbrowser-based environment, one would use the returned data
            // structures directly and most likely forgo this transformation
            // step.
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Behavior Definitions</title>");
            out.println("</head>");
            out.println("<br></br>");
            out.println("<center>");
            out.println("<table border='1' cellpadding='5'>");
            out.println("<tr>");
            out.println("<td><b><font size='+2'><b>PID</font></td></b>");
            out.println("<td><b><font size='+2'>Version Date</font></b></td>");
            out.println("<td><b><font size='+2'>Behavior Definitions</font>"
                        + "</b></td");
            out.println("</tr>");

            // Format table such that repeating fields display only once.
            int rows = behaviorDefs.length - 1;
            for (int i=0; i<behaviorDefs.length; i++)
            {
              out.println("<tr>");
              if (i == 0)
              {
                out.println("<td><font color='blue'><a href='" + requestURL
                            + "action_=GetObjectMethods&PID_=" + PID+ "'>" + PID
                            + "</a></font></td>");
                out.println("<td><font color='green'>"
                            + DateUtility.convertDateToString(versDateTime)
                            + "</font></td>");
                out.println("<td><font color='red'>" + behaviorDefs[i]
                            + "</font></td>");
              } else if (i == 1)
              {
                out.println("<td colspan='2' rowspan='" + rows
                            + "'></td><td><font color='red'>" + behaviorDefs[i]
                            + "</font></td>");
              } else
              {
                out.println("<td><font color='red'>" + behaviorDefs[i]
                            + "</font></td>");
              }
              out.println("</tr>");
            }
            out.println("</table>");
            out.println("</center>");
            out.println("<br></br>");
            out.println("</body>");
            out.println("</html>");
          } else
          {
            // Behavior Definition request returned nothing.
            String message = "FedoraSoapServlet: No Behavior Definitons "
                           + "returned.";
            System.err.println(message);
            showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                         userParms, clearCache, response, message);
          }

          // FIXME!! Needs more refined Exception handling.
        } catch (Exception e)
        {
          String message = "FedoraSoapServlet: Failed to get Behavior "
                         + "Definitions <br > Exception: "
                         + e.getClass().getName() + " <br> Reason: "
                         + e.getMessage();
          System.err.println(message);
          showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                       userParms, clearCache, response, message);
        }
      } else if (action.equals(GET_BEHAVIOR_METHODS))
      {
        MethodDef[] methodDefs = null;
        try
        {
          // Call Fedora Access SOAP service to request Method Definitions.
          methodDefs = getBehaviorMethods(PID, bDefPID, asOfDateTime);
          if (methodDefs != null)
          {
            // Method Definitions found; output HTML table of results
            // with links to each method enabling dissemination of the
            // method and a link to the object PID enabling further
            // reflection on the object.
            //
            // Note that what is returned by the Fedora Access SOAP service is
            // a data structure. In a browser-based environment, it makes more
            // sense to return something that is "browser-friendly" so the
            // returned datastructure is transformed into an html table. In a
            // nonbrowser-based environment, one would use the returned data
            // structures directly and most likely forgo this transformation
            // step.
            response.setContentType(CONTENT_TYPE_HTML);
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Behavior Methods</title>");
            out.println("</head>");
            out.println("<br></br>");
            out.println("<center>");
            out.println("<table border='1' cellpadding='5'>");
            out.println("<tr>");
            out.println("<td><b><font size='+2'> Object PID "
                        + " </font></b></td>");
            out.println("<td><b><font size='+2'> BDEF PID"
                        + " </font></b></td>");
            out.println("<td><b><font size='+2'> Version Date"
                        + " </font></b></td>");
            out.println("<td><b><font size='+2'> Method Name"
                        + " </font></b></td>");
            out.println("</tr>");

            // Format table such that repeating fields display only once.
            int rows = methodDefs.length - 1;
            for (int i=0; i<methodDefs.length; i++)
            {
              fedora.server.types.gen.MethodDef results = methodDefs[i];
              out.println("<tr>");
              if (i == 0)
              {
                out.println("<td><font color=\"blue\"> " + "<a href=\""
                            + requestURL + "action_=GetObjectMethods&PID_="
                            + PID + "\"> " + PID + " </a></font></td>");
                out.println("<td><font color=\"green\"> " + bDefPID
                            + " </font></td>");
                out.println("<td><font color=\"green\"> "
                            + DateUtility.convertDateToString(versDateTime)
                            + "</font></td>");
                out.println("<td><font color=\"red\"> " + "<a href=\""
                            + requestURL + "action_=GetDissemination&PID_="
                            + PID + "&bDefPID_=" + bDefPID + "&methodName_="
                            + results.getMethodName() + "\"> "
                            + results.getMethodName()
                            + " </a></td>");
              } else if (i == 1)
              {
                out.println("<td colspan='3' rowspan='" + rows + "'></td>");
                out.println("<td><font color=\"red\"> " + "<a href=\""
                            + requestURL + "action_=GetDissemination&PID_="
                            + PID + "&bDefPID_=" + bDefPID + "&methodName_="
                            + results.getMethodName() + "\"> "
                            + results.getMethodName()
                            + " </a></td>");
              } else
              {
                out.println("<td><font color=\"red\"> " + "<a href=\""
                            + requestURL + "action_=GetDissemination&PID_="
                            + PID + "&bDefPID_=" + bDefPID + "&methodName_="
                            + results.getMethodName() + "\"> "
                            + results.getMethodName()
                            + " </a></td>");
              }
              out.println("</tr>");
            }
            out.println("</table>");
            out.println("</center>");
            out.println("</body>");
            out.println("</html>");
          } else
          {
            // Method Definitions request returned nothing.
            String message = "FedoraSoapServlet: No Behavior Methods returned.";
            System.err.println(message);
            showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                         userParms, clearCache, response, message);
          }

          // FIXME!! Needs more refined Exception handling.
        } catch (Exception e)
        {
          String message = "FedoraSoapServlet: No Behavior Methods returned."
                         + " <br> Exception: " + e.getClass().getName()
                         + " <br> Reason: "  + e.getMessage();
          System.err.println(message);
          showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                       userParms, clearCache, response, message);
        }
      } else if (action.equalsIgnoreCase(GET_BEHAVIOR_METHODS_AS_WSDL))
      {
        MIMETypedStream methodDefs = null;
        try
        {
          // Call Fedora Access SOAP service to request Method Definitions
          // in WSDL form.
          methodDefs = getBehaviorMethodsAsWSDL(PID, bDefPID, asOfDateTime);
          if (methodDefs != null)
          {
            // Method Definitions found; output resutls as XML.
            //
            // Note that what is returned by the Fedora Access SOAP service is
            // a data structure. In a browser-based environment, it makes more
            // sense to return something that is "browser-friendly" so the
            // returned datastructure is transformed into an html table. In a
            // nonbrowser-based environment, one would use the returned data
            // structures directly and most likely forgo this transformation
            // step.
            ByteArrayInputStream methodResults =
                new ByteArrayInputStream(methodDefs.getStream());
            response.setContentType(methodDefs.getMIMEType());

            // FIXME!! Namespacing info should be handled by
            // DefinitiveBMechReader; do it here for now.
            out.println("<?xml version=\"1.0\"?>");
            out.println("<definitions " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema-instance\" "+
                "xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" "+
                "xmlns:http=\"http://schemas.xmlsoap.org/wsdl/http/\" "+
                "xmlns:mime=\"http://schemas.xmlsoap.org/wsdl/mime/\" "+
                "xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\">");
            int byteStream = 0;
            while ((byteStream = methodResults.read()) >= 0)
            {
              out.write(byteStream);
            }
            out.println("</definitions>");
          } else
          {
            // Method Definition request in WSDL form returned nothing.
            String message = "FedoraSoapServlet: No Behavior Methods returned "
                + "as WSDL.";
            System.err.println(message);
            showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                         userParms, clearCache, response, message);
          }
        } catch (Exception e)
        {
          // FIXME!! Needs more refined Exception handling.
          String message = "FedoraSoapServlet: No Behavior Methods returned "
                         + "as WSDL. <br> Exception: " + e.getClass().getName()
                         + " <br> Reason: "  + e.getMessage();
          System.err.println(message);
          showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                       userParms, clearCache, response, message);
        }
      } else if (action.equals(GET_DISSEMINATION))
        {
          try
          {
            // Call Fedora Access SOAP service to request dissemination.
            MIMETypedStream dissemination = null;
            dissemination = getDisseminationFromCache(action, PID, bDefPID,
                methodName, userParms, asOfDateTime, clearCache, response);
            if (dissemination != null)
            {
              // Dissemination found. Output the mime-typed stream.
              //
              // Note that what is returned by the Fedora Access SOAP service is
              // a data structure. In a browser-based environment, it makes more
              // sense to return something that is "browser-friendly" so the
              // returned datastructure is written back to the serlvet response.
              // In a nonbrowser-based environment, one would use the returned
              // data structure directly and most likely forgo this
              // transformation step.
              response.setContentType(dissemination.getMIMEType());
              int byteStream = 0;
              ByteArrayInputStream dissemResult =
                  new ByteArrayInputStream(dissemination.getStream());
              while ((byteStream = dissemResult.read()) >= 0)
              {
                out.write(byteStream);
              }
              dissemResult.close();
            } else
            {
              // Dissemination request returned nothing.
              String message = "FedoraSoapServlet: No Dissemination result "
                  + "returned.";
              System.err.println(message);
              showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                           userParms, clearCache, response, message);
            }
          } catch (Exception e)
          {
            // FIXME!! Needs more refined Exception handling.
            String message = "FedoraSoapServlet: No Dissemination result "
                           + "returned. <br> Exception: "
                           + e.getClass().getName()
                           + " <br> Reason: "  + e.getMessage()
                           + " <br> See server logs for additional info";
            System.err.println(message);
            showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                         userParms, clearCache, response, message);
          }
      } else if (action.equals(GET_OBJECT_METHODS))
      {
        ObjectMethodsDef[] objMethDefArray = null;
        try
        {
          // Call Fedora Access SOAP service to request Object Methods.
          objMethDefArray = getObjectMethods(PID, asOfDateTime);
          if (objMethDefArray != null)
          {
            // Object Methods found. Ouptut HTML table containing all object
            // methods with links on each method enabling dissemination of
            // that particular method.
            //
            // Note that what is returned by the Fedora Access SOAP service is
            // a data structure. In a browser-based environment, it makes more
            // sense to return something that is "browser-friendly" so the
            // returned datastructure is transformed into an html table. In a
            // nonbrowser-based environment, one would use the returned data
            // structures directly and most likely forgo this transformation
            // step.
            response.setContentType(CONTENT_TYPE_HTML);
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Object Methods</title>");
            out.println("</head>");
            out.println("<br></br>");
            out.println("<center>");
            out.println("<table border='1' cellpadding='5' >");
            out.println("<tr>");
            out.println("<td><b><font size='+2'> Object PID "
                        + " </font></b></td>");
            out.println("<td><b><font size='+2'> Version Date"
                        + " </font></b></td>");
            out.println("<td><b><font size='+2'> BDEF PID"
                        + " </font></b></td>");
            out.println("<td><b><font size='+2'> Method Name"
                        + " </font></b></td>");
            out.println("</tr>");

            // Format table such that repeating fields only display once.
            int rows = objMethDefArray.length-1;
            for (int i=0; i<objMethDefArray.length; i++)
            {
              out.println("<tr>");
              if (i == 0)
              {
                out.println("<td><font color=\"blue\"> "
                            + objMethDefArray[i].getPID() + "</font></td>");
                out.flush();
                out.println("<td><font color=\"green\"> "
                            + versDateTime + " </font></td>");
                out.println("<td><font color=\"green\"> "
                            + objMethDefArray[i].getBDefPID()
                            + " </font></td>");
                out.println("<td><font color=\"red\"> "
                            + "<a href=\"" + requestURL
                            + "action_=GetDissemination&PID_="
                            + objMethDefArray[i].getPID() + "&bDefPID_="
                            + objMethDefArray[i].getBDefPID() + "&methodName_="
                            + objMethDefArray[i].getMethodName() + "\"> "
                            + objMethDefArray[i].getMethodName()
                            + " </a></td>");
              } else if (i == 1)
              {
                out.println("<td colspan='2' rowspan='" + rows + "'></td>");
                out.println("<td><font color=\"green\"> "
                            + objMethDefArray[i].getBDefPID()
                            + " </font></td>");
                out.println("<td><font color=\"red\"> "
                            + "<a href=\"" + requestURL
                            + "action_=GetDissemination&PID_="
                            + objMethDefArray[i].getPID() + "&bDefPID_="
                            + objMethDefArray[i].getBDefPID() + "&methodName_="
                            + objMethDefArray[i].getMethodName() + "\"> "
                            + objMethDefArray[i].getMethodName()
                            + " </a></td>");
              } else
              {
                out.println("<td><font color=\"green\"> "
                            + objMethDefArray[i].getBDefPID()
                            + " </font></td>");
                out.println("<td><font color=\"red\"> "
                            + "<a href=\"" + requestURL
                            + "action_=GetDissemination&PID_="
                            + objMethDefArray[i].getPID() + "&bDefPID_="
                            + objMethDefArray[i].getBDefPID() + "&methodName_="
                            + objMethDefArray[i].getMethodName() + "\"> "
                            + objMethDefArray[i].getMethodName()
                            + " </a></td>");
              }
              out.println("</tr>");
            }
            out.println("</table>");
            out.println("</center>");
            out.println("</body>");
            out.println("</html>");
          } else
          {
            // Object Methods Definition request returned nothing.
            String message = "FedoraSoapServlet: No Object Method Definitions "
               + "result returned.";
            System.err.println(message);
            showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                         userParms, clearCache, response, message);
          }
        } catch (Exception e)
        {
          // FIXME!! Needs more refined Exception handling
          String message = "FedoraSoapServlet: No Object Method Definitions "
                         + "result returned. <br> Exception: "
                         + e.getClass().getName()
                         + " <br> Reason: "  + e.getMessage();
          System.err.println(message);
          showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                       userParms, clearCache, response, message);
        }
      }
    }
  }

  /**
   * <p>For now, treat a HTTP POST request just like a GET request.</p>
   *
   * @param request The servet request.
   * @param response The servlet response.
   * @throws ServletException If thrown by <code>doGet</code>.
   * @throws IOException If thrown by <code>doGet</code>.
   * @throws ServletException If an error occurs that effects the servlet's
   *         basic operation.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
   doGet(request, response);
  }

  /**
   * <p>Gets a list of Behavior Definition object PIDs for the specified
   * digital object by invoking the appropriate Fedora Access SOAP service.</p>
   *
   * @param PID The persistent identifier of the digital object.
   * @param asOfDateTime The versioning datetime stamp.
   * @return An array of Behavior Definition PIDs.
   * @throws Exception If an error occurs in communicating with the Fedora
   *         Access SOAP service.
   */
  public String[] getBehaviorDefinitions(String PID, Calendar asOfDateTime)
      throws Exception
  {
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    Service service = new Service();
    Call call = (Call) service.createCall();
    call.setTargetEndpointAddress( new URL(FEDORA_ACCESS_ENDPOINT) );
    call.setOperationName(new QName(FEDORA_API_URI, GET_BEHAVIOR_DEFINITIONS) );
    String[] behaviorDefs = (String[]) call.invoke(new Object[] { PID,
          versDateTime });
    return behaviorDefs;
  }

  /**
   * <p>Gets a list of Behavior Methods associated with the specified
   * Behavior Mechanism object by invoking the appropriate Fedora Access
   * SOAP service.</p>
   *
   * @param PID The persistent identifier of Digital Object.
   * @param bDefPID The persistent identifier of Behavior Definition object.
   * @param asOfDateTime The versioning datetime stamp.
   * @return An array of method definitions.
   * @throws Exception If an error occurs in communicating with the Fedora
   *         Access SOAP service.
   */
  public MethodDef[] getBehaviorMethods(String PID,
      String bDefPID, Calendar asOfDateTime) throws Exception
  {
    MethodDef[] methodDefs = null;
    Service service = new Service();
    Call call = (Call) service.createCall();
    call.setOperationName(new QName(FEDORA_API_URI, GET_BEHAVIOR_METHODS) );
    QName qn = new QName(FEDORA_TYPE_URI, "MethodDef");
    QName qn2 = new QName(FEDORA_TYPE_URI, "MethodParmDef");
    call.setTargetEndpointAddress( new URL(FEDORA_ACCESS_ENDPOINT) );

    // Any Fedora-defined types required by the SOAP service must be registered
    // prior to invocation so the SOAP service knows the appropriate
    // serializer/deserializer to use for these types.
    call.registerTypeMapping(MethodDef.class, qn,
        new BeanSerializerFactory(MethodDef.class, qn),
        new BeanDeserializerFactory(MethodDef.class, qn));
    call.registerTypeMapping(MethodParmDef.class, qn2,
        new BeanSerializerFactory(MethodParmDef.class, qn2),
        new BeanDeserializerFactory(MethodParmDef.class, qn2));
    methodDefs = (MethodDef[]) call.invoke( new Object[] { PID,
          bDefPID, asOfDateTime} );
    return methodDefs;
  }

  /**
   * <p>Gets a bytestream containing the WSDL that defines the Behavior Methods
   * of the associated Behavior Mechanism object by invoking the appropriate
   * Fedora Access SOAP service.
   *
   * @param PID The persistent identifier of digital object.
   * @param bDefPID The persistent identifier of Behavior Definition object.
   * @param asOfDateTime The versioning datetime stamp.
   * @return MIME-typed stream containing XML-encoded method definitions
   *         from WSDL.
   * @throws Exception If an error occurs in communicating with the Fedora
   *         Access SOAP service.
   */
  public MIMETypedStream getBehaviorMethodsAsWSDL(
      String PID, String bDefPID, Calendar asOfDateTime) throws Exception
  {
    MIMETypedStream methodDefs = null;
    Service service = new Service();
    Call call = (Call) service.createCall();
    call.setOperationName(new QName(FEDORA_API_URI,
                                    GET_BEHAVIOR_METHODS_AS_WSDL) );
    QName qn = new QName(FEDORA_TYPE_URI, "MIMETypedStream");
    call.setTargetEndpointAddress( new URL(FEDORA_ACCESS_ENDPOINT) );

    // Any Fedora-defined types required by the SOAP service must be registered
    // prior to invocation so the SOAP service knows the appropriate
    // serializer/deserializer to use for these types.
    call.registerTypeMapping(MIMETypedStream.class, qn,
        new BeanSerializerFactory(MIMETypedStream.class, qn),
        new BeanDeserializerFactory(MIMETypedStream.class, qn));
    methodDefs = (MIMETypedStream)
                 call.invoke( new Object[] { PID, bDefPID, asOfDateTime} );
    return methodDefs;
  }

  /**
   * <p>Gets a MIME-typed bytestream containing the result of a dissemination
   * by invoking the appropriate Fedora Access SOAP service.
   *
   * @param PID The persistent identifier of the digital object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param methodName The name of the method.
   * @param asOfDateTime The version datetime stamp of the digital object.
   * @param userParms An array of user-supplied method parameters and values.
   * @return A MIME-typed stream containing the dissemination result.
   * @throws Exception If an error occurs in communicating with the Fedora
   *         Access SOAP service.
   */
  public MIMETypedStream getDissemination(String PID, String bDefPID,
      String methodName, Property[] userParms, Calendar asOfDateTime)
      throws Exception
   {
    // Generate a call to the Fedora SOAP service requesting the
    // GetDissemination method
    MIMETypedStream dissemination = null;
    Service service = new Service();
    Call call = (Call) service.createCall();
    call.setTargetEndpointAddress( new URL(FEDORA_ACCESS_ENDPOINT) );
    call.setOperationName(new QName(FEDORA_API_URI, GET_DISSEMINATION) );
    QName qn =  new QName(FEDORA_TYPE_URI, "MIMETypedStream");
    QName qn2 = new QName(FEDORA_TYPE_URI, "Property");

    // Any Fedora-defined types required by the SOAP service must be registered
    // prior to invocation so the SOAP service knows the appropriate
    // serializer/deserializer to use for these types.
    call.registerTypeMapping(MIMETypedStream.class, qn,
        new BeanSerializerFactory(MIMETypedStream.class, qn),
        new BeanDeserializerFactory(MIMETypedStream.class, qn));
    call.registerTypeMapping(fedora.server.types.gen.Property.class, qn2,
        new BeanSerializerFactory(Property.class, qn2),
        new BeanDeserializerFactory(Property.class, qn2));
    dissemination = (MIMETypedStream) call.invoke( new Object[] { PID, bDefPID,
        methodName, userParms, asOfDateTime} );
    return dissemination;
   }

   /**
    * <p>Gets a list of all method definitions for the specified object by
    * invoking the appropriate Fedora Access SOAP service.</p>
    *
    * @param PID The persistent identifier for the digital object.
    * @param asOfDateTime The versioning datetime stamp.
    * @return An array of object method definitions.
    * @throws Exception If an error occurs in communicating with the Fedora
    *         Access SOAP service.
    */
  public ObjectMethodsDef[] getObjectMethods(String PID,
      Calendar asOfDateTime) throws Exception
  {
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    ObjectMethodsDef[] objMethDefArray = null;
    Service service = new Service();
    Call call = (Call) service.createCall();
    call.setOperationName(new QName(FEDORA_API_URI, GET_OBJECT_METHODS) );
    QName qn = new QName(FEDORA_TYPE_URI, "ObjectMethodsDef");
    call.setTargetEndpointAddress( new URL(FEDORA_ACCESS_ENDPOINT) );

    // Any Fedora-defined types required by the SOAP service must be registered
    // prior to invocation so the SOAP service knows the appropriate
    // serializer/deserializer to use for these types.
    call.registerTypeMapping(ObjectMethodsDef.class, qn,
        new BeanSerializerFactory(ObjectMethodsDef.class, qn),
        new BeanDeserializerFactory(ObjectMethodsDef.class, qn));
    objMethDefArray =
        (ObjectMethodsDef[]) call.invoke( new Object[] { PID, asOfDateTime} );
    return objMethDefArray;
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
   * returned. If not found, this method calls <code>getDissemination</code>
   * to get the dissemination from the Fedora Access SOAP service. If the
   * retrieval is successful, the dissemination is added to the cache. The
   * cache may be manually cleared by setting the URL servlet parameter
   * <code>clearCache</code> to a value of "yes". The cache is also flushed
   * when it reaches the limit specified by <code>DISS_CACHE_SIZE</code>.</p>
   *
   * @param action The Fedora service requested.
   * @param PID The persistent identifier of the Digital Object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param methodName The method name.
   * @param userParms An array of user-supplied method parameters.
   * @param asOfDateTime The version datetime stamp of the digital object.
   * @param clearCache The dissemination cache flag.
   * @param response The servlet response.
   * @return The MIME-typed stream containing dissemination result.
   * @throws Exception If an error occurs in communicating with the Fedora
   *         Access SOAP service.
   */
  private synchronized MIMETypedStream getDisseminationFromCache(String action,
      String PID, String bDefPID, String methodName,
      Property[] userParms, Calendar asOfDateTime, String clearCache,
      HttpServletResponse response) throws Exception
  {
    // Clear cache if size gets larger than DISS_CACHE_SIZE
    if (disseminationCache.size() > DISS_CACHE_SIZE ||
        (clearCache != null && clearCache.equalsIgnoreCase(YES)))
    {
      clearDisseminationCache();
    }
    MIMETypedStream disseminationResult = null;
    // See if dissemination request is in local cache
    disseminationResult =
        (MIMETypedStream)disseminationCache.get(requestURI);
    if (disseminationResult == null)
    {
      // Dissemination request NOT in local cache.
      // Try retrieving using Fedora Access SOAP service
      disseminationResult = getDissemination(PID, bDefPID, methodName,
          userParms, asOfDateTime);
      if (disseminationResult != null)
      {
        // Dissemination request succeeded, so add to local cache
        disseminationCache.put(requestURI, disseminationResult);
         if (debug) System.err.println("ADDED to CACHE: "+requestURI);
      }
      if (debug) System.err.println("CACHE SIZE: "+disseminationCache.size());
    }
    return disseminationResult;
  }

  /**
   * <p>Validates required servlet URL parameters. Different parameters
   * are required based on the requested action.</p>
   *
   * @param action The Fedora service to be executed
   * @param PID The persistent identifier of the Digital Object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param methodName The method name.
   * @param versDateTime The version datetime stamp of the digital object.
   * @param h_userParms A hashtabe of user-supplied method parameters.
   * @param clearCache A boolean flag to clear dissemination cache.
   * @param response The servlet response.
   * @return True if required parameters are valid; false otherwise.
   * @throws IOException If an error occurrs with an input or output operation.
   */
  private boolean isValidURLParms(String action, String PID, String bDefPID,
                          String methodName, Date versDateTime,
                          Hashtable h_userParms, String clearCache,
                          HttpServletResponse response)
      throws IOException
  {
    // Check for missing parameters required either by the servlet or the
    // requested Fedora Access SOAP service.
    boolean isValid = true;
    PrintWriter out = response.getWriter();
    String versDate = DateUtility.convertDateToString(versDateTime);
    if (action != null && action.equals(GET_DISSEMINATION))
    {
      if (PID == null || bDefPID == null || methodName == null)
      {
        // Dissemination requires PID, bDefPID, and methodName;
        // asOfDateTime is optional.
        response.setContentType(CONTENT_TYPE_HTML);
        out.println("<html>");
        out.println("<head>");
        out.println("<title>FedoraServlet</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<p><font size='+1' color='red'>"
                    + "Required parameter missing "
                    + "in Dissemination Request:</font></p>");
        out.println("<table cellpadding='5'>");
        out.println("<tr>");
        out.println("<td><font color='red'>action_</font></td>");
        out.println("<td> = </td>");
        out.println("<td>" + action + "</td>");
        out.println("<td><font color='blue'>(REQUIRED)</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><font color='red'>PID_</font></td>");
        out.println("<td> = </td>");
        out.println("<td>" + PID + "</td>");
        out.println("<td><font color='blue'>(REQUIRED)</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><font color='red'>bDefPID_</font></td>");
        out.println("<td> = </td><td>" + bDefPID + "</td>");
        out.println("<td><font color='blue'>(REQUIRED)</font></td>");
        out.println("</tr>");
        out.println("</font><tr>");
        out.println("<td><font color='red'>methodName_</font></td>");
        out.println("<td> = </td>");
        out.println("<td>" + methodName + "</td>");
        out.println("<td><font color='blue'>(REQUIRED)</font></td>");
        out.println("</tr>");
        out.println("</font><tr>");
        out.println("<td><font color='red'>asOfDateTime_</font></td>");
        out.println("<td> = </td>");
        out.println("<td>" + versDate + "</td>");
        out.println("<td><font color='green'>(OPTIONAL)</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><font color='red'>clearCache_</font></td>");
        out.println("<td> = </td>");
        out.println("<td>" + clearCache + "</td>");
        out.println("<td><font color='green'>(OPTIONAL)</font></td>");
        out.println("</tr></font>");
        out.println("<tr>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td colspan='5'><font size='+1' color='blue'>"
                    + "Other Parameters Found:</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("</tr>");
        for (Enumeration e = h_userParms.keys() ; e.hasMoreElements(); )
        {
          String name = (String)e.nextElement();
          out.println("<tr>");
          out.println("<td><font color='red'>"+name+"</font></td>");
          out.println("<td>= </td>");
          out.println("<td>" + h_userParms.get(name) + "</td>");
          out.println("</tr>");
        }
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
        isValid = false;
      }
      //FIXME!! Validation for any user-supplied parameters not implemented.
    } else if (action != null &&
               (action.equals(GET_BEHAVIOR_DEFINITIONS) ||
               action.equals(GET_OBJECT_METHODS)))
    {
      if (PID == null)
      {
        // GetBehaviorDefinitions and GetObjectMethods require PID;
        // asOfDateTime is optional.
        response.setContentType(CONTENT_TYPE_HTML);
        out.println("<html>");
        out.println("<head>");
        out.println("<title>FedoraServlet</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<p><font size='+1' color='red'>"
                    + "Required parameter missing in Behavior "
                    + "Definition Request:</font></p>");
        out.println("<table cellpadding='5'>");
        out.println("<tr>");
        out.println("<td><font color='red'>action_</td>");
        out.println("<td> = </td>");
        out.println("<td>" + action + "</td>");
        out.println("<td><font color='blue'>(REQUIRED)</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><font color='red'>PID_</td>");
        out.println("<td> = </td>");
        out.println("<td>" + PID + "</td>");
        out.println("<td><font color='blue'>(REQUIRED)</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><font color='red'>asOfDateTime_</td>");
        out.println("<td> = </td>");
        out.println("<td>" + versDate + "</td>");
        out.println("<td><font color='green'>(OPTIONAL)</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><font color='red'>clearCache_</td>");
        out.println("<td> = </td>");
        out.println("<td>" + clearCache + "</td>");
        out.println("<td><font color='green'>(OPTIONAL)</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td colspan='5'><font size='+1' color='blue'>"
                    + "Other Parameters Found:</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("</tr>");
        for (Enumeration e = h_userParms.keys() ; e.hasMoreElements(); )
        {
          String name = (String)e.nextElement();
          out.println("<tr>");
          out.println("<td><font color='red'>"+name+"</font></td>");
          out.println("<td>= </td>");
          out.println("<td>"+h_userParms.get(name)+"</td>");
          out.println("</tr>");
        }
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
        isValid = false;
      }
    } else if (action != null &&
               (action.equalsIgnoreCase(GET_BEHAVIOR_METHODS) ||
               action.equalsIgnoreCase(GET_BEHAVIOR_METHODS_AS_WSDL)))
    {
      if (PID == null || bDefPID == null)
      {
        // GetBehaviorMethods and GetBehaviorMethodsAsWSDL require PID, bDefPID;
        // asOfDateTime is optional.
        response.setContentType(CONTENT_TYPE_HTML);
        out.println("<html>");
        out.println("<head>");
        out.println("<title>FedoraServlet</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<p><font size='+1' color='red'>"
                    + "Required parameter missing in Behavior "
                    + "Methods Request:</font></p>");
        out.println("<table cellpadding='5'>");
        out.println("<tr>");
        out.println("<td><font color='red'>action_</td>");
        out.println("<td> = </td>");
        out.println("<td>" + action + "</td>");
        out.println("<td><font color='blue'>(REQUIRED)</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><font color='red'>PID_</td>");
        out.println("<td> = </td>");
        out.println("<td>" + PID + "</td>");
        out.println("<td><font color='blue'>(REQUIRED)</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><font color='red'>bDefPID_</td>");
        out.println("<td> = </td>");
        out.println("<td>" + bDefPID + "</td>");
        out.println("<td><font color='blue'>(REQUIRED)</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<tr>");
        out.println("<td><font color='red'>asOfDateTime_</td>");
        out.println("<td> = </td>");
        out.println("<td>" + versDate + "</td>");
        out.println("<td><font color='green'>(OPTIONAL)</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td><font color='red'>clearCache_</td>");
        out.println("<td> = </td>");
        out.println("<td>" + clearCache + "</td>");
        out.println("<td><font color='green'>(OPTIONAL)</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td colspan='5'><font size='+1' color='blue'>"
                    + "Other Parameters Found:</font></td>");
        out.println("</tr>");
        out.println("<tr>");
        out.println("</tr>");
        for (Enumeration e = h_userParms.keys() ; e.hasMoreElements(); )
        {
          String name = (String)e.nextElement();
          out.println("<tr>");
          out.println("<td><font color='red'>"+name+"</font></td>");
          out.println("<td>= </td>");
          out.println("<td>" + h_userParms.get(name) + "</td>");
          out.println("</tr>");
        }
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
        isValid = false;
      }
    } else
    {
      // Unknown Fedora service has been requested.
      response.setContentType(CONTENT_TYPE_HTML);
      out.println("<html>");
      out.println("<head>");
      out.println("<title>FedoraServlet</title>");
      out.println("</head>");
      out.println("<body>");
      out.println("<p><font size='+1' color='red'>Invalid 'action' "
                  + "parameter specified in Servlet Request: action= "
                  + action+"<p>");
      out.println("<br></br><font color='blue'>Reserved parameters "
                  + "in Request:</font>");
      out.println("<table cellpadding='5'>");
      out.println("<tr>");
      out.println("<td><font color='red'>action_</td>");
      out.println("<td> = </td>");
      out.println("<td>" + action + "</td>");
      out.println("</tr>");
      out.println("<tr>");
      out.println("<td><font color='red'>PID_</td>");
      out.println("<td> = </td>");
      out.println("<td>" + PID + "</td>");
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
      out.println("<td colspan='5'><font size='+1' color='blue'>"
                  + "Other Parameters Found:</font></td>");
      out.println("</tr>");
      out.println("<tr>");
      out.println("</tr>");
      for (Enumeration e = h_userParms.keys() ; e.hasMoreElements(); )
      {
        String name = (String)e.nextElement();
        out.println("<tr>");
        out.println("<td><font color='red'>"+name+"</font></td>");
        out.println("<td>= </td>");
        out.println("<td>" + h_userParms.get(name) + "</td>");
        out.println("</tr>");
      }
      out.println("</table>");
      out.println("</body>");
      out.println("</html>");
      isValid = false;
    }

    if (debug)
    {
      System.err.println("PID: " + PID + "\nbDEF: " + bDefPID
                         + "\nmethodName: " + methodName
                         + "\naction: "+action);

      for ( Enumeration e = h_userParms.keys(); e.hasMoreElements(); )
      {
        String name = (String)e.nextElement();
        System.err.println("userParm: " + name
                           + "\nuserValue: " + h_userParms.get(name));
      }
    }
    return isValid;
  }

  /**
   * <p>Displays a list of the servlet input parameters. This method is
   * generally called when a service request returns no data. Usually
   * this is a result of an incorrect spelling of either a required
   * URL parameter or in one of the user-supplied parameters. The output
   * from this method can be used to help verify the URL parameters
   * sent to the servlet and hopefully fix the problem.</p>
   *
   * @param action The Fedora service requested.
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
  private void showURLParms(String action, String PID, String bDefPID,
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
    out.println("<td><font color='red'>action_</td>");
    out.println("<td> = </td>");
    out.println("<td>" + action + "</td>");
    out.println("</tr>");
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
      out.println("<td><font color='red'>" + userParms[i].getName()
                  + "</font></td>");
      out.println("<td> = </td>");
      out.println("<td>" + userParms[i].getValue() + "</td>");
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
          System.err.println("userParm: " + userParms[i].getName()
              + "\nuserValue: "+userParms[i].getValue());
        }
      }
    }
    System.err.println("REQUEST Returned NO Data");
  }
}