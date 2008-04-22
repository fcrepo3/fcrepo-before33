/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */
package fedora.server.messaging;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;
import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fedora.common.Constants;
import fedora.server.Context;
import fedora.server.MockContext;
import fedora.server.Server;
import fedora.server.management.Management;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.RelationshipTuple;


/**
 *
 * @author Edwin Shin
 * @version $Id$
 */
public class AtomAPIMMessageTest {
    static String entry;
    static {
        StringBuilder sb = new StringBuilder();
        sb.append("<entry xmlns=\"http://www.w3.org/2005/Atom\">");
        sb.append("<id>ingestdemo:atomTest2008-03-15T11:12:00Z</id>");
        sb.append("<title type=\"text\">ingest</title>");
        sb.append("<updated>2008-03-15T11:12:00Z</updated>");
        sb.append("<author><name>fedoraAdmin</name><uri>http://localhost:8080/fedora</uri></author>");
        sb.append("<summary>demo:atomTest</summary>");
        sb.append("<content type=\"text\">demo:atomTest</content>");
        sb.append("</entry>");
        entry = sb.toString();
    }
    
    private String baseURL = "http://localhost:8080/fedora";
    private String serverVersion = Server.VERSION_MAJOR + Server.VERSION_MINOR;
    private String messageFormat = Constants.ATOM_APIM1_0.uri;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testStringConstructor() throws Exception {
        APIMMessage message = new AtomAPIMMessage(entry);
        assertEquals("demo:atomTest", message.getPID());
        assertEquals("ingest", message.getMethodName());
        assertEquals(baseURL, message.getBaseUrl());
    }
    
    @Test
    public void testFedoraMethodConstructor() throws Exception {
        Context c = new MockContext();
        FedoraMethod fm;
        APIMMessage message;
        
        fm = new FedoraMethod(
            Management.class.getDeclaredMethod(
                "purgeObject",
                new Class[]{Context.class, String.class, String.class, boolean.class}),
            new Object[] {c, "demo:foo", "a log message", false}, 
            "blah");
        message = new AtomAPIMMessage(fm, baseURL, serverVersion, messageFormat);
        assertNotNull(message.getDate());
        assertEquals("purgeObject", message.getMethodName());
        assertEquals("demo:foo", message.getPID().toString());
        assertEquals("http://localhost:8080/fedora", message.getBaseUrl());
        
        fm = new FedoraMethod(
              Management.class
                        .getDeclaredMethod("addDatastream", new Class[] {
                                Context.class, String.class, String.class,
                                String[].class, String.class, boolean.class,
                                String.class, String.class, String.class, 
                                String.class, String.class, String.class,
                                String.class, String.class}), new Object[] {
                  c, "demo:foo", "DS1", 
                  new String[] {"altid1, altid2"}, "a label", true, 
                  "text/xml", "some format uri", "dsLocation", 
                  "X", "A", "none", 
                  "n/a", "a log message"}, "asdf");
        
        message = new AtomAPIMMessage(fm, baseURL, serverVersion, messageFormat);
        assertNotNull(message.getDate());
        assertEquals("addDatastream", message.getMethodName());
        assertEquals("demo:foo", message.getPID().toString());
        assertEquals("http://localhost:8080/fedora", message.getBaseUrl());
        
        DatastreamXMLMetadata ds = new DatastreamXMLMetadata();
        ds.DatastreamID = "DS1";
        ds.DSVersionID = "DS1.0";
        ds.DSControlGrp = "X";
        ds.xmlContent = "<doc/>".getBytes();
        ds.DSCreateDT = new Date();
        fm = new FedoraMethod(
                              Management.class
                                        .getDeclaredMethod("getDatastream", new Class[] {
                                                Context.class, String.class, String.class,
                                                Date.class}), new Object[] {
                                  c, "demo:foo", "DS1", new Date()}, ds);
                        
                        message = new AtomAPIMMessage(fm, baseURL, serverVersion, messageFormat);
                        assertNotNull(message.getDate());
                        assertEquals("getDatastream", message.getMethodName());
                        assertEquals("demo:foo", message.getPID().toString());
                        assertEquals("http://localhost:8080/fedora", message.getBaseUrl());
        
        RelationshipTuple tuple = new RelationshipTuple("urn:subject", "urn:predicate", "object", true, null);
        fm = new FedoraMethod(
                              Management.class.getDeclaredMethod(
                                  "getRelationships",
                                  new Class[]{Context.class, String.class, String.class}),
                              new Object[] {c, "demo:foo", "urn:foo"}, 
                              new RelationshipTuple[] {tuple});
                          message = new AtomAPIMMessage(fm, baseURL, serverVersion, messageFormat);           
    }
    
    @Test
    public void testRoundTrip() throws Exception {
        Context context = new MockContext();        
        Method method = 
            Management.class.getDeclaredMethod(
                "purgeObject",
                new Class[]{Context.class, String.class, String.class, boolean.class});
        Object returnValue = "return";        
        
        // Quotes test
        String logMessage = "a log message with \"quotes\" included";
        Object[] parameters = 
            new Object[] {context, "demo:foo", logMessage, false};
        FedoraMethod fm = new FedoraMethod(method, parameters, returnValue);

        APIMMessage message = new AtomAPIMMessage(fm, baseURL, serverVersion, messageFormat);       
        String messageText = message.toString();
        APIMMessage messageFromText = new AtomAPIMMessage(messageText);
        
        assertNotNull(messageFromText.getDate());
        assertEquals("purgeObject", messageFromText.getMethodName());
        assertEquals("demo:foo", messageFromText.getPID().toString());
        assertEquals("http://localhost:8080/fedora", messageFromText.getBaseUrl());        
        assertEquals(normalize(messageText), normalize(messageFromText.toString()));
        
        // Special characters test
        logMessage = "a log message with special characters (!@#$%^&*<>?`':;,.|[]{}) included";
        parameters = new Object[] {context, "demo:foo", logMessage, false};        
        fm = new FedoraMethod(method, parameters, returnValue);
        
        message = new AtomAPIMMessage(fm, baseURL, serverVersion, messageFormat);      
        messageText = message.toString();
        messageFromText = new AtomAPIMMessage(messageText);
        assertEquals(normalize(messageText), normalize(messageFromText.toString()));
    }
    
    private String normalize(String xml) {
        String newline = System.getProperty("line.separator");
        return xml.replaceAll(newline, "").
                   replaceAll("\n", "").
                   replaceAll("\r", "").
                   replaceAll(" ", "");
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(AtomAPIMMessageTest.class);
    }
}
