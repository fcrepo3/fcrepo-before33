package fedora.access;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */

public class FedoraServlet extends HttpServlet implements FedoraAccess
{
  private static final String CONTENT_TYPE = "text/html";

  //Initialize global variables
  public void init() throws ServletException
  {
  }

  //Process the HTTP Get request
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String PID = request.getParameter("PID_");
    if (PID == null)
    {
      PID = "";
    }
    String bDefPID = request.getParameter("bDefPID_");
    if (bDefPID == null)
    {
      bDefPID = "";
    }
    String method = request.getParameter("method_");
    if (method == null)
    {
      method = "";
    }
    String asOfDate = request.getParameter("asOfDate_");
    if (asOfDate == null)
    {
      asOfDate = "";
    }
    response.setContentType(CONTENT_TYPE);
    PrintWriter out = response.getWriter();
    out.println("<html>");
    out.println("<head><title>FedoraServlet</title></head>");
    out.println("<body>");
    out.println("<p>The servlet has received a GET. This is the reply.</p>");
    out.println("</body></html>");
  }

  //Process the HTTP Post request
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    String PID = request.getParameter("PID_");
    if (PID == null)
    {
      PID = "";
    }
    String bDefPID = request.getParameter("bDefPID_");
    if (bDefPID == null)
    {
      bDefPID = "";
    }
    String method = request.getParameter("method_");
    if (method == null)
    {
      method = "";
    }
    String asOfDate = request.getParameter("asOfDate_");
    if (asOfDate == null)
    {
      asOfDate = "";
    }
    response.setContentType(CONTENT_TYPE);
    PrintWriter out = response.getWriter();
    out.println("<html>");
    out.println("<head><title>FedoraServlet</title></head>");
    out.println("<body>");
    out.println("<p>The servlet has received a POST. This is the reply.</p>");
    out.println("</body></html>");
  }

  /**
   *
   * @param bDefPID
   * @return
   */
  public ArrayofString GetBehaviorDefinitions(String PID, Date asOfDate)
  {
  ArrayofString bDefTypes = new ArrayofString();
  // voodoo
  return bDefTypes;
  }

  /**
   *
   * @param bDefPID
   * @return
   */
  public MIMEStream GetMethods(String PID, String bDefPID, Date asOfDate)
  {
  MIMEStream methods = new MIMEStream();
  // voodoo
  return methods;
  }
  /**
   *
   * @param PID
   * @param bDefPID
   * @param method
   * @param asOfDate
   * @param userParms
   * @return
   */
  public MIMEStream GetDissemination(String PID, String bDefPID, String method, String[][] userParms, Date asOfDate)
  {
  MIMEStream dissemination = new MIMEStream();
  // voodoo
  return dissemination;
  }

  //Clean up resources
  public void destroy()
  {
  }
}