package fedora.server.access;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Vector;

public class FedoraAccessServlet
        extends HttpServlet implements FedoraAccess {

    public String[] GetBehaviorDefinitions(String PID, Calendar asOfDate) {
        return null;
    }

    public MIMETypedStream GetBehaviorMethods(String PID, String bDefPID, 
            Calendar asOfDate) {
        return null;
    }

    public MIMETypedStream GetDissemination(String PID, String bDefPID,
            String method, Vector userParms, Calendar asOfDate) {
        return null;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
            PrintWriter out; 
            String title = "Fedora Access Servlet"; 
            response.setContentType("text/html");
            out = response.getWriter(); 
            out.println("<html><head><title>" + title 
                    + "</title></head><body bgcolor=\"ffffff\"><h2>" 
                    + title + "</h2><p>This servlet gets requests, sends them "
                    + "to a class implementing the FedoraAccess interface, and "
                    + "sends the response back to the http client."
                    + "</p></body></html>");
            out.close();
    }

}