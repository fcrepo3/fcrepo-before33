<%@ page info="500==internal-server-error response" %>
<%@ page isErrorPage="true" %>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.addHeader("Content-Type", "text/html; charset=UTF8");        
%>
<html><head>
      <title>Fedora error</title></head>
   <body>
      <center>
      	<h2>Fedora error</h2>
An error has occurred in the Fedora Access Subsystem.  
<% if ((exception != null) && (exception.getCause() != null)) { %>
The error was 
"<% System.out.println(exception.getCause().getClass().getName()); %>".  Reason: 
"<% System.out.println(exception.getCause().getMessage()); %>".  
<% } %>
      </center>
<% if ((exception != null) && (exception.getCause() != null)) { %>
<% exception.getCause().printStackTrace(); %>
<% } %>      
   </body></html>
