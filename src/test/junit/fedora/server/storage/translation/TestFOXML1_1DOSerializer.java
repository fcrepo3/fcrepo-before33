
package fedora.server.storage.translation;

import javax.xml.transform.TransformerException;

import org.junit.Test;

import org.w3c.dom.Document;

import fedora.server.storage.types.DigitalObject;

import static fedora.common.Constants.MODEL;
import static fedora.common.Constants.RDF;

/**
 * Unit tests for FOXML1_1DOSerializer.
 * 
 * @author Chris Wilper
 */
public class TestFOXML1_1DOSerializer
        extends TestFOXMLDOSerializer {

    public TestFOXML1_1DOSerializer() {
        // superclass sets protected field m_serializer as given below
        super(new FOXML1_1DOSerializer());
    }

    //---
    // Tests
    //---

    @Test
    public void testVersionAttribute() throws TransformerException {
        DigitalObject obj = createTestObject(DigitalObject.FEDORA_OBJECT);
        Document xml = doSerializeOrFail(obj);
        assertXpathExists(ROOT_PATH + "[@VERSION = '1.1']", xml);
    }

    @Test
    public void testCModelFedoraObjectType() throws TransformerException {
        DigitalObject obj;
        Document xml;

        obj = createTestObject(DigitalObject.FEDORA_CONTENT_MODEL_OBJECT);
        xml = doSerializeOrFail(obj);
        checkProperty(xml, RDF.TYPE, MODEL.CMODEL_OBJECT.localName);
    }

    @Test
    public void testSerializeSimpleCModelObject() {
        doSerializeAllOrFail(createTestObject(DigitalObject.FEDORA_CONTENT_MODEL_OBJECT));
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TestFOXML1_1DOSerializer.class);
    }

}
