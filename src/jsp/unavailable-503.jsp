<%@ page info="503==Unavailable" %>
<%@page isErrorPage="true" %>
<!-- http://java.sun.com/developer/EJTechTips/2003/tt0114.html -->
<%
        response.setStatus(HttpServletResponse.SC_UNAVAILABLE);
        response.addHeader("Content-Type", "text/html; charset=UTF8");        
%>
<html><head>
   <title>Fedora Unavailable</title></head>
   <body>
      <center>
         <table border="0" cellpadding="0" cellspacing="0" width="784">
            <tbody><tr>
               <td height="134" valign="top" width="141"><img src="/images/newlogo2.jpg" height="134" width="141"></td>
               <td valign="top" width="643">
                  <center>
                     <h2>Fedora Unavailable</h2>
                     <h3>detail follows</h3>
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

