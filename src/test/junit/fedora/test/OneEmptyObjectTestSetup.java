package fedora.test;

import junit.extensions.TestSetup;

import junit.framework.Test;

import fedora.common.Constants;

import fedora.server.management.FedoraAPIM;

public class OneEmptyObjectTestSetup
        extends TestSetup
        implements Constants {

    private final String m_pid;

    private FedoraAPIM m_apim;

    public OneEmptyObjectTestSetup(Test test, String pid) {
        super(test);
        m_pid = pid;
    }

    private static byte[] getTestObjectBytes(String pid) throws Exception {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<foxml:digitalObject VERSION=\"1.1\" xmlns:xsi=\"" + XSI.uri + "\"\n");

        xml.append("           xmlns:foxml=\"" + FOXML.uri + "\"\n");
        xml.append("           xsi:schemaLocation=\"" + FOXML.uri + " " + FOXML1_1.xsdLocation + "\"\n");
        xml.append("\n           PID=\"" + pid + "\">\n");
        xml.append("  <foxml:objectProperties>\n");
        xml.append("    <foxml:property NAME=\"" + RDF.TYPE.uri + "\" VALUE=\"FedoraObject\"/>\n");
        xml.append("    <foxml:property NAME=\"" + MODEL.LABEL.uri + "\" VALUE=\"label\"/>\n");
        xml.append("    <foxml:property NAME=\"" + MODEL.CONTENT_MODEL.uri + "\" VALUE=\"\"/>\n");
        xml.append("  </foxml:objectProperties>\n");
        xml.append("</foxml:digitalObject>");
        return xml.toString().getBytes("UTF-8");
    }

    public void setUp() throws Exception {
        System.out.println("Ingesting test object: " + m_pid);
        m_apim = FedoraServerTestCase.getFedoraClient().getAPIM();
        m_apim.ingest(getTestObjectBytes(m_pid), FOXML1_1.uri, "");
    }

    public void tearDown() throws Exception {
        System.out.println("Purging test object: " + m_pid);
        m_apim.purgeObject(m_pid, "", false);
    }

}
