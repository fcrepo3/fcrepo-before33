package fedora.server.resourceIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.sql.Connection;
import java.sql.Statement;

import java.util.Date;
import java.util.HashMap;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import org.jrdf.graph.Triple;

import org.trippi.RDFFormat;
import org.trippi.RDFUtil;
import org.trippi.TripleIterator;
import org.trippi.TriplestoreConnector;

import fedora.server.TestLogging;

import fedora.server.storage.ConnectionPool;

import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;

/**
 * Tests interactions between <code>ResourceIndexImpl</code>
 * and a <code>TriplestoreConnector</code> instance.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ResourceIndexImplTrippiIntegrationTest extends TestCase {

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

    private void init(int indexLevel) throws Exception {
        // construct the ri
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
        config.put("poolInitialSize", "2");
        config.put("poolMaxSize", "5");

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
            triples = TripleIterator.fromStream(new FileInputStream(dump), RDFFormat.TURTLE);
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
        doAddEmptyObjectCountTest(0, 0);
    }

    public void testAddEmptyObjectLevelOne() throws Exception {
        doAddEmptyObjectCountTest(1, 6);
    }

    public void testAddEmptyObjectLevelTwo() throws Exception {
        doAddEmptyObjectCountTest(1, 6);
    }

    public void testAddTenEmptyObjectsLevelTwo() throws Exception {
        init(2);
        for (int i = 0; i < 10; i++) {
            _ri.addDigitalObject(createObject("test:empty" + i, 
                                              "label" + i));
        }
        _ri.flushBuffer();
        int count = _ri.countTriples(null, null, null, -1);
        assertEquals("Wrong number of triples in RI after adding 10 empty "
                + "objects at indexing level 2", 60, count);
    }
/*
    public void testModifyEmptyObject1000Times() throws Exception {
        doModifyEmptyObject1000TimesTest(false);
    }
*/

    public void testModifyEmptyObject1000TimesWhileFlushing() throws Exception {
        doModifyEmptyObject1000TimesTest(true);
    }
/*
    public void testModify10EmptyObjects100TimesEach() throws Exception {
        doModify10EmptyObjects1000TimesEach(false);
    }

    public void testModify10EmptyObjects100TimesEachWhileFlushing() throws Exception {
        doModify10EmptyObjects1000TimesEach(true);
    }
*/

    // Utility methods for tests

    public void doModify10EmptyObjects1000TimesEach(boolean whileFlushing) throws Exception {
        init(2);
        DigitalObject[] objs = new DigitalObject[10];
        for (int i = 1; i <= objs.length; i++) {
            objs[i - 1] = createObject("test:object" + i, "label0");
            _ri.addDigitalObject(objs[i - 1]);
        }
        Flusher flusher = new Flusher(0);
        if (whileFlushing) {
            flusher.start();
        }
        for (int i = 0; i < objs.length; i++) {
            modify1000Times(objs[i]);
        }
        if (whileFlushing) {
            flusher.finish();
        }
        _ri.flushBuffer();
        int count = _ri.countTriples(null, null, null, -1);
        if (count != 60) {
            TripleIterator triples = _ri.findTriples(null, null, null, -1);
            int i = 0;
            while (triples.hasNext()) {
                i++;
                Triple triple = triples.next();
                System.out.println("Triple #" + i + "/" + count + " : " + RDFUtil.toString(triple));
            }
        }
        assertEquals("Wrong number of triples in RI after modifying 10 empty "
                + "objects 1000 times each", 60, count);
    }

    public void doModifyEmptyObject1000TimesTest(boolean whileFlushing) throws Exception {
        init(2);
        DigitalObject obj = createObject("test:empty", "label0");
        _ri.addDigitalObject(obj);
        Flusher flusher = new Flusher(0);
        if (whileFlushing) {
            flusher.start();
        }
        modify1000Times(obj);
        if (whileFlushing) {
            flusher.finish();
        }
        _ri.flushBuffer();
        int count = _ri.countTriples(null, null, null, -1);
        if (count != 6) {
            TripleIterator triples = _ri.findTriples(null, null, null, -1);
            int i = 0;
            while (triples.hasNext()) {
                i++;
                Triple triple = triples.next();
                System.out.println("Triple #" + i + "/" + count + " : " + RDFUtil.toString(triple));
            }
        }
        assertEquals("Wrong number of triples in RI after modifying empty "
                + "object 100 times", 6, count);
    }

    private void modify1000Times(DigitalObject obj) throws Exception {
        for (int i = 1; i <= 1000; i++) {
            obj.setLabel("label" + i);
            _ri.modifyDigitalObject(obj);
        }
    }

    private void doAddEmptyObjectCountTest(int level, 
                                           int expectedCount) 
            throws Exception {
        init(level);
        _ri.addDigitalObject(createObject("test:empty", "label"));
        _ri.flushBuffer();
        int count = _ri.countTriples(null, null, null, -1);
        assertEquals("Wrong number of triples in RI after adding empty "
                + "object at indexing level " + level, expectedCount, count);
    }

    private static DigitalObject createObject(String pid,
                                              String label) {
        Date now = new Date();
        return createObject(pid, DigitalObject.FEDORA_OBJECT, "A", 
                "fedoraAdmin", label, null, now, now);
    }

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

    public class Flusher extends Thread {

        private int _sleepMS;
        private boolean _shouldFinish = false;
        private Exception _error;

        public Flusher(int sleepMS) {
            _sleepMS = sleepMS;
        }

        /**
         * Set signal for flusher to finish and wait for it.
         * If the flusher encountered an error any time while it was running,
         * it will be thrown here.
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
