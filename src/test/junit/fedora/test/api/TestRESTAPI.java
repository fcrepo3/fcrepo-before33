package fedora.test.api;

import static org.apache.commons.httpclient.HttpStatus.SC_CREATED;
import static org.apache.commons.httpclient.HttpStatus.SC_NOT_FOUND;
import static org.apache.commons.httpclient.HttpStatus.SC_OK;
import static org.apache.commons.httpclient.HttpStatus.SC_TEMPORARY_REDIRECT;

import java.io.UnsupportedEncodingException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.junit.Test;

import fedora.common.PID;
import fedora.server.management.FedoraAPIM;
import fedora.test.FedoraServerTestCase;

/**
 * Tests of the REST API.
 * 
 * //TODO: actually validate the ResponseBody instead of just HTTP status codes
 * 
 * @author Edwin Shin
 * @version $Id$
 *
 */
public class TestRESTAPI extends FedoraServerTestCase {
    
    private FedoraAPIM apim;
    private static byte[] DEMO_REST_FOXML;
    
    private final PID pid = PID.getInstance("demo:REST");
    private String url;
    
    static {        
        // Test FOXML object with RELS-EXT datastream
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<foxml:digitalObject VERSION=\"1.1\" PID=\"demo:REST\" ");
        sb.append("  xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\" ");
        sb.append("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("  xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# ");
        sb.append("  http://www.fedora.info/definitions/1/0/foxml1-1.xsd\">");
        sb.append("  <foxml:objectProperties>");
        sb.append("    <foxml:property NAME=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\" VALUE=\"FedoraObject\"/>");
        sb.append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"A\"/>");
        sb.append("  </foxml:objectProperties>");
        sb.append("  <foxml:datastream ID=\"DC\" CONTROL_GROUP=\"X\" STATE=\"A\">");
        sb.append("    <foxml:datastreamVersion ID=\"DC1.0\" MIMETYPE=\"text/xml\" LABEL=\"DC Record for Coliseum image object\">");
        sb.append("      <foxml:xmlContent>");
        sb.append("        <oai_dc:dc xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\">");
        sb.append("          <dc:title>Coliseum in Rome</dc:title>");
        sb.append("          <dc:creator>Thornton Staples</dc:creator>");
        sb.append("          <dc:subject>Architecture, Roman</dc:subject>");
        sb.append("          <dc:description>Image of Coliseum in Rome</dc:description>");
        sb.append("          <dc:publisher>University of Virginia Library</dc:publisher>");
        sb.append("          <dc:format>image/jpeg</dc:format>");
        sb.append("          <dc:identifier>demo:REST</dc:identifier>");
        sb.append("        </oai_dc:dc>");
        sb.append("      </foxml:xmlContent>");
        sb.append("    </foxml:datastreamVersion>");
        sb.append("  </foxml:datastream>");
        sb.append("  <foxml:datastream ID=\"RELS-EXT\" CONTROL_GROUP=\"M\" STATE=\"A\">");
        sb.append("    <foxml:datastreamVersion ID=\"RELS-EXT.0\" MIMETYPE=\"text/xml\" LABEL=\"Relationships\">");
        sb.append("      <foxml:xmlContent>");
        sb.append("        <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"" +
                  "                 xmlns:rel=\"info:fedora/fedora-system:def/relations-external#\">");
        sb.append("          <rdf:Description rdf:about=\"info:fedora/demo:REST\">");
        sb.append("            <rel:hasFormalContentModel rdf:resource=\"info:fedora/demo:UVA_STD_IMAGE_1\"/>");
        sb.append("          </rdf:Description>");
        sb.append("        </rdf:RDF>");
        sb.append("      </foxml:xmlContent>");
        sb.append("    </foxml:datastreamVersion>");
        sb.append("  </foxml:datastream>");
        sb.append("</foxml:digitalObject>");

        try {
            DEMO_REST_FOXML = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee)
        {}
    }
    
    public void setUp() throws Exception {
        apim = getFedoraClient().getAPIM();
        apim.ingest(DEMO_REST_FOXML, FOXML1_1.uri, "ingesting new foxml object");
    }
    
    public void tearDown() throws Exception {
        apim.purgeObject(pid.toString(), "", false);
    }
    
    @Test
    public void testGetWADL() throws Exception {
        url = "/objects/application.wadl";
        
        assertEquals(SC_OK, get(false));
        assertEquals(SC_OK, get(true));
    }
    
    
    public void testDescribeRepository() throws Exception {
        // TODO
	}
    
    // API-A
    @Test
    public void testGetObjectProfile() throws Exception {
        url = String.format("/objects/%s", pid.toString());
        assertEquals(SC_OK, get(false));
        assertEquals(SC_OK, get(true));
        
        url = String.format("/objects/%s?format=xml", pid.toString());
        assertEquals(SC_OK, get(false));
        assertEquals(SC_OK, get(true));
        
        // sanity check
        url = String.format("/objects/%s", "demo:BOGUS_PID");
        assertEquals(SC_NOT_FOUND, get(false));
        assertEquals(SC_NOT_FOUND, get(true));
	}
    
    public void testListMethods() throws Exception {
        url = String.format("/objects/%s/methods", pid.toString());
        assertEquals(SC_OK, get(false));
        assertEquals(SC_OK, get(true));
        
        url = String.format("/objects/%s/methods?format=xml", pid.toString());
        assertEquals(SC_OK, get(false));
        assertEquals(SC_OK, get(true));
    }
    
    public void testListDatastreams() throws Exception {
        url = String.format("/objects/%s/datastreams", pid.toString());
        assertEquals(SC_OK, get(false));
        assertEquals(SC_OK, get(true));
        
        url = String.format("/objects/%s/datastreams?format=xml", pid.toString());
        assertEquals(SC_OK, get(false));
        assertEquals(SC_OK, get(true));
    }
    
    public void testGetDatastreamDissemination() throws Exception {
        url = String.format("/objects/%s/datastreams/RELS-EXT", pid.toString());
        assertEquals(SC_OK, get(false));
        assertEquals(SC_OK, get(true));
        
        // sanity check
        url = String.format("/objects/%s/datastreams/BOGUS_DSID", pid.toString());
        assertEquals(SC_NOT_FOUND, get(false));
        assertEquals(SC_NOT_FOUND, get(true));
    }
    
    public void testGetDissemination() throws Exception {

    }
    
    public void testFindObjects() throws Exception {
        url = String.format("/objects?pid=true&terms=%s&query=&format=xml", pid.toString());
        assertEquals(SC_OK, get(false));
    }
    
    public void testResumeFindObjects() throws Exception {
        //TODO
    }
    
    public void testGetObjectHistory() throws Exception {
        url = String.format("/objects/%s/versions", pid.toString());
        assertEquals(SC_OK, get(false));
        assertEquals(SC_OK, get(true));
    }
    
    // API-M
    public void testIngest() throws Exception {
        url = String.format("/objects/new");
        assertEquals(SC_CREATED, post("", true));
    }
    
    public void testIngestObject() throws Exception {
        //TODO
    }
    
    public void testModifyObject() throws Exception {
        url = String.format("/objects/%s?label=%s", pid.toString(), "foo");
        HttpMethod method = put("", true);
        assertFalse("For testing, we expect to have to follow redirects manually", 
                method.getFollowRedirects());
        assertEquals(SC_TEMPORARY_REDIRECT, method.getStatusCode());
        Header locationHeader = method.getResponseHeader("location");
        assertNotNull(locationHeader);
        assertEquals(pid.toString(), locationHeader.getValue());
        method.releaseConnection();
    }
    
    public void testGetObjectXML() throws Exception {
        
    }
    
    public void testExportObject() throws Exception {
        
    }
    
    public void testPurgeObject() throws Exception {
        url = String.format("/objects/%s", "demo:TEST_PURGE");
        assertEquals(SC_CREATED, post("", true));
        url = String.format("/objects/demo:TEST_PURGE");
        assertEquals(SC_OK, delete(true));
    }
    
    public void testAddDatastream() throws Exception {
        
    }
    
    public void testDescribeUser() throws Exception {
        
    }
    
    public void testModifyDatastreamByReference() throws Exception {
        
    }

    public void testModifyDatastreamByValue() throws Exception {
        
    }
    
    public void testSetDatastreamSet() throws Exception {
        
    }
    
    public void testSetDatastreamVersionable() throws Exception {
        
    }
    
    public void testCompareDatastreamChecksumRequest() throws Exception {
        
    }
    
    public void testGetDatastream() throws Exception {
        
    }
    
    public void testGetDatastreams() throws Exception {
        
    }
    
    public void testGetDatastreamHistory() throws Exception {
        
    }
    
    public void testPurgeDatastream() throws Exception {
        
    }
    
    public void testGetNextPID() throws Exception {
        
    }
    
    public void testGetRelationship() throws Exception {
        // TODO
    }
    
    public void testAddRelationship() throws Exception {
        // TODO
    }
    
    public void testPurgeRelationship() throws Exception {
        // TODO
    }
    
    // helper methods
    // TODO 
    // I haven't yet sorted out the return values I want for the various
    // HTTP GET/PUT/POST/DELETE helper methods.
    // For future testing, we need more than just the status code, but it's
    // also nice not to require callers to issue HttpMethod.releaseConnection().
    // Something in between is returning some data structure that has all the 
    // response codes/headers/body that we want so that we don't need to return
    // an HttpMethod.
    private HttpClient getClient(boolean auth) {
        HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(
            true);
        if (auth) {
            client.getState().setCredentials(
                new AuthScope(getHost(), Integer.valueOf(getPort()), "realm"),
                new UsernamePasswordCredentials(getUsername(), getPassword()));
        }
        return client;
    }
    
    /**
     * Issues an HTTP GET for the specified URL.
     * 
     * @param url The URL to GET: either an absolute URL or URL relative to 
     * the Fedora webapp (e.g. "/objects/demo:10"). 
     * @param authenticate
     * @return HTTP Response Code
     * @throws Exception
     */
    private int get(boolean authenticate) throws Exception {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("url must be a non-empty value");
        } else if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            url = getBaseURL() + url;
        }
        
        HttpMethod httpMethod = null;
        try {
            httpMethod = new GetMethod(url);

            httpMethod.setDoAuthentication(authenticate);
            httpMethod.getParams().setParameter(
                "Connection", "Keep-Alive");
            return getClient(authenticate).executeMethod(
                httpMethod);
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }
    }
    
    /**
     * Issues an HTTP PUT to <code>url</code>.
     * Callers are responsible for calling releaseConnection() on the returned
     * <code>HttpMethod</code>.
     * 
     * @param requestContent
     * @param authenticate
     * @return
     * @throws Exception
     */
    private HttpMethod put(String requestContent, boolean authenticate) throws Exception {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("url must be a non-empty value");
        } else if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            url = getBaseURL() + url;
        }
        EntityEnclosingMethod httpMethod = null;
        httpMethod = new PutMethod(url);
        httpMethod.setDoAuthentication(authenticate);
        httpMethod.getParams().setParameter("Connection", "Keep-Alive");
        httpMethod.setContentChunked(true);
        httpMethod.setRequestEntity(new StringRequestEntity(requestContent,
                "text/xml", "utf-8"));
        getClient(authenticate).executeMethod(httpMethod);
        return httpMethod;
    }
    
    private int post(String requestContent, boolean authenticate) throws Exception {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("url must be a non-empty value");
        } else if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            url = getBaseURL() + url;
        }
        
        EntityEnclosingMethod httpMethod = null;
        try {
            httpMethod = new PostMethod(url);

            httpMethod.setDoAuthentication(authenticate);
            httpMethod.getParams().setParameter(
                "Connection", "Keep-Alive");
            httpMethod.setContentChunked(true);
            httpMethod.setRequestEntity(new StringRequestEntity(requestContent,
                    "text/xml", "utf-8"));
            return getClient(authenticate).executeMethod(
                httpMethod);
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }
    }
    
    private int delete(boolean authenticate) throws Exception {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("url must be a non-empty value");
        } else if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            url = getBaseURL() + url;
        }
        
        HttpMethod httpMethod = null;
        try {
            httpMethod = new DeleteMethod(url);

            httpMethod.setDoAuthentication(authenticate);
            httpMethod.getParams().setParameter(
                "Connection", "Keep-Alive");
            return getClient(authenticate).executeMethod(
                httpMethod);
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }
    }
    
    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TestRESTAPI.class);
    }
    
    

}
