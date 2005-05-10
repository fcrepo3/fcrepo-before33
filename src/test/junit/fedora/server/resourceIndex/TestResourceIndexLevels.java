package fedora.server.resourceIndex;

import java.io.File;

import org.trippi.TripleMaker;
import org.jrdf.graph.URIReference;

import fedora.server.storage.types.DigitalObject;

/**
 * @author Edwin Shin
 */
public class TestResourceIndexLevels extends TestResourceIndex {
    private int m_level;
    private DigitalObject bdef, bmech, dataobject;
    private URIReference disseminates, dependsOn;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        bdef = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bdefs/demo_ri8.xml"));
        bmech = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bmechs/demo_ri9.xml"));
        dataobject = getFoxmlObject(new File(
                DEMO_OBJECTS_ROOT_DIR + "/dataobjects/demo_ri10.xml"));
        
        disseminates = TripleMaker.createResource("info:fedora/fedora-system:def/view#disseminates");
        dependsOn = TripleMaker.createResource("info:fedora/fedora-system:def/model#dependsOn");
    }
    
    public void testLevelOff() throws Exception {
        m_ri = new ResourceIndexImpl(ResourceIndex.INDEX_LEVEL_OFF, m_conn, m_cPool, null, null);
        
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
        m_ri = new ResourceIndexImpl(ResourceIndex.INDEX_LEVEL_ON, m_conn, m_cPool, null, null);
        m_ri.addDigitalObject(bdef);
        m_ri.commit();
        assertEquals(29, m_ri.countTriples(null, null, null, 0));
        assertEquals(3, m_ri.countTriples(null, disseminates, null, 0));
        assertEquals(0, m_ri.countTriples(null, dependsOn, null, 0));
        
        m_ri.addDigitalObject(bmech);
        m_ri.commit();
        assertEquals(75, m_ri.countTriples(null, null, null, 0));
        assertEquals(9, m_ri.countTriples(null, disseminates, null, 0));
        assertEquals(0, m_ri.countTriples(null, dependsOn, null, 9));
        
        m_ri.addDigitalObject(dataobject);
        m_ri.commit();
        assertEquals(166, m_ri.countTriples(null, null, null, 0));
        assertEquals(20, m_ri.countTriples(null, disseminates, null, 0));
        assertEquals(2, m_ri.countTriples(null, dependsOn, null, 9));
        
        m_ri.modifyDigitalObject(bdef);
        m_ri.commit();
        assertEquals(166, m_ri.countTriples(null, null, null, 0));
        assertEquals(20, m_ri.countTriples(null, disseminates, null, 0));
        
        m_ri.modifyDigitalObject(bmech);
        m_ri.commit();
        assertEquals(166, m_ri.countTriples(null, null, null, 0));
        assertEquals(20, m_ri.countTriples(null, disseminates, null, 0));
        
        m_ri.modifyDigitalObject(dataobject);
        m_ri.commit();
        export("/tmp/rdf/c.rdf");
        assertEquals(166, m_ri.countTriples(null, null, null, 0));
        assertEquals(20, m_ri.countTriples(null, disseminates, null, 0));
    }
    
    public void testLevelWithPermutations() throws Exception {
        m_ri = new ResourceIndexImpl(ResourceIndex.INDEX_LEVEL_PERMUTATIONS, m_conn, m_cPool, null, null);
        
        m_ri.addDigitalObject(bdef);
        m_ri.commit();
        assertEquals(29, m_ri.countTriples(null, null, null, 0));
        m_ri.addDigitalObject(bmech);
        m_ri.commit();
        assertEquals(75, m_ri.countTriples(null, null, null, 0));
        m_ri.addDigitalObject(dataobject);
        m_ri.commit();
        assertEquals(206, m_ri.countTriples(null, null, null, 0));
        
        m_ri.modifyDigitalObject(bdef);
        m_ri.commit();
        assertEquals(206, m_ri.countTriples(null, null, null, 0));
        m_ri.modifyDigitalObject(bmech);
        m_ri.commit();
        assertEquals(206, m_ri.countTriples(null, null, null, 0));
        m_ri.modifyDigitalObject(dataobject);
        m_ri.commit();
        assertEquals(206, m_ri.countTriples(null, null, null, 0));
    }
}
