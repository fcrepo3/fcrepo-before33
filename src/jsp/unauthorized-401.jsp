<%@ page info="401==unauthorized response; tomcat didn't get user credentials where web.xml says they are needed " %>
<%
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.addHeader("Content-Type", "text/html; charset=UTF8");        
        response.addHeader("WWW-Authenticate", "BASIC realm=\"Fedora Management Interface\"");
%>
<html><head>
      <title>Fedora:  401 Authn Error</title></head>
   <body>
      <center>
         <table border="0" cellpadding="0" cellspacing="0" width="784">
            <tbody><tr>
               <td height="134" valign="top" width="141"><img src="/images/newlogo2.jpg" height="134" width="141"></td>
               <td valign="top" width="643">
                  <center>
                     <h2>Fedora Security Error</h2>
                     <h3>authentication failed</h3>
                  </center>
               </td>
            </tr>
         </tbody></table>
      </center>
   </body></html>
