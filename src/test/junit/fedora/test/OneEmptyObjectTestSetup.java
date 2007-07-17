package fedora.test;

import junit.extensions.TestSetup;

import junit.framework.Test;

import fedora.server.management.FedoraAPIM;

public class OneEmptyObjectTestSetup extends TestSetup {

    private final String m_pid;

    private FedoraAPIM m_apim;

    public OneEmptyObjectTestSetup(Test test, String pid) {
        super(test);
        m_pid = pid;
    }

    private static byte[] getTestObjectBytes(String pid) throws Exception {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<foxml:digitalObject xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");

        xml.append("           xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\"\n");
        xml.append("           xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-0.xsd\"\n");
        xml.append("\n           PID=\"" + pid + "\">\n");
        xml.append("  <foxml:objectProperties>\n");
        xml.append("    <foxml:property NAME=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\" VALUE=\"FedoraObject\"/>\n");
        xml.append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"label\"/>\n");
        xml.append("    <foxml:property NAME=\"info:fedora/fedora-system:def/model#contentModel\" VALUE=\"\"/>\n");
        xml.append("  </foxml:objectProperties>\n");
        xml.append("</foxml:digitalObject>");
        return xml.toString().getBytes("UTF-8");
    }

    public void setUp() throws Exception {
        System.out.println("Ingesting test object: " + m_pid);
        m_apim = FedoraServerTestCase.getFedoraClient().getAPIM();
        m_apim.ingest(getTestObjectBytes(m_pid), "foxml1.0", "");
    }

    public void tearDown() throws Exception {
        System.out.println("Purging test object: " + m_pid);
        m_apim.purgeObject(m_pid, "", false);
    }

}
