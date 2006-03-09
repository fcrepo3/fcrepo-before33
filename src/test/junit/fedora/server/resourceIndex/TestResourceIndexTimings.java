package fedora.server.resourceIndex;

import java.io.File;

import org.jrdf.graph.Triple;
import org.trippi.TripleMaker;

import fedora.server.storage.types.DigitalObject;

/**
 * @author Edwin Shin
 *  
 */
public class TestResourceIndexTimings extends TestResourceIndex {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestResourceIndexTimings.class);
    }

    public void footestAddAndDelete() throws Exception {
        DigitalObject bdef = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bdefs/demo_ri8.xml"));
        DigitalObject bmech = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bmechs/demo_ri9.xml"));
        DigitalObject dataobject = getFoxmlObject(new File(
                DEMO_OBJECTS_ROOT_DIR + "/dataobjects/demo_ri10.xml"));

        long add_start = System.currentTimeMillis();
        m_ri.addDigitalObject(bdef);
        m_ri.addDigitalObject(bmech);
        for (int i = 0; i < 100; i++) {
            dataobject.setPid("test:" + i);
            m_ri.addDigitalObject(dataobject);
        }
        m_ri.commit();
        long add_stop = System.currentTimeMillis();

        m_ri.modifyDigitalObject(bdef);
        m_ri.modifyDigitalObject(bmech);
        long mod_start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            dataobject.setPid("test:" + i);
            m_ri.modifyDigitalObject(dataobject);
        }
        m_ri.commit();
        long mod_stop = System.currentTimeMillis();
        
        long del_start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            dataobject.setPid("test:" + i);
            m_ri.deleteDigitalObject(dataobject);
        }
        m_ri.deleteDigitalObject(bmech);
        m_ri.deleteDigitalObject(bdef);
        m_ri.commit();
        long del_stop = System.currentTimeMillis();
        
        System.out.println("Add time: " + (add_stop - add_start));
        System.out.println("Modify time: " + (mod_stop - mod_start));
        System.out.println("Delete time: " + (del_stop - del_start));
        
    }
    
    public void testModify() throws Exception {
        DigitalObject bdef = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bdefs/demo_ri8.xml"));
        DigitalObject bmech = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/bmechs/demo_ri9.xml"));
        //DigitalObject dataobject = getFoxmlObject(new File(
        //        DEMO_OBJECTS_ROOT_DIR + "/dataobjects/demo_ri10.xml"));
        DigitalObject dataobject = getFoxmlObject(new File("/tmp/foo.xml"));

        m_ri.addDigitalObject(bdef);
        m_ri.addDigitalObject(bmech);
        m_ri.commit();

        //
        
        for (int i = 0; i < 10000; i++) {
            dataobject.setPid("test:" + i);
            m_ri.addDigitalObject(dataobject);
        }
        m_ri.commit();
        int a = m_ri.countTriples(null, null, null, 0);
        
        //
        int n = 10;
        long[] times = new long[n];
        long mod_start;
        long mod_end;
        for (int i = 0; i < n; i++) {
            dataobject.setLabel("foo " + i);
            mod_start = System.currentTimeMillis();
            m_ri.modifyDigitalObject(dataobject);
            mod_end = System.currentTimeMillis();
            times[i] = mod_end - mod_start;
        }
        //m_ri.commit();
        int b = m_ri.countTriples(null, null, null, 0);
        
        
        long x0, x1, x2;
        
        for (int i = 0; i < 5; i++) {
        	
        	x0 = System.currentTimeMillis();
        	
        	dataobject.setLabel("bar " + i);
            m_ri.modifyDigitalObject(dataobject);
            
            x1 = System.currentTimeMillis();
            
        	m_ri.commit();
        	
        	x2 = System.currentTimeMillis();
        	System.out.println("commit " + i + ":" + ((long)x1-(long)x0) + ", " + ((long)x2-(long)x1));
        }
        
        m_ri.deleteDigitalObject(dataobject);
        m_ri.deleteDigitalObject(bmech);
        m_ri.deleteDigitalObject(bdef);
        
        System.out.println("#----------");
        System.out.println("Triples before mods: " + a);
        System.out.println("Triples after mods: " + b);
        System.out.println("\n\n**********************************************************************");
        System.out.println("\tModification Times");
        for (int i = 0; i < n; i++) {
        	System.out.println("\t" + i + ": " + times[i] + "ms");
        }
    }
}