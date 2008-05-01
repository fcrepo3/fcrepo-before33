
package fedora.test.api;

import java.io.UnsupportedEncodingException;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import junit.framework.Test;
import junit.framework.TestSuite;

import fedora.server.management.FedoraAPIM;

import fedora.test.DemoObjectTestSetup;
import fedora.test.FedoraServerTestCase;

/**
 * Performs tests to check notifications provided when management services
 * are exercised. Notifications are assumed to be via JMS.
 *
 * @author Bill Branan
 */
public class TestManagementNotifications
        extends FedoraServerTestCase
        implements MessageListener{

    private FedoraAPIM apim;
    private String currentMessageText;
    private int messageCount = 0; // The number of messages that have been received
    private int messageNumber = 0; // The number of the next message to be processed 
    private int messageTimeout = 5000; // Maximum number of milliseconds to wait for a message
    private Connection jmsConnection;
    private Session jmsSession;
    
    public static byte[] dsXML;
    public static byte[] demo998FOXMLObjectXML;

    static {

        // create test xml datastream
        StringBuffer sb = new StringBuffer();
        sb.append("<oai_dc:dc xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\">");
        sb.append("<dc:title>Dublin Core Record</dc:title>");
        sb.append("<dc:creator>Author</dc:creator>");
        sb.append("<dc:subject>Subject</dc:subject>");
        sb.append("<dc:description>Description</dc:description>");
        sb.append("<dc:publisher>Publisher</dc:publisher>");
        sb.append("<dc:format>MIME type</dc:format>");
        sb.append("<dc:identifier>Identifier</dc:identifier>");
        sb.append("</oai_dc:dc>");
        try {
            dsXML = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee) {
        }

        // create test FOXML object specifying pid=demo:998
        sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<foxml:digitalObject VERSION=\"1.1\" PID=\"demo:998\" xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd\">");
        sb.append("  <foxml:objectProperties>");
        sb.append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"A\"/>");
        sb.append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"Image of Coliseum in Rome\"/>");
        sb.append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#createdDate\" VALUE=\"2004-12-10T00:21:57Z\"/>");
        sb.append("    <foxml:property NAME=\"info:fedora/fedora-system:def/view#lastModifiedDate\" VALUE=\"2004-12-10T00:21:57Z\"/>");
        sb.append("  </foxml:objectProperties>");
        sb.append("</foxml:digitalObject>");

        try {
            demo998FOXMLObjectXML = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee) {
        }

    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Management Notifications TestSuite");
        suite.addTestSuite(TestManagementNotifications.class);
        return new DemoObjectTestSetup(suite);
    }

    public void setUp() throws Exception {
        apim = getFedoraClient().getAPIM();
        
        // Create and start a subscriber
        Properties props = new Properties();
        props.setProperty(Context.INITIAL_CONTEXT_FACTORY, 
                          "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
        props.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
        props.setProperty("topic.notificationTopic", "fedora.apim.update");
        
        Context jndi = new InitialContext(props);               
        ConnectionFactory jmsConnectionFactory = 
            (ConnectionFactory)jndi.lookup("ConnectionFactory");        
        jmsConnection = jmsConnectionFactory.createConnection();
        jmsSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
           
        Destination destination = (Topic)jndi.lookup("notificationTopic");  

        MessageConsumer messageConsumer = jmsSession.createConsumer(destination);       
        messageConsumer.setMessageListener(this);

        jmsConnection.start();        
    }

    public void tearDown() throws Exception {
        jmsConnection.stop();
        jmsSession.close();
        jmsConnection.close();
    }

    /**
     * Tests notifications on
     * 1) ingest
     * 2) modifyObject
     * 3) addRelationship
     * 4) purgeRelationship
     * 5) purgeObject
     * 
     * @throws Exception
     */
    public void testObjectMethodNotifications() throws Exception {

        // (1) test ingest
        System.out.println("Running TestManagementNotifications.testIngest...");
        String pid =
                apim.ingest(demo998FOXMLObjectXML,
                            FOXML1_1.uri,
                            "ingesting new foxml object");
        assertNotNull(pid);       

        // Check on the notification produced by ingest 
        checkNotification("ingest");               
        
        // (2) test modifyObject
        System.out.println("Running TestManagementNotifications.testModifyObject...");
        String modifyResult =
                apim.modifyObject("demo:998",
                                  "I",
                                  "Updated Object Label",
                                  null,
                                  "Changed state to inactive and updated label");
        assertNotNull(modifyResult);

        // Check on the notification produced by modifyObject 
        checkNotification("modifyObject");                               
        
        // (3) test addRelationship
        System.out.println("Running TestManagementNotifications.testAddRelationship...");
        boolean addRelResult =
                apim.addRelationship("demo:998",
                                     "rel:isRelatedTo",
                                     "demo:5",
                                     false,
                                     null);
        assertTrue(addRelResult);        
        
        // Check on the notification produced by addRelationship 
        checkNotification("addRelationship");         

        // (4) test purgeRelationship
        System.out.println("Running TestManagementNotifications.testPurgeRelationship...");
        boolean purgeRelResult =
                apim.purgeRelationship("demo:998",
                                       "rel:isRelatedTo",
                                       "demo:5",
                                       false,
                                       null);
        assertTrue(purgeRelResult);        
        
        // Check on the notification produced by purgeRelationship 
        checkNotification("purgeRelationship");      
        
        // (5) test purgeObject
        System.out.println("Running TestManagementNotifications.testPurgeObject...");
        String purgeResult = apim.purgeObject(pid, "Purging object " + pid, false);
        assertNotNull(purgeResult);
        
        // Check on the notification produced by purgeObject 
        checkNotification("purgeObject");          

    }

    /**
     *  Test notifications on 
     *  1) addDatastream
     *  2) modifyDatastreamByReference
     *  3) modifyDatastreamByValue
     *  4) setDatastreamState
     *  5) setDatastreamVersionable  
     *  6) purgeDatastream
     * 
     * @throws Exception
     */
    public void testDatastreamMethodNotifications() throws Exception {

        // (1) test addDatastream
        System.out.println("Running TestManagementNotifications.testAddDatastream...");

        String[] altIds = new String[1];
        altIds[0] = "Datastream Alternate ID";
        
        String datastreamId =
                    apim.addDatastream("demo:14",
                                       "NEWDS1",
                                       altIds,
                                       "A New M-type Datastream",
                                       true,
                                       "text/xml",
                                       "info:myFormatURI/Mtype/stuff#junk",
                                       "http://www.fedora.info/junit/datastream1.xml",
                                       "M",
                                       "A",
                                       null,
                                       null,
                                       "adding new datastream");

        // test that datastream was added
        assertEquals(datastreamId, "NEWDS1");
        
        // Check on the notification produced by addDatastream 
        checkNotification("addDatastream");        

        datastreamId =
                    apim.addDatastream("demo:14",
                                       "NEWDS2",
                                       altIds,
                                       "A New X-type Datastream",
                                       true,
                                       "text/xml",
                                       "info:myFormatURI/Mtype/stuff#junk",
                                       "http://www.fedora.info/junit/datastream2.xml",
                                       "X",
                                       "A",
                                       null,
                                       null,
                                       "adding new datastream");

        // test that datastream was added
        assertEquals(datastreamId, "NEWDS2");
        
        // Check on the notification produced by addDatastream 
        checkNotification("addDatastream"); 
        
        // (2) test modifyDatastreamByReference
        System.out.println("Running TestManagementNotifications.testModifyDatastreamByReference...");       
        String updateTimestamp =
                apim.modifyDatastreamByReference("demo:14",
                                                 "NEWDS1",
                                                 altIds,
                                                 "Modified Datastream by Reference",
                                                 "text/xml",
                                                 "info:newMyFormatURI/Mtype/stuff#junk",
                                                 "http://www.fedora.info/junit/datastream3.xml",
                                                 null,
                                                 null,
                                                 "modified datastream",
                                                 false);
        // test that method returned properly
        assertNotNull(updateTimestamp);       

        // Check on the notification produced by modifyDatastreamByReference 
        checkNotification("modifyDatastreamByReference");         
        
        // (3) test modifyDatastreamByValue
        System.out.println("Running TestManagementNotifications.testModifyDatastreamByValue...");
        updateTimestamp =
                apim.modifyDatastreamByValue("demo:14",
                                             "NEWDS2",
                                             altIds,
                                             "Modified Datastream by Value",
                                             "text/xml",
                                             "info:newMyFormatURI/Xtype/stuff#junk",
                                             dsXML,
                                             null,
                                             null,
                                             "modified datastream",
                                             false);
        // test that method returned properly
        assertNotNull(updateTimestamp);
       
        // Check on the notification produced by modifyDatastreamByValue 
        checkNotification("modifyDatastreamByValue");        
        
        // (4) test setDatastreamState
        System.out.println("Running TestManagementNotifications.testSetDatastreamState...");
        String setStateresult =
                apim.setDatastreamState("demo:14",
                                        "NEWDS1",
                                        "I",
                                        "Changed state of datstream DC to Inactive");
        assertNotNull(setStateresult);        

        // Check on the notification produced by setDatastreamState 
        checkNotification("setDatastreamState");          
        
        // (5) test setDatastreamVersionable
        System.out.println("Running TestManagementNotifications.testSetDatastreamVersionable...");
        String setVersionableResult =
                apim.setDatastreamVersionable("demo:14",
                                              "NEWDS2",
                                              false,
                                              "Changed versionable on datastream NEWDS1 to false");
        assertNotNull(setVersionableResult);   
        
        // Check on the notification produced by setDatastreamVersionable 
        checkNotification("setDatastreamVersionable");          
        
        // (5) test purgeDatastream
        System.out.println("Running TestManagementNotifications.testPurgeDatastream...");
        
        String[] results =
                apim.purgeDatastream("demo:14",
                                     "NEWDS1",
                                     null,
                                     null,
                                     "purging datastream NEWDS1",
                                     false);
        assertTrue(results.length > 0);
        
        // Check on the notification produced by purgeDatastream 
        checkNotification("purgeDatastream");        
        
        results =
            apim.purgeDatastream("demo:14",
                                 "NEWDS2",
                                 null,
                                 null,
                                 "purging datastream NEWDS2",
                                 false);
        assertTrue(results.length > 0); 
        
        // Check on the notification produced by purgeDatastream 
        checkNotification("purgeDatastream");           
     }

    /**
     * Waits for a notification message and checks to see if the message
     * body includes the includedText. 
     *  
     * @param includedText - the text that should be found in the message body
     */
    private void checkNotification(String includedText) {
        long startTime = System.currentTimeMillis();
        messageNumber++;
        
        while(true) { // Wait for the notification message
            if(messageCount >= messageNumber) {
                String failureText = "Notification did not include text: " + includedText; 
                assertTrue(failureText, currentMessageText.contains(includedText));
                break;
            } else { // Check for timeout
                long currentTime = System.currentTimeMillis();
                if(currentTime > (startTime + messageTimeout)) {
                    fail("Timeout reached waiting for notification " +
                         "on message regarding: " + includedText);
                    break;
                }
            }
        }
    }
    
    /**
     * Handles messages sent as notifications.
     * 
     * {@inheritDoc}
     */
    public void onMessage(Message msg) {
       if(msg instanceof TextMessage) {
           TextMessage currentMessage = (TextMessage) msg;

           try {
               currentMessageText = currentMessage.getText();                     
           } catch(Exception e) {
               System.out.println("Message received, exception attempting to read: " + e.getMessage());
           }
           
           messageCount++;
       }
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestManagementNotifications.class);
    }

}
