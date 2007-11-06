package fedora.server.storage.translation;

import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.SimpleXpathEngine;

import org.junit.Before;
import org.junit.Test;

import org.w3c.dom.Document;

import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Disseminator;

import static fedora.common.Constants.METS;
import static fedora.common.Constants.OLD_XLINK;

/**
 * Unit tests for METSFedoraExt1_0DOSerializer.
 *
 * @author Chris Wilper
 */
@SuppressWarnings("deprecation")
public class TestMETSFedoraExt1_0DOSerializer
        extends TestMETSFedoraExtDOSerializer {
    
    private static final String STRUCTMAP_PATH
            = ROOT_PATH + "/" + METS.STRUCT_MAP.qName;
   
    private static final String BEHAVIORSEC_PATH
            = ROOT_PATH + "/" + METS.BEHAVIOR_SEC.qName;
   
    public TestMETSFedoraExt1_0DOSerializer() {
        // superclass sets protected field m_serializer as given below
        super(new METSFedoraExt1_0DOSerializer());
    }
    
    //---
    // Setup/Teardown
    //---

    @Before
    @Override
    public void setUp() {
        super.setUp();
        SimpleXpathEngine.registerNamespace(OLD_XLINK.prefix, OLD_XLINK.uri);
    }
    
    //---
    // Tests
    //---
    
    @Test
    public void testOldXLinkNamespace() throws TransformerException {
        doTestXLinkNamespace();
    }
    
    @Test
    public void testTwoDisseminators() throws TransformerException {
        DigitalObject obj = createTestObject(DigitalObject.FEDORA_OBJECT);
        final String dissID1 = "DISS1";
        Disseminator diss1 = createDisseminator(dissID1, 1);
        final String dissID2 = "DISS2";
        Disseminator diss2 = createDisseminator(dissID2, 1);
        obj.disseminators(dissID1).add(diss1);
        obj.disseminators(dissID2).add(diss2);
        
        Document xml = doSerializeOrFail(obj);
        assertXpathEvaluatesTo("2", "count(" + BEHAVIORSEC_PATH + ")", xml);
        assertXpathEvaluatesTo("2", "count(" + STRUCTMAP_PATH + ")", xml);
    }
    
    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(
                TestMETSFedoraExt1_0DOSerializer.class);
    }

}
