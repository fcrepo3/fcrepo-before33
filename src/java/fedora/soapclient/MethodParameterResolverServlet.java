package fedora.soapclient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/**
 * <p><b>Title: </b>MethodParameterResolverServlet.java</p>
 * <p><b>Description: </b>This servlet accepts the result of a posted web form
 * containing information about which method parameter values were selected
 * for a dissemination request. The information is read from the form and
 * translated into an appropriate dissemination request and then executes
 * the dissemination request.</p>
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class MethodParameterResolverServlet extends HttpServlet
{

  /** A string constant for the html MIME type */
  static final private String HTML_CONTENT_TYPE = "text/html";

  /** Servlet mapping for this servlet */
  private static String SERVLET_PATH = null;

  /** Properties file for soap client */
  private static final String soapClientPropertiesFile =
      "WEB-INF/soapclient.properties";

  public void init() throws ServletException
  {
    try
    {
      System.out.println("Realpath Properties File: "
          + getServletContext().getRealPath(soapClientPropertiesFile));
      FileInputStream fis = new FileInputStream(this.getServletContext().getRealPath(soapClientPropertiesFile));
      Properties p = new Properties();
      p.load(fis);
      SERVLET_PATH = p.getProperty("soapClientServletPath");
      System.out.println("soapClientServletPath: " + SERVLET_PATH);

    } catch (Throwable th)
    {
      String message = "[FedoraSOAPServlet] An error has occurred. "
          + "The error was a \"" + th.getClass().getName() + "\"  . The "
          + "Reason: \"" + th.getMessage() + "\"  .";
      throw new ServletException(message);
    }
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    doPost(request, response);
  }

  //Process the HTTP Post request.
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException
  {
    String PID = null;
    String bDefPID = null;
    String methodName = null;
    String serverURI = null;
    String versDateTime = null;
    StringBuffer methodParms = new StringBuffer();
    Hashtable h_methodParms = new Hashtable();
    response.setContentType(HTML_CONTENT_TYPE);
    PrintWriter out = response.getWriter();

    // Get servlet parameters.
    Enumeration parms = request.getParameterNames();
    while (parms.hasMoreElements())
    {
      String name = new String((String)parms.nextElement());
      String value = new String(request.getParameter(name));
      if (name.equals("PID"))
      {
        PID = URLDecoder.decode((String)request.getParameter(name), "UTF-8");
      } else if (name.equals("bDefPID"))
      {
        bDefPID = URLDecoder.decode((String)request.getParameter(name), "UTF-8");
      } else if (name.equals("methodName"))
      {
        methodName = URLDecoder.decode((String)request.getParameter(name), "UTF-8");
        } else if (name.equals("asOfDateTime"))
        {
        versDateTime = (String)request.getParameter(name);
      } else if (name.equals("Submit")) {
        // Submit parameter is ignored.
      } else
      {
        // Any remaining parameters are assumed to be method parameters so
        // decode and place in hashtable.
        h_methodParms.put(URLDecoder.decode(name, "UTF-8"),
            URLDecoder.decode((String)request.getParameter(name), "UTF-8"));
      }
    }

    // Check for any missing required parameters.
    if ((PID == null || PID.equalsIgnoreCase("")) ||
        (bDefPID == null || bDefPID.equalsIgnoreCase("")) ||
        (methodName == null || methodName.equalsIgnoreCase("")) )
    {
      String message = "[MethodParameterResolverServlet] Insufficient "
          + "information to construct dissemination request. Parameters "
          + "received from web form were: PID: " + PID
          + " -- bDefPID: " + bDefPID
          + " -- methodName: " + methodName
          + " -- methodParms: " + methodParms.toString() + "\".  ";
      System.out.println(message);
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    } else
    {
      // Translate parameters into dissemination request.
      StringBuffer redirectURL = new StringBuffer();
      redirectURL.append(SERVLET_PATH
          + "?action_=GetDissemination&"
          + "PID_=" + PID + "&"
          + "bDefPID_=" + bDefPID + "&"
          + "methodName_=" + methodName);
      // Add method parameters.
      int i = 0;
      for (Enumeration e = h_methodParms.keys() ; e.hasMoreElements(); )
      {
        String name = URLEncoder.encode((String) e.nextElement(), "UTF-8");
        String value = URLEncoder.encode((String) h_methodParms.get(name), "UTF-8");
        i++;
        if (i == h_methodParms.size())
        {
          methodParms.append(name + "=" + value);
        } else
        {
          methodParms.append(name + "=" + value + "&");
        }

      }
      if (h_methodParms.size() > 0)
      {
        if (versDateTime == null || versDateTime.equalsIgnoreCase(""))
        {
          redirectURL.append("&"+methodParms.toString());
        } else
        {
          redirectURL.append("&asOfDateTime_="+versDateTime+"&"+methodParms.toString());
        }
      } else
      {
        if (versDateTime != null && !versDateTime.equalsIgnoreCase(""))
        {
          redirectURL.append("&asOfDateTime_="+versDateTime);
        }
      }

      // Redirect to API-A interface.
      response.sendRedirect(redirectURL.toString());
    }
  }

  //Clean up resources
  public void destroy()
  {
  }
}