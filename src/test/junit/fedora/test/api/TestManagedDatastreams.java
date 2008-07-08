/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.test.api;

import java.rmi.RemoteException;

import java.util.Date;

import junit.framework.JUnit4TestAdapter;

import org.apache.axis.utils.ByteArrayOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fedora.common.PID;

import fedora.server.management.FedoraAPIM;

import fedora.test.FedoraServerTestCase;

import fedora.utilities.FoxmlDocument;
import fedora.utilities.FoxmlDocument.ControlGroup;
import fedora.utilities.FoxmlDocument.Property;
import fedora.utilities.FoxmlDocument.State;

/**
 * @author Edwin Shin
 * @since 2.2.3
 * @version $Id$
 */
public class TestManagedDatastreams
        extends FedoraServerTestCase {

    private FedoraAPIM apim;
    
    private String[] copyTempFileLocations = {
            "copy:///tmp/foo.txt",
            "copy://tmp/foo.txt",
            "copy://../etc/passwd",
            "temp:///tmp/foo.txt",
            "temp://tmp/foo.txt",
            "temp://../etc/passwd",
            "file:///tmp/foo.txt",
            "file:/tmp/foo.txt"};
    
    private String[] uploadedLocations = {"uploaded:///tmp/foo.txt"};

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
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
                apim.ingest(getObject(pid, contentLocation), "foxml1.0", null);
                fail("ingest should have failed with " + contentLocation);
            } catch (RemoteException e) {
                assertTrue(e.getMessage().contains("ObjectIntegrityException"));
            }
        }

        for (String contentLocation : uploadedLocations) {
            try {
                apim.ingest(getObject(pid, contentLocation), "foxml1.0", null);
                fail("ingest should have failed with " + contentLocation);
            } catch (RemoteException e) {
                assertTrue(e.getMessage().contains("StreamReadException"));
            }
        }
    }

    @Test
    public void testAddDatastream() throws Exception {
        String pid = "demo:m_ds_test_add";

        apim.ingest(getObject(pid, null), "foxml1.0", null);

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
        apim.ingest(getObject(pid, dsLocation), "foxml1.0", null);

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
    
    private byte[] getObject(String pid, String contentLocation) throws Exception {
        FoxmlDocument doc = createFoxmlObject(pid, contentLocation);
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
    
    private FoxmlDocument createFoxmlObject(String spid, String contentLocation) throws Exception {        
        PID pid = PID.getInstance(spid);
        Date date = new Date(1);

        FoxmlDocument doc = new FoxmlDocument(pid.toString());
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
