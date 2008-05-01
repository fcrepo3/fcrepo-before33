
package fedora.test.api;

import static org.apache.commons.httpclient.HttpStatus.SC_CREATED;
import static org.apache.commons.httpclient.HttpStatus.SC_NOT_FOUND;
import static org.apache.commons.httpclient.HttpStatus.SC_OK;
import static org.apache.commons.httpclient.HttpStatus.SC_TEMPORARY_REDIRECT;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.junit.Test;

import fedora.common.PID;
import fedora.server.management.FedoraAPIM;
import fedora.test.FedoraServerTestCase;

/**
 * Tests of the REST API. Tests assume a running instance of Fedora with the
 * REST API enabled. 
 * 
 * //TODO: actually validate the ResponseBody instead of just HTTP status codes
 * 
 * @author Edwin Shin
 * @since 3.0
 * @version $Id$
 */
public class TestRESTAPI
        extends FedoraServerTestCase {

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
        sb
                .append("  xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# ");
        sb.append("  http://www.fedora.info/definitions/1/0/foxml1-1.xsd\">");
        sb.append("  <foxml:objectProperties>");
        sb
                .append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"A\"/>");
        sb.append("  </foxml:objectProperties>");
        sb
                .append("  <foxml:datastream ID=\"DC\" CONTROL_GROUP=\"X\" STATE=\"A\">");
        sb
                .append("    <foxml:datastreamVersion ID=\"DC1.0\" MIMETYPE=\"text/xml\" LABEL=\"DC Record for Coliseum image object\">");
        sb.append("      <foxml:xmlContent>");
        sb
                .append("        <oai_dc:dc xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\">");
        sb.append("          <dc:title>Coliseum in Rome</dc:title>");
        sb.append("          <dc:creator>Thornton Staples</dc:creator>");
        sb.append("          <dc:subject>Architecture, Roman</dc:subject>");
        sb
                .append("          <dc:description>Image of Coliseum in Rome</dc:description>");
        sb
                .append("          <dc:publisher>University of Virginia Library</dc:publisher>");
        sb.append("          <dc:format>image/jpeg</dc:format>");
        sb.append("          <dc:identifier>demo:REST</dc:identifier>");
        sb.append("        </oai_dc:dc>");
        sb.append("      </foxml:xmlContent>");
        sb.append("    </foxml:datastreamVersion>");
        sb.append("  </foxml:datastream>");
        sb
                .append("  <foxml:datastream ID=\"RELS-EXT\" CONTROL_GROUP=\"M\" STATE=\"A\">");
        sb
                .append("    <foxml:datastreamVersion ID=\"RELS-EXT.0\" MIMETYPE=\"text/xml\" LABEL=\"Relationships\">");
        sb.append("      <foxml:xmlContent>");
        sb
                .append("        <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""
                        + "                 xmlns:rel=\"info:fedora/fedora-system:def/relations-external#\">");
        sb
                .append("          <rdf:Description rdf:about=\"info:fedora/demo:REST\">");
        sb
                .append("            <rel:hasFormalContentModel rdf:resource=\"info:fedora/demo:UVA_STD_IMAGE_1\"/>");
        sb.append("          </rdf:Description>");
        sb.append("        </rdf:RDF>");
        sb.append("      </foxml:xmlContent>");
        sb.append("    </foxml:datastreamVersion>");
        sb.append("  </foxml:datastream>");
        sb
                .append("  <foxml:datastream ID=\"DS1\" CONTROL_GROUP=\"X\" STATE=\"A\">");
        sb
                .append("    <foxml:datastreamVersion ID=\"DS1.0\" MIMETYPE=\"text/xml\" LABEL=\"DC Record for Coliseum image object\">");
        sb.append("      <foxml:xmlContent>");
        sb.append("        <foo>");
        sb.append("          <bar>baz</bar>");
        sb.append("        </foo>");
        sb.append("      </foxml:xmlContent>");
        sb.append("    </foxml:datastreamVersion>");
        sb.append("  </foxml:datastream>");
        sb.append("</foxml:digitalObject>");

        try {
            DEMO_REST_FOXML = sb.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException uee) {
        }
    }

    public void setUp() throws Exception {
        apim = getFedoraClient().getAPIM();
        apim
                .ingest(DEMO_REST_FOXML,
                        FOXML1_1.uri,
                        "ingesting new foxml object");
    }

    public void tearDown() throws Exception {
        apim.purgeObject(pid.toString(), "", false);
    }

    @Test
    public void testGetWADL() throws Exception {
        url = "/objects/application.wadl";

        assertEquals(SC_OK, get(false).getStatusCode());
        assertEquals(SC_OK, get(true).getStatusCode());
    }

    //public void testDescribeRepository() throws Exception {}

    // API-A
    @Test
    public void testGetObjectProfile() throws Exception {
        url = String.format("/objects/%s", pid.toString());
        assertEquals(SC_OK, get(false).getStatusCode());
        assertEquals(SC_OK, get(true).getStatusCode());

        url = String.format("/objects/%s?format=xml", pid.toString());
        assertEquals(SC_OK, get(false).getStatusCode());
        assertEquals(SC_OK, get(true).getStatusCode());

        // sanity check
        url = String.format("/objects/%s", "demo:BOGUS_PID");
        assertEquals(SC_NOT_FOUND, get(false).getStatusCode());
        assertEquals(SC_NOT_FOUND, get(true).getStatusCode());
    }

    public void testListMethods() throws Exception {
        url = String.format("/objects/%s/methods", pid.toString());
        assertEquals(SC_OK, get(false).getStatusCode());
        assertEquals(SC_OK, get(true).getStatusCode());

        url = String.format("/objects/%s/methods?format=xml", pid.toString());
        assertEquals(SC_OK, get(false).getStatusCode());
        assertEquals(SC_OK, get(true).getStatusCode());
    }

    public void testListDatastreams() throws Exception {
        url = String.format("/objects/%s/datastreams", pid.toString());
        assertEquals(SC_OK, get(false).getStatusCode());
        assertEquals(SC_OK, get(true).getStatusCode());

        url =
                String.format("/objects/%s/datastreams?format=xml", pid
                        .toString());
        assertEquals(SC_OK, get(false).getStatusCode());
        assertEquals(SC_OK, get(true).getStatusCode());
    }

    public void testGetDatastreamDissemination() throws Exception {
        url = String.format("/objects/%s/datastreams/RELS-EXT", pid.toString());
        assertEquals(SC_OK, get(false).getStatusCode());
        assertEquals(SC_OK, get(true).getStatusCode());

        // sanity check
        url =
                String.format("/objects/%s/datastreams/BOGUS_DSID", pid
                        .toString());
        assertEquals(SC_NOT_FOUND, get(false).getStatusCode());
        assertEquals(SC_NOT_FOUND, get(true).getStatusCode());
    }

    //public void testGetDissemination() throws Exception {}

    public void testFindObjects() throws Exception {
        url =
                String.format("/objects?pid=true&terms=%s&query=&format=xml",
                              pid.toString());
        assertEquals(SC_OK, get(false).getStatusCode());
    }

    public void testResumeFindObjects() throws Exception {
        // TODO parse responseBody for token
        url = "/objects?pid=true&query=&format=xml";
        assertEquals(SC_OK, get(false).getStatusCode());

        //url = String.format("/objects?sessionToken=%s", "");
        //assertEquals(SC_OK, get(false));
    }

    public void testGetObjectHistory() throws Exception {
        url = String.format("/objects/%s/versions", pid.toString());
        assertEquals(SC_OK, get(false).getStatusCode());
        assertEquals(SC_OK, get(true).getStatusCode());
    }

    // API-M
    public void testIngest() throws Exception {
        url = String.format("/objects/new");
        assertEquals(SC_CREATED, post("", true).getStatusCode());
    }

    public void testIngestObject() throws Exception {
        //TODO
    }

    public void testModifyObject() throws Exception {
        url = String.format("/objects/%s?label=%s", pid.toString(), "foo");
        HttpResponse response = put("", true);
        assertEquals(SC_TEMPORARY_REDIRECT, response.getStatusCode());
        Header locationHeader = response.getResponseHeader("location");
        assertNotNull(locationHeader);
        assertEquals(pid.toString(), locationHeader.getValue());
    }

    public void testGetObjectXML() throws Exception {
        url = String.format("/objects/%s/objectXML", pid.toString());
        assertEquals(SC_OK, get(true).getStatusCode());
    }

    public void testExportObject() throws Exception {
        url = String.format("/objects/%s/export", pid.toString());
        assertEquals(SC_OK, get(true).getStatusCode());

        url =
                String.format("/objects/%s/export?context=public", pid
                        .toString());
        assertEquals(SC_OK, get(true).getStatusCode());
    }

    public void testPurgeObject() throws Exception {
        url = String.format("/objects/%s", "demo:TEST_PURGE");
        assertEquals(SC_CREATED, post("", true).getStatusCode());
        url = String.format("/objects/demo:TEST_PURGE");
        assertEquals(SC_OK, delete(true).getStatusCode());
    }

    public void testAddDatastream() throws Exception {
        // inline (X) datastream
        String xmlData = "<foo>bar</foo>";
        url =
                String
                        .format("/objects/%s/datastreams/FOO?controlGroup=X&dsLabel=bar",
                                pid.toString());
        assertEquals(SC_CREATED, post(xmlData, true).getStatusCode());

        // managed (M) datastream
        url =
                String
                        .format("/objects/%s/datastreams/BAR?controlGroup=M&dsLabel=bar",
                                pid.toString());
        File temp = File.createTempFile("test", null);
        DataOutputStream os = new DataOutputStream(new FileOutputStream(temp));
        os.write(42);
        os.close();
        assertEquals(SC_CREATED, post(temp, true).getStatusCode());
    }

    public void testModifyDatastreamByReference() throws Exception {
        url =
                String
                        .format("/objects/%s/datastreams/BAR?controlGroup=M&dsLabel=bar",
                                pid.toString());
        File temp = File.createTempFile("test", null);
        DataOutputStream os = new DataOutputStream(new FileOutputStream(temp));
        os.write(42);
        os.close();
        assertEquals(SC_CREATED, post(temp, true).getStatusCode());

        url =
                String
                        .format("/objects/%s/datastreams/BAR?controlGroup=M",
                                pid.toString());
        
        temp = File.createTempFile("test2", null);
        os = new DataOutputStream(new FileOutputStream(temp));
        os.write(42);
        os.close();
        assertEquals(SC_CREATED, put(temp, true).getStatusCode());
    }

    public void testModifyDatastreamByValue() throws Exception {
        String xmlData = "<baz>quux</baz>";
        url =
                String.format("/objects/%s/datastreams/DS1?controlGroup=X", pid
                        .toString());

        assertEquals(SC_CREATED, put(xmlData, true).getStatusCode());
    }

    //public void testSetDatastreamState() throws Exception {}

    //public void testSetDatastreamVersionable() throws Exception {}

    //public void testCompareDatastreamChecksumRequest() throws Exception {}

    //public void testGetDatastream() throws Exception {}

    //public void testGetDatastreams() throws Exception {}

    //public void testGetDatastreamHistory() throws Exception {}

    public void testPurgeDatastream() throws Exception {
        url = String.format("/objects/%s/datastreams/RELS-EXT", pid.toString());
        assertEquals(SC_OK, delete(true).getStatusCode());
    }

    public void testGetNextPID() throws Exception {
        url = "/objects/nextPID";
        assertEquals(SC_OK, get(true).getStatusCode());

        url = "/objects/nextPID.xml";
        assertEquals(SC_OK, get(true).getStatusCode());
    }

    //public void testGetRelationships() throws Exception {}

    //public void testAddRelationship() throws Exception {}

    //public void testPurgeRelationship() throws Exception {}

    // helper methods
    private HttpClient getClient(boolean auth) {
        HttpClient client = new HttpClient();
        client.getParams().setAuthenticationPreemptive(true);
        if (auth) {
            client
                    .getState()
                    .setCredentials(new AuthScope(getHost(), Integer
                                            .valueOf(getPort()), "realm"),
                                    new UsernamePasswordCredentials(getUsername(),
                                                                    getPassword()));
        }
        return client;
    }

    /**
     * Issues an HTTP GET for the specified URL.
     * 
     * @param url
     *        The URL to GET: either an absolute URL or URL relative to the
     *        Fedora webapp (e.g. "/objects/demo:10").
     * @param authenticate
     * @return HttpResponse
     * @throws Exception
     */
    private HttpResponse get(boolean authenticate) throws Exception {
        return getOrDelete("GET", authenticate);
    }

    private HttpResponse delete(boolean authenticate) throws Exception {
        return getOrDelete("DELETE", authenticate);
    }

    /**
     * Issues an HTTP PUT to <code>url</code>. Callers are responsible for
     * calling releaseConnection() on the returned <code>HttpMethod</code>.
     * 
     * @param requestContent
     * @param authenticate
     * @return
     * @throws Exception
     */
    private HttpResponse put(String requestContent, boolean authenticate)
            throws Exception {
        return putOrPost("PUT", requestContent, authenticate);
    }

    private HttpResponse post(String requestContent, boolean authenticate)
            throws Exception {
        return putOrPost("POST", requestContent, authenticate);
    }
    
    private HttpResponse put(File requestContent, boolean authenticate) throws Exception {
        return putOrPost("PUT", requestContent, authenticate);
    }
    
    private HttpResponse post(File requestContent, boolean authenticate) throws Exception {
        return putOrPost("POST", requestContent, authenticate);
    }
    
    private HttpResponse getOrDelete(String method, boolean authenticate)
            throws Exception {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("url must be a non-empty value");
        } else if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            url = getBaseURL() + url;
        }
        HttpMethod httpMethod = null;
        try {
            if (method.equals("GET")) {
                httpMethod = new GetMethod(url);
            } else if (method.equals("DELETE")) {
                httpMethod = new DeleteMethod(url);
            } else {
                throw new IllegalArgumentException("method must be one of GET or DELETE.");
            }

            httpMethod.setDoAuthentication(authenticate);
            httpMethod.getParams().setParameter("Connection", "Keep-Alive");
            getClient(authenticate).executeMethod(httpMethod);
            return new HttpResponse(httpMethod);
        } finally {
            if (httpMethod != null) {
                httpMethod.releaseConnection();
            }
        }
    }

    private HttpResponse putOrPost(String method,
                                   Object requestContent,
                                   boolean authenticate) throws Exception {
        if (url == null || url.length() == 0) {
            throw new IllegalArgumentException("url must be a non-empty value");
        } else if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            url = getBaseURL() + url;
        }

        EntityEnclosingMethod httpMethod = null;
        try {
            if (method.equals("PUT")) {
                httpMethod = new PutMethod(url);
            } else if (method.equals("POST")) {
                httpMethod = new PostMethod(url);
            } else {
                throw new IllegalArgumentException("method must be one of PUT or POST.");
            }

            httpMethod.setDoAuthentication(authenticate);
            httpMethod.getParams().setParameter("Connection", "Keep-Alive");
            httpMethod.setContentChunked(true);
            if (requestContent instanceof String) {
                httpMethod
                        .setRequestEntity(new StringRequestEntity((String) requestContent,
                                                                  "text/xml",
                                                                  "utf-8"));
            } else if (requestContent instanceof File) {
                Part[] parts =
                        {
                                new StringPart("param_name", "value"),
                                new FilePart(((File) requestContent).getName(),
                                             (File) requestContent)};
                httpMethod
                        .setRequestEntity(new MultipartRequestEntity(parts,
                                                                     httpMethod
                                                                             .getParams()));
            } else {
                throw new IllegalArgumentException("requestContent must be a String or File");
            }
            getClient(authenticate).executeMethod(httpMethod);
            return new HttpResponse(httpMethod);
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

    class HttpResponse {

        private final int statusCode;

        private final byte[] responseBody;

        private final Header[] responseHeaders;

        private final Header[] responseFooters;

        HttpResponse(int status, byte[] body, Header[] headers, Header[] footers) {
            statusCode = status;
            responseBody = body;
            responseHeaders = headers;
            responseFooters = footers;
        }

        HttpResponse(HttpMethod method)
                throws IOException {
            statusCode = method.getStatusCode();
            responseBody = method.getResponseBody();
            responseHeaders = method.getResponseHeaders();
            responseFooters = method.getResponseFooters();
        }

        public int getStatusCode() {
            return statusCode;
        }

        public byte[] getResponseBody() {
            return responseBody;
        }

        public Header[] getResponseHeaders() {
            return responseHeaders;
        }

        public Header[] getResponseFooters() {
            return responseFooters;
        }

        public Header getResponseHeader(String headerName) {
            for (Header header : responseHeaders) {
                if (header.getName().equalsIgnoreCase(headerName)) {
                    return header;
                }
            }
            return null;
        }
    }
}
