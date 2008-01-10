
package fedora.server.storage.translation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.Iterator;

import org.junit.Test;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Disseminator;

import static fedora.server.storage.translation.DOTranslationUtility.DESERIALIZE_INSTANCE;
import static fedora.server.storage.translation.DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL;

/**
 * Common unit tests and utility methods for XML-based deserializers.
 * 
 * @author Chris Wilper
 */
@SuppressWarnings("deprecation")
public abstract class TestXMLDODeserializer
        extends TranslationTest {

    /** The deserializer to test. */
    protected final DODeserializer m_deserializer;

    /** The associated (separately unit-tested) serializer. */
    protected final DOSerializer m_serializer;

    TestXMLDODeserializer(DODeserializer deserializer, DOSerializer serializer) {
        m_deserializer = deserializer;
        m_serializer = serializer;
    }

    //---
    // Tests
    //---

    @Test
    public void testDeserializeSimpleDataObject() {
        doSimpleTest(DigitalObject.FEDORA_OBJECT);
    }

    @Test
    public void testDeserializeSimpleBMechObject() {
        doSimpleTest(DigitalObject.FEDORA_BMECH_OBJECT);
    }

    @Test
    public void testDeserializeSimpleBDefObject() {
        doSimpleTest(DigitalObject.FEDORA_BDEF_OBJECT);
    }

    @Test
    public void testTwoInlineDatastreams() {
        DigitalObject obj = createTestObject(DigitalObject.FEDORA_OBJECT);

        final String dsID1 = "DS1";
        DatastreamXMLMetadata ds1 = createXDatastream(dsID1);

        final String dsID2 = "DS2";
        DatastreamXMLMetadata ds2 = createXDatastream(dsID2);

        obj.datastreams(dsID1).add(ds1);
        obj.datastreams(dsID2).add(ds2);

        DigitalObject result = doDeserializeOrFail(obj);
        int numDatastreams = 0;
        Iterator<String> iter = result.datastreamIdIterator();
        while (iter.hasNext()) {
            iter.next();
            numDatastreams++;
        }
        assertEquals(2, numDatastreams);
        assertEquals(1, result.datastreams(dsID1).size());
        assertEquals(1, result.datastreams(dsID2).size());
    }

    //---
    // Instance helpers
    //---

    protected void doSimpleTest(int fType) {
        DigitalObject input = createTestObject(fType);
        DigitalObject obj = doDeserializeOrFail(input);
        assertTrue(obj.isFedoraObjectType(fType));
        assertEquals(TEST_PID, obj.getPid());
    }

    protected DigitalObject doDeserializeOrFail(DigitalObject obj) {
        DigitalObject result = null;
        try {
            result = doDeserialize(obj);
        } catch (ObjectIntegrityException e) {
            e.printStackTrace();
            fail("Deserializer threw ObjectIntegrityException");
        } catch (StreamIOException e) {
            e.printStackTrace();
            fail("Deserializer threw StreamIOException");
        }
        return result;
    }

    protected DigitalObject doDeserialize(DigitalObject obj)
            throws ObjectIntegrityException, StreamIOException {
        return doDeserialize(getStream(obj));
    }

    protected DigitalObject doDeserialize(InputStream in)
            throws ObjectIntegrityException, StreamIOException {
        DigitalObject obj = new BasicDigitalObject();
        try {
            m_deserializer.deserialize(in, obj, "UTF-8", DESERIALIZE_INSTANCE);
        } catch (UnsupportedEncodingException wontHappen) {
            fail("Deserializer doesn't support UTF-8?!");
        }
        return obj;
    }

    // use the associated serializer to create a stream for the object, or fail
    protected InputStream getStream(DigitalObject obj) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            m_serializer.serialize(obj,
                                   out,
                                   "UTF-8",
                                   SERIALIZE_STORAGE_INTERNAL);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to serialize test object for deserialization test");
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    protected void doTestTwoDisseminators() {
        DigitalObject obj = createTestObject(DigitalObject.FEDORA_OBJECT);

        final String dissID1 = "DISS1";
        Disseminator diss1 = createDisseminator(dissID1, 1);

        final String dissID2 = "DISS2";
        Disseminator diss2 = createDisseminator(dissID2, 1);

        obj.disseminators(dissID1).add(diss1);
        obj.disseminators(dissID2).add(diss2);

        DigitalObject result = doDeserializeOrFail(obj);
        int numDisseminators = 0;
        Iterator<String> iter = result.disseminatorIdIterator();
        while (iter.hasNext()) {
            String id = iter.next();
            numDisseminators++;
        }
        assertEquals(2, numDisseminators);
        assertEquals(1, result.disseminators(dissID1).size());
        assertEquals(1, result.disseminators(dissID2).size());
    }

}
