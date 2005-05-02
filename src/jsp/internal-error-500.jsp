<%@ page info="500==internal-server-error response" %>
<%@ page isErrorPage="true";%>
<%@ page import="javax.servlet.http.HttpServletRequest" %>
<%
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.addHeader("Content-Type", "text/html; charset=UTF8");        
%>
<html><head>
      <title>Fedora:  Error in <%= exception.getAction() %></title></head>
   <body>
      <center>
      	<h2>Fedora:  Error in <%= exception.getAction() %></h2>
An error has occured in accessing the Fedora Access Subsystem.  The error was 
"<%= exception.getCause.getClass().getName() %>".  Reason: 
"<%= exception.getCause.getMessage() %>".  Input Request was:  
"<%= ((RootException)exception).getRequest().getRequestURL().toString() %>".
      </center>
<% exception.getCause().printStackTrace() %>      
   </body></html>
