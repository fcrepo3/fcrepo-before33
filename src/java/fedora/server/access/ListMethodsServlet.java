package fedora.server.access;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.net.URLDecoder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import fedora.common.Constants;
import fedora.server.Context;
import fedora.server.ReadOnlyContext;
import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.utilities.DateUtility;
import fedora.server.utilities.Logger;
import fedora.server.utilities.StreamUtility;

/**
 * <p><b>Title: </b>ListMethodsServlet.java</p>
 * <p><b>Description: </b>Implements listMethods method of Fedora Access
 * LITE (API-A-LITE) interface using a java servlet front end.
 * <ol>
 * <li>ListMethods URL syntax:
 * <p>http://hostname:port/fedora/listMethods/PID[/dateTime][?xml=BOOLEAN]</p>
 * <p>This syntax requests a list of methods for the specified digital object.
 * The xml parameter determines the type of output returned.
 * If the parameter is omitted or has a value of "false", a MIME-typed stream
 * consisting of an html table is returned providing a browser-savvy means
 * of viewing the object profile. If the value specified is "true", then
 * a MIME-typed stream consisting of XML is returned.</p></li>
 * <ul>
 * <li>hostname - required hostname of the Fedora server.</li>
 * <li>port - required port number on which the Fedora server is running.</li>
 * <li>fedora - required name of the Fedora access service.</li>
 * <li>get - required verb of the Fedora service.</li>
 * <li>PID - required persistent identifier of the digital object.</li>
 * <li>dateTime - optional dateTime value indicating dissemination of a
 *                version of the digital object at the specified point in time.
 *                (NOT currently implemented.)
 * <li>xml - an optional parameter indicating the requested output format.
 *           A value of "true" indicates a return type of text/xml; the
 *           absence of the xml parameter or a value of "false"
 *           indicates format is to be text/html.</li>
 * </ul>
 * </ol>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class ListMethodsServlet extends HttpServlet
{
  /** Content type for html. */
  private static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";

  /** Content type for xml. */
  private static final String CONTENT_TYPE_XML  = "text/xml; charset=UTF-8";

  /** Instance of the Fedora server. */
  private static Server s_server = null;

  /** Instance of the access subsystem. */
  private static Access s_access = null;

  /** Portion of initial request URL from protocol up to query string */
  private String requestURI = null;

  /** Instance of URLDecoder */
  private URLDecoder decoder = new URLDecoder();

  /** Host name of the Fedora server **/
  private static String fedoraServerHost = null;

  /** Port number on which the Fedora server is running. **/
  private static String fedoraServerPort = null;

  /** Instance of Logger to log servlet events in Fedora server log */
  private static Logger logger = null;

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
      String dsID = null;
      Date asOfDateTime = null;
      Date versDateTime = null;
      long servletStartTime = new Date().getTime();
      boolean isListMethodsRequest = false;
      boolean xml = false;
      requestURI = request.getRequestURL().toString() + "?" + request.getQueryString();

      // Parse servlet URL.
      String[] URIArray = request.getRequestURL().toString().split("/");
      if (URIArray.length == 6 || URIArray.length == 7) {
          // Request is either unversioned or versioned listMethods request
          try {
              PID = Server.getPID(URIArray[5]).toString();  // normalize the PID
          } catch (Throwable th) {
              String message = "[FedoraAccessServlet] An error has occured in "
                      + "accessing the Fedora Access Subsystem. The error was \" "
                      + th.getClass().getName()
                      + " \". Reason: "  + th.getMessage()
                      + "  Input Request was: \"" + request.getRequestURL().toString();
              logger.logWarning(message);
              response.setContentType(CONTENT_TYPE_HTML);
              ServletOutputStream out = response.getOutputStream();
              out.println("<html><body><h3>" + message + "</h3></body></html>");
              return;
        }
          if (URIArray.length == 7) {
              // Request is a versioned listMethods request
              versDateTime = DateUtility.convertStringToDate(URIArray[6]);
              if (versDateTime == null) {
                  String message = "ListMethods Request Syntax Error: DateTime value "
                      + "of \"" + URIArray[6] + "\" is not a valid DateTime format. "
                      + " ----- The expected format for DateTime is \""
                      + "YYYY-MM-DDTHH:MM:SS.SSSZ\".  "
                      + " ----- The expected syntax for "
                      + "ListMethods requests is: \""
                      + URIArray[0] + "//" + URIArray[2] + "/"
                      + URIArray[3] + "/" + URIArray[4]
                      + "/PID[/dateTime] \"  ."
                      + " ----- Submitted request was: \"" + requestURI + "\"  .  ";
                  logger.logWarning(message);
                  response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                  response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
                  return;
              } else {
                  asOfDateTime=versDateTime;
              }
          }
          logger.logFinest("[ListMethodsServlet] ListMethods Syntax "
              + "Encountered: "+ requestURI);
          logger.logFinest("PID: " + PID + " asOfDate: " + versDateTime);
          isListMethodsRequest = true;
      } else {
          // Bad syntax; redirect to syntax documentation page.
          response.sendRedirect("/userdocs/client/browser/apialite/index.html");
          return;
      }

      // Separate out servlet parameters from method parameters
      Hashtable h_parms = new Hashtable();
      for ( Enumeration e = request.getParameterNames(); e.hasMoreElements();)
      {
          String name = URLDecoder.decode((String)e.nextElement(), "UTF-8");
          if (isListMethodsRequest && name.equalsIgnoreCase("xml"))
          {
              xml = new Boolean(request.getParameter(name)).booleanValue();
          }
          else
          {
              String value = URLDecoder.decode(request.getParameter(name), "UTF-8");
              h_parms.put(name,value);
          }
      }

      try {
          if (isListMethodsRequest) {
              Context context = ReadOnlyContext.getContext(Constants.HTTP_REQUEST.REST.uri, request, ReadOnlyContext.DO_NOT_USE_CACHED_OBJECT);
              listMethods(context, PID, asOfDateTime, xml, request, response);
              long stopTime = new Date().getTime();
              long interval = stopTime - servletStartTime;
              logger.logFiner("[ListMethodsServlet] Servlet Roundtrip "
                  + "listMethods: " + interval + " milliseconds.");
          }

          } catch (Throwable th)
          {
              String message = "[ListMethodsServlet] An error has occured in "
                             + "accessing the Fedora Access Subsystem. The error was \" "
                             + th.getClass().getName()
                             + " \". Reason: "  + th.getMessage()
                             + "  Input Request was: \"" + request.getRequestURL().toString();
              logger.logWarning(message);
              th.printStackTrace();
          }
  }

  public void listMethods(Context context, String PID, Date asOfDateTime,
      boolean xml, HttpServletRequest request,
      HttpServletResponse response) throws ServerException
  {

      OutputStreamWriter out = null;
      Date versDateTime = asOfDateTime;
      ObjectMethodsDef[] methodDefs = null;
      PipedWriter pw = null;
      PipedReader pr = null;

      try
      {
          pw = new PipedWriter();
          pr = new PipedReader(pw);
          methodDefs = s_access.listMethods(context, PID, asOfDateTime);

          // Object Profile found.
          // Serialize the ObjectProfile object into XML
          new ObjectMethodsDefSerializerThread(PID, methodDefs, versDateTime, pw).start();
          if (xml)
          {
              // Return results as raw XML
              response.setContentType(CONTENT_TYPE_XML);

              // Insures stream read from PipedReader correctly translates utf-8
              // encoded characters to OutputStreamWriter.
              out = new OutputStreamWriter(response.getOutputStream(),"UTF-8");
              int bufSize = 4096;
              char[] buf=new char[bufSize];
              int len=0;
              while ( (len = pr.read(buf, 0, bufSize)) != -1) {
                  out.write(buf, 0, len);
              }
              out.flush();
          } else
          {
              // Transform results into an html table
              response.setContentType(CONTENT_TYPE_HTML);
              out = new OutputStreamWriter(response.getOutputStream(),"UTF-8");
              File xslFile = new File(s_server.getHomeDir(), "access/listMethods.xslt");
              TransformerFactory factory = TransformerFactory.newInstance();
              Templates template = factory.newTemplates(new StreamSource(xslFile));
              Transformer transformer = template.newTransformer();
              Properties details = template.getOutputProperties();
              transformer.transform(new StreamSource(pr), new StreamResult(out));
          }
          out.flush();
          } catch (Throwable th)
          {
              String message = "[ListMethodsServlet] An error has occured. "
                             + " The error was a \" "
                             + th.getClass().getName()
                             + " \". Reason: "  + th.getMessage();
              logger.logWarning(message);
              th.printStackTrace();
              throw new GeneralException(message);
          } finally
          {
              try
              {
                  if (pr != null) pr.close();
                  if (out != null) out.close();
                  } catch (Throwable th)
                  {
                      String message = "[ListMethodsServlet] An error has occured. "
                          + " The error was a \" "
                          + th.getClass().getName()
                          + " \". Reason: "  + th.getMessage();
                      throw new StreamIOException(message);
                  }
          }
  }

  /**
   * <p> A Thread to serialize an ObjectMethodDef object into XML.</p>
   *
   */
  public class ObjectMethodsDefSerializerThread extends Thread
  {
      private PipedWriter pw = null;
      private String PID = null;
      private ObjectMethodsDef[] methodDefs = null;
      private Date versDateTime = null;

      /**
       * <p> Constructor for ProfileSerializeThread.</p>
       *
       * @param PID The persistent identifier of the specified digital object.
       * @param methodDefs An array of ObjectMethodsDefs.
       * @param versDateTime The version datetime stamp of the request.
       * @param pw A PipedWriter to which the serialization info is written.
       */
      public ObjectMethodsDefSerializerThread(String PID, ObjectMethodsDef[] methodDefs,
              Date versDateTime, PipedWriter pw)
      {
          this.pw = pw;
          this.PID = PID;
          this.methodDefs = methodDefs;
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
                  pw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                  if (versDateTime == null || DateUtility.
                      convertDateToString(versDateTime).equalsIgnoreCase(""))
                  {
                      pw.write("<objectMethods "
                          + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
                          + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                          + "xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/access/ "
                          + "http://" + fedoraServerHost + ":" + fedoraServerPort
                          + "/listMethods.xsd\"" + " pid=\"" + StreamUtility.enc(PID) + "\" "
                          + "baseURL=\"http://" + fedoraServerHost + ":" + fedoraServerPort
                          + "/fedora/\" >");
                  } else
                  {
                      pw.write("<objectMethods "
                          + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
                          + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                          + "xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/access/ "
                          + "http://" + fedoraServerHost + ":" + fedoraServerPort
                          + "/listMethods.xsd\"" + " pid=\"" + StreamUtility.enc(PID) + "\" "
                          + "asOfDateTime=\"" + DateUtility.convertDateToString(versDateTime) + "\" "
                          + "baseURL=\"http://" + fedoraServerHost + ":" + fedoraServerPort + "/fedora/\""
                          + " >");
                  }

                  // ObjectMethodsDef SERIALIZATION
                  String nextBdef = "null";
                  String currentBdef = "";
                  for (int i=0; i<methodDefs.length; i++)
                  {
                      currentBdef = methodDefs[i].bDefPID;
                      if (!currentBdef.equalsIgnoreCase(nextBdef))
                      {
                          if (i != 0) pw.write("</bDef>");
                          pw.write("<bDef pid=\"" + StreamUtility.enc(methodDefs[i].bDefPID) + "\" >");
                      }
                      pw.write("<method name=\"" + StreamUtility.enc(methodDefs[i].methodName) + "\" >");
                      MethodParmDef[] methodParms = methodDefs[i].methodParmDefs;
                      for (int j=0; j<methodParms.length; j++)
                      {
                          pw.write("<methodParm parmName=\"" + StreamUtility.enc(methodParms[j].parmName)
                              + "\" parmDefaultValue=\"" + StreamUtility.enc(methodParms[j].parmDefaultValue)
                              + "\" parmRequired=\"" + methodParms[j].parmRequired
                              + "\" parmLabel=\"" + StreamUtility.enc(methodParms[j].parmLabel) + "\" >");
                          if (methodParms[j].parmDomainValues.length > 0 )
                          {
                              pw.write("<methodParmDomain>");
                              for (int k=0; k<methodParms[j].parmDomainValues.length; k++)
                              {
                                  pw.write("<methodParmValue>" + StreamUtility.enc(methodParms[j].parmDomainValues[k])
                                      + "</methodParmValue>");
                              }
                              pw.write("</methodParmDomain>");
                          }
                          pw.write("</methodParm>");
                      }

                      pw.write("</method>");
                      nextBdef = currentBdef;
                  }
                  pw.write("</bDef>");
                  pw.write("</objectMethods>");

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
          s_server=Server.getInstance(new File(System.getProperty("fedora.home")), false);
          fedoraServerHost = s_server.getParameter("fedoraServerHost");
          fedoraServerPort = s_server.getParameter("fedoraServerPort");
          s_access = (Access) s_server.getModule("fedora.server.access.Access");
          logger = new Logger();
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

}