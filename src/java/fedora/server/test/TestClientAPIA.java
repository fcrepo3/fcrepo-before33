package fedora.server.test;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Ross Wayland
 * @version 1.0
 */


import javax.xml.namespace.QName;
import org.apache.axis.client.Service;
import org.apache.axis.client.Call;
import java.util.Calendar;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestClientAPIA
{

  public TestClientAPIA()
  {
  }

  public static void main(String[] args)
  {
    String PID = "uva-lib:1220";
    String qName1 = "http://www.fedora.info/definitions/1/0/api/";
    String endpoint = "http://localhost:8080/fedora/access/soap";
    Calendar asOfDate = null;
    try
    {
      Service service = new Service();
      Call call = (Call) service.createCall();
      call.setTargetEndpointAddress( new java.net.URL(endpoint) );
      call.setOperationName(new javax.xml.namespace.QName(qName1, "GetBehaviorDefinitions") );

      String[] bDefs = (String[])call.invoke(new Object[] { PID });
      System.out.println("\n*****GETBEHAVIORDEFINITIONS RESULTS*****");
      for (int i=0; i<bDefs.length; i++)
      {
        System.out.println("bDef["+i+"] = "+bDefs[i]);
      }

      // Test GetBehaviormethods
      String bDefPID = "uva-bdef-stdimg:8";
      call.setOperationName(new javax.xml.namespace.QName(qName1, "GetBehaviorMethods") );
      QName qn = new QName("http://www.fedora.info/definitions/1/0/types/",
                            "MIMETypedStream");
      call.registerTypeMapping(fedora.server.types.MIMETypedStream.class,
                               qn,
                               new org.apache.axis.encoding.ser.BeanSerializerFactory(fedora.server.types.MIMETypedStream.class, qn),
                               new org.apache.axis.encoding.ser.BeanDeserializerFactory(fedora.server.types.MIMETypedStream.class, qn));
      //call.setReturnType(new javax.xml.namespace.QName(qName2, "MIMETypedStream"), fedora.server.types.MIMETypedStream.class);

      fedora.server.types.MIMETypedStream methodDefs =
          (fedora.server.types.MIMETypedStream) call.invoke( new Object[] { PID, bDefPID, asOfDate} );
      String mime = methodDefs.getMIMEType();
      System.out.println("\n\n*****GETBEHAVIORMETHODS RESULTS*****\nMIME: "+mime);
      ByteArrayInputStream bais = new ByteArrayInputStream(methodDefs.getStream());
      int c = 0;
      while ((c = bais.read()) > 0)
      {
        System.out.write(c);
      }

      // Test GetDissemination
      PID = "1007.lib.dl.test/text_ead/viu00003";
      bDefPID = "web_ead";
      String method = "get_admin";
      call.setOperationName(new javax.xml.namespace.QName(qName1, "GetDissemination") );
      fedora.server.types.MIMETypedStream dissemination =
          (fedora.server.types.MIMETypedStream) call.invoke( new Object[] { PID, bDefPID, method, asOfDate} );
      mime = methodDefs.getMIMEType();
      System.out.println("\n\n****DISSEMINATION RESULTS*****\nDissemination MIME: "+mime);
      BufferedReader br = new BufferedReader(
          new InputStreamReader(
          new ByteArrayInputStream(dissemination.getStream())));

      String line = null;
      while ((line=br.readLine()) != null )
      {
        System.out.println(line);
      }
    } catch (Exception e)
    {
      System.err.println(e.toString());
    }
  }
}