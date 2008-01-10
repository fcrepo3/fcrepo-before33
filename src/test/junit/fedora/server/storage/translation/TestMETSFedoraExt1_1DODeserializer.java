
package fedora.server.storage.translation;

import org.junit.Test;

import fedora.server.storage.types.DigitalObject;

/**
 * Unit tests for METSFedoraExt1_1DODeserializer.
 * 
 * @author Chris Wilper
 */
public class TestMETSFedoraExt1_1DODeserializer
        extends TestMETSFedoraExtDODeserializer {

    public TestMETSFedoraExt1_1DODeserializer() {
        // superclass sets protected fields 
        // m_deserializer and m_serializer as given below
        super(new METSFedoraExt1_1DODeserializer(),
              new METSFedoraExt1_1DOSerializer());
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
        return new junit.framework.JUnit4TestAdapter(TestMETSFedoraExt1_1DODeserializer.class);
    }

}
