package fedora.server.resourceIndex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.sql.Connection;
import java.sql.Statement;

import java.util.HashMap;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import org.trippi.RDFFormat;
import org.trippi.TripleIterator;
import org.trippi.TriplestoreConnector;

import fedora.server.TestLogging;
import fedora.server.storage.ConnectionPool;

/**
 * Tests interactions between <code>ResourceIndexImpl</code>
 * and a <code>TriplestoreConnector</code> instance.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ResourceIndexImplTrippiIntegrationTest extends TestCase {

    private static final String TEST_DIR    = "build/junit";

    private static final String DB_DRIVER   = "org.h2.Driver";

    private static final String DB_URL      = "jdbc:h2:file:" + TEST_DIR + "/h2/test";

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

    public ResourceIndexImplTrippiIntegrationTest(String name) { super (name); }

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
                       + "  methodId   VARCHAR PRIMARY KEY,\n"
                       + "  bDefPid    VARCHAR NOT NULL,\n"
                       + "  methodName VARCHAR NOT NULL\n"
                       + ")");
        st.executeUpdate("CREATE INDEX riMethod_bDefPid ON riMethod(bDefPid)");

        st.executeUpdate("CREATE TABLE riMethodImpl (\n"
                       + "  methodImplId VARCHAR PRIMARY KEY,\n"
                       + "  bMechPid     VARCHAR NOT NULL,\n"
                       + "  methodId     VARCHAR NOT NULL\n"
                       + ")");
        st.executeUpdate("CREATE INDEX riMethodImpl_bMechPid ON riMethodImpl(bMechPid)");
        st.executeUpdate("CREATE INDEX riMethodImpl_methodId ON riMethodImpl(methodId)");

        st.executeUpdate("CREATE TABLE riMethodImplBinding (\n"
                       + "  methodImplBindingId INT AUTO_INCREMENT,\n"
                       + "  methodImplId        VARCHAR NOT NULL,\n"
                       + "  dsBindKey           VARCHAR NOT NULL\n"
                       + ")");
        st.executeUpdate("CREATE INDEX riMethodImplBinding_methodImplId ON riMethodImplBinding(methodImplId)");
        st.executeUpdate("CREATE INDEX riMethodImplBinding_dsBindKey ON riMethodImplBinding(dsBindKey)");

        st.executeUpdate("CREATE TABLE riMethodPermutation (\n"
                       + "  permutationId INT AUTO_INCREMENT,\n"
                       + "  methodId      VARCHAR NOT NULL,\n"
                       + "  permutation   VARCHAR NOT NULL\n"
                       + ")");
        st.executeUpdate("CREATE INDEX riMethodPermutation_methodId ON riMethodPermutation(methodId)");

        st.executeUpdate("CREATE TABLE riMethodMimeType (\n"
                       + "  mimeTypeId   INT AUTO_INCREMENT,\n"
                       + "  methodImplId VARCHAR NOT NULL,\n"
                       + "  mimeType     VARCHAR NOT NULL\n"
                       + ")");
        st.executeUpdate("CREATE INDEX riMethodMimeType_methodImplId ON riMethodMimeType(methodImplId)");

        st.close();
        _dbPool.free(conn);

        // construct the ri
        _ri = new ResourceIndexImpl(2, 
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
        config.put("ddlGenerator",            "org.nsdl.mptstore.impl.h2.H2DDLGenerator");
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

        // close the RI
        _ri.close();

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

    public void testNuthin() {
        System.out.println("Nothing");
    }

}
