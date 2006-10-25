package fedora.server.utilities;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fedora.common.Constants;
import fedora.common.rdf.RDFName;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;

/**
 *
 * <p><b>Title:</b> DCFields.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class DCFields
        extends DefaultHandler {

	public static final String OAIDC_PREFIX="oai_dc";
	public static final String OAIDC_NS="http://www.openarchives.org/OAI/2.0/oai_dc/";
	public static final String DC_PREFIX="dc";
	public static final String DC_NS="http://purl.org/dc/elements/1.1/";
			
    private ArrayList<String> m_titles=new ArrayList<String>();
    private ArrayList<String> m_creators=new ArrayList<String>();
    private ArrayList<String> m_subjects=new ArrayList<String>();
    private ArrayList<String> m_descriptions=new ArrayList<String>();
    private ArrayList<String> m_publishers=new ArrayList<String>();
    private ArrayList<String> m_contributors=new ArrayList<String>();
    private ArrayList<String> m_dates=new ArrayList<String>();
    private ArrayList<String> m_types=new ArrayList<String>();
    private ArrayList<String> m_formats=new ArrayList<String>();
    private ArrayList<String> m_identifiers=new ArrayList<String>();
    private ArrayList<String> m_sources=new ArrayList<String>();
    private ArrayList<String> m_languages=new ArrayList<String>();
    private ArrayList<String> m_relations=new ArrayList<String>();
    private ArrayList<String> m_coverages=new ArrayList<String>();
    private ArrayList<String> m_rights=new ArrayList<String>();

    private StringBuffer m_currentContent;

    public DCFields() {
    }

    public DCFields(InputStream in)
            throws RepositoryConfigurationException, ObjectIntegrityException,
            StreamIOException {
        SAXParser parser=null;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
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

    /**
     * Returns a Map with RDFName keys, each value containing List
     * of String values for that field.
     *
     */
    public Map<RDFName, List<String>> getMap() {
        Map<RDFName, List<String>> map = new HashMap<RDFName, List<String>>();

        map.put(Constants.DC.TITLE, m_titles);
        map.put(Constants.DC.CREATOR, m_creators);
        map.put(Constants.DC.SUBJECT, m_subjects);
        map.put(Constants.DC.DESCRIPTION, m_descriptions);
        map.put(Constants.DC.PUBLISHER, m_publishers);
        map.put(Constants.DC.CONTRIBUTOR, m_contributors);
        map.put(Constants.DC.DATE, m_dates);
        map.put(Constants.DC.TYPE, m_types);
        map.put(Constants.DC.FORMAT, m_formats);
        map.put(Constants.DC.IDENTIFIER, m_identifiers);
        map.put(Constants.DC.SOURCE, m_sources);
        map.put(Constants.DC.LANGUAGE, m_languages);
        map.put(Constants.DC.RELATION, m_relations);
        map.put(Constants.DC.COVERAGE, m_coverages);
        map.put(Constants.DC.RIGHTS, m_rights);

        return map;
    }

    public List<String> titles() {
        return m_titles;
    }

    public List<String> creators() {
        return m_creators;
    }

    public List<String> subjects() {
        return m_subjects;
    }

    public List<String> descriptions() {
        return m_descriptions;
    }

    public List<String> publishers() {
        return m_publishers;
    }

    public List<String> contributors() {
        return m_contributors;
    }

    public List<String> dates() {
        return m_dates;
    }

    public List<String> types() {
        return m_types;
    }

    public List<String> formats() {
        return m_formats;
    }

    public List<String> identifiers() {
        return m_identifiers;
    }

    public List<String> sources() {
        return m_sources;
    }

    public List<String> languages() {
        return m_languages;
    }

    public List<String> relations() {
        return m_relations;
    }

    public List<String> coverages() {
        return m_coverages;
    }

    public List<String> rights() {
        return m_rights;
    }

    /**
     * Get the DCFields as a String in namespace-qualified XML form,
     * matching the oai_dc schema.... but without the xml declaration.
     */
    public String getAsXML() {
        StringBuffer out=new StringBuffer();
		out.append("<" + OAIDC_PREFIX + ":dc"
			+ " xmlns:"	+ OAIDC_PREFIX + "=\"" + OAIDC_NS + "\""
			+ " xmlns:"	+ DC_PREFIX + "=\"" + DC_NS + "\">\n");
        appendXML(titles(), "title", out);
        appendXML(creators(), "creator", out);
        appendXML(subjects(), "subject", out);
        appendXML(descriptions(), "description", out);
        appendXML(publishers(), "publisher", out);
        appendXML(contributors(), "contributor", out);
        appendXML(dates(), "date", out);
        appendXML(types(), "type", out);
        appendXML(formats(), "format", out);
        appendXML(identifiers(), "identifier", out);
        appendXML(sources(), "source", out);
        appendXML(languages(), "language", out);
        appendXML(relations(), "relation", out);
        appendXML(coverages(), "coverage", out);
        appendXML(rights(), "rights", out);
        out.append("</oai_dc:dc>\n");
        return out.toString();
    }

    private void appendXML(List values, String name, StringBuffer out) {
        for (int i=0; i<values.size(); i++) {
            out.append("  <dc:" + name + ">");
            out.append(StreamUtility.enc((String) values.get(i)));
            out.append("</dc:" + name + ">\n");
        }
    }

}
