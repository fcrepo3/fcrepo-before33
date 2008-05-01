
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
import static fedora.common.Constants.XLINK;

import static fedora.common.Models.FEDORA_OBJECT_3_0;

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
        DigitalObject obj = createTestObject(FEDORA_OBJECT_3_0);
        Document xml = doSerializeOrFail(obj);
        assertXpathExists(ROOT_PATH + "[@OBJID='" + TEST_PID + "']", xml);
    }

    //@Test
    /* FIXME: not sure how this one is supposed to work in METS... */
    //public void testCommonFedoraObjectTypes() throws TransformerException {
    //    DigitalObject obj;
    //    Document xml;
    //    obj = createTestObject(DigitalObject.FEDORA_OBJECT);
    //    xml = doSerializeOrFail(obj);
    //    assertXpathExists(ROOT_PATH + "[@TYPE='" + MODEL.DATA_OBJECT.localName
    //            + "']", xml);
    //    obj = createTestObject(DigitalObject.FEDORA_SERVICE_DEPLOYMENT_OBJECT);
    //    xml = doSerializeOrFail(obj);
    //    assertXpathExists(ROOT_PATH + "[@TYPE='"
    //            + MODEL.SERVICE_DEPLOYMENT_OBJECT.localName + "']", xml);
    //    obj = createTestObject(DigitalObject.FEDORA_SERVICE_DEFINITION_OBJECT);
    //    xml = doSerializeOrFail(obj);
    //    assertXpathExists(ROOT_PATH + "[@TYPE='"
    //            + MODEL.SERVICE_DEFINITION_OBJECT.localName + "']", xml);
    //}
    @Test
    public void testNoDatastreams() throws TransformerException {
        DigitalObject obj = createTestObject(FEDORA_OBJECT_3_0);
        Document xml = doSerializeOrFail(obj);

        /* rels-ext */
        assertXpathEvaluatesTo("1", "count(" + AMDSEC_PATH + ")", xml);
    }

    @Test
    public void testTwoInlineDatastreams() throws TransformerException {
        DigitalObject obj = createTestObject(FEDORA_OBJECT_3_0);

        final String dsID1 = "DS1";
        DatastreamXMLMetadata ds1 = createXDatastream(dsID1);

        final String dsID2 = "DS2";
        DatastreamXMLMetadata ds2 = createXDatastream(dsID2);

        obj.addDatastreamVersion(ds1, true);
        obj.addDatastreamVersion(ds2, true);
        Document xml = doSerializeOrFail(obj);
        /* ds1, ds2 + rels-ext */
        assertXpathEvaluatesTo("3", "count(" + AMDSEC_PATH + ")", xml);
    }

    //---
    // Instance Helpers
    //---

    protected void doTestXLinkNamespace() throws TransformerException {
        DigitalObject obj = createTestObject(FEDORA_OBJECT_3_0);
        final String url = "http://example.org/DS1";
        DatastreamReferencedContent ds = createRDatastream("DS1", url);
        obj.addDatastreamVersion(ds, true);
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
