package fedora.server.resourceIndex;

import java.io.File;
import java.io.FileOutputStream;

import fedora.server.storage.types.DigitalObject;
import org.trippi.RDFFormat;

/**
 * @author Edwin Shin
 */
public class TestResourceIndexLevels extends TestResourceIndex {
    private int m_level;
    private DigitalObject bdef, bmech, dataobject;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        bdef = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bdefs/demo_ri8.xml"));
        bmech = getDigitalObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bmechs/demo_ri9.xml"));
        dataobject = getDigitalObject(new File(
                DEMO_OBJECTS_ROOT_DIR + "/dataobjects/demo_ri10.xml"));
    }
    
    public void testLevelOff() throws Exception {
        m_ri = new ResourceIndexImpl(0, m_conn, m_cPool, null);
        
        m_ri.addDigitalObject(bdef);
        m_ri.commit();
        assertEquals(0, m_ri.countTriples(null, null, null, 0));
        m_ri.addDigitalObject(bmech);
        m_ri.commit();
        assertEquals(0, m_ri.countTriples(null, null, null, 0));
        m_ri.addDigitalObject(dataobject);
        m_ri.commit();
        assertEquals(0, m_ri.countTriples(null, null, null, 0));
        
        m_ri.modifyDigitalObject(bdef);
        m_ri.commit();
        assertEquals(0, m_ri.countTriples(null, null, null, 0));
        m_ri.modifyDigitalObject(bmech);
        m_ri.commit();
        assertEquals(0, m_ri.countTriples(null, null, null, 0));
        m_ri.modifyDigitalObject(dataobject);
        m_ri.commit();
        assertEquals(0, m_ri.countTriples(null, null, null, 0));
    }
    
    public void testLevelOn() throws Exception {
        m_ri = new ResourceIndexImpl(1, m_conn, m_cPool, null);
        m_ri.addDigitalObject(bdef);
        m_ri.commit();
        assertEquals(17, m_ri.countTriples(null, null, null, 0));
        m_ri.addDigitalObject(bmech);
        m_ri.commit();
        assertEquals(39, m_ri.countTriples(null, null, null, 0));
        m_ri.addDigitalObject(dataobject);
        m_ri.commit();
        assertEquals(81, m_ri.countTriples(null, null, null, 0));
        
        m_ri.modifyDigitalObject(bdef);
        m_ri.commit();
        assertEquals(81, m_ri.countTriples(null, null, null, 0));
        m_ri.modifyDigitalObject(bmech);
        m_ri.commit();
        assertEquals(81, m_ri.countTriples(null, null, null, 0));
        m_ri.modifyDigitalObject(dataobject);
        m_ri.commit();
        assertEquals(81, m_ri.countTriples(null, null, null, 0));
        
        //m_ri.export(new FileOutputStream("/tmp/out1.rdf"), RDFFormat.RDF_XML);
    }
    
    public void testLevelWithPermutations() throws Exception {
        m_ri = new ResourceIndexImpl(2, m_conn, m_cPool, null);
        
        m_ri.addDigitalObject(bdef);
        m_ri.commit();
        assertEquals(17, m_ri.countTriples(null, null, null, 0));
        m_ri.addDigitalObject(bmech);
        m_ri.commit();
        assertEquals(39, m_ri.countTriples(null, null, null, 0));
        m_ri.addDigitalObject(dataobject);
        m_ri.commit();
        assertEquals(106, m_ri.countTriples(null, null, null, 0));
        
        m_ri.modifyDigitalObject(bdef);
        m_ri.commit();
        assertEquals(106, m_ri.countTriples(null, null, null, 0));
        m_ri.modifyDigitalObject(bmech);
        m_ri.commit();
        assertEquals(106, m_ri.countTriples(null, null, null, 0));
        m_ri.modifyDigitalObject(dataobject);
        m_ri.commit();
        assertEquals(106, m_ri.countTriples(null, null, null, 0));
    }
}
