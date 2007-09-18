<%@ page info="404==not found" %>
<%
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        response.addHeader("Content-Type", "text/html; charset=UTF8");
%>
<html><head>
      <title>Fedora: 404 Not Found</title></head>
   <body>
      <center>
         <table border="0" cellpadding="0" cellspacing="0" width="784">
            <tbody><tr>
               <td height="134" valign="top" width="141"><img src="/fedora/images/newlogo2.jpg" height="134" width="141"></td>
               <td valign="top" width="643">
                  <center>
                     <h2>404 Not Found</h2>
                     <h3>No such object, datastream, or dissemination.</h3>
                  </center>
               </td>
            </tr>
         </tbody></table>
      </center>
   </body></html>
