
package fedora.test.integration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import junit.framework.JUnit4TestAdapter;

import org.custommonkey.xmlunit.SimpleXpathEngine;

import org.junit.Test;

import fedora.client.utility.export.Export;
import fedora.client.utility.ingest.Ingest;

import fedora.server.management.FedoraAPIM;

import fedora.test.FedoraTestCase;
import fedora.test.api.TestAPIM;

/**
 * Tests the command-line Ingest and Export interfaces with varied formats.
 *
 * @author Bill Branan
 */
public class TestCommandLineFormats
        extends FedoraTestCase {

    private FedoraAPIM apim;
    
    @Override
    public void setUp() throws Exception {
        apim = getFedoraClient(getBaseURL(), getUsername(), getPassword()).getAPIM();
        SimpleXpathEngine.registerNamespace("foxml", "info:fedora/fedora-system:def/foxml#");
        SimpleXpathEngine.registerNamespace("METS", "http://www.loc.gov/METS/");
        SimpleXpathEngine.registerNamespace("", "http://www.w3.org/2005/Atom");
    }

    @Test
    public void testIngestFOXML10() throws Exception {
        System.out.println("Testing Ingest with FOXML 1.0 format");
        File foxml10 = File.createTempFile("demo_997", ".xml");
        FileOutputStream fileWriter = new FileOutputStream(foxml10);
        fileWriter.write(TestAPIM.demo997FOXML10ObjectXML);
        fileWriter.close();
        
        String[] parameters = {"f ", foxml10.getAbsolutePath(), 
                FOXML1_0.uri, getHost() + ":" + getPort(), 
                getUsername(), getPassword(), getProtocol(), 
                "\"Ingest\""};
        
        Ingest.main(parameters);
        foxml10.delete();
        
        byte[] objectXML = apim.getObjectXML("demo:997");
        assertTrue(objectXML.length > 0);
        String xmlIn = new String(objectXML, "UTF-8");
        assertXpathExists("foxml:digitalObject[@PID='demo:997']", xmlIn);
        assertXpathExists("//foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#state' and @VALUE='Active']", xmlIn);
        assertXpathExists("//foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#label' and @VALUE='Image of Coliseum in Rome']", xmlIn);
        assertXpathEvaluatesTo("6", "count(//foxml:datastream)", xmlIn);
        assertXpathNotExists("//foxml:disseminator", xmlIn);
        
        apim.purgeObject("demo:997", "", false);
    }

    @Test
    public void testIngestFOXML11() throws Exception {
        System.out.println("Testing Ingest with FOXML 1.1 format");
        File foxml11 = File.createTempFile("demo_998", ".xml");
        FileOutputStream fileWriter = new FileOutputStream(foxml11);
        fileWriter.write(TestAPIM.demo998FOXMLObjectXML);
        fileWriter.close();
        
        String[] parameters = {"f ", foxml11.getAbsolutePath(), 
                FOXML1_1.uri, getHost() + ":" + getPort(), 
                getUsername(), getPassword(), getProtocol(), 
                "\"Ingest\""};
        
        Ingest.main(parameters);
        foxml11.delete();
        
        byte[] objectXML = apim.getObjectXML("demo:998");
        assertTrue(objectXML.length > 0);
        String xmlIn = new String(objectXML, "UTF-8");
        assertXpathExists("foxml:digitalObject[@PID='demo:998']", xmlIn);
        assertXpathExists("//foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#state' and @VALUE='Active']", xmlIn);
        assertXpathExists("//foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#label' and @VALUE='Image of Coliseum in Rome']", xmlIn);
        assertXpathEvaluatesTo("7", "count(//foxml:datastream)", xmlIn);
        assertXpathNotExists("//foxml:disseminator", xmlIn);
        
        apim.purgeObject("demo:998", "", false);
    }
    
    @Test    
    public void testIngestMETS() throws Exception {
        System.out.println("Testing Ingest with METS format");
        File mets = File.createTempFile("demo_999", ".xml");
        FileOutputStream fileWriter = new FileOutputStream(mets);
        fileWriter.write(TestAPIM.demo999METSObjectXML);
        fileWriter.close();
        
        String[] parameters = {"f ", mets.getAbsolutePath(), 
                METS_EXT1_1.uri, getHost() + ":" + getPort(), 
                getUsername(), getPassword(), getProtocol(), 
                "\"Ingest\""};
        
        Ingest.main(parameters);
        mets.delete();
        
        byte[] objectXML = apim.getObjectXML("demo:999");
        assertTrue(objectXML.length > 0);
        String xmlIn = new String(objectXML, "UTF-8");
        assertXpathExists("foxml:digitalObject[@PID='demo:999']", xmlIn);
        assertXpathExists("//foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#state' and @VALUE='Active']", xmlIn);
        assertXpathExists("//foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#label' and @VALUE='Image of Coliseum in Rome']", xmlIn);
        assertXpathEvaluatesTo("6", "count(//foxml:datastream)", xmlIn);
        assertXpathNotExists("//foxml:disseminator", xmlIn);
        
        apim.purgeObject("demo:999", "", false);
    }
    
    @Test    
    public void testIngestATOM() throws Exception {
        System.out.println("Testing Ingest with ATOM format");
        File atom = File.createTempFile("demo_1000", ".xml");
        FileOutputStream fileWriter = new FileOutputStream(atom);
        fileWriter.write(TestAPIM.demo1000ATOMObjectXML);
        fileWriter.close();
        
        String[] parameters = {"f ", atom.getAbsolutePath(), 
                ATOM1_0.uri, getHost() + ":" + getPort(), 
                getUsername(), getPassword(), getProtocol(), 
                "\"Ingest\""};
        
        Ingest.main(parameters);
        atom.delete();
        
        byte[] objectXML = apim.getObjectXML("demo:1000");
        assertTrue(objectXML.length > 0);
        String xmlIn = new String(objectXML, "UTF-8");
        assertXpathExists("foxml:digitalObject[@PID='demo:1000']", xmlIn);
        assertXpathExists("//foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#state' and @VALUE='Active']", xmlIn);
        assertXpathExists("//foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#label' and @VALUE='Image of Coliseum in Rome']", xmlIn);
        assertXpathEvaluatesTo("7", "count(//foxml:datastream)", xmlIn);
        assertXpathNotExists("//foxml:disseminator", xmlIn);
        
        apim.purgeObject("demo:1000", "", false);
    }
        
    @Test    
    public void testExportFOXML10() throws Exception {
        System.out.println("Testing Export in FOXML 1.0 format");
        apim.ingest(TestAPIM.demo998FOXMLObjectXML, FOXML1_1.uri, "Ingest for test");
        
        File temp = File.createTempFile("temp", "");
        String[] parameters = {getHost() + ":" + getPort(),
                getUsername(), getPassword(), "demo:998", FOXML1_0.uri,  
                "public", temp.getParent(), "http"};
        
        Export.main(parameters);
        File foxml10 = new File(temp.getParent() + "/demo_998.xml");
        FileInputStream fileReader = new FileInputStream(foxml10);
        byte[] objectXML = new byte[fileReader.available()];
        fileReader.read(objectXML);
        String xmlIn = new String(objectXML, "UTF-8");
        assertXpathExists("foxml:digitalObject[@PID='demo:998']", xmlIn);
        assertXpathExists("//foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#state' and @VALUE='Active']", xmlIn);
        assertXpathExists("//foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#label' and @VALUE='Image of Coliseum in Rome']", xmlIn);
        assertXpathEvaluatesTo("7", "count(//foxml:datastream)", xmlIn);
        assertXpathNotExists("//foxml:disseminator", xmlIn);
        assertXpathNotExists("foxml:digitalObject[@VERSION='1.1']", xmlIn);
        
        temp.delete();
        foxml10.delete();      
        apim.purgeObject("demo:998", "Purge test object", false);
    }
    
    @Test    
    public void testExportFOXML11() throws Exception {
        System.out.println("Testing Export in FOXML 1.1 format");
        apim.ingest(TestAPIM.demo998FOXMLObjectXML, FOXML1_1.uri, "Ingest for test");
        
        File temp = File.createTempFile("temp", "");
        String[] parameters = {getHost() + ":" + getPort(),
                getUsername(), getPassword(), "demo:998", FOXML1_1.uri,  
                "public", temp.getParent(), "http"};
        
        Export.main(parameters);
        File foxml11 = new File(temp.getParent() + "/demo_998.xml");
        FileInputStream fileReader = new FileInputStream(foxml11);
        byte[] objectXML = new byte[fileReader.available()];
        fileReader.read(objectXML);
        String xmlIn = new String(objectXML, "UTF-8");
        assertXpathExists("foxml:digitalObject[@PID='demo:998']", xmlIn);
        assertXpathExists("//foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#state' and @VALUE='Active']", xmlIn);
        assertXpathExists("//foxml:objectProperties/foxml:property[@NAME='info:fedora/fedora-system:def/model#label' and @VALUE='Image of Coliseum in Rome']", xmlIn);
        assertXpathEvaluatesTo("7", "count(//foxml:datastream)", xmlIn);
        assertXpathNotExists("//foxml:disseminator", xmlIn);
        assertXpathExists("foxml:digitalObject[@VERSION='1.1']", xmlIn);
        
        temp.delete();
        foxml11.delete();      
        apim.purgeObject("demo:998", "Purge test object", false);
    }
    
    @Test    
    public void testExportMETS() throws Exception {
        System.out.println("Testing Export in METS format");
        apim.ingest(TestAPIM.demo998FOXMLObjectXML, FOXML1_1.uri, "Ingest for test");
        
        File temp = File.createTempFile("temp", "");
        String[] parameters = {getHost() + ":" + getPort(),
                getUsername(), getPassword(), "demo:998", METS_EXT1_1.uri,  
                "public", temp.getParent(), "http"};
        
        Export.main(parameters);
        File mets = new File(temp.getParent() + "/demo_998.xml");
        FileInputStream fileReader = new FileInputStream(mets);
        byte[] objectXML = new byte[fileReader.available()];
        fileReader.read(objectXML);
        String xmlIn = new String(objectXML, "UTF-8");
        assertXpathExists("METS:mets[@OBJID='demo:998']", xmlIn);
        assertXpathExists("METS:mets[@LABEL='Image of Coliseum in Rome']", xmlIn);
        assertXpathExists("METS:mets[@EXT_VERSION='1.1']", xmlIn);
        assertXpathEvaluatesTo("4", "count(//METS:fileGrp[@STATUS='A'])", xmlIn);
        
        temp.delete();
        mets.delete();
        apim.purgeObject("demo:998", "Purge test object", false);
    }
    
    @Test    
    public void testExportATOM() throws Exception {
        System.out.println("Testing Export in ATOM format");
        apim.ingest(TestAPIM.demo998FOXMLObjectXML, FOXML1_1.uri, "Ingest for test");
        
        File temp = File.createTempFile("temp", "");
        String[] parameters = {getHost() + ":" + getPort(),
                getUsername(), getPassword(), "demo:998", ATOM1_0.uri,  
                "public", temp.getParent(), "http"};
        
        Export.main(parameters);
        File atom = new File(temp.getParent() + "/demo_998.xml");
        FileInputStream fileReader = new FileInputStream(atom);
        byte[] objectXML = new byte[fileReader.available()];
        fileReader.read(objectXML);
        String xmlIn = new String(objectXML, "UTF-8");
        // FIXME: Determine how to perform xpath tests with default namespace
        assertTrue(xmlIn.indexOf("<id>info:fedora/demo:998</id>") > -1);
        assertTrue(xmlIn.indexOf("<title type=\"text\">Image of Coliseum in Rome</title>") > -1);
        // assertXpathEvaluatesTo("info:fedora/demo:998", "feed/id", xmlIn);
        // assertXpathEvaluatesTo("Image of Coliseum in Rome", "feed/title[@type='text']", xmlIn);
        // assertXpathEvaluatesTo("6", "count(feed/entry)", xmlIn);
        
        temp.delete();
        atom.delete();
        apim.purgeObject("demo:998", "Purge test object", false);
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(TestCommandLineFormats.class);
    }
}
