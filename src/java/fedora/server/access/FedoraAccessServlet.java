package fedora.server.access;

import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.net.URLDecoder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import com.icl.saxon.expr.StringValue;

import fedora.server.Context;
import fedora.server.Logging;
import fedora.server.ReadOnlyContext;
import fedora.server.utilities.TypeUtility;
import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOManager;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.Property;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.utilities.DateUtility;

/**
 * <p>Title: FedoraAccessServlet.java</p>
 * <p>Description: Implements Fedora Access LITE (API-A-LITE) interface using a
 * java servlet front end. The syntax defined by API-A-LITE has two bindings:
 * <ol>
 * <li>http://hostname:port/PID/bDefPID/methodName[/dateTime][?parmArray] - this
 *     syntax requests a dissemination of the specified object using the
 *     specified method of the associated behavior definition object. The result
 *     is returned as a MIME-typed stream.</li>
 * <ul>
 * <li>hostname - required hostname of the Fedora server.</li>
 * <li>port - required port number on which the Fedora server is running.</li>
 * <li>PID - required persistent idenitifer of the digital object.</li>
 * <li>bDefPID - required persistent identifier of the behavior definition
 *               object to which the digital object subscribes.</li>
 * <li>methodName - required name of the method to be executed.</li>
 * <li>dateTime - optional dateTime value indicating dissemination of a
 *     version of the digital object at the specified point in time. (NOT
 *     implemented in release 1.0.)
 * <li>parmArray - optional array of method parameters consisting of
 *     name/value pairs in the form parm1=value1&parm2=value2...</li>
 * </ul>
 * <li>http://hostname:port/PID[/dateTime][?xmlEncode=BOOLEAN] - this syntax
 *     requests a list all methods associated with the specified digital
 *     object. The xmlEncode parameter determines the type of output returned.
 *     If the parameter is omitted or has a value of "no", a MIME-typed stream
 *     consisting of an html table is returned providing a browser-savvy means
 *     of browsing the object's methods. If the value specified is "yes", then
 *     a MIME-typed stream consisting of XML is returned.</li>
 * <ul>
 * <li>hostname - required hostname of the Fedora server.</li>
 * <li>port - required port number on which the Fedora server is running.</li>
 * <li>PID - required persistent identifier of the digital object.</li>
 * <li>dateTime - optional dateTime value indicating dissemination of a
 *                version of the digital object at the specified point in time.
 *                (NOT implemented in release 1.0.)
 * <li>xmlEncode - an optional parameter indicating the requested output format.
 *                 A value of "yes" indicates a return type of text/xml; the
 *                 absence of the xmlEncode parameter or a value of "no"
 *                 indicates format is to be text/html.</li>
 * </ul>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class FedoraAccessServlet extends HttpServlet implements Logging
{
  /** Content type for html. */
  private static final String CONTENT_TYPE_HTML = "text/html";

  /** Content type for xml. */
  private static final String CONTENT_TYPE_XML  = "text/xml";

  /** Instance of the Fedora server. */
  private static Server s_server = null;

  /** Instance of the access subsystem. */
  private static Access s_access = null;

  /** Constant indicating value of the string "yes". */
  private static final String YES = "yes";

  /** Instance of DOManager. */
  private static DOManager m_manager = null;

  private Hashtable h_userParms = new Hashtable();
  private String requestURL = null;
  private String requestURI = null;
  private URLDecoder decoder = new URLDecoder();


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
    Calendar asOfDateTime = null;
    Date versDateTime = null;
    String action = null;
    Property[] userParms = null;
    long servletStartTime = new Date().getTime();
    boolean isGetObjectMethodsRequest = false;
    boolean isGetObjectProfileRequest = false;
    boolean isGetDisseminationRequest = false;

    HashMap h=new HashMap();
    h.put("application", "apia");
    h.put("useCachedObject", "true");
    h.put("userId", "fedoraAdmin");
    h.put("host", request.getRemoteAddr());
    ReadOnlyContext context = new ReadOnlyContext(h);

    requestURI = request.getRequestURL().toString() + "?"
        + request.getQueryString();

    // Parse servlet URL.
    String[] URIArray = request.getRequestURL().toString().split("/");
    if (URIArray.length == 6 || URIArray.length == 7)
    {
      PID = decoder.decode(URIArray[5], "UTF-8");
      if (URIArray.length > 6)
      {
        versDateTime = DateUtility.convertStringToDate(URIArray[6]);
      }
      logFinest("[FedoraAccessServlet] GetObjectMethods Syntax "
          + "Encountered: "+ requestURI);
      logFinest("PID: " + PID + " bDefPID: "
          + " asOfDate: " + versDateTime);
      //isGetObjectMethodsRequest = true;
      isGetObjectProfileRequest = true;

    } else if (URIArray.length > 7)
    {
      PID = decoder.decode(URIArray[5],"UTF-8");
      bDefPID = decoder.decode(URIArray[6],"UTF-8");
      methodName = URIArray[7];
      if (URIArray.length > 8)
      {
        versDateTime = DateUtility.convertStringToDate(URIArray[8]);
      }
      logFinest("[FedoraAccessServlet] Dissemination Syntax "
          + "Encountered");
      logFinest("PID: " + PID + " bDefPID: " + bDefPID
          + " methodName: " + methodName + " asOfDate: " + versDateTime);
      isGetDisseminationRequest = true;

    } else
    {
      String message = "Dissemination Request Syntax Error: Required fields "
          + "are missing in the Dissemination Request. The expected syntax "
          + "is: "+URIArray[0]+"//"+URIArray[2]+URIArray[3]+URIArray[4]
          + "/PID/bDefPID/methodName[/dateTime][?ParmArray] \"  "
          + "Submitted request was: \"" + requestURI + "\"  .  ";
      logWarning(message);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.sendError(response.SC_INTERNAL_SERVER_ERROR, message);

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

    try
    {
/*
      if (isGetObjectMethodsRequest)
      {
        getObjectMethods(context, PID, asOfDateTime, xmlEncode, request, response);
        long stopTime = new Date().getTime();
        long interval = stopTime - servletStartTime;
        System.out.println("[FedoraAccessServlet] Servlet Roundtrip "
            + "GetObjectMethods: " + interval + " milliseconds.");
        logFiner("[FedoraAccessServlet] Servlet Roundtrip "
            + "GetObjectMethods: " + interval + " milliseconds.");
      }
*/
      if (isGetObjectProfileRequest)
      {
        getObjectProfile(context, PID, asOfDateTime, xmlEncode, request, response);
        long stopTime = new Date().getTime();
        long interval = stopTime - servletStartTime;
        System.out.println("[FedoraAccessServlet] Servlet Roundtrip "
            + "GetObjectprofile: " + interval + " milliseconds.");
        logFiner("[FedoraAccessServlet] Servlet Roundtrip "
            + "GetObjectProfile: " + interval + " milliseconds.");
      }
      else if (isGetDisseminationRequest)
      {
        getDissemination(context, PID, bDefPID, methodName, userParms, asOfDateTime,
                         response);
        long stopTime = new Date().getTime();
        long interval = stopTime - servletStartTime;
        System.out.println("[FedoraAccessServlet] Servlet Roundtrip "
                           + "GetDissemination: " + interval + " milliseconds.");
        logFiner("[FedoraAccessServlet] Servlet Roundtrip "
            + "GetDissemination: " + interval + " milliseconds.");
      }
      } catch (ServerException se)
      {
        String message = "[FedoraAccessServlet] An error has occured in "
            + "accessing the Fedora Access Subsystem. The error was \" "
            + se.getClass().getName()
            + " \". Reason: "  + se.getMessage();
        showURLParms(PID, bDefPID, methodName, asOfDateTime,
                     userParms, response, message);
        //response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        //response.sendError(response.SC_INTERNAL_SERVER_ERROR, message);
        logWarning(message);
        se.printStackTrace();
    }
  }

  public void getObjectProfile(Context context, String PID, Calendar asOfDateTime,
      String xmlEncode, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServerException
  {

    ServletOutputStream out = response.getOutputStream();
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    ObjectProfile objProfile = null;
    PipedWriter pw = new PipedWriter();
    PipedReader pr = new PipedReader(pw);

    try
    {
      out = response.getOutputStream();
      pw = new PipedWriter();
      pr = new PipedReader(pw);
      objProfile = s_access.getObjectProfile(context, PID, asOfDateTime);
      if (objProfile != null)
      {
        // Object Methods found.
        // Deserialize ObjectmethodsDef datastructure into XML
        new ProfileSerializerThread(PID, objProfile, versDateTime, pw).start();
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
          File xslFile = new File(s_server.getHomeDir(), "access/viewObjectProfile.xslt");
          TransformerFactory factory = TransformerFactory.newInstance();
          Templates template = factory.newTemplates(new StreamSource(xslFile));
          Transformer transformer = template.newTransformer();
          Properties details = template.getOutputProperties();
          transformer.transform(new StreamSource(pr), new StreamResult(out));
        }
        pr.close();

      } else
      {
        // Object Profile Definition request returned nothing.
        String message = "[FedoraAccessServlet] No Object Profile returned.";
        logInfo(message);
        showURLParms(PID, "", "", asOfDateTime, new Property[0], response, message);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        response.sendError(response.SC_NO_CONTENT, message);
      }
    } catch (Throwable th)
    {
      String message = "[FedoraAccessServlet] An error has occured. "
                     + " The error was a \" "
                     + th.getClass().getName()
                     + " \". Reason: "  + th.getMessage();
      logWarning(message);
      th.printStackTrace();
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.sendError(response.SC_INTERNAL_SERVER_ERROR, message);
    } finally
    {
        if (pr != null) pr.close();
    }
    out.close();
  }

  /**
   * <p> A Thread to serialize an ObjectMethodsDef object into XML.</p>
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
     * @param objMethDefArray An array of object mtehod definitions.
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
          pw.write("<objLabel>" + objProfile.objectLabel + "</objLabel>");
          pw.write("<objContentModel>" + objProfile.objectContentModel + "</objContentModel>");
          pw.write("<objCreateDate>" + objProfile.objectCreateDate + "</objCreateDate>");
          pw.write("<objLastModDate>" + objProfile.objectLastModDate + "</objLastModDate>");
          String objType = objProfile.objectType;
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
          pw.write("<objDissIndexViewURL>" + objProfile.dissIndexViewURL + "</objDissIndexViewURL>");
          pw.write("<objItemIndexViewURL>" + objProfile.itemIndexViewURL + "</objItemIndexViewURL>");
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
   * @throws ServerException If an error occurs in the Access Subsystem.
   */
  public void getObjectMethods(Context context, String PID, Calendar asOfDateTime,
      String xmlEncode, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServerException
  {

    ServletOutputStream out = response.getOutputStream();
    Date versDateTime = DateUtility.convertCalendarToDate(asOfDateTime);
    ObjectMethodsDef[] objMethDefArray = null;
    PipedWriter pw = new PipedWriter();
    PipedReader pr = new PipedReader(pw);

    try
    {
      pw = new PipedWriter();
      pr = new PipedReader(pw);
      objMethDefArray = s_access.getObjectMethods(context, PID, asOfDateTime);
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
          File xslFile = new File(s_server.getHomeDir(), "access/objectmethods.xslt");
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
        logInfo(message);
        showURLParms(PID, "", "", asOfDateTime, new Property[0], response, message);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        response.sendError(response.SC_NO_CONTENT, message);
      }
    } catch (Throwable th)
    {
      String message = "[FedoraAccessServlet] An error has occured. "
                     + " The error was a \" "
                     + th.getClass().getName()
                     + " \". Reason: "  + th.getMessage();
      logWarning(message);
      th.printStackTrace();
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.sendError(response.SC_INTERNAL_SERVER_ERROR, message);
    } finally
    {
        if (pr != null) pr.close();
    }
    out.close();
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
              if (methodParms[j].parmDomainValues.length > 0 )
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
   * <p>This method calls the Fedora Access Subsystem to retrieve a MIME-typed
   * stream corresponding to the dissemination request.</p>
   *
   * @param context The read only context of the request.
   * @param PID The persistent identifier of the Digital Object.
   * @param bDefPID The persistent identifier of the Behavior Definition object.
   * @param methodName The method name.
   * @param userParms An array of user-supplied method parameters.
   * @param asOfDateTime The version datetime stamp of the digital object.
   * @param response The servlet response.
   * @throws IOException If an error occurrs with an input or output operation.
   * @throws ServerException If an error occurs in the Access Subsystem.
   */
  public void getDissemination(Context context, String PID, String bDefPID, String methodName,
      Property[] userParms, Calendar asOfDateTime, HttpServletResponse response)
      throws IOException, ServerException
  {
    ServletOutputStream out = response.getOutputStream();
    MIMETypedStream dissemination = null;
    dissemination =
        s_access.getDissemination(context, PID, bDefPID, methodName,
                                  userParms, asOfDateTime);
    if (dissemination != null)
    {
      // Dissemination was successful;
      // Return MIMETypedStream back to browser client
      if (dissemination.MIMEType.equalsIgnoreCase("application/fedora-redirect"))
      {
        // A MIME type of application/fedora-redirect signals that the
        // MIMETypedStream returned from the dissemination is a special
        // Fedora-specific MIME type. In this case, teh Fedora server will
        // not proxy the stream, but instead perform a simple redirect to
        // the URL contained within the body of the MIMETypedStream. This
        // special MIME type is used primarily for streaming media.

        // RLW: change required by conversion fom byte[] to InputStream
        BufferedReader br = new BufferedReader(
            new InputStreamReader(dissemination.getStream()));
        //BufferedReader br = new BufferedReader(
        //    new InputStreamReader(
        //        new ByteArrayInputStream(dissemination.stream)));
        // RLW: change required by conversion fom byte[] to InputStream
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = br.readLine()) != null)
        {
          sb.append(line);
        }

        response.sendRedirect(sb.toString());
      } else
      {
        response.setContentType(dissemination.MIMEType);
        long startTime = new Date().getTime();
        int byteStream = 0;
        // RLW: change required by conversion fom byte[] to InputStream
        //ByteArrayInputStream dissemResult =
        //    new ByteArrayInputStream(dissemination.stream);
        InputStream dissemResult = dissemination.getStream();
        // RLW: change required by conversion fom byte[] to InputStream
        /*while ((byteStream = dissemResult.read()) != -1)
        {
          out.write(byteStream);
        }*/
        byte[] buffer = new byte[255];
        while ((byteStream = dissemResult.read(buffer)) != -1)
        {
          out.write(buffer, 0, byteStream);
        }
        buffer = null;
        dissemResult.close();
        dissemResult = null;
        long stopTime = new Date().getTime();
        long interval = stopTime - startTime;
        System.out.println("[FedoraAccessServlet] Read InputStream: "
                           + interval + " milliseconds.");
        logFiner("[FedoraAccessServlet] Read InputStream "
            + interval + " milliseconds.");
      }
    } else
    {
      // Dissemination request failed; echo back request parameter.
      String message = "[FedoraAccessServlet] No Dissemination Result "
          + " was returned.";
      showURLParms(PID, bDefPID, methodName, asOfDateTime, userParms,
                  response, message);
      logInfo(message);
      response.setStatus(HttpServletResponse.SC_NO_CONTENT);
      response.sendError(response.SC_NO_CONTENT, message);
    }
    out.close();
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
  {
    try
    {
      s_server=Server.getInstance(new File(System.getProperty("fedora.home")));
      m_manager=(DOManager) s_server.getModule("fedora.server.storage.DOManager");
      s_access = (Access) s_server.getModule("fedora.server.access.Access");
    } catch (InitializationException ie)
    {
      throw new ServletException("Unable to get Fedora Server instance."
          + ie.getMessage());
    }
  }

  /**
   * <p>Cleans up servlet resources.</p>
   */
  public void destroy()
  {}

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
   * @param response The servlet response.
   * @param message The message text to include at the top of the output page.
   * @throws IOException If an error occurrs with an input or output operation.
   */
  private void showURLParms(String PID, String bDefPID,
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
    html.append("<td><font color='red'>PID</td>");
    html.append("<td> = <td>" + PID + "</td>");
    html.append("</tr>");
    html.append("<tr>");
    html.append("<td><font color='red'>bDefPID</td>");
    html.append("<td> = </td>");
    html.append("<td>" + bDefPID + "</td>");
    html.append("</tr>");
    html.append("<tr>");
    html.append("<td><font color='red'>methodName</td>");
    html.append("<td> = </td>");
    html.append("<td>" + methodName + "</td>");
    html.append("</tr>");
    html.append("<tr>");
    html.append("<td><font color='red'>asOfDateTime</td>");
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
      html.append("<td><font color='red'>" + userParms[i].name
                  + "</font></td>");
      html.append("<td> = </td>");
      html.append("<td>" + userParms[i].value + "</td>");
        html.append("</tr>");
    }
    }
    html.append("</table></center></font>");
    html.append("</body></html>");
    out.println(html.toString());

    logFinest("PID: " + PID + " bDefPID: " + bDefPID
              + " methodName: " + methodName);
    if (userParms != null)
    {
      for (int i=0; i<userParms.length; i++)
      {
        logFinest("userParm: " + userParms[i].name
        + " userValue: "+userParms[i].value);
      }
    }

    out.close();
    html = null;
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