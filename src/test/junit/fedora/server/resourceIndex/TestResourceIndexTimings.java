package fedora.server.resourceIndex;

import java.io.File;

import fedora.server.storage.types.DigitalObject;

/**
 * @author Edwin Shin
 *  
 */
public class TestResourceIndexTimings extends TestResourceIndex {
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestResourceIndexTimings.class);
    }

    public void testAddAndDelete() throws Exception {
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

        //long mod_start = System.currentTimeMillis();
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
}