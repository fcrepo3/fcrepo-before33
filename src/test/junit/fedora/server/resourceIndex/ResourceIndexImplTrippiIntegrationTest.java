package fedora.server.resourceIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.sql.Connection;
import java.sql.Statement;

import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.jrdf.graph.Triple;

import org.trippi.RDFFormat;
import org.trippi.RDFUtil;
import org.trippi.TripleIterator;
import org.trippi.TriplestoreConnector;

import fedora.server.TestLogging;

import fedora.server.storage.ConnectionPool;

import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamReferencedContent;
import fedora.server.storage.types.DigitalObject;

/**
 * Tests interactions between <code>ResourceIndexImpl</code>
 * and a <code>TriplestoreConnector</code> instance.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ResourceIndexImplTrippiIntegrationTest extends TestCase {

    private static final Logger LOG = 
            Logger.getLogger(
            ResourceIndexImplTrippiIntegrationTest.class.getName());

    private static final int BASE_TRIPLES_PER_OBJECT = 6;
    
    private static final int TRIPLES_PER_DATASTREAM = 7;

    private static final String TEST_DIR    = "build/junit";

    private static final String DB_DRIVER   = "org.apache.derby.jdbc.EmbeddedDriver";

    private static final String DB_URL      = "jdbc:derby:test;create=true";

    private static final String DB_USERNAME = "test";

    private static final String DB_PASSWORD = "test";

    /**
     * The <code>ResourceIndexImpl</code> instance we'll be using.
     */
    private ResourceIndex _ri;

    /**
     * Where to get DB connections from.
     */
    private ConnectionPool _dbPool;

    public ResourceIndexImplTrippiIntegrationTest(String name) { 
        super (name); 
        System.setProperty("derby.system.home", TEST_DIR + "/derby");
    }

    /**
     * Prepare for testing by instantiating a fresh 
     * <code>ResourceIndexImpl</code>.
     *
     * @throws Exception if setup fails for any reason.
     */
    public void setUp() throws Exception {

        // set up the db pool
        _dbPool = new ConnectionPool(DB_DRIVER, DB_URL, DB_USERNAME, 
                DB_PASSWORD, 20, 5, -1L, 0, -1L, 3, -1L, false, false, false, 
                (byte) 1);

        // set up the tables needed by the ri
        Connection conn = _dbPool.getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate("CREATE TABLE riMethod (\n"
                       + "  methodId   VARCHAR(255) NOT NULL,\n"
                       + "  bDefPid    VARCHAR(255) NOT NULL,\n"
                       + "  methodName VARCHAR(255) NOT NULL\n"
                       + ")");
        st.executeUpdate("CREATE INDEX riMethod_bDefPid ON riMethod(bDefPid)");

        st.executeUpdate("CREATE TABLE riMethodImpl (\n"
                       + "  methodImplId VARCHAR(255) NOT NULL,\n"
                       + "  bMechPid     VARCHAR(255) NOT NULL,\n"
                       + "  methodId     VARCHAR(255) NOT NULL\n"
                       + ")");
        st.executeUpdate("CREATE INDEX riMethodImpl_bMechPid ON riMethodImpl(bMechPid)");
        st.executeUpdate("CREATE INDEX riMethodImpl_methodId ON riMethodImpl(methodId)");

        st.executeUpdate("CREATE TABLE riMethodImplBinding (\n"
                       + "  methodImplBindingId INT NOT NULL GENERATED ALWAYS AS IDENTITY,\n"
                       + "  methodImplId        VARCHAR(255) NOT NULL,\n"
                       + "  dsBindKey           VARCHAR(255) NOT NULL\n"
                       + ")");
        st.executeUpdate("CREATE INDEX riMethodImplBinding_methodImplId ON riMethodImplBinding(methodImplId)");
        st.executeUpdate("CREATE INDEX riMethodImplBinding_dsBindKey ON riMethodImplBinding(dsBindKey)");

        st.executeUpdate("CREATE TABLE riMethodPermutation (\n"
                       + "  permutationId INT NOT NULL GENERATED ALWAYS AS IDENTITY,\n"
                       + "  methodId      VARCHAR(255) NOT NULL,\n"
                       + "  permutation   VARCHAR(255) NOT NULL\n"
                       + ")");
        st.executeUpdate("CREATE INDEX riMethodPermutation_methodId ON riMethodPermutation(methodId)");

        st.executeUpdate("CREATE TABLE riMethodMimeType (\n"
                       + "  mimeTypeId   INT NOT NULL GENERATED ALWAYS AS IDENTITY,\n"
                       + "  methodImplId VARCHAR(255) NOT NULL,\n"
                       + "  mimeType     VARCHAR(255) NOT NULL\n"
                       + ")");
        st.executeUpdate("CREATE INDEX riMethodMimeType_methodImplId ON riMethodMimeType(methodImplId)");

        st.close();
        _dbPool.free(conn);

    }

    /**
     * Initialize the RI at the given level.
     *
     * This should be called in a test prior to accessing the RI.
     */
    private void init(int indexLevel) throws Exception {
        _ri = new ResourceIndexImpl(indexLevel, 
                                    getConnector(),
                                    _dbPool,
                                    new HashMap(),
                                    new TestLogging());
    }

    /**
     * Get the <code>TriplestoreConnector</code> to be used in conjunction
     * with the <code>ResourceIndexImpl</code>.
     *
     * @throws Exception if constructing the connector fails for any reason.
     */
    private static TriplestoreConnector getConnector() throws Exception {

        HashMap config = new HashMap();

        config.put("backslashIsEscape",       "false");
        config.put("ddlGenerator",            "org.nsdl.mptstore.impl.derby.DerbyDDLGenerator");
        config.put("autoFlushBufferSize",     "1000");
        config.put("autoFlushDormantSeconds", "5");
        config.put("bufferFlushBatchSize",    "1000");
        config.put("bufferSafeCapacity",      "1000");
        config.put("fetchSize",               "1000");
        config.put("jdbcDriver",              DB_DRIVER);
        config.put("jdbcURL",                 DB_URL);
        config.put("username",                DB_USERNAME);
        config.put("password",                DB_PASSWORD);
        config.put("poolInitialSize",         "5");
        config.put("poolMaxSize",             "10");

        return TriplestoreConnector.init("org.trippi.impl.mpt.MPTConnector",
                                         config);
    }
           
    /**
     * Clean up so the next test can run with fresh data.
     *
     * @throws Exception if tearDown fails for any reason.
     */
    public void tearDown() throws Exception {
        if (_ri != null) tearDownTriplestore();
        if (_dbPool != null) tearDownDB();
    }

    private void tearDownTriplestore() throws Exception {

        // delete all triples from the RI
        File dump = new File(TEST_DIR + "/all-triples.txt");
        FileOutputStream out = null;
        try {
            // write all to temp file
            TripleIterator triples = _ri.findTriples(null, null, null, -1);
            out = new FileOutputStream(dump);
            triples.toStream(out, RDFFormat.TURTLE);
            try { out.close(); } catch (Exception e) { }
            out = null;

            // load all from temp file
            triples = TripleIterator.fromStream(new FileInputStream(dump), 
                                                RDFFormat.TURTLE);
            _ri.delete(triples, true);
        } finally {
            if (out != null) out.close();
            dump.delete();
        }

        _ri.close();
    }

    private void tearDownDB() throws Exception {

        // destroy the Fedora-related RI tables
        Connection conn = _dbPool.getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate("DROP TABLE riMethod");
        st.executeUpdate("DROP TABLE riMethodImpl");
        st.executeUpdate("DROP TABLE riMethodImplBinding");
        st.executeUpdate("DROP TABLE riMethodPermutation");
        st.executeUpdate("DROP TABLE riMethodMimeType");
        st.close();
        _dbPool.free(conn);
    }

    // Tests

    public void testAddEmptyObjectLevelZero() throws Exception {
        // ri level 0, expect 0 triples
        doAddEmptyObjectCountTest(0, 0);
    }

    public void testAddEmptyObjectLevelOne() throws Exception {
        // ri level 1, expect base triples
        doAddEmptyObjectCountTest(1, BASE_TRIPLES_PER_OBJECT);
    }

    public void testAddEmptyObject() throws Exception {
        // 1 obj, 0 datastreams, 0 mods, don't flush till end
        doModifyObjectsTest(1, 0, 0, false);
    }

    public void testAddSeveralObjectsWithDatastreams() throws Exception {
        // 10 obj, 10 datastreams, 0 mods, don't flush until end
        doModifyObjectsTest(10, 10, 0, false);
    }

    public void testModifyOneObjectSeveralTimes() throws Exception {
        // 1 obj, 10 datastreams, 10 mods, don't flush till end
        doModifyObjectsTest(1, 10, 10, false); 
    }

    public void testModifySeveralObjectsSeveralTimesWhileFlushing() throws Exception {
        // 5 obj, 10 datastreams, 10 mods, flush while modifying
        doModifyObjectsTest(5, 10, 10, true); 
    }

    // Utility methods for tests

    /**
     * Create some number of test objects with some number of datastreams
     * each, modify them each some number of times, and verify we end up
     * with the number of triples expected.
     */
    public void doModifyObjectsTest(int numObjects,
                                    int datastreamsPerObject,
                                    int modificationsPerObject,
                                    boolean whileFlushing) throws Exception {

        int expectedCount = (numObjects * BASE_TRIPLES_PER_OBJECT)
                          + (numObjects * 
                            (datastreamsPerObject * TRIPLES_PER_DATASTREAM));

        // initialize ri at level 2
        init(2);

        // start by instantiating and adding all objects
        DigitalObject[] objects = new DigitalObject[numObjects];
        for (int i = 1; i <= numObjects; i++) {
            objects[i - 1] = createObject("test:object" + i,
                                          "first label",
                                          datastreamsPerObject);
            _ri.addDigitalObject(objects[i - 1]);
        }

        // start the flusher if necessary
        Flusher flusher = new Flusher(0);
        if (whileFlushing) {
            flusher.start();
        }

        // do the mods for each object
        for (int i = 0; i < numObjects; i++) {
            doModifyLoop(objects[i], modificationsPerObject);
        }

        // let the flusher finish
        if (whileFlushing) {
            flusher.finish();
        }

        // flush at end
        _ri.flushBuffer();

        // determine the count
        int count = _ri.countTriples(null, null, null, -1);

        // if not what's expected, log the triples and fail the test
        if (count != expectedCount) {
            String msg = "Wrong number of triples in RI after modification "
                    + "test.  Expected " + expectedCount + ", got "
                    + count + ".";
            StringBuffer allTriples = new StringBuffer();
            TripleIterator triples = _ri.findTriples(null, null, null, -1);
            int i = 0;
            while (triples.hasNext()) {
                i++;
                Triple triple = triples.next();
                allTriples.append("\n" + RDFUtil.toString(triple));
            }
            LOG.warn(msg + "  Triples follow." + allTriples.toString());
            fail(msg + "  Triples have been logged.");
        }
    }

    /**
     * Modify the given object in a trivial way a given number of times.
     */
    private void doModifyLoop(DigitalObject obj, int num) throws Exception {
        for (int i = 1; i <= num; i++) {
            obj.setLabel("label" + i);
            _ri.modifyDigitalObject(obj);
        }
    }

    private void doAddEmptyObjectCountTest(int level, 
                                           int expectedCount) 
            throws Exception {
        init(level);
        _ri.addDigitalObject(createObject("test:empty", "label", 0));
        _ri.flushBuffer();
        int count = _ri.countTriples(null, null, null, -1);
        assertEquals("Wrong number of triples in RI after adding empty "
                + "object at indexing level " + level, expectedCount, count);
    }

    /**
     * Create a simple test object with the given number of datastreams.
     */
    private static DigitalObject createObject(String pid,
                                              String label,
                                              int numDatastreams) {
        Date now = new Date();
        DigitalObject obj = createObject(pid, DigitalObject.FEDORA_OBJECT, "A", 
                "fedoraAdmin", label, null, now, now);
        for (int i = 1; i <= numDatastreams; i++) {
            String id = "DS" + i;
            obj.datastreams(id).add(createEDatastream(id));
        }
        return obj;
    }

    /**
     * Create a test object with the given properties.
     */
    private static DigitalObject createObject(String pid,
                                              int fedoraObjectType,
                                              String state,
                                              String ownerId,
                                              String label,
                                              String contentModelId,
                                              Date createDate,
                                              Date lastModDate) {
        DigitalObject obj = new BasicDigitalObject();
        obj.setPid(pid);
        obj.setFedoraObjectType(fedoraObjectType);
        obj.setState(state);
        obj.setOwnerId(ownerId);
        obj.setLabel(label);
        obj.setContentModelId(contentModelId);
        obj.setCreateDate(createDate);
        obj.setLastModDate(lastModDate);
        return obj;
    }

    /**
     * Create an externally-referenced datastream version for a datastream
     * with the given id.
     */
    private static Datastream createEDatastream(String id) {
        DatastreamReferencedContent ds = new DatastreamReferencedContent();
        ds.DatastreamID = id;
        ds.DSMIME = "text/plain";
        ds.DSControlGrp = "E";
        ds.DSState = "A";
        ds.DSVersionable = true;
        ds.DSVersionID = id + ".0";
        ds.DSLabel = "ds label";
        ds.DSCreateDT = new Date();
        ds.DSSize = 100;
        ds.DSLocation = "http://www.nowhere.com/hi.txt";
        ds.DSLocationType = "URL";
        return ds;
    }

    /**
     * A Thread that continuously flushes the buffer.
     */
    public class Flusher extends Thread {

        private int _sleepMS;
        private boolean _shouldFinish = false;
        private Exception _error;

        /**
         * Construct a flusher that sleeps the given number of milliseconds
         * between flush attempts.
         *
         * @param sleepMS milliseconds to sleep.  Will simply yield between
         *                flush attempts if less than 1.
         */
        public Flusher(int sleepMS) {
            _sleepMS = sleepMS;
        }

        /**
         * Set signal for flusher to finish and wait for it.
         *
         * @throws Exception if the flusher encountered an error any time 
         *                   while it was running.
         */
        public void finish() throws Exception {
            _shouldFinish = true;
            while (this.isAlive()) {
                try { Thread.sleep(50); } catch (InterruptedException e) { }
            }
            if (_error != null) {
                throw _error;
            }
        }

        /**
         * Flush the buffer until the finish signal arrives from another
         * thread.
         */
        public void run() {
            try {
                while (!_shouldFinish) {
                    if (_sleepMS > 0) {
                        try { Thread.sleep(_sleepMS); } catch (InterruptedException e) { }
                    } else {
                        this.yield();
                    }
                    _ri.flushBuffer();
                }
            } catch (Exception e) {
                _error = e;
            }
        }

    }
}
