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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import fedora.server.Context;
import fedora.server.ReadOnlyContext;
import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.GeneralException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.security.Authorization;
import fedora.server.storage.types.DatastreamDef;
import fedora.server.utilities.DateUtility;
import fedora.server.utilities.Logger;
import fedora.server.utilities.StreamUtility;

/**
 * <p><b>Title: </b>ListDatastreamsServlet.java</p>
 * <p><b>Description: </b>Implements listDatastreams method of Fedora Access
 * LITE (API-A-LITE) interface using a java servlet front end.
 * <ol>
 * <li>ListDatastreams URL syntax:
 * <p>http://hostname:port/fedora/listDatastreams/PID[/dateTime][?xml=BOOLEAN]</p>
 * <p>This syntax requests a list of datastreams for the specified digital object.
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
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class ListDatastreamsServlet extends HttpServlet
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
      boolean isListDatastreamsRequest = false;
      boolean xml = false;
 
      requestURI = request.getRequestURL().toString() + "?" + request.getQueryString();

      // Parse servlet URL.
      String[] URIArray = request.getRequestURL().toString().split("/");
      if (URIArray.length == 6 || URIArray.length == 7) {
          // Request is either unversioned or versioned listDatastreams request
          try {
              PID = Server.getPID(URIArray[5]).toString();  // normalize the PID
          } catch (Throwable th) {
              String message = "[ListDatastreamsServlet] An error has occured in "
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
              // Request is a versioned listDatastreams request
              versDateTime = DateUtility.convertStringToDate(URIArray[6]);
              if (versDateTime == null) {
                  String message = "ListDatastreams Request Syntax Error: DateTime value "
                      + "of \"" + URIArray[6] + "\" is not a valid DateTime format. "
                      + " ----- The expected format for DateTime is \""
                      + "YYYY-MM-DDTHH:MM:SS.SSSZ\".  "
                      + " ----- The expected syntax for "
                      + "ListDatastreams requests is: \""
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
          logger.logFinest("[ListDatastreamsServlet] ListDatastreams Syntax "
              + "Encountered: "+ requestURI);
          logger.logFinest("PID: " + PID + " asOfDate: " + versDateTime);
          isListDatastreamsRequest = true;
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
          if (isListDatastreamsRequest && name.equalsIgnoreCase("xml"))
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
          if (isListDatastreamsRequest) {    
              Context context = ReadOnlyContext.getContext(Authorization.ENVIRONMENT_REQUEST_MESSAGE_PROTOCOL_REST, request, ReadOnlyContext.DO_NOT_USE_CACHED_OBJECT);
              listDatastreams(context, PID, asOfDateTime, xml, request, response);
              long stopTime = new Date().getTime();
              long interval = stopTime - servletStartTime;
              logger.logFiner("[ListDatastreamsServlet] Servlet Roundtrip "
                  + "listDatastreams: " + interval + " milliseconds.");
          }

          } catch (Throwable th)
          {
              String message = "[ListDatastreamsServlet] An error has occured in "
                  + "accessing the Fedora Access Subsystem. The error was \" "
                  + th.getClass().getName()
                  + " \". Reason: "  + th.getMessage()
                  + "  Input Request was: \"" + request.getRequestURL().toString();
              logger.logWarning(message);
              th.printStackTrace();
          }
  }

  public void listDatastreams(Context context, String PID, Date asOfDateTime,
      boolean xml, HttpServletRequest request,
      HttpServletResponse response) throws ServerException
  {

      OutputStreamWriter out = null;
      Date versDateTime = asOfDateTime;
      DatastreamDef[] dsDefs = null;
      PipedWriter pw = null;
      PipedReader pr = null;

      try
      {
          pw = new PipedWriter();
          pr = new PipedReader(pw);
          dsDefs = s_access.listDatastreams(context, PID, asOfDateTime);

          // Object Profile found.
          // Serialize the ObjectProfile object into XML
          new DatastreamDefSerializerThread(PID, dsDefs, versDateTime, pw).start();
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
              File xslFile = new File(s_server.getHomeDir(), "access/listDatastreams.xslt");
              TransformerFactory factory = TransformerFactory.newInstance();
              Templates template = factory.newTemplates(new StreamSource(xslFile));
              Transformer transformer = template.newTransformer();
              Properties details = template.getOutputProperties();
              transformer.transform(new StreamSource(pr), new StreamResult(out));
          }
          out.flush();
          } catch (Throwable th)
          {
              String message = "[ListDatastreamsServlet] An error has occured. "
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
                      String message = "[ListDatastreamsServlet] An error has occured. "
                          + " The error was a \" "
                          + th.getClass().getName()
                          + " \". Reason: "  + th.getMessage();
                      throw new StreamIOException(message);
                  }
          }
  }

  /**
   * <p> A Thread to serialize a DatastreamDef object into XML.</p>
   *
   */
  public class DatastreamDefSerializerThread extends Thread
  {
      private PipedWriter pw = null;
      private String PID = null;
      private DatastreamDef[] dsDefs = null;
      private Date versDateTime = null;

      /**
       * <p> Constructor for ProfileSerializeThread.</p>
       *
       * @param PID The persistent identifier of the specified digital object.
       * @param dsDefs An array of DatastreamDefs.
       * @param versDateTime The version datetime stamp of the request.
       * @param pw A PipedWriter to which the serialization info is written.
       */
      public DatastreamDefSerializerThread(String PID, DatastreamDef[] dsDefs,
              Date versDateTime, PipedWriter pw)
      {
          this.pw = pw;
          this.PID = PID;
          this.dsDefs = dsDefs;
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
                      pw.write("<objectDatastreams "
                          + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
                          + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                          + "xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/access/ "
                          + "http://" + fedoraServerHost + ":" + fedoraServerPort
                          + "/listDatastreams.xsd\"" + " pid=\"" + PID + "\" "
                          + "baseURL=\"http://" + fedoraServerHost + ":" + fedoraServerPort + "/fedora/\" "
                          + ">");
                  } else
                  {
                      pw.write("<objectDatastreams "
                          + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
                          + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                          + "xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/access/ "
                          + "http://" + fedoraServerHost + ":" + fedoraServerPort
                          + "/listDatastreams.xsd\"" + " pid=\"" + StreamUtility.enc(PID) + "\" "
                          + "asOfDateTime=\"" + DateUtility.convertDateToString(versDateTime) + "\" "
                          + "baseURL=\"http://" + fedoraServerHost + ":" + fedoraServerPort + "/fedora/\" "
                          + ">");
                  }

                  // DatastreamDef SERIALIZATION
                  for (int i=0; i<dsDefs.length; i++) {
                      pw.write("    <datastream "
                          + "dsid=\"" + StreamUtility.enc(dsDefs[i].dsID) + "\" "
                          + "label=\"" + StreamUtility.enc(dsDefs[i].dsLabel) + "\" "
                          + "mimeType=\"" + StreamUtility.enc(dsDefs[i].dsMIME)  + "\" />");
                  }
                  pw.write("</objectDatastreams>");
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
