package fedora.server.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.net.URL;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.xml.namespace.QName;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import com.icl.saxon.expr.StringValue;

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
 * <li>GetBehaviorMethodsXML - Gets Behavior Methods as XML</li>
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
  private static final boolean debug = false;

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

  /** GetBehaviorMethodsXML service name. */
  private static final String GET_BEHAVIOR_METHODS_XML =
      "GetBehaviorMethodsXML";

  /** GetDissemination service name. */
  private static final String GET_DISSEMINATION =
      "GetDissemination";

  /** GetObjectMethods service name. */
  private static final String GET_OBJECT_METHODS =
      "GetObjectMethods";

  /** Servlet path for the parameter resolver servlet */
  private static final String PARAMETER_RESOLVER_SERVLET_PATH =
      "/fedora/getParmResolver?";

  /** User-supplied method parameters from servlet URL. */
  private Hashtable h_userParms = null;

  /** The incoming request URL. */
  private String requestURL = null;

  /** The incoming request complete URI. */
  private String requestURI = null;

  /** Constant indicating value of the string "yes". */
  private static final String YES = "yes";;

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
    String xmlEncode = "";
    Property[] userParms = null;
    long servletStartTime = new Date().getTime();
    h_userParms = new Hashtable();

    // getRequestURL only available in Servlet API 2.3.
    // Use following for earlier releases servlet API:
    // requestURL = new String("http://" + request.getServerName()
    //                        + ":" + request.getServerPort()
    //                        + request.getRequestURI() + "?");
    requestURL = request.getRequestURL().toString()+"?";
    requestURI = new String(requestURL + request.getQueryString());
    //PrintWriter out = response.getWriter();
    ServletOutputStream out = response.getOutputStream();

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
      } else if (parm.equals("xmlEncode_"))
      {
        xmlEncode = request.getParameter(parm);
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
        long stopTime = new Date().getTime();
        long interval = stopTime - servletStartTime;
        System.out.println("[FedoraAccessSOAPServlet] Roundtrip "
            + "GetBehaviorDefinitions: " + interval + " milliseconds.");
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
        long stopTime = new Date().getTime();
        long interval = stopTime - servletStartTime;
        System.out.println("[FedoraAccessSOAPServlet] Roundtrip "
            + "GetBehaviorDefinitions: " + interval + " milliseconds.");
      } else if (action.equalsIgnoreCase(GET_BEHAVIOR_METHODS_XML))
      {
        MIMETypedStream methodDefs = null;
        try
        {
          // Call Fedora Access SOAP service to request Method Definitions
          // in XML form.
          methodDefs = getBehaviorMethodsXML(PID, bDefPID, asOfDateTime);
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
            byte[] buffer = new byte[255];
            while ((byteStream = methodResults.read(buffer)) >= 0)
            {
              out.write(buffer, 0, byteStream);
            }
            buffer = null;
            out.println("</definitions>");
          } else
          {
            // Method Definition request in XML form returned nothing.
            String message = "FedoraSoapServlet: No Behavior Methods returned "
                + "as XML.";
            System.err.println(message);
            showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                         userParms, clearCache, response, message);
          }
        } catch (Exception e)
        {
          // FIXME!! Needs more refined Exception handling.
          String message = "FedoraSoapServlet: No Behavior Methods returned "
                         + "as XML. <br> Exception: " + e.getClass().getName()
                         + " <br> Reason: "  + e.getMessage();
          System.err.println(message);
          showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                       userParms, clearCache, response, message);
        }
        long stopTime = new Date().getTime();
        long interval = stopTime - servletStartTime;
        System.out.println("[FedoraAccessSOAPServlet] Roundtrip "
            + "GetBehaviorMethodsAsWSDL: " + interval + " milliseconds.");
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
              //
              if (dissemination.getMIMEType().equalsIgnoreCase("application/fedora-redirect"))
              {
                // A MIME type of application/fedora-redirect signals that the
                // MIMETypedStream returned from the dissemination is a special
                // Fedora-specific MIME type. In this case, teh Fedora server will
                // not proxy the stream, but instead perform a simple redirect to
                // the URL contained within the body of the MIMETypedStream. This
                // special MIME type is used primarily for streaming media.
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                    new ByteArrayInputStream(dissemination.getStream())));
                StringBuffer sb = new StringBuffer();
                String line = null;
                while ((line = br.readLine()) != null)
                {
                  sb.append(line);
                }
                response.sendRedirect(sb.toString());
              } else
              {
                response.setContentType(dissemination.getMIMEType());
                int byteStream = 0;
                ByteArrayInputStream dissemResult =
                    new ByteArrayInputStream(dissemination.getStream());
                byte[] buffer = new byte[255];
                while ((byteStream = dissemResult.read(buffer)) >= 0)
                {
                  out.write(buffer, 0, byteStream);
                }
                buffer = null;
                dissemResult.close();
              }
            } else
            {
              // Dissemination request returned nothing.
              String message = "FedoraSoapServlet: No Dissemination result "
                  + "returned. <br> See server logs for additional info";
              System.err.println(message);
              showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                           userParms, clearCache, response, message);
            }
          } catch (Exception e)
          {
            // FIXME!! Needs more refined Exception handling.
            e.printStackTrace();
            String message = "FedoraSoapServlet: No Dissemination result "
                           + "returned. <br> Exception: "
                           + e.getClass().getName()
                           + " <br> Reason: "  + e.getMessage()
                           + " <br> See server logs for additional info";
            System.err.println(message);
            showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                         userParms, clearCache, response, message);
          }
          long stopTime = new Date().getTime();
          long interval = stopTime - servletStartTime;
          System.out.println("[FedoraAccessSOAPServlet] Roundtrip "
              + "GetDissemination: " + interval + " milliseconds.");
      } else if (action.equals(GET_OBJECT_METHODS))
      {
        ObjectMethodsDef[] objMethDefArray = null;
        PipedWriter pw = new PipedWriter();
        PipedReader pr = new PipedReader(pw);

        try
        {
          //out = response.getWriter();
          out = response.getOutputStream();
          pw = new PipedWriter();
          pr = new PipedReader(pw);
          objMethDefArray = getObjectMethods(PID, asOfDateTime);
          if (objMethDefArray != null)
          {
            // Object Methods found.
            // Deserialize ObjectmethodsDef datastructure into XML
            new SerializerThread(PID, objMethDefArray, versDateTime, pw).start();
            if (xmlEncode.equalsIgnoreCase(YES))
            {
              // Return results as raw XML
              response.setContentType(CONTENT_TYPE_XML);
              int bytestream = 0;
              while ( (bytestream = pr.read()) >= 0)
              {
                out.write(bytestream);
              }
            } else
            {
              // Transform results into an html table
              response.setContentType(CONTENT_TYPE_HTML);
              File xslFile = new File("dist/server/access/objectmethods2.xslt");
              TransformerFactory factory = TransformerFactory.newInstance();
              Templates template = factory.newTemplates(new StreamSource(xslFile));
              Transformer transformer = template.newTransformer();
              Properties details = template.getOutputProperties();
              transformer.transform(new StreamSource(pr), new StreamResult(out));
            }
            pr.close();

          } else
          {
            // Object Methods Definition request returned nothing.
            String message = "[FedoraAccessServlet] No Object Method Definitions "
                + "returned.";
            System.out.println(message);
            showURLParms(action, PID, "", "", asOfDateTime, new Property[0], "", response, message);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            response.sendError(response.SC_NO_CONTENT, message);
          }
        } catch (Throwable th)
        {
          String message = "[FedoraAccessServlet] An error has occured. "
                         + " The error was a \" "
                         + th.getClass().getName()
                         + " \". Reason: "  + th.getMessage();
          System.out.println(message);
          th.printStackTrace();
          response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          response.sendError(response.SC_INTERNAL_SERVER_ERROR, message);
        } finally
        {
            if (pr != null) pr.close();
        }
        out.close();
      }
    } else
    {
      // Object Methods Definition request returned nothing.
      String message = "[FedoraAccessServlet] No Object Method Definitions "
          + "returned.";
      System.out.println(message);
      showURLParms(action, PID, "", "", asOfDateTime, new Property[0], "", response, message);
      response.setStatus(HttpServletResponse.SC_NO_CONTENT);
      response.sendError(response.SC_NO_CONTENT, message);
    }
    long stopTime = new Date().getTime();
    long interval = stopTime - servletStartTime;
    System.out.println("[FedoraAccessSOAPServlet] Roundtrip "
        + "GetObjectMethods: " + interval + " milliseconds.");
  }

  //

  /**
   * <p> A Thread to serialize an ObjectMethodsDef object into XML.</p>
   *
   */
  public class SerializerThread extends Thread
  {
    private PipedWriter pw = null;
    private String PID = null;
    private ObjectMethodsDef[] objMethDefArray = new ObjectMethodsDef[0];
    private Date versDateTime = null;

    /**
     * <p> Constructor for SerializeThread.</p>
     *
     * @param PID The persistent identifier of the specified digital object.
     * @param objMethDefArray An array of object mtehod definitions.
     * @param versDateTime The version datetime stamp of the request.
     * @param pw A PipedWriter to which the serialization info is written.
     */
    public SerializerThread(String PID, ObjectMethodsDef[] objMethDefArray,
                        Date versDateTime, PipedWriter pw)
    {
      this.pw = pw;
      this.PID = PID;
      this.objMethDefArray = objMethDefArray;
      this.versDateTime = versDateTime;
    }

    /**
     * <p> This method executes the thread.</p>
     */
    public void run()
    {
      if (pw != null)
      {
        try
        {
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
            currentBdef = objMethDefArray[i].getBDefPID();
            if (!currentBdef.equalsIgnoreCase(nextBdef))
            {
              if (i != 0) pw.write("</bdef>");
              pw.write("<bdef pid=\"" + objMethDefArray[i].getBDefPID() + "\" >");
            }
            pw.write("<method name=\"" + objMethDefArray[i].getMethodName() + "\" >");
            MethodParmDef[] methodParms = objMethDefArray[i].getMethodParmDefs();
            for (int j=0; j<methodParms.length; j++)
            {
              pw.write("<parm parmName=\"" + methodParms[j].getParmName()
                  + "\" parmDefaultValue=\"" + methodParms[j].getParmDefaultValue()
                  + "\" parmRequired=\"" + methodParms[j].isParmRequired()
                  + "\" parmType=\"" + methodParms[j].getParmType()
                  + "\" parmLabel=\"" + methodParms[j].getParmLabel() + "\" >");
              if (methodParms[j].getParmDomainValues().length > 0 )
              {
                pw.write("<parmDomainValues>");
                for (int k=0; k<methodParms[j].getParmDomainValues().length; k++)
                {
                  pw.write("<value>" + methodParms[j].getParmDomainValues()[k]
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
          pw.flush();
          pw.close();
        } catch (IOException ioe) {
          System.err.println("WriteThread IOException: " + ioe.getMessage());
        } finally
        {
          try
          {
            if (pw != null) pw.close();
          } catch (IOException ioe)
          {
            System.err.println("WriteThread IOException: " + ioe.getMessage());
          }
        }
      }
    }
  }
/*        try
        {
          objMethDefArray = getObjectMethods(PID, asOfDateTime);
          if (objMethDefArray != null)
          {
            // Object Methods found.
            // Deserialize ObjectmethodsDef datastructure into XML
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
              currentBdef = objMethDefArray[i].getBDefPID();
              if (!currentBdef.equalsIgnoreCase(nextBdef))
              {
                if (i != 0) pw.write("</bdef>");
                pw.write("<bdef pid=\"" + objMethDefArray[i].getBDefPID() + "\" >");
              }
              pw.write("<method name=\"" + objMethDefArray[i].getMethodName() + "\" >");
              MethodParmDef[] methodParms = objMethDefArray[i].getMethodParmDefs();
              for (int j=0; j<methodParms.length; j++)
              {
                pw.write("<parm parmName=\"" + methodParms[j].getParmName()
                    + "\" parmDefaultValue=\"" + methodParms[j].getParmDefaultValue()
                    + "\" parmRequired=\"" + methodParms[j].isParmRequired()
                    + "\" parmType=\"" + methodParms[j].getParmType()
                    + "\" parmLabel=\"" + methodParms[j].getParmLabel() + "\" >");
                if (methodParms[j].getParmDomainValues().length >0)
                {
                  pw.write("<parmDomainValues>");
                  for (int k=0; k<methodParms[j].getParmDomainValues().length; k++)
                  {
                    pw.write("<value>" + methodParms[j].getParmDomainValues()[k]
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
              File xslFile = new File("dist/server/access/objectmethods2.xslt");
              TransformerFactory factory = TransformerFactory.newInstance();
              Templates template = factory.newTemplates(new StreamSource(xslFile));
              Transformer transformer = template.newTransformer();
              Properties details = template.getOutputProperties();
              transformer.transform(new StreamSource(pr), new StreamResult(out));
              pr.close();
            }
          } else
          {
            // Object Methods Definition request returned nothing.
            String message = "[FedoraAccessServlet] No Object Method Definitions "
                + "returned.";
            System.out.println(message);
            showURLParms(action, PID, "", "", asOfDateTime, new Property[0], "", response, message);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            response.sendError(response.SC_NO_CONTENT, message);
          }
          long stopTime = new Date().getTime();
          long interval = stopTime - servletStartTime;
          System.out.println("[FedoraAccessSOAPServlet] Roundtrip "
            + "GetObjectMethods: " + interval + " milliseconds.");
        } catch (TransformerException te)
        {
          String message = "[FedoraAccessServlet] An error has occured in "
                         + "transforming the deserialized XML from getObjectMethods"
                         + " into html. The "
                         + "error was a \" "
                         + te.getClass().getName()
                         + " \". Reason: "  + te.getMessage();
          System.out.println(message);
          te.printStackTrace();
          response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          response.sendError(response.SC_INTERNAL_SERVER_ERROR, message);
        } catch (Exception e)
        {
          // FIXME!! Needs more refined Exception handling
          String message = "FedoraSoapServlet: No Object Method Definitions "
                         + "result returned. <br> Exception: "
                         + e.getClass().getName()
                         + " <br> Reason: "  + e.getMessage();
          System.err.println(message);
          e.printStackTrace();
          showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                       userParms, clearCache, response, message);
      } finally
      {
        if (pw != null) pw.close();
        if (pr != null) pr.close();
      }
        out.close();
      }
    }
  }*/
/*        try
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
            out.println("<td>&nbsp;</td><td><b><font size='+2'> Parm Name"
                        + " </font></b></td>");
            out.println("<td colspan=\"100%\"><b><font size='+2'> Allowed Parm Values<br>(Select a value for each Parameter)"
                        + " </font></b></td>");
            out.println("</tr>");

            // Format table such that repeating fields only display once.
            int rows = objMethDefArray.length-1;
            for (int i=0; i<objMethDefArray.length; i++)
            {
              if (debug)
              {
                MethodParmDef[] methodParms = null;
                methodParms = objMethDefArray[i].getMethodParmDefs();
                if (methodParms != null)
                {
                  for(int j=0; j<methodParms.length; j++)
                  {
                    System.out.println("ParmName: "+methodParms[j].getParmName());
                    String[] values = methodParms[j].getParmDomainValues();
                    if(values != null)
                    {
                      for(int k=0; k<values.length; k++)
                      {
                        System.out.println("parmValue: "+values[k]);
                      }
                    }
                  }
                }
              }
              out.println("<form name=\"parmResolverForm\" "
                  + "method=\"post\" action=\""
                  + PARAMETER_RESOLVER_SERVLET_PATH + "\"><tr>");
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
                            + objMethDefArray[i].getMethodName()
                            + " </font></td>");

                // Setup special formatting if there are any method parameters
                StringBuffer sb = createParmForm(PID, objMethDefArray[i].getBDefPID(),
                    objMethDefArray[i].getMethodName(),
                    objMethDefArray[i].getMethodParmDefs());
                out.println(sb.toString());
                out.println("</form>");
              } else if (i == 1)
              {
                out.println("<td colspan='2' rowspan='" + rows + "'></td>");
                out.println("<td><font color=\"green\"> "
                            + objMethDefArray[i].getBDefPID()
                            + " </font></td>");
                out.println("<td><font color=\"red\"> "
                            + objMethDefArray[i].getMethodName()
                            + " </font></td>");

                // Setup special formatting if there are any method parameters
                StringBuffer sb = createParmForm(PID, objMethDefArray[i].getBDefPID(),
                    objMethDefArray[i].getMethodName(),
                    objMethDefArray[i].getMethodParmDefs());
                out.println(sb.toString());
                out.println("</form>");
              } else
              {
                out.println("<td><font color=\"green\"> "
                            + objMethDefArray[i].getBDefPID()
                            + " </font></td>");
                out.println("<td><font color=\"red\"> "
                            + objMethDefArray[i].getMethodName()
                            + " </font></td>");

                // Setup special formatting if there are any method parameters
                StringBuffer sb = createParmForm(PID, objMethDefArray[i].getBDefPID(),
                    objMethDefArray[i].getMethodName(),
                    objMethDefArray[i].getMethodParmDefs());
                out.println(sb.toString());
                out.println("</form>");
              }
              //out.println("</tr>");
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
          }*/


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
   * <p>Gets a bytestream containing the XML that defines the Behavior Methods
   * of the associated Behavior Mechanism object by invoking the appropriate
   * Fedora Access SOAP service.
   *
   * @param PID The persistent identifier of digital object.
   * @param bDefPID The persistent identifier of Behavior Definition object.
   * @param asOfDateTime The versioning datetime stamp.
   * @return MIME-typed stream containing XML-encoded method definitions.
   * @throws Exception If an error occurs in communicating with the Fedora
   *         Access SOAP service.
   */
  public MIMETypedStream getBehaviorMethodsXML(
      String PID, String bDefPID, Calendar asOfDateTime) throws Exception
  {
    MIMETypedStream methodDefs = null;
    Service service = new Service();
    Call call = (Call) service.createCall();
    call.setOperationName(new QName(FEDORA_API_URI,
                                    GET_BEHAVIOR_METHODS_XML) );
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
    QName qn2 = new QName(FEDORA_TYPE_URI, "MethodParmDef");
    call.setTargetEndpointAddress( new URL(FEDORA_ACCESS_ENDPOINT) );

    // Any Fedora-defined types required by the SOAP service must be registered
    // prior to invocation so the SOAP service knows the appropriate
    // serializer/deserializer to use for these types.
    call.registerTypeMapping(ObjectMethodsDef.class, qn,
        new BeanSerializerFactory(ObjectMethodsDef.class, qn),
        new BeanDeserializerFactory(ObjectMethodsDef.class, qn));
    call.registerTypeMapping(MethodParmDef.class, qn2,
        new BeanSerializerFactory(MethodParmDef.class, qn2),
        new BeanDeserializerFactory(MethodParmDef.class, qn2));
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
  {
  }

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
    //PrintWriter out = response.getWriter();
    ServletOutputStream out = response.getOutputStream();
    String versDate = DateUtility.convertDateToString(versDateTime);
    StringBuffer html = new StringBuffer();
    if (action != null && action.equals(GET_DISSEMINATION))
    {
      if (PID == null || bDefPID == null || methodName == null)
      {
        // Dissemination requires PID, bDefPID, and methodName;
        // asOfDateTime is optional.
        response.setContentType(CONTENT_TYPE_HTML);
        html.append("<html>");
        html.append("<head>");
        html.append("<title>FedoraServlet</title>");
        html.append("</head>");
        html.append("<body>");
        html.append("<p><font size='+1' color='red'>"
                    + "Required parameter missing "
                    + "in Dissemination Request:</font></p>");
        html.append("<table cellpadding='5'>");
        html.append("<tr>");
        html.append("<td><font color='red'>action_</font></td>");
        html.append("<td> = </td>");
        html.append("<td>" + action + "</td>");
        html.append("<td><font color='blue'>(REQUIRED)</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td><font color='red'>PID_</font></td>");
        html.append("<td> = </td>");
        html.append("<td>" + PID + "</td>");
        html.append("<td><font color='blue'>(REQUIRED)</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td><font color='red'>bDefPID_</font></td>");
        html.append("<td> = </td><td>" + bDefPID + "</td>");
        html.append("<td><font color='blue'>(REQUIRED)</font></td>");
        html.append("</tr>");
        html.append("</font><tr>");
        html.append("<td><font color='red'>methodName_</font></td>");
        html.append("<td> = </td>");
        html.append("<td>" + methodName + "</td>");
        html.append("<td><font color='blue'>(REQUIRED)</font></td>");
        html.append("</tr>");
        html.append("</font><tr>");
        html.append("<td><font color='red'>asOfDateTime_</font></td>");
        html.append("<td> = </td>");
        html.append("<td>" + versDate + "</td>");
        html.append("<td><font color='green'>(OPTIONAL)</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td><font color='red'>clearCache_</font></td>");
        html.append("<td> = </td>");
        html.append("<td>" + clearCache + "</td>");
        html.append("<td><font color='green'>(OPTIONAL)</font></td>");
        html.append("</tr></font>");
        html.append("<tr>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td colspan='5'><font size='+1' color='blue'>"
                    + "Other Parameters Found:</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("</tr>");
        for (Enumeration e = h_userParms.keys() ; e.hasMoreElements(); )
        {
          String name = (String)e.nextElement();
          html.append("<tr>");
          html.append("<td><font color='red'>"+name+"</font></td>");
          html.append("<td>= </td>");
          html.append("<td>" + h_userParms.get(name) + "</td>");
          html.append("</tr>");
        }
        html.append("</table>");
        html.append("</body>");
        html.append("</html>");
        out.println(html.toString());
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
        html.append("<html>");
        html.append("<head>");
        html.append("<title>FedoraServlet</title>");
        html.append("</head>");
        html.append("<body>");
        html.append("<p><font size='+1' color='red'>"
                    + "Required parameter missing in Behavior "
                    + "Definition Request:</font></p>");
        html.append("<table cellpadding='5'>");
        html.append("<tr>");
        html.append("<td><font color='red'>action_</td>");
        html.append("<td> = </td>");
        html.append("<td>" + action + "</td>");
        html.append("<td><font color='blue'>(REQUIRED)</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td><font color='red'>PID_</td>");
        html.append("<td> = </td>");
        html.append("<td>" + PID + "</td>");
        html.append("<td><font color='blue'>(REQUIRED)</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td><font color='red'>asOfDateTime_</td>");
        html.append("<td> = </td>");
        html.append("<td>" + versDate + "</td>");
        html.append("<td><font color='green'>(OPTIONAL)</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td><font color='red'>clearCache_</td>");
        html.append("<td> = </td>");
        html.append("<td>" + clearCache + "</td>");
        html.append("<td><font color='green'>(OPTIONAL)</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td colspan='5'><font size='+1' color='blue'>"
                    + "Other Parameters Found:</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("</tr>");
        for (Enumeration e = h_userParms.keys() ; e.hasMoreElements(); )
        {
          String name = (String)e.nextElement();
          html.append("<tr>");
          html.append("<td><font color='red'>"+name+"</font></td>");
          html.append("<td>= </td>");
          html.append("<td>"+h_userParms.get(name)+"</td>");
          html.append("</tr>");
        }
        html.append("</table>");
        html.append("</body>");
        html.append("</html>");
        out.println(html.toString());
        isValid = false;
      }
    } else if (action != null &&
               (action.equalsIgnoreCase(GET_BEHAVIOR_METHODS) ||
               action.equalsIgnoreCase(GET_BEHAVIOR_METHODS_XML)))
    {
      if (PID == null || bDefPID == null)
      {
        // GetBehaviorMethods and GetBehaviorMethodsXML require PID, bDefPID;
        // asOfDateTime is optional.
        response.setContentType(CONTENT_TYPE_HTML);
        html.append("<html>");
        html.append("<head>");
        html.append("<title>FedoraServlet</title>");
        html.append("</head>");
        html.append("<body>");
        html.append("<p><font size='+1' color='red'>"
                    + "Required parameter missing in Behavior "
                    + "Methods Request:</font></p>");
        html.append("<table cellpadding='5'>");
        html.append("<tr>");
        html.append("<td><font color='red'>action_</td>");
        html.append("<td> = </td>");
        html.append("<td>" + action + "</td>");
        html.append("<td><font color='blue'>(REQUIRED)</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td><font color='red'>PID_</td>");
        html.append("<td> = </td>");
        html.append("<td>" + PID + "</td>");
        html.append("<td><font color='blue'>(REQUIRED)</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td><font color='red'>bDefPID_</td>");
        html.append("<td> = </td>");
        html.append("<td>" + bDefPID + "</td>");
        html.append("<td><font color='blue'>(REQUIRED)</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<tr>");
        html.append("<td><font color='red'>asOfDateTime_</td>");
        html.append("<td> = </td>");
        html.append("<td>" + versDate + "</td>");
        html.append("<td><font color='green'>(OPTIONAL)</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td><font color='red'>clearCache_</td>");
        html.append("<td> = </td>");
        html.append("<td>" + clearCache + "</td>");
        html.append("<td><font color='green'>(OPTIONAL)</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td colspan='5'><font size='+1' color='blue'>"
                    + "Other Parameters Found:</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("</tr>");
        for (Enumeration e = h_userParms.keys() ; e.hasMoreElements(); )
        {
          String name = (String)e.nextElement();
          html.append("<tr>");
          html.append("<td><font color='red'>"+name+"</font></td>");
          html.append("<td>= </td>");
          html.append("<td>" + h_userParms.get(name) + "</td>");
          html.append("</tr>");
        }
        html.append("</table>");
        html.append("</body>");
        html.append("</html>");
        out.println(html.toString());
        isValid = false;
      }
    } else
    {
      // Unknown Fedora service has been requested.
      response.setContentType(CONTENT_TYPE_HTML);
      html.append("<html>");
      html.append("<head>");
      html.append("<title>FedoraServlet</title>");
      html.append("</head>");
      html.append("<body>");
      html.append("<p><font size='+1' color='red'>Invalid 'action' "
                  + "parameter specified in Servlet Request: action= "
                  + action+"<p>");
      html.append("<br></br><font color='blue'>Reserved parameters "
                  + "in Request:</font>");
      html.append("<table cellpadding='5'>");
      html.append("<tr>");
      html.append("<td><font color='red'>action_</td>");
      html.append("<td> = </td>");
      html.append("<td>" + action + "</td>");
      html.append("</tr>");
      html.append("<tr>");
      html.append("<td><font color='red'>PID_</td>");
      html.append("<td> = </td>");
      html.append("<td>" + PID + "</td>");
      html.append("</tr>");
      html.append("<tr>");
      html.append("<td><font color='red'>bDefPID_</td>");
      html.append("<td> = </td>");
      html.append("<td>" + bDefPID + "</td>");
      html.append("</tr>");
      html.append("<tr>");
      html.append("<td><font color='red'>methodName_</td>");
      html.append("<td> = </td>");
      html.append("<td>" + methodName + "</td>");
      html.append("</tr>");
      html.append("<tr>");
      html.append("<td><font color='red'>asOfDateTime_</td>");
      html.append("<td> = </td>");
      html.append("<td>" + versDate + "</td>");
      html.append("</tr>");
      html.append("<tr>");
      html.append("<td><font color='red'>clearCache_</td>");
      html.append("<td> = </td>");
      html.append("<td>" + clearCache + "</td>");
      html.append("</tr>");
      html.append("<tr>");
      html.append("</tr>");
      html.append("<tr>");
      html.append("<td colspan='5'><font size='+1' color='blue'>"
                  + "Other Parameters Found:</font></td>");
      html.append("</tr>");
      html.append("<tr>");
      html.append("</tr>");
      for (Enumeration e = h_userParms.keys() ; e.hasMoreElements(); )
      {
        String name = (String)e.nextElement();
        html.append("<tr>");
        html.append("<td><font color='red'>"+name+"</font></td>");
        html.append("<td>= </td>");
        html.append("<td>" + h_userParms.get(name) + "</td>");
        html.append("</tr>");
      }
      html.append("</table>");
      html.append("</body>");
      html.append("</html>");
      out.println(html.toString());
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
    //PrintWriter out = response.getWriter();
    ServletOutputStream out = response.getOutputStream();
    response.setContentType(CONTENT_TYPE_HTML);

    // Display servlet input parameters
    StringBuffer html = new StringBuffer();
    html.append("<html>");
    html.append("<head>");
    html.append("<title>FedoraServlet</title>");
    html.append("</head>");
    html.append("<body>");
    html.append("<br></br><font size='+2'>" + message + "</font>");
    html.append("<br></br><font color='red'>Request Parameters</font>");
    html.append("<br></br>");
    html.append("<table cellpadding='5'>");
    html.append("<tr>");
    html.append("<td><font color='red'>action_</td>");
    html.append("<td> = </td>");
    html.append("<td>" + action + "</td>");
    html.append("</tr>");
    html.append("<tr>");
    html.append("<td><font color='red'>PID_</td>");
    html.append("<td> = <td>" + PID + "</td>");
    html.append("</tr>");
    html.append("<tr>");
    html.append("<td><font color='red'>bDefPID_</td>");
    html.append("<td> = </td>");
    html.append("<td>" + bDefPID + "</td>");
    html.append("</tr>");
    html.append("<tr>");
    html.append("<td><font color='red'>methodName_</td>");
    html.append("<td> = </td>");
    html.append("<td>" + methodName + "</td>");
    html.append("</tr>");
    html.append("<tr>");
    html.append("<td><font color='red'>asOfDateTime_</td>");
    html.append("<td> = </td>");
    html.append("<td>" + versDate + "</td>");
    html.append("</tr>");
    html.append("<tr>");
    html.append("<td><font color='red'>clearCache_</td>");
    html.append("<td> = </td>");
    html.append("<td>" + clearCache + "</td>");
    html.append("</tr>");
    html.append("<tr>");
    html.append("</tr>");
    html.append("<tr>");
    html.append("<td colspan='5'><font size='+1' color='blue'>"+
                "Other Parameters Found:</font></td>");
    html.append("</tr>");
    html.append("<tr>");
    html.append("</tr>");

    // List user-supplied parameters if any
    if (userParms != null)
    {
    for (int i=0; i<userParms.length; i++)
    {
      html.append("<tr>");
      html.append("<td><font color='red'>" + userParms[i].getName()
                  + "</font></td>");
      html.append("<td> = </td>");
      html.append("<td>" + userParms[i].getValue() + "</td>");
        html.append("</tr>");
    }
    }
    html.append("</table></center></font>");
    html.append("</body></html>");
    out.println(html.toString());

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

  /**
   * <p>Creates a web form that allows one to select the values of method
   * parameters to be used for method specified in a dissemination request.
   * If the method has no parameters, this is noted in the form output.</p>
   *
   * @param PID The persistent idenitifer for the digital object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param methodName The name of the method.
   * @param methodParms An array of method parameter definitions.
   * @param requestURI The URI of the calling servlet.
   * @return A string buffer containing the generated html form information.
   */
  public StringBuffer createParmForm(String PID, String bDefPID,
      String methodName, MethodParmDef[] methodParms)
  {
    StringBuffer sb = new StringBuffer();
    if (methodParms == null || methodParms.length == 0)
    {
      // The method has no parameters.
      sb.append("<td><input type=\"hidden\" name=\"PID\" value=\""
          + PID + "\">"
          + "<input type=\"hidden\" name=\"bDefPID\" value=\""
          + bDefPID + "\">"
          + "<input type=\"hidden\" name=\"methodName\" value=\""
          + methodName + "\">"
          + "<input type=\"submit\" name=\"Submit\" "
          + "value=\"RunDissemination\"></td>"
          + "<td colspan=\"100%\"><font color=\"purple\">"
          + "No Parameters Defined</font></td>");
      return sb;
    }

    // Format table such that repeating fields only display once.
    int rows = methodParms.length-1;
    for (int i=0; i<methodParms.length; i++)
    {
      String parmName = methodParms[i].getParmName();
      String[] parmValues = methodParms[i].getParmDomainValues();
      if (i == 0)
      {
        sb.append(
            "<td><input type=\"submit\" name=\"Submit\" "
            + "value=\"RunDissemination\">"
            + "<input type=\"hidden\" name=\"PID\" value=\""
            + PID + "\">"
            + "<input type=\"hidden\" name=\"bDefPID\" value=\""
            + bDefPID + "\">"
            + "<input type=\"hidden\" name=\"methodName\" value=\""
            + methodName + "\">"
            + "<td><b><font color=\"purple\">"
            + parmName + "</font></b></td>");
        if(parmValues != null)
        {
          for (int j=0; j<parmValues.length; j++)
          {
            if (parmValues[j].equalsIgnoreCase("null"))
            {
              sb.append("<td>"
                  + "<input type=\"text\"  size=\"10\" maxlength=\"32\" "
                  + "name=\"" + parmName + "\" value=\"\"></td>");
            } else
            {
              sb.append("<td>" + parmValues[j] + "</td>"
                  + "<td>"
                  + "<input type=\"radio\" name=\""
                  + parmName +"\" value=\"" + parmValues[j] + "\"></td>");
            }
          }
        }
      } else if (i == 1)
      {
        sb.append("</tr><tr><td colspan=\"5\" rowspan=\"" + rows + "\"></td>"
            + "<td><b><font color=\"purple\">" + parmName + "</font></b></td>");
        if(parmValues != null)
        {
          for (int j=0; j<parmValues.length; j++)
          {
            if (parmValues[j].equalsIgnoreCase("null"))
            {
              sb.append("<td>"
                  + "<input type=\"text\"  size=\"10\" maxlength=\"32\" "
                  + "name=\"" + parmName + "\" value=\"\"></td>");
            } else
            {
              sb.append("<td>" + parmValues[j] + "</td>"
                  + "<td>"
                  + "<input type=\"radio\" name=\""
                  + parmName +"\" value=\"" + parmValues[j] + "\"></td>");
            }
          }
        }
      } else
      {
        sb.append("</tr><tr><td><b><font color=\"purple\">"
            + parmName + "</font></b></td>");
        if(parmValues != null)
        {
          for (int j=0; j<parmValues.length; j++)
          {
            if (parmValues[j].equalsIgnoreCase("null"))
            {
              sb.append("<td>"
                  + "<input type=\"text\"  size=\"10\" maxlength=\"32\" "
                  + "name=\"" + parmName + "\" value=\"\"></td>");
            } else
            {
              sb.append("<td>" + parmValues[j] + "</td>"
                  + "<td>"
                  + "<input type=\"radio\" name=\""
                  + parmName +"\" value=\"" + parmValues[j] + "\"></td>");
            }
          }
        }
      }
    }
    return sb;
  }
}
