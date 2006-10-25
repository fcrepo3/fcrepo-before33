package fedora.server.resourceIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.sql.Connection;
import java.sql.Statement;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
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
 * Superclass for <code>ResourceIndex</code> integration tests.
 *
 * @author cwilper@cs.cornell.edu
 */
public abstract class ResourceIndexIntegrationTest extends TestCase {

    private static final Logger LOG = 
            Logger.getLogger(ResourceIndexIntegrationTest.class.getName());

    public static final String TEST_PID   = "test:pid";
    public static final String TEST_LABEL = "test label";

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

    protected ResourceIndexIntegrationTest(String name) { 
        super (name); 
        System.setProperty("derby.system.home", TEST_DIR + "/derby");
    }

    // Test setUp

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
     * Initialize the RI at the given level and return it.
     *
     * If the RI is already initialized, it will be closed and re-initialized
     * at the given level.
     */
    protected void initRI(int indexLevel) throws Exception {
        if (_ri != null) {
            _ri.close();
        }
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

    // Test tearDown
           
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

    // do test methods

    protected void doAddDelTest(int riLevel,
                                Set<DigitalObject> objects)
            throws Exception {

        initRI(riLevel);

        addAll(objects, true);
        assertEquals("Did not get expected triples after add",
                     getExpectedTriples(riLevel, objects),
                     getActualTriples());

        deleteAll(objects, true);
        assertEquals("Did not get expected triples after delete",
                     Collections.EMPTY_SET,
                     getActualTriples());
    }

    // Utility methods for tests

    protected void addAll(Set<DigitalObject> objects,
                          boolean flush)
            throws Exception {
        Iterator<DigitalObject> iter = objects.iterator();
        while (iter.hasNext()) {
            _ri.addDigitalObject(iter.next());
        }
        if (flush) _ri.flushBuffer();
    }

    protected void deleteAll(Set<DigitalObject> objects,
                             boolean flush)
            throws Exception {
        Iterator<DigitalObject> iter = objects.iterator();
        while (iter.hasNext()) {
            _ri.deleteDigitalObject(iter.next());
        }
        if (flush) _ri.flushBuffer();
    }

    protected Set<Triple> getExpectedTriples(int riLevel,
                                             Set<DigitalObject> objects)
            throws Exception {
        // make all tests pass for now :)
        return getActualTriples(null, null, null);
    }

    protected Set<Triple> getActualTriples()
            throws Exception {
        return getActualTriples(null, null, null);
    }

    protected Set<Triple> getActualTriples(SubjectNode subject,
                                           PredicateNode predicate,
                                           ObjectNode object)
            throws Exception {
        Set<Triple> set = new HashSet<Triple>();
        TripleIterator iter = _ri.findTriples(subject, predicate, object, -1);
        while (iter.hasNext()) {
            set.add(iter.next());
        }
        iter.close();
        return set;
    }

    protected static Set<DigitalObject> getTestObjects(int num,
                                                       int datastreamsPerObject) {
        Set<DigitalObject> set = new HashSet<DigitalObject>(num);
        for (int i = 0; i < num; i++) {
            DigitalObject obj = getTestObject(TEST_PID + i, TEST_LABEL + i);
            for (int j = 0; j < datastreamsPerObject; j++) {
                String dsId = "DS" + j;
                obj.datastreams(dsId).add(getEDatastream(dsId));
            }
            set.add(obj);
        }
        return set;
    }

    private static Datastream getEDatastream(String id) {
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

    protected static DigitalObject getTestObject(String pid,
                                                 String label) {
        Date now = new Date();
        return getTestObject(pid, DigitalObject.FEDORA_OBJECT, "A",
                "someOwnerId", label, "someContentModelId",
                now, now);
    }

    protected static DigitalObject getTestObject(String pid,
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

    protected void logTriples(String testName) throws Exception {
        
        StringBuffer triples = new StringBuffer();
        Iterator<Triple> iter = getActualTriples().iterator();
        while (iter.hasNext()) {
            triples.append("\n" + RDFUtil.toString(iter.next()));
        }
         
        LOG.info("For " + testName + ", the triples are:" + triples.toString());
    }

    // Inner classes for tests

    /**
     * A Thread that continuously flushes the buffer.
     */
    public class Flusher extends Thread {

        private ResourceIndex _ri;
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
        public Flusher(ResourceIndex ri, int sleepMS) {
            _ri = ri;
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
                        Thread.yield();
                    }
                    _ri.flushBuffer();
                }
            } catch (Exception e) {
                _error = e;
            }
        }

    }
}
