package fedora.soapclient;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Enumeration;

/**
 * <p>Title: MethodParameterResolverServlet.java</p>
 * <p>Description: <p>This servlet accepts the result of a posted web form
 * containing information about which method parameter values were selected
 * for a dissemination request. The information is read from the form and
 * translated into an appropriate dissemination request and then executes
 * the dissemination request.</p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */
public class MethodParameterResolverServlet extends HttpServlet
{

  /** A string constant for the html MIME type */
  static final private String HTML_CONTENT_TYPE = "text/html";

  private static final String API_A_SERVLET_PATH = "/fedora/access/soapservlet?";

  public void init() throws ServletException
  {}

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
        PID = (String)request.getParameter(name);
      } else if (name.equals("bDefPID"))
      {
        bDefPID = (String)request.getParameter(name);
      } else if (name.equals("methodName"))
      {
        methodName = (String)request.getParameter(name);
        } else if (name.equals("asOfDateTime"))
        {
        versDateTime = (String)request.getParameter(name);
      } else if (name.equals("Submit")) {
        // Submit parameter is ignored.
      } else
      {
        // Any remaining parameters are method parameters.
        methodParms.append(name+"="+(String)request.getParameter(name)+"&");
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
      response.sendError(response.SC_INTERNAL_SERVER_ERROR, message);
    } else
    {
      // Translate parameters into dissemination request.
      StringBuffer redirectURL = new StringBuffer();
      URLDecoder decode = new URLDecoder();
      PID = decode.decode(PID, "UTF-8");
      bDefPID = decode.decode(bDefPID,"UTF-8");
      redirectURL.append(API_A_SERVLET_PATH
          + "action_=GetDissemination&"
          + "PID_=" + PID + "&"
          + "bDefPID_=" + bDefPID + "&"
          + "methodName_=" + methodName);
      if (methodParms.length() > 0)
      {
        if (versDateTime == null || versDateTime.equalsIgnoreCase(""))
        {
          redirectURL.append("&"+methodParms.toString());
        } else
        {
          redirectURL.append("&asOfDate_="+versDateTime+"&"+methodParms.toString());
        }
      } else
      {
        if (versDateTime != null && !versDateTime.equalsIgnoreCase(""))
        {
          redirectURL.append("&asOfDate_="+versDateTime);
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