/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.translation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.junit.Test;

import fedora.common.Models;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;

/**
 * 
 *
 * @author Edwin Shin
 * @version $Id$
 */
public class TestAtomDODeserializer
        extends TestFOXMLDODeserializer {

    public TestAtomDODeserializer() {
        super(new AtomDODeserializer(), new AtomDOSerializer());
    }

    public TestAtomDODeserializer(DODeserializer deserializer,
                                  DOSerializer serializer) {
        super(deserializer, serializer);
    }

    @Test
    public void testDeserializeSimpleCModelObject() {
        doSimpleTest(Models.CONTENT_MODEL_3_0);
    }

    @Test
    public void testDeserialize() throws Exception {
        // create a digital object
        DigitalObject original = createTestObject(Models.FEDORA_OBJECT_3_0);
        original.setLastModDate(new Date());
        DatastreamXMLMetadata ds1 = createXDatastream("DS1");
        ds1.DSCreateDT = new Date();
        original.addDatastreamVersion(ds1, true);

        // serialize the object as Atom
        DOSerializer serA = new AtomDOSerializer();
        File f = File.createTempFile("test", null);
        OutputStream out = new FileOutputStream(f);
        serA.serialize(original,
                       out,
                       "utf-8",
                       DOTranslationUtility.SERIALIZE_EXPORT_ARCHIVE);

        // deserialize the object
        DigitalObject candidate = new BasicDigitalObject();
        DODeserializer deserA = new AtomDODeserializer();
        InputStream in = new FileInputStream(f);
        deserA.deserialize(in,
                           candidate,
                           "utf-8",
                           DOTranslationUtility.DESERIALIZE_INSTANCE);

        // check the deserialization
        assertEquals(original.getLastModDate(), candidate.getLastModDate());
        DatastreamXMLMetadata candidateDS =
                (DatastreamXMLMetadata) candidate.datastreams("DS1").iterator()
                        .next();
        assertEquals(ds1.DatastreamID, candidateDS.DatastreamID);
        assertEquals(ds1.DSCreateDT, candidateDS.DSCreateDT);

        // FIXME dsSize tests omitted for now b/c of handling of closing tags
        //assertEquals(ds1.DSSize, candidateDS.DSSize);

        // also make sure we can serialize the object as foxml
        DOSerializer serF = new FOXML1_1DOSerializer();
        serF.serialize(candidate,
                       out,
                       "utf-8",
                       DOTranslationUtility.SERIALIZE_EXPORT_ARCHIVE);
    }

    public void testDeserializeFromDemoObjects() throws Exception {
        String[] sources =
                {
                        "src/demo-objects/atom/local-server-demos/simple-image-demo/sdep_demo_2.xml",
                        "src/demo-objects/atom/local-server-demos/formatting-objects-demo/obj_demo_26.xml"};
        for (String source : sources) {
            InputStream in = new FileInputStream(source);
            DigitalObject candidate = new BasicDigitalObject();
            DODeserializer deserA = new AtomDODeserializer();
            deserA.deserialize(in,
                               candidate,
                               "utf-8",
                               DOTranslationUtility.DESERIALIZE_INSTANCE);
        }
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TestAtomDODeserializer.class);
    }

}
