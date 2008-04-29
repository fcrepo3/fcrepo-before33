
package fedora.server.storage.translation;

import static fedora.server.storage.translation.DOTranslationUtility.DESERIALIZE_INSTANCE;
import static fedora.server.storage.translation.DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.storage.types.AuditRecord;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Disseminator;

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

    /**
     * Tests for deterministic inline-XML content between generations. Addresses
     * bug #1771136: inlineXML would increase in size between copy generations
     * due to added whitespace.
     * 
     * @throws Exception
     */
    @Test
    public void testInlineXMLCopyIntegrity() throws Exception {

        DigitalObject original = createTestObject(DigitalObject.FEDORA_OBJECT);
        final String dsID1 = "DS1";

        /* Populate the object with a test datastream and serialize */
        DatastreamXMLMetadata ds1 = createXDatastream(dsID1);
        original.datastreams(dsID1).add(ds1);

        DigitalObject copy = translatedCopy(original);
        DigitalObject copyOfCopy = translatedCopy(copy);

        DatastreamXMLMetadata ds1copy =
                (DatastreamXMLMetadata) copy.datastreams(dsID1).get(0);
        DatastreamXMLMetadata ds1copyOfCopy =
                (DatastreamXMLMetadata) copyOfCopy.datastreams(dsID1).get(0);

        assertEquals("Length of XML datastream copies is not deterministic!",
                     ds1copy.xmlContent.length,
                     ds1copyOfCopy.xmlContent.length);
    }
    
    @Test
    public void testAuditDatastream() throws Exception {
        AuditRecord record = new AuditRecord();
        record.action = "modifyDatastreamByReference";
        record.componentID = "DRAWING-ICON";
        record.date = new Date(0L);
        record.id = "AUDREC1";
        record.justification = "malice";
        record.processType = "Fedora API-M";
        record.responsibility = "fedoraAdmin";
        
        DigitalObject original = createTestObject(DigitalObject.FEDORA_OBJECT);
        original.getAuditRecords().add(record);
        
        // serialize to file
        File temp = File.createTempFile("audit", ".xml");
        OutputStream out = new FileOutputStream(temp);
        m_serializer.serialize(original, out, "utf-8", DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC);
        out.close();
        
        // deserialize
        DigitalObject candidate = new BasicDigitalObject();
        InputStream in = new FileInputStream(temp);
        m_deserializer.deserialize(in, candidate, "utf-8", DOTranslationUtility.DESERIALIZE_INSTANCE);
        List<AuditRecord> a1 = original.getAuditRecords();
        List<AuditRecord> a2 = candidate.getAuditRecords();
        assertEquals(a1.size(), a2.size());
        for (int i = 0; i < a1.size(); i++) {
            assertEquals(a1.get(i).action, a2.get(i).action);
            assertEquals(a1.get(i).componentID, a2.get(i).componentID);
            assertEquals(a1.get(i).date, a2.get(i).date);
            assertEquals(a1.get(i).id, a2.get(i).id);
            assertEquals(a1.get(i).justification, a2.get(i).justification);
            assertEquals(a1.get(i).processType, a2.get(i).processType);
            assertEquals(a1.get(i).responsibility, a2.get(i).responsibility);
        }
        
        temp.delete();
    }

    /**
     * Copies of an object by deserializing and re-serializing. In theory, there
     * should be no difference between copy generations..
     * 
     * @param original
     *        Object to copy
     * @return Copy formed by serializing and de-serializing the original.
     * @throws UnsupportedEncodingException
     * @throws ObjectIntegrityException
     * @throws StreamIOException
     */
    private DigitalObject translatedCopy(DigitalObject original)
            throws UnsupportedEncodingException, ObjectIntegrityException,
            StreamIOException {
        DigitalObject copy = new BasicDigitalObject();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        m_serializer.serialize(original,
                               out,
                               "UTF-8",
                               SERIALIZE_STORAGE_INTERNAL);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        m_deserializer.deserialize(in,
                                   copy,
                                   "UTF-8",
                                   SERIALIZE_STORAGE_INTERNAL);
        return copy;
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
            iter.next();
            numDisseminators++;
        }
        assertEquals(2, numDisseminators);
        assertEquals(1, result.disseminators(dissID1).size());
        assertEquals(1, result.disseminators(dissID2).size());
    }

}
