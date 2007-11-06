package fedora.server.storage.translation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;

import org.xml.sax.SAXException;

import org.w3c.dom.Document;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.storage.types.DigitalObject;

import static fedora.server.storage.translation.DOTranslationUtility.SERIALIZE_EXPORT_ARCHIVE;
import static fedora.server.storage.translation.DOTranslationUtility.SERIALIZE_EXPORT_MIGRATE;
import static fedora.server.storage.translation.DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC;
import static fedora.server.storage.translation.DOTranslationUtility.SERIALIZE_STORAGE_INTERNAL;

/**
 * Common unit tests and utility methods for XML-based serializers.
 *
 * @author Chris Wilper
 */
public abstract class TestXMLDOSerializer
        extends TranslationTest {
    
    /** The serializer to test. */
    protected final DOSerializer m_serializer;
    
    TestXMLDOSerializer(DOSerializer serializer) {
        m_serializer = serializer;
    }
    
    //---
    // Tests
    //---
    
    @Test
    public void testSerializeSimpleDataObject() {
        doSerializeAllOrFail(
                createTestObject(DigitalObject.FEDORA_OBJECT));
    }
    
    @Test
    public void testSerializeSimpleBMechObject() {
        doSerializeAllOrFail(
                createTestObject(DigitalObject.FEDORA_BMECH_OBJECT));
    }
    
    @Test
    public void testSerializeSimpleBDefObject() {
        doSerializeAllOrFail(
                createTestObject(DigitalObject.FEDORA_BDEF_OBJECT));
    }
    
    //---
    // Instance helpers
    //---
    
    protected void doSerializeAllOrFail(DigitalObject obj) {
        doSerializeOrFail(obj, SERIALIZE_EXPORT_ARCHIVE);
        doSerializeOrFail(obj, SERIALIZE_EXPORT_MIGRATE);
        doSerializeOrFail(obj, SERIALIZE_EXPORT_PUBLIC);
        doSerializeOrFail(obj, SERIALIZE_STORAGE_INTERNAL);
    }
   
    protected Document doSerializeOrFail(DigitalObject obj) {
        return doSerializeOrFail(obj, SERIALIZE_STORAGE_INTERNAL);
    }
    
    /**
     * Serialize the object, failing the test if an exception is thrown.
     */
    protected Document doSerializeOrFail(DigitalObject obj, int transContext) {
        Document result = null;
        try {
            result = doSerialize(obj, transContext);
        } catch (ObjectIntegrityException e) {
            e.printStackTrace();
            fail("Serializer threw ObjectIntegrityException");
        } catch (SAXException e) {
            e.printStackTrace();
            fail("Serialized XML was not well-formed");
        }
        return result;
    }
  
    /**
     * Serialize the object, failing the test only if obviously incorrect
     * behavior occurs.
     * 
     * @throws ObjectIntegrityException if the serializer fails due to same.
     * @throws SAXException if the result XML is not well-formed.
     */
    protected Document doSerialize(DigitalObject obj, int transContext)
            throws ObjectIntegrityException, SAXException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            m_serializer.serialize(obj, out, "UTF-8", transContext);
        } catch (StreamIOException e) {
            fail("Serializer threw StreamIOException");
        } catch (UnsupportedEncodingException e) {
            fail("Serializer doesn't support UTF-8!?");
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException wontHappen) {
            throw new Error(wontHappen);
        }
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        try {
            return builder.parse(in);
        } catch (SAXException notWellFormed) {
            throw notWellFormed;
        } catch (IOException wontHappen) {
            throw new Error(wontHappen);
        }
    }
   
}
