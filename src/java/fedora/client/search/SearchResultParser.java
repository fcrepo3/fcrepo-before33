package fedora.client.search;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * Parses plain old xml results from a Fedora search request.
 * 
 */
public class SearchResultParser
        extends DefaultHandler {

    private boolean READING_PID;
    private boolean READING_TOKEN;

    private StringBuffer m_currentPID;
    private StringBuffer m_sessionToken;
    private Set m_pids=new HashSet();

    public SearchResultParser(InputStream xml) 
            throws IOException {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser parser = spf.newSAXParser();
            parser.parse(xml, this);
        } catch (Exception e) {
            if (e.getMessage()!=null) {
                throw new IOException(e.getMessage());
            } else {
                throw new IOException("Error parsing search result xml: "
                        + e.getClass().getName());
            }
        }
    }

    public void startElement(String uri, 
                             String localName, 
                             String qName, 
                             Attributes a) {
        if (localName.equals("pid")) {
            READING_PID=true;
            m_currentPID=new StringBuffer();
        } else if (localName.equals("token")) {
            READING_TOKEN=true;
            m_sessionToken=new StringBuffer();
        }
    }

    public void characters(char[] ch, int start, int length) {
        if (READING_PID) {
            m_currentPID.append(ch, start, length);
        } else if (READING_TOKEN) {
            m_sessionToken.append(ch, start, length);
        }
    }

    public void endElement(String uri, 
                           String localName, 
                           String qName) 
            throws SAXException {
        if (localName.equals("pid")) {
            READING_PID=false;
            m_pids.add(m_currentPID.toString());
        } else if (localName.equals("token")) {
            READING_TOKEN=false;
        }
    }

    public Set getPIDs() {
        return m_pids;
    }

    public String getToken() {
        if (m_sessionToken==null) return null;
        return m_sessionToken.toString();
    }
}