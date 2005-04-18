<%@ page info="500==internal-server-error response" %>
<%
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.addHeader("Content-Type", "text/html; charset=UTF8");        
%>
<html><head>
      <title>Fedora:  500 Internal Server Error</title></head>
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
               </td>
            </tr>
         </tbody></table>
      </center>
   </body></html>
