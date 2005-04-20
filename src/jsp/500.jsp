<%@ page info="500==internal-server-error response" %>
<%@page isErrorPage="true" %>
<!-- http://java.sun.com/developer/EJTechTips/2003/tt0114.html -->
<%
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.addHeader("Content-Type", "text/html; charset=UTF8");        
%>
<html><head>
      <title>Fedora Server Exception</title></head>
   <body>
      <center>
         <table border="0" cellpadding="0" cellspacing="0" width="784">
            <tbody><tr>
               <td height="134" valign="top" width="141"><img src="/images/newlogo2.jpg" height="134" width="141"></td>
               <td valign="top" width="643">
                  <center>
                     <h2>Fedora Error</h2>
                     <h3>internal server error</h3>
                  </center>
                  <% if (exception != null) { %>
                    <%= exception.getMessage() %>
                    <% if (exception.getCause() != null) { %>
                      <hr></hr>
                      <%= exception.getCause().getMessage() %>
                      <% if (exception.getCause().getCause() != null) { %>
                        <hr></hr>
                        <%= exception.getCause().getCause().getMessage() %>
                        <% if (exception.getCause().getCause().getCause() != null) { %>
                          <hr></hr>
                          <%= exception.getCause().getCause().getCause().getMessage() %>
                          <% if (exception.getCause().getCause().getCause().getCause() != null) { %>
                            <hr></hr>
                            <%= exception.getCause().getCause().getCause().getCause().getMessage() %>                      
                          <% } 
                           } 
                         } 
                       } 
                     } %>
               </td>
            </tr>
         </tbody></table>
      </center>
   </body></html>
