package fedora.server.storage;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamReadException;
import fedora.server.storage.types.AuditRecord;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.utilities.DateUtility;

import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Reads a METS-Fedora XML stream into a DigitalObject.
 */
public class METSDODeserializer 
        extends DefaultHandler
        implements DODeserializer {
        
    private final static String M="http://www.loc.gov/METS/";

    private SAXParser m_parser;
    private String m_characterEncoding;
    
    private DigitalObject m_obj;
    private HashMap m_prefixes; // uri-to-prefix mapping
    
    private boolean m_rootElementFound;
    private String m_dsId;
    private String m_dsVersId;
    private Date m_dsCreateDate;
    private String m_dsState;
    private String m_dsInfoType;
    private String m_dsLabel;
    private int m_dsMDClass;
    private StringBuffer m_dsXMLBuffer;
    private ArrayList m_dsPrefixes;    // namespace prefixes used in the currently scanned datastream
    private boolean m_inXMLMetadata;
    private int m_xmlDataLevel;
    private StringBuffer m_auditBuffer;  // char buffer for audit element contents
    private String m_auditProcessType;
    private String m_auditAction;
    private String m_auditResponsibility;
    private String m_auditDate;
    private String m_auditJustification;
    
    /**
     * Initializes by setting up a parser that doesn't validate.
     */
    public METSDODeserializer(String characterEncoding) 
            throws FactoryConfigurationError, ParserConfigurationException, 
            SAXException, UnsupportedEncodingException {
        this(characterEncoding, false);
    }

    /**
     * Initializes by setting up a parser that validates only if validate=true.
     * <p></p>
     * The character encoding of the XML is auto-determined by sax, but
     * we need it for when we set the byte[] in DatastreamXMLMetadata, so
     * we effectively, we need to also specify the encoding of the datastreams.
     * this could be different than how the digital object xml was encoded,
     * and this class won't care.  However, the caller should keep track
     * of the byte[] encoding if it plans on doing any translation of
     * that to characters (such as in xml serialization)
     */
    public METSDODeserializer(String characterEncoding, boolean validate)
            throws FactoryConfigurationError, ParserConfigurationException, 
            SAXException, UnsupportedEncodingException {
        // ensure the desired encoding is supported before starting
        // unsuppenc will be thrown if not
        m_characterEncoding=characterEncoding;
        StringBuffer buf=new StringBuffer();
        buf.append("test");
        byte[] temp=buf.toString().getBytes(m_characterEncoding); 
        // then init sax
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(validate);
        spf.setNamespaceAware(true);
        // allows us to see xmlns:id so we can build a table...
        // hmmm.. maybe this is doable with startNamespacePrefixMapping
        // spf.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        m_parser=spf.newSAXParser();
    }
    
    public void deserialize(InputStream in, DigitalObject obj) 
            throws ObjectIntegrityException, StreamIOException {
        m_obj=obj;
        m_rootElementFound=false;
        m_dsId=null;
        m_dsVersId=null;
        m_dsCreateDate=null;
        m_dsState=null;
        m_dsInfoType=null;
        m_dsLabel=null;
        m_dsXMLBuffer=null;
        m_prefixes=new HashMap();
        try {
            m_parser.parse(in, this);
        } catch (IOException ioe) {
            throw new StreamIOException("low-level stream io problem occurred "
                    + "while sax was parsing this object.");
        } catch (SAXException se) {
            throw new ObjectIntegrityException("mets stream was bad : " + se.getMessage());
        }
        if (!m_rootElementFound) {
            throw new ObjectIntegrityException("METS root element not found -- must have 'mets' element in namespace " + M + " as root element.");
        }
        obj.setNamespaceMapping(m_prefixes);
    }
    
    public void startPrefixMapping(String prefix, String uri) {
        m_prefixes.put(uri, prefix);
    }
    
    public void startElement(String uri, String localName, String qName, 
            Attributes a) {
        if (uri.equals(M) && !m_inXMLMetadata) {
            // a new mets element is starting
            if (localName.equals("mets")) {
                m_rootElementFound=true;
                m_obj.setPid(grab(a, M, "OBJID"));
                m_obj.setLabel(grab(a, M, "LABEL"));
                m_obj.setContentModelId(grab(a, M, "PROFILE"));
            } else if (localName.equals("metsHdr")) {
                m_obj.setCreateDate(DateUtility.convertStringToDate(
                        grab(a, M, "CREATEDATE")));
                m_obj.setLastModDate(DateUtility.convertStringToDate(
                        grab(a, M, "LASTMODDATE")));
                m_obj.setState(grab(a, M, "RECORDSTATUS"));
            } else if (localName.equals("amdSec")) {
                m_dsId=grab(a, M, "ID");
            } else if (localName.equals("techMD") || localName.equals("dmdSec") 
                    || localName.equals("sourceMD")
                    || localName.equals("rightsMD")
                    || localName.equals("digiprovMD")) {
                m_dsVersId=grab(a, M, "ID");
                if (localName.equals("techMD")) {
                    m_dsMDClass=DatastreamXMLMetadata.TECHNICAL;
                }
                if (localName.equals("sourceMD")) {
                    m_dsMDClass=DatastreamXMLMetadata.SOURCE;
                }
                if (localName.equals("rightsMD")) {
                    m_dsMDClass=DatastreamXMLMetadata.RIGHTS;
                }
                if (localName.equals("digiprovMD")) {
                    m_dsMDClass=DatastreamXMLMetadata.DIGIPROV;
                }
                if (localName.equals("dmdSec")) {
                    m_dsMDClass=DatastreamXMLMetadata.DESCRIPTIVE;
                    // dmdsec metadata has primary id in GROUPID attribute
                    // whereas amdSec metadata has an outerlying element
                    // that includes the primary id in an ID attribute
                    m_dsId=grab(a, M, "GROUPID");
                }
                m_dsCreateDate=DateUtility.convertStringToDate(
                        grab(a, M, "CREATED"));
                m_dsState=grab(a, M, "STATUS");
            } else if (localName.equals("mdWrap")) {
                m_dsInfoType=grab(a, M, "MDTYPE");
                m_dsLabel=grab(a, M, "LABEL");
            } else if (localName.equals("xmlData")) {
                m_dsXMLBuffer=new StringBuffer();
                m_dsPrefixes=new ArrayList();
                m_xmlDataLevel=0;
                m_inXMLMetadata=true;
            }
        } else {
            if (m_inXMLMetadata) {
                // must be in xmlData... just output it, remembering the number
                // of METS:xmlData elements we see
                m_dsXMLBuffer.append('<');
                String prefix=(String) m_prefixes.get(uri);
                if (prefix!=null) {
                    if (!m_dsPrefixes.contains(prefix)) {
                        if (!"".equals(prefix)) {
                            m_dsPrefixes.add(prefix);
                        }
                    }
                    m_dsXMLBuffer.append(prefix);
                    m_dsXMLBuffer.append(':');
                }
                m_dsXMLBuffer.append(localName);
                for (int i=0; i<a.getLength(); i++) {
                    m_dsXMLBuffer.append(' ');
                    String aPrefix=(String) m_prefixes.get(a.getURI(i));
                    if (aPrefix!=null) {
                        if (!m_dsPrefixes.contains(prefix)) {
                            if (!"".equals(prefix)) {
                                m_dsPrefixes.add(prefix);
                            }
                        }
                        m_dsXMLBuffer.append(aPrefix);
                        m_dsXMLBuffer.append(':');
                    }
                    m_dsXMLBuffer.append(a.getLocalName(i));
                    m_dsXMLBuffer.append("=\"");
                    m_dsXMLBuffer.append(a.getValue(i));
                    m_dsXMLBuffer.append("\"");
                }
                m_dsXMLBuffer.append('>');
                if (uri.equals(M) && localName.equals("xmlData")) {
                    m_xmlDataLevel++;
                }
                // remember this stuff... (we don't have to look at level
                // because the audit schema doesn't allow for xml elements inside
                // these, so they're never set incorrectly)
                // signaling that we're interested in sending char data to
                // the m_auditBuffer by making it non-null, and getting
                // ready to accept data by allocating a new StringBuffer
                if (m_dsId.equals("FEDORA-AUDITTRAIL")) {
                    if (localName.equals("process")) {
                        m_auditProcessType=grab(a, uri, "type");
                    } else if ( (localName.equals("action")) 
                            || (localName.equals("responsibility")) 
                            || (localName.equals("date")) 
                            || (localName.equals("justification")) ) {
                        m_auditBuffer=new StringBuffer();
                    }
                }
            } else {
                // ignore all else
            }
        }
    }
    
    public void characters(char[] ch, int start, int length) {
        if (m_inXMLMetadata) {
            m_dsXMLBuffer.append(ch, start, length);
            if (m_auditBuffer!=null) {
                m_auditBuffer.append(ch, start, length);
            }
        }
    }
    
    public void endElement(String uri, String localName, String qName) {
        if (m_inXMLMetadata) {
            if (uri.equals(M) && localName.equals("xmlData") && m_xmlDataLevel==0) {
                // finished all xml metadata for this datastream
                if (m_dsId.equals("FEDORA-AUDITTRAIL")) {
                    // we've been looking at an audit trail...
                    // m_auditProcessType, m_auditAction, 
                    // m_auditResponsibility, m_auditDate, m_auditJustification
                    // should all be set
                    AuditRecord a=new AuditRecord();
                    a.id=m_dsVersId; // it's like the FEDORA-AUDITTRAIL is a 
                                     // datastream and the records are versions
                    a.processType=m_auditProcessType;
                    a.action=m_auditAction;
                    a.responsibility=m_auditResponsibility;
                    a.date=DateUtility.convertStringToDate(m_auditDate);
                    a.justification=m_auditJustification;
                    m_obj.getAuditRecords().add(a);
                } else {
                    // create the right kind of datastream and add it to m_obj
                    String[] prefixes=new String[m_dsPrefixes.size()];
                    for (int i=0; i<m_dsPrefixes.size(); i++) {
                        prefixes[i]=(String) m_dsPrefixes.get(i);
                    }
                    DatastreamXMLMetadata ds=new DatastreamXMLMetadata();
                    // set the attrs specific to XML_METADATA datastreams
                    ds.namespacePrefixes=prefixes;
                    try {
                        ds.xmlContent=m_dsXMLBuffer.toString().getBytes(
                                m_characterEncoding);
                    } catch (UnsupportedEncodingException uee) {
                      // won't happen -- this was checked in the constructor
                    }
                    // set the attrs common to all datastreams
                    ds.DatastreamID=m_dsId;
                    ds.DSVersionID=m_dsVersId;
                    ds.DSLabel=m_dsLabel;
                    ds.DSMIME="text/xml";
                    ds.DSCreateDT=m_dsCreateDate;
                    ds.DSSize="" + ds.xmlContent.length; // bytes, not chars, but probably N/A anyway
                    ds.DSControlGrp=Datastream.XML_METADATA;
                    ds.DSInfoType=m_dsInfoType;
                    ds.DSMDClass=m_dsMDClass;
                    ds.DSState=m_dsState;
                    ds.DSLocation=null;  // N/A
                    // add it to the digitalObject
                    m_obj.datastreams(m_dsId).add(ds);
                }
                m_inXMLMetadata=false; // other stuff is re-initted upon 
                                       // startElement for next xml metadata element
                                       
            } else {
                // finished an element in xml metadata... print end tag,
                // subtracting the level of METS:xmlData elements we're at
                // if needed
                m_dsXMLBuffer.append("</");
                String prefix=(String) m_prefixes.get(uri);
                if (prefix!=null) {
                    m_dsXMLBuffer.append(prefix);
                    m_dsXMLBuffer.append(':');
                }
                m_dsXMLBuffer.append(localName);
                m_dsXMLBuffer.append(">");
                if (uri.equals(M) && localName.equals("xmlData")) {
                    m_xmlDataLevel--;
                }
                if (m_dsId.equals("FEDORA-AUDITTRAIL")) {
                    if (localName.equals("action")) {
                        m_auditAction=m_auditBuffer.toString();
                        m_auditBuffer=null;
                    } else if (localName.equals("responsibility")) {
                        m_auditResponsibility=m_auditBuffer.toString();
                        m_auditBuffer=null;
                    } else if (localName.equals("date")) {
                        m_auditDate=m_auditBuffer.toString();
                        m_auditBuffer=null;
                    } else if (localName.equals("justification")) {
                        m_auditJustification=m_auditBuffer.toString();
                        m_auditBuffer=null;
                    }
                }
                
                
            }
        }
    }
    
    private static String grab(Attributes a, String namespace, 
            String elementName) {
        String ret=a.getValue(namespace, elementName);
        if (ret==null) {
            ret=a.getValue(elementName);
        }
        return ret;
    }
    
}