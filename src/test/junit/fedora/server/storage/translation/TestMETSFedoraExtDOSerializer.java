
package fedora.server.storage.translation;

import javax.xml.transform.TransformerException;

import org.custommonkey.xmlunit.SimpleXpathEngine;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.w3c.dom.Document;

import fedora.server.storage.types.DatastreamReferencedContent;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;

import static fedora.common.Constants.METS;
import static fedora.common.Constants.MODEL;
import static fedora.common.Constants.XLINK;

/**
 * Common unit tests for METSFedoraExt serializers.
 * 
 * @author Chris Wilper
 */
public abstract class TestMETSFedoraExtDOSerializer
        extends TestXMLDOSerializer {

    protected static final String ROOT_PATH = "/" + METS.METS.qName;

    protected static final String AMDSEC_PATH =
            ROOT_PATH + "/" + METS.AMD_SEC.qName;

    TestMETSFedoraExtDOSerializer(DOSerializer serializer) {
        super(serializer);
    }

    //---
    // Setup/Teardown
    //---

    @Before
    @Override
    public void setUp() {
        super.setUp();
        SimpleXpathEngine.registerNamespace(METS.prefix, METS.uri);
    }

    @Override
    @After
    public void tearDown() {
        SimpleXpathEngine.clearNamespaces();
    }

    //---
    // Tests
    //---

    @Test
    public void testOBJIDAttribute() throws TransformerException {
        DigitalObject obj = createTestObject(DigitalObject.FEDORA_OBJECT);
        Document xml = doSerializeOrFail(obj);
        assertXpathExists(ROOT_PATH + "[@OBJID='" + TEST_PID + "']", xml);
    }

    @Test
    public void testCommonFedoraObjectTypes() throws TransformerException {
        DigitalObject obj;
        Document xml;

        obj = createTestObject(DigitalObject.FEDORA_OBJECT);
        xml = doSerializeOrFail(obj);
        assertXpathExists(ROOT_PATH + "[@TYPE='" + MODEL.DATA_OBJECT.localName
                + "']", xml);

        obj = createTestObject(DigitalObject.FEDORA_BMECH_OBJECT);
        xml = doSerializeOrFail(obj);
        assertXpathExists(ROOT_PATH + "[@TYPE='" + MODEL.BMECH_OBJECT.localName
                + "']", xml);

        obj = createTestObject(DigitalObject.FEDORA_BDEF_OBJECT);
        xml = doSerializeOrFail(obj);
        assertXpathExists(ROOT_PATH + "[@TYPE='" + MODEL.BDEF_OBJECT.localName
                + "']", xml);

    }

    @Test
    public void testNoDatastreams() throws TransformerException {
        DigitalObject obj = createTestObject(DigitalObject.FEDORA_OBJECT);
        Document xml = doSerializeOrFail(obj);
        assertXpathEvaluatesTo("0", "count(" + AMDSEC_PATH + ")", xml);
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
        assertXpathEvaluatesTo("2", "count(" + AMDSEC_PATH + ")", xml);
    }

    //---
    // Instance Helpers
    //---

    protected void doTestXLinkNamespace() throws TransformerException {
        DigitalObject obj = createTestObject(DigitalObject.FEDORA_OBJECT);
        final String url = "http://example.org/DS1";
        DatastreamReferencedContent ds = createRDatastream("DS1", url);
        obj.datastreams("DS1").add(ds);
        Document xml = doSerializeOrFail(obj);
        String xpath =
                ROOT_PATH + "/" + METS.FILE_SEC.qName + "/"
                        + METS.FILE_GRP.qName + "[@ID='DATASTREAMS']" + "/"
                        + METS.FILE_GRP.qName + "[@ID='DS1']" + "/"
                        + METS.FILE.qName + "/" + METS.FLOCAT.qName + "[@"
                        + XLINK.HREF.qName + "='" + url + "']";
        assertXpathExists(xpath, xml);
    }

}
