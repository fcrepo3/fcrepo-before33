package fedora.server.search;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;

public class DCFields 
        extends DefaultHandler {

    private ArrayList m_titles=new ArrayList();
    private ArrayList m_creators=new ArrayList();
    private ArrayList m_subjects=new ArrayList();
    private ArrayList m_descriptions=new ArrayList();
    private ArrayList m_publishers=new ArrayList();
    private ArrayList m_contributors=new ArrayList();
    private ArrayList m_dates=new ArrayList();
    private ArrayList m_types=new ArrayList();
    private ArrayList m_formats=new ArrayList();
    private ArrayList m_identifiers=new ArrayList();
    private ArrayList m_sources=new ArrayList();
    private ArrayList m_languages=new ArrayList();
    private ArrayList m_relations=new ArrayList();
    private ArrayList m_coverages=new ArrayList();
    private ArrayList m_rights=new ArrayList();

    private StringBuffer m_currentContent;

    public DCFields() {
    }
    
    public DCFields(InputStream in) 
            throws RepositoryConfigurationException, ObjectIntegrityException,
            StreamIOException {
        SAXParser parser=null;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            parser=spf.newSAXParser();
        } catch (Exception e) {
            throw new RepositoryConfigurationException("Error getting SAX "
                    + "parser for DC metadata: " + e.getClass().getName() 
                    + ": " + e.getMessage());
        }
        try {
            parser.parse(in, this);
        } catch (SAXException saxe) {
            throw new ObjectIntegrityException("Parse error parsing DC XML Metadata: " + saxe.getMessage());
        } catch (IOException ioe) {
            throw new StreamIOException("Stream error parsing DC XML Metadata: " + ioe.getMessage());
        }
    }
    
    public void startElement(String uri, String localName, String qName, 
            Attributes attrs) {
        m_currentContent=new StringBuffer();
    }
    
    public void characters(char[] ch, int start, int length) {
        m_currentContent.append(ch, start, length);
    }
    
    public void endElement(String uri, String localName, String qName) {
        if (localName.equals("title")) {
            titles().add(m_currentContent.toString());
        } else if (localName.equals("creator")) {
            creators().add(m_currentContent.toString());
        } else if (localName.equals("subject")) {
            subjects().add(m_currentContent.toString());
        } else if (localName.equals("description")) {
            descriptions().add(m_currentContent.toString());
        } else if (localName.equals("publisher")) {
            publishers().add(m_currentContent.toString());
        } else if (localName.equals("contributor")) {
            contributors().add(m_currentContent.toString());
        } else if (localName.equals("date")) {
            dates().add(m_currentContent.toString());
        } else if (localName.equals("type")) {
            types().add(m_currentContent.toString());
        } else if (localName.equals("format")) {
            formats().add(m_currentContent.toString());
        } else if (localName.equals("identifier")) {
            identifiers().add(m_currentContent.toString());
        } else if (localName.equals("source")) {
            sources().add(m_currentContent.toString());
        } else if (localName.equals("language")) {
            languages().add(m_currentContent.toString());
        } else if (localName.equals("relation")) {
            relations().add(m_currentContent.toString());
        } else if (localName.equals("coverage")) {
            coverages().add(m_currentContent.toString());
        } else if (localName.equals("rights")) {
            rights().add(m_currentContent.toString());
        }
    }
    
    public List titles() {
        return m_titles;
    }
    
    public List creators() {
        return m_creators;
    }
    
    public List subjects() {
        return m_subjects;
    }
    
    public List descriptions() {
        return m_descriptions;
    }
    
    public List publishers() {
        return m_publishers;
    }
    
    public List contributors() {
        return m_contributors;
    }
    
    public List dates() {
        return m_dates;
    }
    
    public List types() {
        return m_types;
    }
    
    public List formats() {
        return m_formats;
    }
    
    public List identifiers() {
        return m_identifiers;
    }
    
    public List sources() {
        return m_sources;
    }
    
    public List languages() {
        return m_languages;
    }
    
    public List relations() {
        return m_relations;
    }
    
    public List coverages() {
        return m_coverages;
    }
    
    public List rights() {
        return m_rights;
    }
    
}    
