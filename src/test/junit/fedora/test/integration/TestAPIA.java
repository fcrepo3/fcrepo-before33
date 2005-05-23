/*
 * Created on May 23, 2005
 *
 */
package fedora.test.integration;

import fedora.client.FedoraClient;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.Datastream;
import fedora.test.FedoraServerTestCase;

/**
 * @author Edwin Shin
 *
 */
public class TestAPIA extends FedoraServerTestCase {
    private FedoraClient client;
    
    public void testFoo() throws Exception {
        client = new FedoraClient(getBaseURL(), getUsername(), getPassword());
        FedoraAPIM apim = client.getAPIM();
       Datastream ds = apim.getDatastream("demo:1", "DC", null);
       System.out.println("****" +  ds.getID() );
       
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestAPIA.class);
    }

}
