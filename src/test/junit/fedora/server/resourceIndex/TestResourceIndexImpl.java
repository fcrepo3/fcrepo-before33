package fedora.server.resourceIndex;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import fedora.common.PID;
import fedora.server.storage.types.DigitalObject;

import org.jrdf.graph.Triple;

import org.trippi.RDFFormat;
import org.trippi.TripleIterator;
import org.trippi.TripleMaker;

/**
 * @author Edwin Shin
 *  
 */
public class TestResourceIndexImpl extends TestResourceIndex {
    private DigitalObject bdef, bmech, dataobject;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestResourceIndexImpl.class);
    }

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        bdef = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bdefs/demo_ri8.xml"));
        bmech = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bmechs/demo_ri9.xml"));
        dataobject = getDigitalObject(new File(
                DEMO_OBJECTS_ROOT_DIR + "/dataobjects/demo_ri10.xml"));
    }

    public void testAddDigitalObject() throws Exception {
        DigitalObject obj = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1000.xml"));
        m_ri.addDigitalObject(obj);
        m_ri.commit();
        assertEquals(3, m_ri.countTriples(null, null, null, 0));
        TripleIterator it = m_ri.findTriples(TripleMaker.createResource(PID.toURI(obj.getPid())), 
                                             null, null, 0);
        Triple t;
        while (it.hasNext()) {
            t = (Triple)it.next();
            assertEquals(PID.toURI(obj.getPid()), t.getSubject().toString());
            assertNotNull(t.getPredicate());
            assertNotNull(t.getObject());
        }
    }
        
    public void testAddAndDelete() throws Exception {    
        m_ri.addDigitalObject(bdef);
        m_ri.commit();
        int a = m_ri.countTriples(null, null, null, 0);
        assertTrue(m_ri.countTriples(null, null, null, 0) > 0);
        
        m_ri.addDigitalObject(bmech);
        m_ri.commit();
        int b = m_ri.countTriples(null, null, null, 0);
        assertTrue(b > a);
        
        m_ri.addDigitalObject(dataobject);
        m_ri.commit();
        int c = m_ri.countTriples(null, null, null, 0);
        assertTrue(c > b);
        
        m_ri.export(new FileOutputStream("/tmp/out.rdf"), RDFFormat.RDF_XML);
        
        m_ri.deleteDigitalObject(dataobject);
        m_ri.commit();
        int d = m_ri.countTriples(null, null, null, 0);
        assertTrue(d == b);
        
        m_ri.deleteDigitalObject(bmech);
        m_ri.commit();
        
        int e = m_ri.countTriples(null, null, null, 0);
        assertTrue(e == a);
        
        m_ri.deleteDigitalObject(bdef);
        m_ri.commit();
        int f = m_ri.countTriples(null, null, null, 0);
        assertEquals(0, m_ri.countTriples(null, null, null, 0));
    }
    
    public void testDoubleAdd() throws Exception {
        m_ri.addDigitalObject(bdef);
        m_ri.addDigitalObject(bmech);
        m_ri.addDigitalObject(dataobject);
        m_ri.commit();
        int a = m_ri.countTriples(null, null, null, 0);
        
        m_ri.addDigitalObject(dataobject);
        m_ri.commit();
        int b = m_ri.countTriples(null, null, null, 0);
        assertEquals(a, b);
    }
}