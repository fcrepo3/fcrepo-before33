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
import java.util.Enumeration;
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
import fedora.server.errors.authorization.AuthzException;
import fedora.server.errors.servletExceptionExtensions.InternalError500Exception;
import fedora.server.errors.servletExceptionExtensions.RootException;
import fedora.server.utilities.Logger;

/**
 * <p><b>Title: </b>DescribeRepositoryServlet.java</p>
 * <p><b>Description: </b>Implements the "describeRepository" functionality
 * of the Fedora Access LITE (API-A-LITE) interface using a
 * java servlet front end. The syntax defined by API-A-LITE has for getting
 * a description of the repository has the following binding:
 * <ol>
 * <li>describeRepository URL syntax:
 * protocol://hostname:port/fedora/describe[?xml=BOOLEAN]
 * This syntax requests information about the repository.
 * The xml parameter determines the type of output returned.
 * If the parameter is omitted or has a value of "false", a MIME-typed stream
 * consisting of an html table is returned providing a browser-savvy means
 * of viewing the object profile. If the value specified is "true", then
 * a MIME-typed stream consisting of XML is returned.</li>
 * <ul>
 * <li>protocol - either http or https.</li>
 * <li>hostname - required hostname of the Fedora server.</li>
 * <li>port - required port number on which the Fedora server is running.</li>
 * <li>fedora - required name of the Fedora access service.</li>
 * <li>describe - required verb of the Fedora service.</li>
 * <li>xml - an optional parameter indicating the requested output format.
 *           A value of "true" indicates a return type of text/xml; the
 *           absence of the xml parameter or a value of "false"
 *           indicates format is to be text/html.</li>
 * </ul>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class DescribeRepositoryServlet extends HttpServlet
{
  /** Content type for html. */
  private static final String CONTENT_TYPE_HTML = "text/html; charset=UTF-8";

  /** Content type for xml. */
  private static final String CONTENT_TYPE_XML  = "text/xml; charset=UTF-8";

  /** Instance of the Fedora server. */
  private static Server s_server = null;

  /** Instance of the access subsystem. */
  private static Access s_access = null;

  /** Instance of URLDecoder */
  private URLDecoder decoder = new URLDecoder();

  /** Instance of Logger to log servlet events in Fedora server log */
  private static Logger logger = null;  
  
  /** HTTP protocol **/
  private static String HTTP = "http";
  
  /** HTTPS protocol **/
  private static String HTTPS = "https";

  String ACTION_LABEL = "describe repository";  
  
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
    boolean xml = false;  

    logger.logFinest("[DescribeRepositoryServlet] Describe Repository Syntax "
        + "Encountered: "+ request.getRequestURL().toString() + "?"
        + request.getQueryString());

    // Check for xml parameter.
    for ( Enumeration e = request.getParameterNames(); e.hasMoreElements();)
    {
      String name = URLDecoder.decode((String)e.nextElement(), "UTF-8");
      if (name.equalsIgnoreCase("xml"))
      {
        xml = new Boolean(request.getParameter(name)).booleanValue();
      }
    }
    Context context = ReadOnlyContext.getContext(Constants.HTTP_REQUEST.REST.uri, request);
    try {
        describeRepository(context, xml, response);
	} catch (AuthzException ae) {            
        throw RootException.getServletException (ae, request, ACTION_LABEL, new String[0]);		            
    } catch (Throwable th) {
    	throw new InternalError500Exception("", th, request, ACTION_LABEL, "", new String[0]);
    }    
    
  }

  public void describeRepository(Context context, boolean xml,
    HttpServletResponse response) throws ServerException
  {

    OutputStreamWriter out = null;
    RepositoryInfo repositoryInfo = null;
    PipedWriter pw = null;
    PipedReader pr = null;

    try
    {
      pw = new PipedWriter();
      pr = new PipedReader(pw);
      repositoryInfo = s_access.describeRepository(context);
      if (repositoryInfo != null)
      {
        // Repository info obtained.
        // Serialize the RepositoryInfo object into XML
        new ReposInfoSerializerThread(context, repositoryInfo, pw).start();
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
          File xslFile = new File(s_server.getHomeDir(), "access/viewRepositoryInfo.xslt");
          TransformerFactory factory = TransformerFactory.newInstance();
          Templates template = factory.newTemplates(new StreamSource(xslFile));
          Transformer transformer = template.newTransformer();
          Properties details = template.getOutputProperties();
          transformer.transform(new StreamSource(pr), new StreamResult(out));
        }
        out.flush();

      } else
      {
        // Describe request returned nothing.
        String message = "[DescribeRepositoryServlet] No Repository Info returned.";
        logger.logInfo(message);
      }
    } catch (AuthzException ae) {
    	throw ae;
    } catch (Throwable th)
    {
      String message = "[DescribeRepositoryServlet] An error has occured. "
                     + " The error was a \" "
                     + th.getClass().getName()
                     + " \". Reason: "  + th.getMessage();
      logger.logWarning(message);
      throw new GeneralException(message);
    } finally
    {
      try
      {
        if (pr != null) pr.close();
        if (out != null) out.close();
      } catch (Throwable th)
      {
        String message = "[DescribeRepositoryServlet] An error has occured. "
                       + " The error was a \" "
                       + th.getClass().getName()
                     + " \". Reason: "  + th.getMessage();
        throw new StreamIOException(message);
      }
    }
  }

  /**
   * <p> A Thread to serialize an ObjectProfile object into XML.</p>
   *
   */
  public class ReposInfoSerializerThread extends Thread
  {
    private PipedWriter pw = null;
    private RepositoryInfo repositoryInfo = null;
    private String fedoraServerProtocol = null;
    private String fedoraServerHost = null;
    private String fedoraServerPort = null;

    /**
     * <p> Constructor for ReposInfoSerializerThread.</p>
     *
     * @param repositoryInfo A repository info data structure.
     * @param pw A PipedWriter to which the serialization info is written.
     */
    public ReposInfoSerializerThread(Context context, RepositoryInfo repositoryInfo, PipedWriter pw)
    {
      this.pw = pw;
      this.repositoryInfo = repositoryInfo;
      fedoraServerPort = context.getEnvironmentValue(Constants.HTTP_REQUEST.SERVER_PORT.uri);
      fedoraServerHost = context.getEnvironmentValue(Constants.HTTP_REQUEST.SERVER_FQDN.uri);      
      if (Constants.HTTP_REQUEST.SECURE.uri.equals(context.getEnvironmentValue(Constants.HTTP_REQUEST.SECURITY.uri))) {
          fedoraServerProtocol = HTTPS;
      } else if (Constants.HTTP_REQUEST.INSECURE.uri.equals(context.getEnvironmentValue(Constants.HTTP_REQUEST.SECURITY.uri))) {
          fedoraServerProtocol = HTTP;
      }      
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
          pw.write("<fedoraRepository "
              + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
              + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
              + " xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/access/ "
              + fedoraServerProtocol + "://" + fedoraServerHost + ":" + fedoraServerPort
              + "/fedoraRepository.xsd\">");

          // REPOSITORY INFO FIELDS SERIALIZATION
          pw.write("<repositoryName>" + repositoryInfo.repositoryName + "</repositoryName>");
          pw.write("<repositoryBaseURL>" + repositoryInfo.repositoryBaseURL + "</repositoryBaseURL>");
          pw.write("<repositoryVersion>" + repositoryInfo.repositoryVersion + "</repositoryVersion>");
          pw.write("<repositoryPID>");
          pw.write("    <PID-namespaceIdentifier>"
            + repositoryInfo.repositoryPIDNamespace
            + "</PID-namespaceIdentifier>");
          pw.write("    <PID-delimiter>" + ":"+ "</PID-delimiter>");
          pw.write("    <PID-sample>" + repositoryInfo.samplePID + "</PID-sample>");
          String[] retainPIDs = repositoryInfo.retainPIDs;
          for (int i=0; i<retainPIDs.length; i++)
          {
            pw.write("    <retainPID>"+retainPIDs[i]+"</retainPID>");
          }
          pw.write("</repositoryPID>");
          pw.write("<repositoryOAI-identifier>");
          pw.write("    <OAI-namespaceIdentifier>"
            + repositoryInfo.OAINamespace
            + "</OAI-namespaceIdentifier>");
          pw.write("    <OAI-delimiter>" + ":"+ "</OAI-delimiter>");
          pw.write("    <OAI-sample>" + repositoryInfo.sampleOAIIdentifer + "</OAI-sample>");
          pw.write("</repositoryOAI-identifier>");
          pw.write("<sampleSearch-URL>" + repositoryInfo.sampleSearchURL + "</sampleSearch-URL>");
          pw.write("<sampleAccess-URL>" + repositoryInfo.sampleAccessURL + "</sampleAccess-URL>");
          pw.write("<sampleOAI-URL>" + repositoryInfo.sampleOAIURL + "</sampleOAI-URL>");
          String[] emails = repositoryInfo.adminEmailList;
          for (int i=0; i<emails.length; i++)
          {
            pw.write("<adminEmail>" + emails[i] + "</adminEmail>");
          }
          pw.write("</fedoraRepository>");
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

  private Server getServer() {
      return s_server;
  }

}