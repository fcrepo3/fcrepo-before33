package fedora.server.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.custommonkey.xmlunit.SimpleXpathEngine;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;

import fedora.test.FedoraTestCase;

/**
 * @author Edwin Shin
 */
public class TestServerConfiguration extends FedoraTestCase {
    private static final String NS_FCFG_PREFIX = "fcfg";
    //private File originalFile;
    //private FileInputStream fis;
    //private ServerConfiguration config;
    private DocumentBuilder builder;
    private ByteArrayOutputStream out;
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestServerConfiguration.class);
    }
    
    protected void setUp() throws Exception {
        out = new ByteArrayOutputStream();
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
    
    public void testFoo() throws Exception {
        File originalFile = new File(FCFG);
        FileInputStream fis = new FileInputStream(originalFile);
        ServerConfigurationParser scp = new ServerConfigurationParser(fis);
        ServerConfiguration config = scp.parse();
        
        Properties props = new Properties();
        props.load(new FileInputStream("src/fcfg/server/eddie.properties"));
        config.applyProperties(props);
        config.serialize(new FileOutputStream("/tmp/eddie.fcfg"));
        
    }

    public void testApplyProperties() throws Exception {
        File originalFile = new File(FCFG);
        FileInputStream fis = new FileInputStream(originalFile);
        ServerConfigurationParser scp = new ServerConfigurationParser(fis);
        ServerConfiguration config = scp.parse();
        
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
        File originalFile = new File(FCFG);
        FileInputStream fis = new FileInputStream(originalFile);
        ServerConfigurationParser scp = new ServerConfigurationParser(fis);
        ServerConfiguration config = scp.parse();
        
        Document original = builder.parse(originalFile);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        config.serialize(out);
        
        //config.serialize(new FileOutputStream("/tmp/fcfg.fcfg"));
        
        Document generated = builder.parse(new ByteArrayInputStream(out.toByteArray()));
        XMLUnit.setIgnoreWhitespace(true);
        assertXMLEqual(original, generated);
        XMLUnit.setIgnoreWhitespace(false);
    }
    
    private Document getDocument(ByteArrayOutputStream out) throws Exception {
        return builder.parse(new ByteArrayInputStream(out.toByteArray()));
    }
    
    private String getDocumentAsString(ByteArrayOutputStream out) throws Exception {
        return new String(out.toByteArray(), "UTF-8");
    }
}
