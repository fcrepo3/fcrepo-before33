package fedora.server.config;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

public class ServerConfigurationParser 
        extends DefaultHandler {

    private SAXParser m_parser;
    private InputStream m_xmlStream;

    private List m_serverParameters;
    private List m_moduleConfigurations;
    private List m_datastoreConfigurations;

    private Parameter m_lastParam;
    private String m_moduleOrDatastoreComment;
    private String m_paramComment;

    private List m_currentParameters;  // module/datastore
    private StringBuffer m_commentBuffer;
    private boolean m_inParam;
    private boolean m_inModuleOrDatastore;

    public ServerConfigurationParser(InputStream xmlStream) throws IOException {
        m_xmlStream = xmlStream;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            m_parser=spf.newSAXParser();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error getting XML parser: " + e.getMessage());
        }
    }

    public ServerConfiguration parse() throws IOException {
        m_serverParameters = new ArrayList();
        m_moduleConfigurations = new ArrayList();
        m_datastoreConfigurations = new ArrayList();
        try {
            m_parser.parse(m_xmlStream, this);
            return new ServerConfiguration(m_serverParameters, 
                                           m_moduleConfigurations, 
                                           m_datastoreConfigurations);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Error parsing XML: " + e.getMessage());
        }
    }

    public void startElement(String uri, 
                             String localName, 
                             String qName,
                             Attributes a) throws SAXException {
        if (localName.equals("module")) {
            String moduleRole = a.getValue("role");
            System.out.println(moduleRole);
        } else if (localName.equals("comment")) {
            m_commentBuffer = new StringBuffer();
        } else if (localName.equals("param")) {
            m_inParam = true;
            m_paramComment = null;
        }
    }

    public void endElement(String uri, String localName, String qName) {
        if (localName.equals("module")) {
            // add a new ModuleConfiguration to m_moduleConfigurations
        } else if (localName.equals("datastore")) {
            // add a new DatastoreConfiguration to m_datastoreConfigurations
        } else if (localName.equals("comment")) {
            // figure out what kind of thing this is a comment for
            // if we're in a param, it's for the param.
            if (m_inParam) {
                m_paramComment = m_commentBuffer.toString();
            } else if (m_inModuleOrDatastore) {
                m_moduleOrDatastoreComment = m_commentBuffer.toString();
            } else {
                // the old style was to have a comment after (not inside) a param
                if (m_lastParam != null) {
                    m_lastParam.setComment(m_commentBuffer.toString());
                }
            }
        } else if (localName.equals("param")) {
            m_inParam = false;
        }
    }

    public void characters(char[] ch, int start, int length) {
        m_commentBuffer.append(ch, start, length);
    }

}
