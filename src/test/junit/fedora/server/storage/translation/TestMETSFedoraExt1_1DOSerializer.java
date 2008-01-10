
package fedora.server.storage.translation;

import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.SimpleXpathEngine;

import org.junit.Before;
import org.junit.Test;

import org.w3c.dom.Document;

import fedora.server.storage.types.DigitalObject;

import static fedora.common.Constants.MODEL;
import static fedora.common.Constants.XLINK;

/**
 * Unit tests for METSFedoraExt1_1DOSerializer.
 * 
 * @author Chris Wilper
 */
public class TestMETSFedoraExt1_1DOSerializer
        extends TestMETSFedoraExtDOSerializer {

    public TestMETSFedoraExt1_1DOSerializer() {
        // superclass sets protected field m_serializer as given below
        super(new METSFedoraExt1_1DOSerializer());
    }

    //---
    // Setup/Teardown
    //---

    @Before
    @Override
    public void setUp() {
        super.setUp();
        SimpleXpathEngine.registerNamespace(XLINK.prefix, XLINK.uri);
    }

    //---
    // Tests
    //---

    @Test
    public void testXLinkNamespace() throws TransformerException {
        doTestXLinkNamespace();
    }

    @Test
    public void testVersionAttribute() throws TransformerException {
        DigitalObject obj = createTestObject(DigitalObject.FEDORA_OBJECT);
        Document xml = doSerializeOrFail(obj);
        assertXpathExists(ROOT_PATH + "[@EXT_VERSION = '1.1']", xml);
    }

    @Test
    public void testCModelFedoraObjectType() throws TransformerException {
        DigitalObject obj;
        Document xml;

        obj = createTestObject(DigitalObject.FEDORA_CONTENT_MODEL_OBJECT);
        xml = doSerializeOrFail(obj);
        assertXpathExists(ROOT_PATH + "[@TYPE='"
                + MODEL.CMODEL_OBJECT.localName + "']", xml);
    }

    @Test
    public void testSerializeSimpleCModelObject() {
        doSerializeAllOrFail(createTestObject(DigitalObject.FEDORA_CONTENT_MODEL_OBJECT));
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TestMETSFedoraExt1_1DOSerializer.class);
    }

}
