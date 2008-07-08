/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.test.api;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;

import java.rmi.RemoteException;

import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.apache.abdera.Abdera;
import org.apache.abdera.ext.thread.ThreadHelper;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fedora.common.PID;

import fedora.server.management.FedoraAPIM;

import fedora.test.FedoraServerTestCase;

import fedora.utilities.Foxml11Document;
import fedora.utilities.Foxml11Document.ControlGroup;
import fedora.utilities.Foxml11Document.Property;
import fedora.utilities.Foxml11Document.State;

/**
 * @author Edwin Shin
 * @since 3.0
 * @version $Id$
 */
public class TestManagedDatastreams
        extends FedoraServerTestCase {

    private FedoraAPIM apim;

    private Abdera abdera;
    
    private String[] copyTempFileLocations = {
            "copy:///tmp/foo.txt",
            "copy://tmp/foo.txt",
            "copy://../etc/passwd",
            "temp:///tmp/foo.txt",
            "temp://tmp/foo.txt",
            "temp://../etc/passwd",
            "file:///tmp/foo.txt",
            "file:/tmp/foo.txt"};
    
    private String[] uploadedLocations = {
            "uploaded:///tmp/foo.txt",
            "uploaded://tmp/foo.txt"};

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        abdera = Abdera.getInstance();
        apim = getFedoraClient().getAPIM();
        System.setProperty("fedoraServerHost", "localhost");
        System.setProperty("fedoraServerPort", "8080");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testIngest() throws Exception {
        String pid = "demo:m_ds_test";
        
        for (String contentLocation : copyTempFileLocations) {
            try {
                apim.ingest(getAtomObject(pid, contentLocation), ATOM1_1.uri, null);
                fail("ingest should have failed with " + contentLocation);
            } catch (RemoteException e) {
                assertTrue(e.getMessage().contains("ValidationException"));
            }
            try {
                apim.ingest(getFoxmlObject(pid, contentLocation), FOXML1_1.uri, null);
                fail("ingest should have failed with " + contentLocation);
            } catch (RemoteException e) {
                assertTrue(e.getMessage().contains("ObjectIntegrityException"));
            }
        }

        for (String contentLocation : uploadedLocations) {
            try {
                apim.ingest(getAtomObject(pid, contentLocation), ATOM1_1.uri, null);
                fail("ingest should have failed with " + contentLocation);
            } catch (RemoteException e) {
                assertTrue(e.getMessage().contains("StreamReadException"));
            }
            try {
                apim.ingest(getFoxmlObject(pid, contentLocation), FOXML1_1.uri, null);
                fail("ingest should have failed with " + contentLocation);
            } catch (RemoteException e) {
                assertTrue(e.getMessage().contains("StreamReadException"));
            }
        }
    }

    @Test
    public void testAddDatastream() throws Exception {
        String pid = "demo:m_ds_test_add";

        apim.ingest(getAtomObject(pid, null), ATOM1_1.uri, null);

        try {
            for (String contentLocation : copyTempFileLocations) {
                try {
                    addDatastream(pid, contentLocation);
                    fail("addDatastream should have failed with "
                            + contentLocation);
                } catch (RemoteException e) {
                    assertTrue(e.getMessage().contains("ValidationException"));
                }
            }
            
            for (String contentLocation : uploadedLocations) {
                try {
                    addDatastream(pid, contentLocation);
                    fail("addDatastream should have failed with "
                            + contentLocation);
                } catch (RemoteException e) {
                    assertTrue(e.getMessage().contains("StreamReadException"));
                }
            }

        } finally {
            apim.purgeObject(pid, "test", false);
        }
    }

    @Test
    public void testModifyDatastreamByReference() throws Exception {
        String pid = "demo:m_ds_test_add";
        String dsLocation = "http://www.fedora.info/junit/datastream1.xml";
        apim.ingest(getAtomObject(pid, dsLocation), ATOM1_1.uri, null);

        try {
            for (String contentLocation : copyTempFileLocations) {
                try {
                    modifyDatastreamByReference(pid, contentLocation);
                    fail("modifyDatastreamByReference should have failed with " + contentLocation);
                } catch (RemoteException e) {
                    assertTrue(e.getMessage().contains("ValidationException"));
                }
            }
            
            for (String contentLocation : uploadedLocations) {
                try {
                    modifyDatastreamByReference(pid, contentLocation);
                    fail("modifyDatastreamByReference should have failed with " + contentLocation);
                } catch (RemoteException e) {
                    assertTrue(e.getMessage().contains("StreamReadException"));
                }
            }
            
            // A null contentLocation should cause the server to generate a 
            // copy:// url
            modifyDatastreamByReference(pid, null);
        } finally {
            apim.purgeObject(pid, "test", false);
        }
    }

    private byte[] getAtomObject(String pid, String contentLocation) throws Exception {
        Feed feed = createAtomObject(pid, contentLocation);
        
        Writer sWriter = new StringWriter();
        feed.writeTo("prettyxml", sWriter);
        return sWriter.toString().getBytes("UTF-8");
    }
	
	private byte[] getFoxmlObject(String pid, String contentLocation) throws Exception {
        Foxml11Document doc = createFoxmlObject(pid, contentLocation);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.serialize(out);
        return out.toByteArray();
    }
	
    private String addDatastream(String pid, String contentLocation)
            throws Exception {
        return apim.addDatastream(pid,
                                  "DS",
                                  null,
                                  "testManagedDatastreams",
                                  true,
                                  "text/plain",
                                  "",
                                  contentLocation,
                                  "M",
                                  "A",
                                  null,
                                  null,
                                  "testManagedDatastreams");
    }

    private String modifyDatastreamByReference(String pid,
                                               String contentLocation)
            throws Exception {
        return apim.modifyDatastreamByReference(pid,
                                                "DS",
                                                new String[] {},
                                                "testManagedDatastreams",
                                                "text/plain",
                                                "",
                                                contentLocation,
                                                null,
                                                null,
                                                "testManagedDatastreams",
                                                false);
    }
    
    private Feed createAtomObject(String spid, String contentLocation) throws Exception {        
        PID pid = PID.getInstance(spid);
        Date date = new Date(1);
        String title = "title";
        String author = "fedora.test.api.TestManagedDatastreams";
        
        Feed feed = abdera.newFeed();
        feed.setId(pid.toURI());
        feed.setTitle(title);
        feed.setUpdated(date);
        feed.addAuthor(author);
        
        if (contentLocation != null && contentLocation.length() > 0) {
            addAtomManagedDatastream(feed, contentLocation);
        }
        
        return feed;
    }
    
    private void addAtomManagedDatastream(Feed feed, String contentLocation) throws Exception {
        String dsId = "DS";
        
        Entry dsEntry = feed.addEntry();
        dsEntry.setId(feed.getId().toString() + "/" + dsId);
        
        Entry dsvEntry = feed.addEntry();
        dsvEntry.setId(dsEntry.getId().toString() + "/" + feed.getUpdatedString());
        
        dsEntry.setTitle(feed.getTitle());
        dsEntry.setUpdated(feed.getUpdated());
        dsEntry.addLink(dsvEntry.getId().toString(), Link.REL_ALTERNATE);
        dsEntry.addCategory(MODEL.STATE.uri, "A", null);
        dsEntry.addCategory(MODEL.CONTROL_GROUP.uri, "M", null);
        dsEntry.addCategory(MODEL.VERSIONABLE.uri, "true", null);
        
        dsvEntry.setTitle(feed.getTitle());
        dsvEntry.setUpdated(feed.getUpdated());
        ThreadHelper.addInReplyTo(dsvEntry, dsEntry.getId());
        dsvEntry.setSummary("summary");
        dsvEntry.setContent(new IRI(contentLocation), "text/plain");
    }
    
    private Foxml11Document createFoxmlObject(String spid, String contentLocation) throws Exception {        
        PID pid = PID.getInstance(spid);
        Date date = new Date(1);

        Foxml11Document doc = new Foxml11Document(pid.toString());
        doc.addObjectProperty(Property.STATE, "A");
        
        if (contentLocation != null && contentLocation.length() > 0) {
            String ds = "DS";
            String dsv = "DS1.0";
            doc.addDatastream(ds, State.A, ControlGroup.M, true);
            doc.addDatastreamVersion(ds, dsv, "text/plain", "label", 1, date);
            doc.setContentLocation(dsv, contentLocation, "URL");
        }
        return doc;
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(TestManagedDatastreams.class);
    }
}
