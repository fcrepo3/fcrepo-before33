
package fedora.server.storage.translation;

import org.custommonkey.xmlunit.XMLTestCase;

import org.junit.Before;

import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DSBinding;
import fedora.server.storage.types.DSBindingMap;
import fedora.server.storage.types.DatastreamReferencedContent;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Disseminator;

/**
 * Convenience superclass for serializer and deserializer tests.
 * 
 * @author Chris Wilper
 */
@SuppressWarnings("deprecation")
public abstract class TranslationTest
        extends XMLTestCase {

    protected static final String TEST_PID = "test:pid";

    //---
    // Setup/Teardown
    //---

    @Override
    @Before
    public void setUp() {
        // HACK: make DOTranslationUtility happy
        System.setProperty("fedoraServerHost", "localhost");
        System.setProperty("fedoraServerPort", "8080");
    }

    //---
    // Static helpers
    //---

    protected static DigitalObject createTestObject(int fType) {
        DigitalObject obj = new BasicDigitalObject();
        obj.addFedoraObjectType(fType);
        obj.setPid(TEST_PID);
        return obj;
    }

    protected static DatastreamXMLMetadata createXDatastream(String id) {
        DatastreamXMLMetadata ds = new DatastreamXMLMetadata();
        ds.DatastreamID = id;
        ds.DSVersionID = id + ".0";
        ds.DSControlGrp = "X";
        ds.xmlContent = "<doc/>".getBytes();
        return ds;
    }

    protected static DatastreamReferencedContent createRDatastream(String id,
                                                                   String url) {
        DatastreamReferencedContent ds = new DatastreamReferencedContent();
        ds.DatastreamID = id;
        ds.DSVersionID = id + ".0";
        ds.DSControlGrp = "R";
        ds.DSLocation = url;
        return ds;
    }

    protected static Disseminator createDisseminator(String id, int numBindings) {
        Disseminator diss = new Disseminator();
        diss.dissID = id;
        diss.dissVersionID = id + ".0";
        diss.bDefID = TEST_PID + "bdef";
        diss.bMechID = TEST_PID + "bmech";
        diss.dsBindMap = new DSBindingMap();
        // the following is only needed for METS
        diss.dsBindMapID = id + "bindMap";
        DSBinding[] dsBindings = new DSBinding[numBindings];
        for (int i = 1; i <= numBindings; i++) {
            dsBindings[i - 1] = new DSBinding();
            dsBindings[i - 1].bindKeyName = "KEY" + i;
            dsBindings[i - 1].datastreamID = "DS" + i;
        }
        diss.dsBindMap.dsBindings = dsBindings;
        return diss;
    }

}
