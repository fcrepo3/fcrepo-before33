package fedora.server.resourceIndex;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jrdf.graph.SubjectNode;

import org.trippi.TupleIterator;

import fedora.server.storage.types.DigitalObject;

/**
 * @author Edwin Shin
 */
public class TestResourceIndexQueries extends TestResourceIndex {
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestResourceIndexQueries.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        //
        DigitalObject ri1010 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1010.xml"));
        DigitalObject ri1011 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1011.xml"));
        DigitalObject ri1100 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1100.xml"));
        DigitalObject ri1101 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1101.xml"));
        DigitalObject ri1102 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1102.xml"));
        DigitalObject ri1103 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1103.xml"));
        DigitalObject ri1104 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1104.xml"));
        DigitalObject ri2010 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri2010.xml"));
        DigitalObject ri2011 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri2011.xml"));
        DigitalObject ri2100 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri2100.xml"));
        DigitalObject ri2101 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri2101.xml"));
        DigitalObject ri2102 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri2102.xml"));
        DigitalObject ri2103 = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri2103.xml"));
        DigitalObject ri2104a = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri2104a.xml"));
        DigitalObject ri2104d = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri2104d.xml"));
        DigitalObject ri2104i = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri2104i.xml"));
        
        m_ri.addDigitalObject(ri1010);
        m_ri.addDigitalObject(ri1011);
        m_ri.addDigitalObject(ri1100);
        m_ri.addDigitalObject(ri1101);
        m_ri.addDigitalObject(ri1102);
        m_ri.addDigitalObject(ri1103);
        m_ri.addDigitalObject(ri1104);
        m_ri.addDigitalObject(ri2010);
        m_ri.addDigitalObject(ri2011);
        m_ri.addDigitalObject(ri2100);
        m_ri.addDigitalObject(ri2101);
        m_ri.addDigitalObject(ri2102);
        m_ri.addDigitalObject(ri2103);
        m_ri.addDigitalObject(ri2104a);
        m_ri.addDigitalObject(ri2104d);
        m_ri.addDigitalObject(ri2104i);
        
        m_ri.commit();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testCount() throws Exception {
        assertEquals(78, m_ri.countTriples(null, null, null, 0));
        //m_ri.export(new FileOutputStream("/tmp/out.rdf"), RDFFormat.RDF_XML);
    }
    
    public void testQueryAll() throws Exception {
        String query = "SELECT ?subject ?predicate ?object " +
                       "WHERE  (?subject ?predicate ?object)";
        TupleIterator it;
        it = m_ri.findTuples("rdql", query, 0, true);
        assertEquals(74, it.count());
        it = m_ri.findTuples("rdql", query, 0, false);
        assertEquals(78, it.count());
        
    }
    
    public void testQueryCollectionMembership() throws Exception {
        String query = "select ?member " +
                       "where  (?member <nsdl:isMemberOf> <info:fedora/demo:ri1010>)";
        
        TupleIterator it;
        it = m_ri.findTuples("rdql", query, 0, true);
        Map tuples;
        List members = new ArrayList();
        while (it.hasNext()) {
            tuples = it.next();
            members.add( ((SubjectNode)tuples.get("member")).toString() );
        }
        Collections.sort(members);
        
        assertEquals(2, members.size());
        assertEquals("info:fedora/demo:ri1100", members.get(0));
        assertEquals("info:fedora/demo:ri1102", members.get(1));
    }
    
    public void testQueryDate() throws Exception {
        //TODO datatyped...
    }
    
    public void testQueryExtProperties() throws Exception {
        //TODO NOT...
    }
    
}
