package fedora.server.test;

import javax.xml.namespace.QName;
import org.apache.axis.client.Service;
import org.apache.axis.client.Call;

/**
 *
 * <p><b>Title:</b> TestClient.java</p>
 * <p><b>Description:</b> </p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
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
            String managementEndpoint = "http://localhost:8080/fedora/management/soap";
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