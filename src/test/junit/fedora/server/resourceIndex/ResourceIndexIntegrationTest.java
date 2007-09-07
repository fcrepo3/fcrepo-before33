package fedora.server.resourceIndex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;

import org.trippi.RDFFormat;
import org.trippi.RDFUtil;
import org.trippi.TripleIterator;
import org.trippi.TriplestoreConnector;

import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.DOReader;
import fedora.server.storage.MockRepositoryReader;
import fedora.server.storage.SimpleBDefReader;
import fedora.server.storage.SimpleBMechReader;
import fedora.server.storage.SimpleDOReader;

import fedora.server.storage.translation.DOTranslationUtility;
import fedora.server.storage.translation.FOXMLDOSerializer;
import fedora.server.storage.translation.FOXMLDODeserializer;

import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DatastreamManagedContent;
import fedora.server.storage.types.DatastreamReferencedContent;
import fedora.server.storage.types.DigitalObject;
//import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.DSBindingMap;
import fedora.server.storage.types.DSBinding;

/**
 * Superclass for <code>ResourceIndex</code> integration tests.
 *
 * @author cwilper@cs.cornell.edu
 */
public abstract class ResourceIndexIntegrationTest {

    private static final Logger LOG = 
            Logger.getLogger(ResourceIndexIntegrationTest.class.getName());

    private static final String TEST_DIR    = "build/junit";

    private static final String DB_DRIVER   = "org.apache.derby.jdbc.EmbeddedDriver";

    private static final String DB_URL      = "jdbc:derby:test;create=true";

    private static final String DB_USERNAME = "test";

    private static final String DB_PASSWORD = "test";

    // needs to be set in order for object serializers/deserializers to work
    static {
        Datastream.defaultChecksumType = "DISABLED";
    }

    /**
     * The <code>ResourceIndexImpl</code> instance we'll be using.
     */
    private ResourceIndex _ri;

    /**
     * The flusher instance we'll use.
     */
    private Flusher _flusher;

    /**
     * Where to get DB connections from.
     */
    private static ConnectionPool _dbPool;

    // Test setUp

    /**
     * Prepare for testing by instantiating a fresh 
     * <code>ResourceIndexImpl</code>.
     *
     * @throws Exception if setup fails for any reason.
     */
    @BeforeClass
    public static void setUpClass() throws Exception {

        System.setProperty("derby.system.home", TEST_DIR + "/derby");

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

    protected void logDBState() throws Exception {
        StringBuffer out = new StringBuffer();
        out.append("CURRENT TABLE STATE\n\n");
        addTableStateInfo("riMethod", false, out);
        addTableStateInfo("riMethodImpl", false, out);
        addTableStateInfo("riMethodImplBinding", true, out);
        addTableStateInfo("riMethodPermutation", true, out);
        addTableStateInfo("riMethodMimeType", true, out);
        LOG.warn(out.toString());
    }

    private void addTableStateInfo(String table, boolean firstInt, StringBuffer out) throws Exception {
        out.append(table);
        out.append("\n---------------------\n");
        Connection conn = _dbPool.getConnection();
        Statement st = conn.createStatement();
        ResultSet r = st.executeQuery("SELECT * FROM " + table);
        int cols = r.getMetaData().getColumnCount();
        while (r.next()) {
            for (int i = 0; i < cols; i++) {
                if (i == 0 && firstInt) {
                    out.append(r.getInt(i+1));
                } else {
                    out.append("\t" + r.getString(i+1));
                }
            }
        }
        out.append("\n\n");
        r.close();
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
            try { _ri.close(); } catch (Exception e) { }
        }
        MethodInfoStore methodInfoStore = 
                new DatabaseMethodInfoStore(_dbPool, indexLevel == 2);
        TripleGenerator generator =
                new MethodAwareTripleGenerator(methodInfoStore);
        
        _ri = new ResourceIndexImpl(getConnector(),
                                       methodInfoStore,
                                       generator,
                                       indexLevel,
                                       false);
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

    @After
    public void tearDownTest() throws Exception {
        if (_ri != null) tearDownTriplestore();
    }

    /**
     * Clean up so the next test can run with fresh data.
     *
     * @throws Exception if tearDown fails for any reason.
     */
    @AfterClass
    public static void tearDownClass() throws Exception {
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

    private static void tearDownDB() throws Exception {

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

    protected void doAddDelTest(int riLevel, DigitalObject obj) 
            throws Exception {
        Set<DigitalObject> set = new HashSet<DigitalObject>();
        set.add(obj);
        doAddDelTest(riLevel, set);
    }

    protected void doAddDelTest(int riLevel,
                                Set<DigitalObject> objects)
            throws Exception {

        initRI(riLevel);

        addAll(objects, true);
        assertTrue("Did not get expected triples after add",
                   sameTriples(getExpectedTriples(riLevel, objects), 
                           getActualTriples(), true));

        deleteAll(objects, true);
        assertEquals("Some triples remained after delete",
                     0,
                     getActualTriples().size());
    }

    protected void doModifyTest(int riLevel,
                                DigitalObject origObject,
                                DigitalObject modifiedObject)
            throws Exception {
        Set<DigitalObject> origObjects = new HashSet<DigitalObject>();
        origObjects.add(origObject);
        doModifyTest(riLevel, origObjects, modifiedObject);
    }

    // if riLevel is -1, assume original objects have already been added
    // and we don't need to change the ri level
    protected void doModifyTest(int riLevel,
                                Set<DigitalObject> origObjects,
                                DigitalObject modifiedObject)
            throws Exception {

        if (riLevel > -1) {
            initRI(riLevel);
            addAll(origObjects, true);
        }

        DigitalObject origObject = null;

        // get a set with the modified object in place of its old version
        Set<DigitalObject> newObjects = new HashSet<DigitalObject>();
        for (DigitalObject orig : origObjects) {
            if (orig.getPid().equals(modifiedObject.getPid())) {
                origObject = orig;
            } else {
                newObjects.add(orig);
            }
        }
        newObjects.add(modifiedObject);

        modify(origObject, modifiedObject, true);

        assertTrue("Did not get expected triples after modify",
                   sameTriples(getExpectedTriples(riLevel, newObjects),
                           getActualTriples(), true));
    }

    // Utility methods for tests

    /**
     * Make a deep copy of the given digital object.
     */
    protected DigitalObject deepCopy(DigitalObject obj) throws Exception {

        // make sure DOTranslationUtility doesn't die
        if (System.getProperty("fedoraServerHost") == null
                || System.getProperty("fedoraServerPort") == null) {
            System.setProperty("fedoraServerHost", "localhost");
            System.setProperty("fedoraServerPort", "8080");
        }

        Map<String, String> nsMap = new HashMap<String, String>();
        nsMap.put(FOXMLDOSerializer.XSI_NS, "xsi");
        obj.setNamespaceMapping(nsMap);
        String charEncoding = "UTF-8";
        int transContext = DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        FOXMLDOSerializer ser = new FOXMLDOSerializer();
        ser.serialize(obj, out, charEncoding, transContext);

        FOXMLDODeserializer deser = new FOXMLDODeserializer(charEncoding);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        DigitalObject objCopy = new BasicDigitalObject();
        deser.deserialize(in, objCopy, charEncoding, transContext);

        // make sure dates of any to-be-added new components differ
        try { Thread.sleep(100); } catch (Exception e) { }

        return objCopy;
    }

    protected void modify(DigitalObject origObject, 
                          DigitalObject modifiedObject,
                          boolean flush) throws Exception {
        if (origObject.isFedoraObjectType(DigitalObject.FEDORA_BDEF_OBJECT))
        {
            _ri.modifyBDefObject(getBDefReader(origObject),
                                 getBDefReader(modifiedObject));
        } 
        if (origObject.isFedoraObjectType(DigitalObject.FEDORA_BMECH_OBJECT))
        {
            _ri.modifyBMechObject(getBMechReader(origObject),
                                  getBMechReader(modifiedObject));
        } 
        if (origObject.isFedoraObjectType(DigitalObject.FEDORA_CONTENT_MODEL_OBJECT))
        {
//            _ri.modifyCModelObject(getDOReader(origObject),
//                                 getDOReader(modifiedObject));
        }
        if (origObject.isFedoraObjectType(DigitalObject.FEDORA_OBJECT))
        {
            _ri.modifyDataObject(getDOReader(origObject),
                                 getDOReader(modifiedObject));
        }
        if (flush) _ri.flushBuffer();
    }

    protected BDefReader getBDefReader(DigitalObject obj) throws Exception {
        return new SimpleBDefReader(null, null, null, null, null, obj);
    }

    protected BMechReader getBMechReader(DigitalObject obj) throws Exception {
        return new SimpleBMechReader(null, null, null, null, null, obj);
    }

    protected DOReader getDOReader(DigitalObject obj) throws Exception {
        return new SimpleDOReader(null, null, null, null, null, obj);
    }

    protected void addObj(DigitalObject obj, boolean flush)
            throws Exception {
        Set<DigitalObject> set = new HashSet<DigitalObject>();
        set.add(obj);
        addAll(set, flush);
    }

    protected void addAll(Set<DigitalObject> objects,
                          boolean flush)
            throws Exception {
        addOrDelAll(objects, flush, true);
    }

    protected void deleteAll(Set<DigitalObject> objects,
                             boolean flush)
            throws Exception {
        addOrDelAll(objects, flush, false);
    }

    private void addOrDelAll(Set<DigitalObject> objects,
            boolean flush, boolean add) throws Exception 
    {
        for (DigitalObject obj : objects) 
        {
            if (add) 
            {
                if (obj.isFedoraObjectType(DigitalObject.FEDORA_BDEF_OBJECT))
                {
                    _ri.addBDefObject(getBDefReader(obj));
                    _ri.flushBuffer();
                }
            } 
            else 
            {
                if (obj.isFedoraObjectType(DigitalObject.FEDORA_OBJECT))
                {
                    _ri.deleteDataObject(getDOReader(obj));
                }
            }
        }
        for (DigitalObject obj : objects) 
        {
            if (obj.isFedoraObjectType(DigitalObject.FEDORA_BMECH_OBJECT))
            {
                if (add) 
                {
                    _ri.addBMechObject(getBMechReader(obj));
                    _ri.flushBuffer();
                } 
                else 
                {
                    _ri.deleteBMechObject(getBMechReader(obj));
                    _ri.flushBuffer();
                }
            }
        }
        for (DigitalObject obj : objects) 
        {
            if (add) 
            {
                if (obj.isFedoraObjectType(DigitalObject.FEDORA_OBJECT))
                {
                    _ri.addDataObject(getDOReader(obj));
                }
            } 
            else 
            {
                if (obj.isFedoraObjectType(DigitalObject.FEDORA_BDEF_OBJECT))
                {
                    _ri.deleteBDefObject(getBDefReader(obj));
                    _ri.flushBuffer();
                }
            }
        }
        if (flush) _ri.flushBuffer();
    }

    protected Set<Triple> getExpectedTriples(int riLevel,
                                             Set<DigitalObject> objects)
            throws Exception {

        // we can return early in this case
        if (riLevel == 0) {
            return new HashSet<Triple>();
        }
        
        // add all to a mock repository reader
        MockRepositoryReader repo = new MockRepositoryReader();
        for (DigitalObject obj : objects) {
            repo.putObject(obj);
        }

        // prepare appropriate MethodInfoStore and TripleGenerator
        MethodInfoStore methodInfo = new MockMethodInfoStore(riLevel == 2);
        TripleGenerator generator = new MethodAwareTripleGenerator(methodInfo);

        Set<Triple> expected = new HashSet<Triple>();

        // get triples for all bdefs
        for (DigitalObject obj : objects) 
        {
            if (obj.isFedoraObjectType(DigitalObject.FEDORA_BDEF_OBJECT))
            {
                BDefReader reader = repo.getBDefReader(false, null, 
                        obj.getPid());
                methodInfo.putBDefInfo(reader);
                expected.addAll(generator.getTriplesForBDef(reader));
            }
        }

        // get triples for all bmechs
        for (DigitalObject obj : objects) 
        {
            if (obj.isFedoraObjectType(DigitalObject.FEDORA_BMECH_OBJECT))
            {
                BMechReader reader = repo.getBMechReader(false, null, 
                        obj.getPid());
                methodInfo.putBMechInfo(reader);
                expected.addAll(generator.getTriplesForBMech(reader));
            }
        }

        // get triples for all data objects
        for (DigitalObject obj : objects) 
        {
            if (obj.isFedoraObjectType(DigitalObject.FEDORA_OBJECT))
            {
                DOReader reader = repo.getReader(false, null, obj.getPid());
                expected.addAll(generator.getTriplesForDataObject(reader));
            }
        }

        return expected;

    }

    protected boolean sameTriples(Set<Triple> expected,
                                  Set<Triple> actual,
                                  boolean logDiffs) {
        TreeSet<String> eStrings = new TreeSet<String>();
        for (Triple triple : expected) {
            eStrings.add(RDFUtil.toString(triple));
        }

        TreeSet<String> aStrings = new TreeSet<String>();
        for (Triple triple : actual) {
            aStrings.add(RDFUtil.toString(triple));
        }

        if (eStrings.equals(aStrings)) {
            return true;
        } else {
            if (logDiffs) {
                StringBuffer out = new StringBuffer();
                out.append("Triple sets differ.\n");
                out.append("Expected set has " + expected.size() + " triples.\n");
                out.append("Actual set has " + actual.size() + " triples.\n\n");
                out.append("Expected triples:\n");
                for (String t : eStrings) {
                    out.append("  " + t + "\n");
                }
                out.append("\nActual triples:\n");
                for (String t : aStrings) {
                    out.append("  " + t + "\n");
                }
                LOG.warn(out.toString());
            }
            return false;
        }

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

    /**
     * Get the DC xml for an object.
     */
    protected String getDC(String content) {
        StringBuffer x = new StringBuffer();
        x.append("<oai_dc:dc xmlns:dc=\"http://purl.org/dc/elements/1.1/\"");
        x.append(" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\">\n");
        x.append(content + "\n");
        x.append("</oai_dc:dc>");
        return x.toString();
    }

    /**
     * Get the RELS-EXT xml for an object.
     */
    protected String getRELSEXT(String content) {
        StringBuffer x = new StringBuffer();
        x.append("<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");
        x.append(" xmlns:foo=\"http://example.org/foo#\">\n");
        x.append("<rdf:Description rdf:about=\"info:fedora/test:1\">\n");
        x.append(content + "\n");
        x.append("</rdf:Description>\n");
        x.append("</rdf:RDF>");
        return x.toString();
    }

    /**
     * Get the METHODMAP xml for a bDef.
     */
    protected static String getMethodMap(Set<ParamDomainMap> methodDefs) {
        return getMethodMap(methodDefs, null, false);
    }

    /**
     * Get the METHODMAP xml for a bDef or bMech.
     */
    protected static String getMethodMap(Set<ParamDomainMap> methodDefs,
                                         Map<String, Set<String>> inputKeys,
                                         boolean forBMech) {
        StringBuffer xml = new StringBuffer();
        xml.append("<MethodMap name=\"MethodMap\" xmlns=\"http://fed"
                + "ora.comm.nsdlib.org/service/methodmap\">\n");
        for (ParamDomainMap methodDef : methodDefs) {
            String method = methodDef.getMethodName();
            xml.append("  <Method operationName=\"" + method + "\"");
            if (forBMech) {
                xml.append(" wsdlMsgName=\"" + method + "Request\"");
                xml.append(" wsdlMsgOutput=\"dissemResponse\"");
            }
            xml.append(">\n");
            for (String paramName : methodDef.keySet()) {
                ParamDomain domain = methodDef.get(paramName);
                xml.append("    <UserInputParm parmName=\"" + paramName
                        + "\" passBy=\"VALUE\" defaultValue=\"\" required=\""
                        + domain.isRequired() + "\">\n");
                if (domain.size() > 0) {
                    xml.append("      <ValidParmValues>\n");
                    for (String value : domain) {
                        xml.append("        <ValidParm value=\"" + value 
                                + "\"/>\n");
                    }
                    xml.append("      </ValidParmValues>\n");
                }
                xml.append("    </UserInputParm>\n");
            }
            if (forBMech) {
                Set<String> keys = inputKeys.get(method);
                if (keys != null) {
                    for (String key : keys) {
                        xml.append("    <DatastreamInputParm parmName=\""
                                + key + "\" passBy=\"URL_REF\"/>\n");
                    }
                }

                xml.append("    <MethodReturnType wsdlMsgName=\""
                        + "dissemResponse\" wsdlMsgTOMIME=\""
                        + "application/octet-stream\"/>\n");
            }
            xml.append("  </Method>\n");
        }
        xml.append("</MethodMap>");
        return xml.toString();
    }

    /**
     * Get the DSINPUTSPEC xml for a BMech.
     */
    protected static String getInputSpec(String bDefPID,
                                         Map<String, Set<String>> inputTypes) {
        StringBuffer xml = new StringBuffer();
        xml.append("<DSInputSpec xmlns=\"http://fedora.comm.nsdlib.org/"
                + "service/bindspec\" bDefPID=\"" + bDefPID 
                + "\" label=\"InputSpec\">\n");
        for (String key : inputTypes.keySet()) {
            xml.append("  <DSInput DSMin=\"1\" DSMax=\"1\" DSOrdinality=\""
                    + "false\" wsdlMsgPartName=\"" + key + "\">\n");
            xml.append("    <DSInputLabel>label</DSInputLabel>\n");
            for (String mimeType : inputTypes.get(key)) {
                xml.append("    <DSMIME>" + mimeType + "</DSMIME>\n");
            }
            xml.append("    <DSInputInstruction>inst</DSInputInstruction>\n");
            xml.append("  </DSInput>\n");
        }
        xml.append("</DSInputSpec>\n");
        return xml.toString();
    }

    private static void addXSDType(String name, StringBuffer xml) {
        xml.append("      <xsd:simpleType name=\"" + name + "\">\n");
        xml.append("        <xsd:restriction base=\"xsd:string\"/>\n");
        xml.append("      </xsd:simpleType>\n");
    }

    /**
     * Get the WSDL xml for a BMech.
     */
    protected static String getWSDL(Set<ParamDomainMap> methodDefs,
                                    Map<String, Set<String>> inputKeys,
                                    Map<String, Set<String>> outputTypes) {
        StringBuffer xml = new StringBuffer();

        xml.append("<definitions xmlns=\"http://schemas.xmlsoap.org/wsdl/\""
                + " xmlns:http=\"http://schemas.xmlsoap.org/wsdl/http/\""
                + " xmlns:mime=\"http://schemas.xmlsoap.org/wsdl/mime/\""
                + " xmlns:this=\"MyService\""
                + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                + " name=\"Name\" targetNamespace=\"MyService\">\n");

        //
        // xsd type definitions
        //

        xml.append("  <types>\n");
        xml.append("    <xsd:schema targetNamespace=\"MyService\">\n");
        Set<String> addedTypes = new HashSet<String>();
        for (ParamDomainMap methodDef : methodDefs) {
            // one type def per distinct user param name
            for (String name : methodDef.keySet()) {
                if (addedTypes.add(name + "Type")) {
                    addXSDType(name + "Type", xml);
                }
            }

            // one type def per distinct ds input key
            for (String key : inputKeys.get(methodDef.getMethodName())) {
                if (addedTypes.add(key + "Type")) {
                    addXSDType(key + "Type", xml);
                }
            }
        }
        xml.append("    </xsd:schema>\n");
        xml.append("  </types>\n");

        //
        // message definitions
        //

        // one request message per method
        for (ParamDomainMap methodDef : methodDefs) {
            String method = methodDef.getMethodName();
            xml.append("  <message name=\"" + method + "Request\">\n");
            // one part per user param
            for (String name : methodDef.keySet()) {
                xml.append("    <part name=\"" + name + "\" type=\"this:" + name
                        + "Type\"/>\n");
            }
            // one part per ds input key
            for (String key : inputKeys.get(method)) {
                xml.append("    <part name=\"" + key + "\" type=\"this:" + key
                        + "Type\"/>\n");
            }
            xml.append("  </message>\n");
        }
        // one dissemResponse output message
        xml.append("  <message name=\"dissemResponse\">\n");
        xml.append("    <part name=\"response\" type=\"xsd:base64Binary\"/>\n");
        xml.append("  </message>\n");

        //
        // port type (per-method input/output messages)
        //
        xml.append("  <portType name=\"MyServicePortType\">\n");
        for (ParamDomainMap methodDef : methodDefs) {
            String method = methodDef.getMethodName();
            xml.append("    <operation name=\"" + method + "\">\n");
            xml.append("      <input message=\"this:" + method 
                    + "Request\"/>\n");
            xml.append("      <output message=\"this:dissemResponse\"/>\n");
            xml.append("    </operation>\n");
        }
        xml.append("  </portType>\n");

        //
        // service location
        // 
        xml.append("  <service name=\"MyService\">\n");
        xml.append("    <port binding=\"this:MyService_http\" "
                + "name=\"MyService_port\">\n");
        xml.append("      <http:address location=\"http://example.org/MyService/\"/>\n");
        xml.append("    </port>\n");
        xml.append("  </service>\n");

        //
        // operation locations and input/output bindings
        //
        xml.append("  <binding name=\"MyService_http\" type=\"this:MyServicePortType\">\n");
        xml.append("    <http:binding verb=\"GET\"/>\n");
        for (ParamDomainMap methodDef : methodDefs) {
            String method = methodDef.getMethodName();
            xml.append("    <operation name=\"" + method + "\">\n");

            // location = ..?userParm1=(userParm1)&key1=KEY1..etc
            StringBuffer location = new StringBuffer();
            location.append(method + "?");
            boolean first = true;
            for (String name : methodDef.keySet()) {
                if (!first) {
                    location.append("&amp;");
                }
                location.append(name + "=(" + name + ")");
                first = false;
            }
            for (String key : inputKeys.get(method)) {
                if (!first) {
                    location.append("&amp;");
                }
                location.append(key.toLowerCase() + "=(" + key + ")");
                first = false;
            }
            xml.append("      <http:operation location=\"" 
                    + location.toString() + "\"/>\n");

            // input is always urlReplacement
            xml.append("      <input><http:urlReplacement/></input>\n");

            // output lists all possible output mime types
            xml.append("      <output>\n");
            for (String mimeType : outputTypes.get(method)) {
                xml.append("        <mime:content type=\"" + mimeType
                        + "\"/>\n");
            }
            xml.append("      </output>\n");

            xml.append("    </operation>\n");
        }
        xml.append("  </binding>\n");

        xml.append("</definitions>\n");
        return xml.toString();
    }

    protected static Set<DigitalObject> getTestObjects(int num,
                                                       int datastreamsPerObject) {
        Set<DigitalObject> set = new HashSet<DigitalObject>(num);
        for (int i = 0; i < num; i++) {
            DigitalObject obj = getTestObject("test:" + i, "label" + i);
            for (int j = 0; j < datastreamsPerObject; j++) {
                addEDatastream(obj, "DS" + j);
            }
            set.add(obj);
        }
        return set;
    }

    protected TripleIterator spo(String query) throws Exception {
        return _ri.findTriples("spo", query, -1, false);
    }

//    protected static void addDisseminator(DigitalObject obj, String id,
//            String bDefPID, String bMechPID, Map<String, String> bindings) {
//        List dissems = obj.disseminators(id);
//        Disseminator diss = new Disseminator();
//        diss.bDefID = bDefPID;
//        diss.bMechID = bMechPID;
//        diss.dissCreateDT = new Date();
//        diss.dissID = id;
//        diss.dissLabel = "disseminator";
//        diss.dissState = "A";
//        diss.dissVersionID = id + "." + dissems.size();
//        DSBindingMap bindMap = new DSBindingMap();
//        bindMap.dsBindMapLabel = "bindmap";
//        bindMap.dsBindMechanismPID = bMechPID;
//        DSBinding[] dsBindings = new DSBinding[bindings.size()];
//        int i = 0;
//        for (String key : bindings.keySet()) {
//            String dsID = bindings.get(key);
//            dsBindings[i] = new DSBinding();
//            dsBindings[i].bindKeyName = key;
//            dsBindings[i].bindLabel = "bindlabel";
//            dsBindings[i].datastreamID = dsID;
//            dsBindings[i].seqNo = "1";
//            i++;
//        }
//        bindMap.dsBindings = dsBindings;
//        diss.dsBindMap = bindMap; 
//        dissems.add(diss);
//    }

    protected static void addEDatastream(DigitalObject obj, String id) {
        DatastreamReferencedContent ds = new DatastreamReferencedContent();
        ds.DSControlGrp = "E";
        ds.DSMIME = "text/plain";
        ds.DSLocation = "http://www.example.org/e.txt";
        ds.DSLocationType = "URL";
        ds.DSSize = 1;
        addDatastream(obj, id, ds);
    }

    protected static void addRDatastream(DigitalObject obj, String id) {
        DatastreamReferencedContent ds = new DatastreamReferencedContent();
        ds.DSControlGrp = "R";
        ds.DSMIME = "text/plain";
        ds.DSLocation = "http://www.example.org/r.txt";
        ds.DSLocationType = "URL";
        ds.DSSize = 2;
        addDatastream(obj, id, ds);
    }

    protected static void addXDatastream(DigitalObject obj, String id, String xml) {
        DatastreamXMLMetadata ds = new DatastreamXMLMetadata();
        ds.DSControlGrp = "X";
        ds.DSMIME = "text/xml";
        ds.DSSize = xml.length();
        try { ds.xmlContent = xml.getBytes("UTF-8"); } catch (Exception e) { }
        addDatastream(obj, id, ds);
    }

    protected static void addMDatastream(DigitalObject obj, String id) {
        DatastreamManagedContent ds = new DatastreamManagedContent();
        ds.DSControlGrp = "M";
        ds.DSMIME = "image/jpeg";
        ds.DSLocation = "bogusLocation";
        ds.DSLocationType = "INTERNAL";
        ds.DSSize = 4;
        addDatastream(obj, id, ds);
    }

    private static void addDatastream(DigitalObject obj, String id, Datastream ds) {
        List versions = obj.datastreams(id);
        ds.DatastreamID = id;
        ds.DSState = "A";
        ds.DSVersionable = true;
        ds.DSVersionID = id + "." + versions.size();
        ds.DSLabel = "ds label";
        ds.DSCreateDT = new Date();
        versions.add(ds);
    }

    protected static DigitalObject getTestObject(String pid,
                                                 String label) {
        Date now = new Date();
        return getTestObject(pid, DigitalObject.FEDORA_OBJECT, "A",
                "someOwnerId", label, "someContentModelId",
                now, now);
    }

    protected static DigitalObject getTestBDef(String pid,
                                               String label,
                                               Set<ParamDomainMap> methodDefs) {
        Date now = new Date();
        DigitalObject obj = getTestObject(pid, 
                DigitalObject.FEDORA_BDEF_OBJECT, "A",
                "someOwnerId", label, "someContentModelId", now, now);
        addXDatastream(obj, "METHODMAP", getMethodMap(methodDefs));
        return obj;
    }

    protected static DigitalObject getTestBMech(String pid, 
            String label,
            String bDefPID, 
            Set<ParamDomainMap> methodDefs,
            Map<String, Set<String>> inputKeys, 
            Map<String, Set<String>> inputTypes,
            Map<String, Set<String>> outputTypes) {

        Date now = new Date();
        DigitalObject obj = getTestObject(pid, 
                DigitalObject.FEDORA_BMECH_OBJECT, "A",
                "someOwnerId", label, "someContentModelId", now, now);

        String methodMapXML = getMethodMap(methodDefs, inputKeys, true);
        addXDatastream(obj, "METHODMAP", methodMapXML);

        String inputSpecXML = getInputSpec(bDefPID, inputTypes);
        addXDatastream(obj, "DSINPUTSPEC", inputSpecXML);

        String wsdlXML = getWSDL(methodDefs, inputKeys, outputTypes);
        addXDatastream(obj, "WSDL", wsdlXML);

        return obj;
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
        obj.addFedoraObjectType(fedoraObjectType);
        obj.setState(state);
        obj.setOwnerId(ownerId);
        obj.setLabel(label);
        obj.setContentModelId(contentModelId);
        obj.setCreateDate(createDate);
        obj.setLastModDate(lastModDate);
        return obj;
    }

    // bdef:1 has one no-parameter method
    protected static DigitalObject getBDefOne() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);
        return getTestBDef("test:bdef1", "bdef1", methodDefs);
    }

    // bdef:1b has two no-parameter methods
    protected static DigitalObject getBDefOneB() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        methodDefs.add(methodTwo);
        return getTestBDef("test:bdef1b", "bdef1b", methodDefs);
    }


    // bdef:1c has one no-parameter method (same as bdef:1)
    protected static DigitalObject getBDefOneC() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);
        return getTestBDef("test:bdef1c", "bdef1c", methodDefs);
    }

    // bdef:2 has one required one-parameter method with two possible values
    protected static DigitalObject getBDefTwo() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", true);
        argOneDomain.add("val1");
        argOneDomain.add("val2");
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        return getTestBDef("test:bdef2", "bdef2", methodDefs);
    }

    // bdef:2b has two required one-parameter methods with two possible values
    protected static DigitalObject getBDefTwoB() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", true);
        argOneDomain.add("val1");
        argOneDomain.add("val2");
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        methodTwo.put("argOne", argOneDomain);
        methodDefs.add(methodTwo);
        return getTestBDef("test:bdef2b", "bdef2b", methodDefs);
    }

    // bdef:3 has one optional one-parameter method with any possible value
    protected static DigitalObject getBDefThree() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", false);
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        return getTestBDef("test:bdef3", "bdef3", methodDefs);
    }

    // bdef:3b has two optional one-parameter methods with any possible value
    protected static DigitalObject getBDefThreeB() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", false);
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        methodTwo.put("argOne", argOneDomain);
        methodDefs.add(methodTwo);
        return getTestBDef("test:bdef3b", "bdef3b", methodDefs);
    }

    // bdef:4 has two one-parameter methods, one required with two possible 
    //        values and the other optional with any possible value
    protected static DigitalObject getBDefFour() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", true);
        argOneDomain.add("val1");
        argOneDomain.add("val2");
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        argOneDomain = new ParamDomain("argOne", false);
        methodTwo.put("argOne", argOneDomain);
        methodDefs.add(methodTwo);
        return getTestBDef("test:bdef4", "bdef4", methodDefs);
    }

    // construct a map representing key-to-ds bindings with 1 or 2 keys
    protected static Map<String, Set<String>> getMap(String key1,
                                                   String[] values1,
                                                   String key2,
                                                   String[] values2) {
        Set<String> valueSet1 = new HashSet<String>();
        for (String value : values1) {
            valueSet1.add(value);
        }
        Map<String, Set<String>> map = new HashMap<String, Set<String>>();
        map.put(key1, valueSet1);
        if (key2 != null) {
            Set<String> valueSet2 = new HashSet<String>();
            for (String value : values2) {
                valueSet2.add(value);
            }
            map.put(key2, valueSet2);
        }
        return map;
    }

    // bmech:1 implements bdef:1 and takes one datastream
    protected static DigitalObject getBMechOne() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);

        return getTestBMech("test:bmech1", "bmech1", "test:bdef1", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, null, null),
                getMap("KEY1", new String[] {"text/xml"}, null, null),
                getMap("methodOne", new String[] {"text/xml"}, null, null));
    }

    // bmech:1b implements bdef:1b and takes one datastream
    protected static DigitalObject getBMechOneB() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        methodDefs.add(methodTwo);

        return getTestBMech("test:bmech1b", "bmech1b", "test:bdef1b", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, 
                       "methodTwo", new String[] {"KEY2"}),
                getMap("KEY1", new String[] {"text/xml"}, 
                       "KEY2", new String[] {"text/xml"}),
                getMap("methodOne", new String[] {"text/xml"},
                       "methodTwo", new String[] {"text/xml"}));
    }

    // bmech:1c implements bdef:1c and takes one datastream
    protected static DigitalObject getBMechOneC() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);

        return getTestBMech("test:bmech1c", "bmech1c", "test:bdef1c", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, null, null),
                getMap("KEY1", new String[] {"text/xml"}, null, null),
                getMap("methodOne", new String[] {"text/xml"}, null, null));
    }

    // bmech:1d implements bdef:1 and takes TWO datastreams
    protected static DigitalObject getBMechOneD() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        methodDefs.add(methodOne);

        return getTestBMech("test:bmech1d", "bmech1d", "test:bdef1", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1", "KEY2"}, null, null),
                getMap("KEY1", new String[] {"text/xml"}, "KEY2", new String[] {"text/xml"}),
                getMap("methodOne", new String[] {"text/xml"}, null, null));
    }

    // bmech:2 implements bdef:2 and takes one datastream
    protected static DigitalObject getBMechTwo() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", true);
        argOneDomain.add("val1");
        argOneDomain.add("val2");
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);

        return getTestBMech("test:bmech2", "bmech2", "test:bdef2", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, null, null),
                getMap("KEY1", new String[] {"text/xml"}, null, null),
                getMap("methodOne", new String[] {"text/xml"}, null, null));
    }

    // bmech:2b implements bdef:2b and takes one datastream
    protected static DigitalObject getBMechTwoB() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", true);
        argOneDomain.add("val1");
        argOneDomain.add("val2");
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        methodTwo.put("argOne", argOneDomain);
        methodDefs.add(methodTwo);

        return getTestBMech("test:bmech2b", "bmech2b", "test:bdef2b", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, 
                       "methodTwo", new String[] {"KEY2"}),
                getMap("KEY1", new String[] {"text/xml"}, 
                       "KEY2", new String[] {"text/xml"}),
                getMap("methodOne", new String[] {"text/xml"},
                       "methodTwo", new String[] {"text/xml"}));
    }

    // bmech:3 implements bdef:3 and takes one datastream
    protected static DigitalObject getBMechThree() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", false);
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);

        return getTestBMech("test:bmech3", "bmech3", "test:bdef3", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, null, null),
                getMap("KEY1", new String[] {"text/xml"}, null, null),
                getMap("methodOne", new String[] {"text/xml"}, null, null));
    }

    // bmech:3b implements bdef:3b and takes one datastream
    protected static DigitalObject getBMechThreeB() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", false);
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        methodTwo.put("argOne", argOneDomain);
        methodDefs.add(methodTwo);

        return getTestBMech("test:bmech3b", "bmech3b", "test:bdef3b", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, 
                       "methodTwo", new String[] {"KEY2"}),
                getMap("KEY1", new String[] {"text/xml"}, 
                       "KEY2", new String[] {"text/xml"}),
                getMap("methodOne", new String[] {"text/xml"},
                       "methodTwo", new String[] {"text/xml"}));
    }

    // bmech:4 implements bdef:4 and takes one datastream
    protected static DigitalObject getBMechFour() {
        Set<ParamDomainMap> methodDefs = new HashSet<ParamDomainMap>();
        ParamDomainMap methodOne = new ParamDomainMap("methodOne");
        ParamDomain argOneDomain = new ParamDomain("argOne", true);
        argOneDomain.add("val1");
        argOneDomain.add("val2");
        methodOne.put("argOne", argOneDomain);
        methodDefs.add(methodOne);
        ParamDomainMap methodTwo = new ParamDomainMap("methodTwo");
        argOneDomain = new ParamDomain("argOne", false);
        methodTwo.put("argOne", argOneDomain);
        methodDefs.add(methodTwo);

        return getTestBMech("test:bmech4", "bmech4", "test:bdef4", 
                methodDefs, 
                getMap("methodOne", new String[] {"KEY1"}, 
                       "methodTwo", new String[] {"KEY2"}),
                getMap("KEY1", new String[] {"text/xml"}, 
                       "KEY2", new String[] {"text/xml"}),
                getMap("methodOne", new String[] {"text/xml"},
                       "methodTwo", new String[] {"text/xml"}));
    }

    // get an object named test:1 with one datastream (DS1) and one 
    // disseminator that has any number of keys (KEY1, etc) pointing to the ds.
    protected DigitalObject getObjectWithDissem(String bDefPID,
                                              String bMechPID,
                                              int numKeys) throws Exception {
        DigitalObject obj = getTestObject("test:1", "test");
        addEDatastream(obj, "DS1");
//        addDisseminator(obj, "DISS1", bDefPID, bMechPID, getBindings(numKeys));
        return obj;
    }

    protected Map<String, String> getBindings(int numKeys) {
        Map<String, String> bindings = new HashMap<String, String>();
        for (int i = 1; i <= numKeys; i++) {
            bindings.put("KEY" + i, "DS1");
        }
        return bindings;
    }

    // get a set containing three digital objects
    protected Set<DigitalObject> getObjectSet(DigitalObject o1,
                                              DigitalObject o2,
                                              DigitalObject o3) {
        Set<DigitalObject> set = new HashSet<DigitalObject>();
        set.add(o1);
        set.add(o2);
        set.add(o3);
        return set;
    }

    public void startFlushing(int sleepMS) throws Exception {
        if (_flusher != null) { 
            try {
                finishFlushing();
            } catch (Exception e) {
                System.err.println("Error stopping old flusher!!");
                e.printStackTrace();
            }
            throw new Exception("Flusher was already running!");
        }
        _flusher = new Flusher(_ri, sleepMS);
        _flusher.start();
    }

    // finish async flushing and do a final flush
    public void finishFlushing() throws Exception {
        _flusher.finish();
        _ri.flushBuffer();
        _flusher = null;
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
