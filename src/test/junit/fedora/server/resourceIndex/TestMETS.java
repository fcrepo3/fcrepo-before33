package fedora.server.resourceIndex;

import java.io.File;

import fedora.server.storage.types.DigitalObject;

/**
 * @author Edwin Shin
 *  
 */
public class TestMETS extends TestResourceIndex {
    private DigitalObject bdef, bmech, dataobject;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestMETS.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testAddMetsObject() throws Exception {
        DigitalObject obj = getMetsObject(new File(
                            "src/test/junit/metsTestObjects/demo_ri1001.mets"));
        m_ri.addDigitalObject(obj);
        m_ri.commit();
    }
    
    
}