package fedora.server.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
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
import fedora.server.types.gen.ObjectProfile;
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
 * communicate directly with the Fedora Access SOAP service rather than use a
 * java servlet as an intermediary. This servlet serves as an example of how to
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
 * <li>GetObjectProfile - Gets object profile.</li>
 * </ol>
 * <li>PID_ - persistent identifier of the digital object</li>
 * <li>bDefPID_ - persistent identifier of the Behavior Definiton object</li>
 * <li>methodName_ - name of the method</li>
 * <li>asOfDateTime_ - versioning datetime stamp</li>
 * <li>xmlEncode_ - boolean switch used in conjunction with GetObjectMethods
 *                  that determines whether output is formatted as XML or
 *                  as HTML; value of "true" indicates XML format; value of
 *                  false or omission indicates HTML format.
 * <li>userParms - behavior methods may require or provide optional parameters
 *                 that may be input as arguments to the method; these method
 *                 parameters are entered as name/value pairs like the other
 *                 serlvet parameters.(optional)</li>
 * </ul>
 * <p><i><b>Note that all servlet parameter names that are implementation
 * specific end with the underscore character ("_"). This is done to avoid
 * possible name clashes with user-supplied method parameter names. As a
 * general rule, user-supplied parameters should never contain names that end
 * with the underscore character to prevent possible name conflicts.</b></i>
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

  /** GetObjectProfile service name. */
  private static final String GET_OBJECT_PROFILE =
      "GetObjectProfile";

  private static final String SERVLET_PATH = "/fedora/access/soapservlet?";

  /** User-supplied method parameters from servlet URL. */
  private Hashtable h_userParms = null;

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
    String methodName = null;
    String PID = null;
    Property[] userParms = null;
    boolean xmlEncode = false;
    long servletStartTime = new Date().getTime();
    h_userParms = new Hashtable();
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
      } else if (parm.equals("xmlEncode_"))
      {
        xmlEncode = new Boolean(request.getParameter(parm)).booleanValue();
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
                      h_userParms, response))
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
            StringBuffer html = new StringBuffer();
            html.append("<html>");
            html.append("<head>");
            html.append("<title>Behavior Definitions</title>");
            html.append("</head>");
            html.append("<br></br>");
            html.append("<center>");
            html.append("<table border='1' cellpadding='5'>");
            html.append("<tr>");
            html.append("<td><b><font size='+2'><b>PID</font></td></b>");
            html.append("<td><b><font size='+2'>Version Date</font></b></td>");
            html.append("<td><b><font size='+2'>Behavior Definitions</font>"
                        + "</b></td");
            html.append("</tr>");

            // Format table such that repeating fields display only once.
            int rows = behaviorDefs.length - 1;
            for (int i=0; i<behaviorDefs.length; i++)
            {
              html.append("<tr>");
              if (i == 0)
              {
                html.append("<td><font color='blue'><a href='"
                    + SERVLET_PATH
                    + "action_=GetObjectMethods&PID_=" + PID+ "'>" + PID
                    + "</a></font></td>");
                html.append("<td><font color='green'>"
                    + DateUtility.convertDateToString(versDateTime)
                    + "</font></td>");
                html.append("<td><font color='red'>" + behaviorDefs[i]
                    + "</font></td>");
              } else if (i == 1)
              {
                html.append("<td colspan='2' rowspan='" + rows
                    + "'></td><td><font color='red'>" + behaviorDefs[i]
                    + "</font></td>");
              } else
              {
                html.append("<td><font color='red'>" + behaviorDefs[i]
                    + "</font></td>");
              }
              html.append("</tr>");
            }
            html.append("</table>");
            html.append("</center>");
            html.append("<br></br>");
            html.append("</body>");
            html.append("</html>");
            out.print(html.toString());
          } else
          {
            // Behavior Definition request returned nothing.
            String message = "[FedoraAccessSOAPServlet] No Behavior Definitons "
                + "returned.";
            System.err.println(message);
            showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                         userParms, response, message);
          }

          // FIXME!! Needs more refined Exception handling.
        } catch (Exception e)
        {
          String message = "[FedoraAccessSOAPServlet] Failed to get Behavior "
              + "Definitions <br > Exception: "
              + e.getClass().getName() + " <br> Reason: "
              + e.getMessage();
          System.err.println(message);
          showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                       userParms, response, message);
        }
        long stopTime = new Date().getTime();
        long interval = stopTime - servletStartTime;
        System.out.println("[FedoraAccessSOAPServlet] Roundtrip "
            + "GetBehaviorDefinitions: " + interval + " milliseconds.");
      }
      else if (action.equals(GET_BEHAVIOR_METHODS))
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
            StringBuffer html = new StringBuffer();
            html.append("<html>");
            html.append("<head>");
            html.append("<title>Behavior Methods</title>");
            html.append("</head>");
            html.append("<br></br>");
            html.append("<center>");
            html.append("<table border='1' cellpadding='5'>");
            html.append("<tr>");
            html.append("<td><b><font size='+2'> Object PID "
                + " </font></b></td>");
            html.append("<td><b><font size='+2'> BDEF PID"
                + " </font></b></td>");
            html.append("<td><b><font size='+2'> Version Date"
                + " </font></b></td>");
            html.append("<td><b><font size='+2'> Method Name"
                + " </font></b></td>");
            html.append("</tr>");

            // Format table such that repeating fields display only once.
            int rows = methodDefs.length - 1;
            for (int i=0; i<methodDefs.length; i++)
            {
              fedora.server.types.gen.MethodDef results = methodDefs[i];
              html.append("<tr>");
              if (i == 0)
              {
                html.append("<td><font color=\"blue\"> " + "<a href=\""
                    + SERVLET_PATH
                    + "action_=GetObjectMethods&PID_="
                    + PID + "\"> " + PID + " </a></font></td>");
                html.append("<td><font color=\"green\"> " + bDefPID
                    + " </font></td>");
                html.append("<td><font color=\"green\"> "
                    + DateUtility.convertDateToString(versDateTime)
                    + "</font></td>");
                html.append("<td><font color=\"red\"> " + "<a href=\""
                    + SERVLET_PATH
                    + "action_=GetDissemination&PID_="
                    + PID + "&bDefPID_=" + bDefPID + "&methodName_="
                    + results.getMethodName() + "\"> "
                    + results.getMethodName()
                    + " </a></td>");
              } else if (i == 1)
              {
                html.append("<td colspan='3' rowspan='" + rows + "'></td>");
                html.append("<td><font color=\"red\"> " + "<a href=\""
                    + SERVLET_PATH
                    + "action_=GetDissemination&PID_="
                    + PID + "&bDefPID_=" + bDefPID + "&methodName_="
                    + results.getMethodName() + "\"> "
                    + results.getMethodName()
                    + " </a></td>");
              } else
              {
                html.append("<td><font color=\"red\"> " + "<a href=\""
                    + SERVLET_PATH
                    + "action_=GetDissemination&PID_="
                    + PID + "&bDefPID_=" + bDefPID + "&methodName_="
                    + results.getMethodName() + "\"> "
                    + results.getMethodName()
                    + " </a></td>");
              }
              html.append("</tr>");
            }
            html.append("</table>");
            html.append("</center>");
            html.append("</body>");
            html.append("</html>");
            out.println(html.toString());
          } else
          {
            // Method Definitions request returned nothing.
            String message = "[FedoraAccessSOAPServlet] No Behavior Methods "
                + "returned.";
            System.err.println(message);
            showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                         userParms, response, message);
          }

          // FIXME!! Needs more refined Exception handling.
        } catch (Exception e)
        {
          String message = "[FedoraAccessSOAPServlet] No Behavior Methods "
              + "returned. <br> Exception: " + e.getClass().getName()
              + " <br> Reason: "  + e.getMessage();
          System.err.println(message);
          showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                       userParms, response, message);
        }
        long stopTime = new Date().getTime();
        long interval = stopTime - servletStartTime;
        System.out.println("[FedoraAccessSOAPServlet] Roundtrip "
            + "GetBehaviorDefinitions: " + interval + " milliseconds.");
      }
      else if (action.equalsIgnoreCase(GET_BEHAVIOR_METHODS_XML))
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
            int byteStream = 0;
            byte[] buffer = new byte[255];
            while ((byteStream = methodResults.read(buffer)) >= 0)
            {
              out.write(buffer, 0, byteStream);
            }
            buffer = null;
          } else
          {
            // Method Definition request in XML form returned nothing.
            String message = "[FedoraAccessSOAPServlet] No Behavior Methods "
                + "returned as XML.";
            System.err.println(message);
            showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                         userParms, response, message);
          }
        } catch (Exception e)
        {
          // FIXME!! Needs more refined Exception handling.
          String message = "[FedoraAccessSOAPServlet] No Behavior Methods "
              + "returned as XML. <br> Exception: " + e.getClass().getName()
              + " <br> Reason: "  + e.getMessage();
          System.err.println(message);
          showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                       userParms, response, message);
        }
        long stopTime = new Date().getTime();
        long interval = stopTime - servletStartTime;
        System.out.println("[FedoraAccessSOAPServlet] Roundtrip "
            + "GetBehaviorMethodsAsWSDL: " + interval + " milliseconds.");
      }
      else if (action.equals(GET_DISSEMINATION))
      {
        try
        {
          // Call Fedora Access SOAP service to request dissemination.
          MIMETypedStream dissemination = null;
          dissemination = getDissemination(PID, bDefPID, methodName,
              userParms, asOfDateTime);
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
            if (dissemination.getMIMEType().
                equalsIgnoreCase("application/fedora-redirect"))
            {
              // A MIME type of application/fedora-redirect signals that the
              // MIMETypedStream returned from the dissemination is a special
              // Fedora-specific MIME type. In this case, teh Fedora server
              // will not proxy the stream, but instead perform a simple
              // redirect to the URL contained within the body of the
              // MIMETypedStream. This special MIME type is used primarily
              // for streaming media.
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
            String message = "[FedoraAccessSOAPServlet] No Dissemination "
                + "result returned. <br> See server logs for additional info";
            System.err.println(message);
            showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                         userParms, response, message);
          }
        } catch (Exception e)
        {
          // FIXME!! Needs more refined Exception handling.
          e.printStackTrace();
          String message = "[FedoraAccessSOAPServlet] No Dissemination "
              + "result returned. <br> Exception: "
              + e.getClass().getName()
              + " <br> Reason: "  + e.getMessage()
              + " <br> See server logs for additional info";
          System.err.println(message);
          showURLParms(action, PID, bDefPID, methodName, asOfDateTime,
                       userParms, response, message);
        }
        long stopTime = new Date().getTime();
        long interval = stopTime - servletStartTime;
        System.out.println("[FedoraAccessSOAPServlet] Roundtrip "
            + "GetDissemination: " + interval + " milliseconds.");
      }
      else if (action.equals(GET_OBJECT_METHODS))
      {
        ObjectMethodsDef[] objMethDefArray = null;
        PipedWriter pw = new PipedWriter();
        PipedReader pr = new PipedReader(pw);

        try
        {
          out = response.getOutputStream();
          pw = new PipedWriter();
          pr = new PipedReader(pw);
          objMethDefArray = getObjectMethods(PID, asOfDateTime);
          if (objMethDefArray != null)
          {
            // Object Methods found.
            // Deserialize ObjectmethodsDef datastructure into XML
            new SerializerThread(PID, objMethDefArray, versDateTime, pw).start();
            if (xmlEncode)
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
              //File xslFile = new File("dist/server/access/objectmethods2.xslt");
              File xslFile = new File("dist/server/access/objectmethods.xslt");
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
            String message = "[FedoraAccessServlet] No Object Method "
                + "Definitions returned.";
            System.out.println(message);
            showURLParms(action, PID, "", "", asOfDateTime, new Property[0],
                         response, message);
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
        long stopTime = new Date().getTime();
        long interval = stopTime - servletStartTime;
        System.out.println("[FedoraAccessSOAPServlet] Roundtrip "
          + "GetObjectMethods: " + interval + " milliseconds.");
      }
      else if (action.equals(GET_OBJECT_PROFILE))
      {
        ObjectProfile objProfile = null;
        PipedWriter pw = new PipedWriter();
        PipedReader pr = new PipedReader(pw);

        try
        {
          out = response.getOutputStream();
          pw = new PipedWriter();
          pr = new PipedReader(pw);
          objProfile = getObjectProfile(PID, asOfDateTime);
          if (objProfile != null)
          {
            // Object Profile found.
            // Deserialize ObjectProfile datastructure into XML
            new ProfileSerializerThread(PID, objProfile, versDateTime, pw).start();
            if (xmlEncode)
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
              File xslFile = new File("dist/server/access/viewObjectProfile.xslt");
              TransformerFactory factory = TransformerFactory.newInstance();
              Templates template = factory.newTemplates(new StreamSource(xslFile));
              Transformer transformer = template.newTransformer();
              Properties details = template.getOutputProperties();
              transformer.transform(new StreamSource(pr), new StreamResult(out));
            }
            pr.close();

          } else
          {
            // No Object Profile returned
            String message = "[FedoraAccessServlet] No Object Profile returned.";
            System.out.println(message);
            showURLParms(action, PID, "", "", asOfDateTime, new Property[0],
                         response, message);
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
        long stopTime = new Date().getTime();
        long interval = stopTime - servletStartTime;
        System.out.println("[FedoraAccessSOAPServlet] Roundtrip "
          + "GetObjectProfile: " + interval + " milliseconds.");
        // end Object Profile processing
      }
      else
      {
        // Action not recognized
        String message = "[FedoraAccessServlet] Requested action not recognized.";
        System.out.println(message);
        showURLParms(action, PID, "", "", asOfDateTime, new Property[0],
                     response, message);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        response.sendError(response.SC_NO_CONTENT, message);
      }
    }
  }

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

  /**
   * <p> A Thread to serialize an ObjectProfile object into XML.</p>
   *
   */
  public class ProfileSerializerThread extends Thread
  {
    private PipedWriter pw = null;
    private String PID = null;
    private ObjectProfile objProfile = null;
    private Date versDateTime = null;

    /**
     * <p> Constructor for ProfileSerializeThread.</p>
     *
     * @param PID The persistent identifier of the specified digital object.
     * @param objProfile An object profile data structure.
     * @param versDateTime The version datetime stamp of the request.
     * @param pw A PipedWriter to which the serialization info is written.
     */
    public ProfileSerializerThread(String PID, ObjectProfile objProfile,
                        Date versDateTime, PipedWriter pw)
    {
      this.pw = pw;
      this.PID = PID;
      this.objProfile = objProfile;
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
            pw.write("<objectProfile "
                + " targetNamespace=\"http://www.fedora.info/definitions/1/0/access/\""
                + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                + " pid=\"" + PID + "\" >");
            pw.write("<import namespace=\"http://www.fedora.info/definitions/1/0/access/\""
                + " location=\"objectProfile.xsd\"/>");
          } else
          {
            pw.write("<objectProfile "
                + " targetNamespace=\"http://www.fedora.info/definitions/1/0/access/\""
                + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                + " pid=\"" + PID + "\""
                + " dateTime=\"" + DateUtility.convertDateToString(versDateTime)
                + "\" >");
            pw.write("<import namespace=\"http://www.fedora.info/definitions/1/0/access/\""
                + " location=\"objectProfile.xsd\"/>");
          }

          // PROFILE FIELDS SERIALIZATION
          pw.write("<objLabel>" + objProfile.getObjLabel() + "</objLabel>");
          pw.write("<objContentModel>" + objProfile.getObjContentModel() + "</objContentModel>");
          String cDate = DateUtility.convertCalendarToString(objProfile.getObjCreateDate());
          pw.write("<objCreateDate>" + cDate + "</objCreateDate>");
          String mDate = DateUtility.convertCalendarToString(objProfile.getObjLastModDate());
          pw.write("<objLastModDate>" + mDate + "</objLastModDate>");
          String objType = objProfile.getObjType();
          pw.write("<objType>");
          if (objType.equalsIgnoreCase("O"))
          {
            pw.write("Fedora Data Object");
          }
          else if (objType.equalsIgnoreCase("D"))
          {
            pw.write("Fedora Behavior Definition Object");
          }
          else if (objType.equalsIgnoreCase("M"))
          {
            pw.write("Fedora Behavior Mechanism Object");
          }
          pw.write("</objType>");
          pw.write("<objDissIndexViewURL>" + objProfile.getObjDissIndexViewURL() + "</objDissIndexViewURL>");
          pw.write("<objItemIndexViewURL>" + objProfile.getObjItemIndexViewURL() + "</objItemIndexViewURL>");
          pw.write("</objectProfile>");
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
    * <p>Gets a object profile for the specified object by
    * invoking the appropriate Fedora Access SOAP service.</p>
    *
    * @param PID The persistent identifier for the digital object.
    * @param asOfDateTime The versioning datetime stamp.
    * @return An object profile data structure.
    * @throws Exception If an error occurs in communicating with the Fedora
    *         Access SOAP service.
    */
  public ObjectProfile getObjectProfile(String PID,
      Calendar asOfDateTime) throws Exception
  {
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    ObjectProfile objProfile = null;
    Service service = new Service();
    Call call = (Call) service.createCall();
    call.setOperationName(new QName(FEDORA_API_URI, GET_OBJECT_PROFILE) );
    QName qn = new QName(FEDORA_TYPE_URI, "ObjectProfile");
    call.setTargetEndpointAddress( new URL(FEDORA_ACCESS_ENDPOINT) );

    // Any Fedora-defined types required by the SOAP service must be registered
    // prior to invocation so the SOAP service knows the appropriate
    // serializer/deserializer to use for these types.
    call.registerTypeMapping(ObjectProfile.class, qn,
        new BeanSerializerFactory(ObjectProfile.class, qn),
        new BeanDeserializerFactory(ObjectProfile.class, qn));
    objProfile =
        (ObjectProfile) call.invoke( new Object[] { PID, asOfDateTime} );
    return objProfile;
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
   * <p>Validates required servlet URL parameters. Different parameters
   * are required based on the requested action.</p>
   *
   * @param action The Fedora service to be executed
   * @param PID The persistent identifier of the Digital Object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param methodName The method name.
   * @param versDateTime The version datetime stamp of the digital object.
   * @param h_userParms A hashtabe of user-supplied method parameters.
   * @param response The servlet response.
   * @return True if required parameters are valid; false otherwise.
   * @throws IOException If an error occurrs with an input or output operation.
   */
  private boolean isValidURLParms(String action, String PID, String bDefPID,
                          String methodName, Date versDateTime,
                          Hashtable h_userParms,
                          HttpServletResponse response)
      throws IOException
  {
    // Check for missing parameters required either by the servlet or the
    // requested Fedora Access SOAP service.
    boolean isValid = true;
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
        html.append("<tr>");
        html.append("<td><font color='red'>methodName_</font></td>");
        html.append("<td> = </td>");
        html.append("<td>" + methodName + "</td>");
        html.append("<td><font color='blue'>(REQUIRED)</font></td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td><font color='red'>asOfDateTime_</font></td>");
        html.append("<td> = </td>");
        html.append("<td>" + versDate + "</td>");
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
      //FIXME!! Validation for any user-supplied parameters not implemented.
    } else if (action != null &&
               (action.equals(GET_BEHAVIOR_DEFINITIONS) ||
                action.equals(GET_OBJECT_METHODS) ||
                action.equals(GET_OBJECT_PROFILE)))
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
   * @param response The servlet response.
   * @param message The message text to include at the top of the output page.
   * @throws IOException If an error occurrs with an input or output operation.
   */
  private void showURLParms(String action, String PID, String bDefPID,
                           String methodName, Calendar asOfDateTime,
                           Property[] userParms,
                           HttpServletResponse response,
                           String message)
      throws IOException
  {

    String versDate = DateUtility.convertCalendarToString(asOfDateTime);
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

    System.err.println("REQUEST Returned NO Data");
  }
}
