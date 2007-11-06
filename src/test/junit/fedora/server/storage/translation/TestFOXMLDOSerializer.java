package fedora.server.storage.translation;

import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.SimpleXpathEngine;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.w3c.dom.Document;

import fedora.common.rdf.RDFName;

import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;

import static fedora.common.Constants.FOXML;
import static fedora.common.Constants.MODEL;
import static fedora.common.Constants.RDF;

/**
 * Common unit tests for FOXML serializers.
 *
 * @author Chris Wilper
 */
public abstract class TestFOXMLDOSerializer
        extends TestXMLDOSerializer {
    
    protected static final String ROOT_PATH 
            = "/" + FOXML.DIGITAL_OBJECT.qName;
    
    protected static final String PROPERTIES_PATH
            = ROOT_PATH + "/" + FOXML.OBJECT_PROPERTIES.qName;
    
    protected static final String PROPERTY_PATH
            = PROPERTIES_PATH + "/" + FOXML.PROPERTY.qName;
    
    protected static final String DATASTREAM_PATH
            = ROOT_PATH + "/" + FOXML.DATASTREAM.qName;
    
    TestFOXMLDOSerializer(DOSerializer serializer) {
        super(serializer);
    }
    
    //---
    // Setup/Teardown
    //---
    
    @Before
    @Override
    public void setUp() {
        super.setUp();
        SimpleXpathEngine.registerNamespace(FOXML.prefix, FOXML.uri);
    }
    
    @After
    public void tearDown() {
        SimpleXpathEngine.clearNamespaces();
    }
    
    //---
    // Tests
    //---
    
    @Test
    public void testPIDAttribute() throws TransformerException {
        DigitalObject obj = createTestObject(DigitalObject.FEDORA_OBJECT);
        Document xml = doSerializeOrFail(obj);
        assertXpathExists(ROOT_PATH + "[@PID='" + TEST_PID + "']", xml);
    }
    
    @Test
    public void testCommonFedoraObjectTypes() throws TransformerException {
        DigitalObject obj;
        Document xml;
        
        obj = createTestObject(DigitalObject.FEDORA_OBJECT);
        xml = doSerializeOrFail(obj);
        checkProperty(xml, RDF.TYPE, MODEL.DATA_OBJECT.localName);
        
        obj = createTestObject(DigitalObject.FEDORA_BMECH_OBJECT);
        xml = doSerializeOrFail(obj);
        checkProperty(xml, RDF.TYPE, MODEL.BMECH_OBJECT.localName);
        
        obj = createTestObject(DigitalObject.FEDORA_BDEF_OBJECT);
        xml = doSerializeOrFail(obj);
        checkProperty(xml, RDF.TYPE, MODEL.BDEF_OBJECT.localName);
    }
    
    @Test
    public void testNoDatastreams() throws TransformerException {
        DigitalObject obj = createTestObject(DigitalObject.FEDORA_OBJECT);
        Document xml = doSerializeOrFail(obj);
        assertXpathEvaluatesTo("0", "count(" + DATASTREAM_PATH + ")", xml);
    }
    
    @Test
    public void testTwoInlineDatastreams() throws TransformerException {
        DigitalObject obj = createTestObject(DigitalObject.FEDORA_OBJECT);
        
        final String dsID1 = "DS1";
        DatastreamXMLMetadata ds1 = createXDatastream(dsID1);
        
        final String dsID2 = "DS2";
        DatastreamXMLMetadata ds2 = createXDatastream(dsID2);
        
        obj.datastreams(dsID1).add(ds1);
        obj.datastreams(dsID2).add(ds2);
        Document xml = doSerializeOrFail(obj);
        assertXpathEvaluatesTo("2", "count(" + DATASTREAM_PATH + ")", xml);
    }
    
    @Test
    public void testTwoDisseminators() throws TransformerException {
        DigitalObject obj = createTestObject(DigitalObject.FEDORA_OBJECT);
    }
    
    //---
    // Instance helpers
    //---
    
    protected void checkProperty(Document xml, RDFName name, String value)
            throws TransformerException {
        assertXpathExists(PROPERTY_PATH + "[@NAME='" + name.uri + "'"
                + " and @VALUE='" + value + "']", xml);
    }
    
}
