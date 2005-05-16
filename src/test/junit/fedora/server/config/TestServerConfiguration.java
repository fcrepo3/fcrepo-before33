package fedora.server.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.custommonkey.xmlunit.SimpleXpathEngine;
import org.w3c.dom.Document;

import fedora.test.FedoraTestCase;

/**
 * @author Edwin Shin
 */
public class TestServerConfiguration extends FedoraTestCase {
    private static final String NS_FCFG = "http://www.fedora.info/definitions/1/0/config/";
    private static final String NS_FCFG_PREFIX = "fcfg";
    private static final String FCFG_LOCATION = "src/fcfg/server/fedora.fcfg";
    private File originalFile;
    private FileInputStream fis;
    private ServerConfiguration config;
    private DocumentBuilder builder;
    private ByteArrayOutputStream out;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestServerConfiguration.class);
    }
    
    protected void setUp() throws Exception {
        out = new ByteArrayOutputStream();
        originalFile = new File(FCFG_LOCATION);
        fis = new FileInputStream(originalFile);
        ServerConfigurationParser scp = new ServerConfigurationParser(fis);
        config = scp.parse();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        builder = factory.newDocumentBuilder();
        
        // Namespace support is enabled by a patch to XMLUnit
        // see: http://sourceforge.net/tracker/index.php?func=detail&aid=953445&group_id=23187&atid=377770
        SimpleXpathEngine.registerNamespace(NS_FCFG_PREFIX, NS_FCFG);
    }
    
    protected void tearDown() throws Exception {
        SimpleXpathEngine.clearNamespaces();
    }
    
    /*
    public void testServerConfiguration() {
        //TODO Implement ServerConfiguration().
    }

    public void testCopy() {
        //TODO Implement copy().
    }
    */

    public void testApplyProperties() throws Exception {
        String testVal = "9999";
        String xpath = "/" + NS_FCFG_PREFIX + ":server/" + NS_FCFG_PREFIX + 
                       ":param[@name='fedoraServerPort'][@value='" + testVal + "']";
        Properties props = new Properties();
        props.put("server.fedoraServerPort", testVal);
        
        // ensure the new property is really new
        config.serialize(out);
        assertXpathNotExists(xpath, getDocument(out));
        
        // apply the new property and ensure it is present in the serialized output
        out.reset();
        props.put("server.fedoraServerPort", testVal);
        config.applyProperties(props);
        config.serialize(out);
        assertXpathExists(xpath, getDocument(out));
    }

    public void testSerialize() throws Exception {
        Document original = builder.parse(originalFile);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        config.serialize(out);
        
        Document generated = builder.parse(new ByteArrayInputStream(out.toByteArray()));        
        assertXMLEqual(original, generated);
    }
    
    private Document getDocument(ByteArrayOutputStream out) throws Exception {
        return builder.parse(new ByteArrayInputStream(out.toByteArray()));
    }
    
    private String getDocumentAsString(ByteArrayOutputStream out) throws Exception {
        return new String(out.toByteArray(), "UTF-8");
    }
}
