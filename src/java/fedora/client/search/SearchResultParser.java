package fedora.client.search;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

/**
 * Parses plain old xml results from a Fedora search request.
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
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