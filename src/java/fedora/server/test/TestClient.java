package fedora.server.test;

import org.apache.axis.client.Service;
import org.apache.axis.client.Call;

/**
 *
 * <p><b>Title:</b> TestClient.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class TestClient {

    public TestClient() {
    }

    public static void main(String[] args) {
        String qName = "http://www.fedora.info/definitions/1/0/api/";

        try {
            String managementEndpoint = "http://localhost:8080/fedora/services/management";
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress( new java.net.URL(managementEndpoint) );
            call.setOperationName(new javax.xml.namespace.QName(qName, "CreateObject") );
            System.out.println("Invoking call to " + managementEndpoint + "#CreateObject()");
            String pid = (String) call.invoke( new Object[] {} );
            System.out.println("Got: " + pid);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}