package fedora.server.access;

import fedora.server.utilities.DateUtility;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.net.URLDecoder;

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
  static final private String CONTENT_TYPE = "text/html";

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
    response.setContentType(CONTENT_TYPE);
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
      } else if (name.equals("serverURI"))
      {
        serverURI = (String)request.getParameter(name);
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
        (methodName == null || methodName.equalsIgnoreCase("")) ||
        (serverURI == null || serverURI.equalsIgnoreCase("")) )
    {
      out.println("<br><b>Unable to resolve dissemination request."
                  + "<br><table border=\"0\"><tr><td>PID</td><td>"
                  + PID + "</td></tr><tr><td>bDefPID</td><td>"
                  + bDefPID + "</td></tr><tr><td>methodName</td><td>"
                  + methodName + "</td></tr><tr><td>serverURI</td><td>"
                  + serverURI + "</td></tr></table><br>");
    } else
    {
      // Translate parameters into dissemination request.
      StringBuffer url = new StringBuffer();
      String[] s = serverURI.split("/");
      StringBuffer servletPath = new StringBuffer();
      for (int i=0; i<5; i++)
      {
        servletPath.append(s[i]+"/");
      }
      URLDecoder decode = new URLDecoder();
      PID = decode.decode(PID, "UTF-8");
      bDefPID = decode.decode(bDefPID,"UTF-8");
      url.append(servletPath
          + PID + "/"
          + bDefPID + "/"
          + methodName);
      if (methodParms.length() > 0)
      {
        if (versDateTime == null || versDateTime.equalsIgnoreCase(""))
        {
          url.append("?"+methodParms.toString());
        } else
        {
          url.append("/"+versDateTime+"/");
        }
      } else
      {
        if (versDateTime == null || versDateTime.equalsIgnoreCase(""))
        {
          url.append("/");
        } else
        {
          url.append("/"+versDateTime+"/");
        }
      }

      // redirect request.
      response.sendRedirect(url.toString());
    }
  }

  //Clean up resources
  public void destroy()
  {
  }
}