
package fedora.server.storage.translation;

import org.junit.Test;

import fedora.server.storage.types.DigitalObject;

/**
 * Unit tests for FOXML1_1DODeserializer.
 * 
 * @author Chris Wilper
 */
public class TestFOXML1_1DODeserializer
        extends TestFOXMLDODeserializer {

    public TestFOXML1_1DODeserializer() {
        // superclass sets protected fields 
        // m_deserializer and m_serializer as given below
        super(new FOXML1_1DODeserializer(), new FOXML1_1DOSerializer());
    }

    //---
    // Tests
    //---

    @Test
    public void testDeserializeSimpleCModelObject() {
        doSimpleTest(DigitalObject.FEDORA_CONTENT_MODEL_OBJECT);
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TestFOXML1_1DODeserializer.class);
    }

}
