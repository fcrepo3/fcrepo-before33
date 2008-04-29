/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.translation;

import static fedora.server.storage.translation.DOTranslationUtility.DESERIALIZE_INSTANCE;
import static fedora.server.storage.translation.DOTranslationUtility.SERIALIZE_EXPORT_ARCHIVE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.custommonkey.xmlunit.SimpleXpathEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;

/**
 * @author Edwin Shin
 */
public class TestAtomDOSerializer
        extends TestXMLDOSerializer {

    private static final String iso_tron =
            "src/schematron/iso_schematron_skeleton.xsl";

    private static final String atom_tron = "src/schematron/atom.sch";

    public TestAtomDOSerializer() {
        super(new AtomDOSerializer());
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    @Override
    public void setUp() {
        super.setUp();
        SimpleXpathEngine.registerNamespace("fedora", "http://www.example.org");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    @Override
    public void tearDown() throws Exception {
        SimpleXpathEngine.clearNamespaces();
    }

    @Test
    public void testSerializeFromFOXML() throws Exception {
        String source = "src/demo-objects/foxml/local-server-demos/image-collection-demo/dataObjects/demo_SmileyBeerGlass.xml";
        source = "src/demo-objects/foxml/local-server-demos/formatting-objects-demo/obj_demo_26.xml";
        InputStream in = new FileInputStream(source);
        File f = File.createTempFile("test", null);
        OutputStream out  = new FileOutputStream(f);

        DODeserializer deser = new FOXML1_1DODeserializer();
        DigitalObject obj = new BasicDigitalObject();
        deser.deserialize(in, obj, "UTF-8", DESERIALIZE_INSTANCE);

        // some sanity checks
        setObjectDefaults(obj);

        DOSerializer serializer = new AtomDOSerializer();
        serializer.serialize(obj, out, "UTF-8", SERIALIZE_EXPORT_ARCHIVE);
    }

    @Test
    public void testSerialize() throws Exception {
        DigitalObject obj = createTestObject(DigitalObject.FEDORA_OBJECT);
        obj.setLastModDate(new Date());
        DatastreamXMLMetadata ds1 = createXDatastream("DS1");
        ds1.DSCreateDT = new Date();
        obj.addDatastreamVersion(ds1, true);

        OutputStream out = new ByteArrayOutputStream();

        DOSerializer serializer = new AtomDOSerializer();
        serializer.serialize(obj, out, "UTF-8", SERIALIZE_EXPORT_ARCHIVE);
        // TODO
        //validateWithISOSchematron(out.toString());
    }

    // TODO
    private void validateWithISOSchematron(String candidate)
            throws TransformerException, IOException {
        StreamSource skeleton = new StreamSource(new File(iso_tron));
        StreamSource schema = new StreamSource(new File(atom_tron));
        StringWriter temp = new StringWriter();
        StreamResult result = new StreamResult(temp);

        // generate the stylesheet
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer xform = factory.newTransformer(skeleton);
        xform.transform(schema, result);
        temp.flush();
        temp.close();
        String stylesheet = temp.toString();

        // now flip
        StringReader in = new StringReader(stylesheet);
        StreamSource sheet = new StreamSource(in);
        Transformer validator = factory.newTransformer(sheet);
        validator.setOutputProperty("method", "text");
        temp = new StringWriter();
        result = new StreamResult(temp);
        validator.transform(new StreamSource(new StringReader(candidate)),
                            result);
        temp.flush();
        String output = temp.toString();

        // Check for no output if all tests pass. 
        assertEquals(output, "", output);
    }

    private void setObjectDefaults(DigitalObject obj) {
        if (obj.getCreateDate() == null) obj.setCreateDate(new Date());

        Iterator<String> dsIds = obj.datastreamIdIterator();
        while (dsIds.hasNext()) {
            String dsid = dsIds.next();
            List<Datastream> dsList = obj.datastreams(dsid);
            for (Datastream ds : dsList) {
                if (ds.DSCreateDT == null) {
                    ds.DSCreateDT = new Date();
                }
            }
        }
    }

    // Supports legacy test runners
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(TestAtomDOSerializer.class);
    }

}
