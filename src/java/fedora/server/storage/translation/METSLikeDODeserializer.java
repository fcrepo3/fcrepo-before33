package fedora.server.storage.translation;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.errors.StreamIOException;
import fedora.server.storage.types.AuditRecord;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamContent;
import fedora.server.storage.types.DatastreamManagedContent;
import fedora.server.storage.types.DatastreamReferencedContent;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.DSBindingMap;
import fedora.server.storage.types.DSBinding;
import fedora.server.utilities.DateUtility;
import fedora.server.utilities.StreamUtility;

import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * <p><b>Title:</b> METSLikeDODeserializer.java</p>
 * <p><b>Description:</b> 
 *       Deserializes XML encoded in accordance with the Fedora extension of
 *       the METS XML Schema defined at: 
 *       http://www.fedora.info/definitions/1/0/mets-fedora-ext.xsd.
 * 
 *       The METS XML is parsed using SAX and is instantiated into a Fedora
 *       digital object in memory (see fedora.server.types.DigitalObject). </p>
 *
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
 *
 * @author cwilper@cs.cornell.edu
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class METSLikeDODeserializer
        extends DefaultHandler
        implements DODeserializer {

    /** The namespace for METS */
    private final static String M="http://www.loc.gov/METS/";

    /** The namespace for XLINK */
    private final static String XLINK_NAMESPACE="http://www.w3.org/TR/xlink";
    // Mets says the above, but the spec at http://www.w3.org/TR/xlink/
    // says it's http://www.w3.org/1999/xlink

    private SAXParser m_parser;
    private String m_characterEncoding;

    /** The object to deserialize to. */
    private DigitalObject m_obj;

    /**
     * URI-to-namespace prefix mapping info from SAX2 startPrefixMapping events.
     */
    private HashMap m_prefixes;
    private HashMap m_prefixUris;

    private boolean m_rootElementFound;
    private String m_dsId;
    private String m_dsVersId;
    private Date m_dsCreateDate;
    private String m_dissemId;
    private String m_dissemState;
    private String m_dsState;
    private String m_dsInfoType;
	private String m_dsOtherInfoType;
    private String m_dsLabel;
    private int m_dsMDClass;
    private long m_dsSize;
	private URL m_dsLocationURL;
	private String m_dsLocation;
	private String m_dsLocationType;
    private String m_dsMimeType;
    private String m_dsControlGrp;

    // key=dsId, value=List of datastream ids (strings)
    private HashMap m_dsAdmIds; // these are saved till end of parse
    private String[] m_dsDmdIds; // these are only saved while parsing cur ds


    private StringBuffer m_dsXMLBuffer;
    private StringBuffer m_dsFirstElementBuffer;

    // are we reading binary in an FContent element? (base64-encoded)
    private boolean m_readingContent;

    private boolean m_firstInlineXMLElement;

    /** Namespace prefixes used in the currently scanned datastream */
    private ArrayList m_dsPrefixes;

    /** While parsing, are we inside XML metadata? */
    private boolean m_inXMLMetadata;

    /**
     * Used to differentiate between a metadata section in this object
     * and a metadata section in an inline XML datastream that happens
     * to be a METS document.
     */
    private int m_xmlDataLevel;

    /** String buffer for audit element contents */
    private StringBuffer m_auditBuffer;
    private String m_auditProcessType;
    private String m_auditAction;
	private String m_auditComponentID;
    private String m_auditResponsibility;
    private String m_auditDate;
    private String m_auditJustification;
    private HashMap m_auditIdToComponentId;

    /** Hashmap for holding disseminators during parsing, keyed
     * by structMapId */
    private HashMap m_dissems;

    /**
     * Currently-being-initialized disseminator, during structmap parsing.
     */
    private Disseminator m_diss;

    /**
     * Whether, while in structmap, we've already seen a div
     */
    private boolean m_indiv;

    /** The structMapId of the dissem currently being parsed. */
    private String m_structId;

    /**
     * Never query the server and take it's values for Content-length and
     * Content-type
     */
    public static int QUERY_NEVER=0;

    /**
     * Query the server and take it's values for Content-length and
     * Content-type if either are undefined.
     */
    public static int QUERY_IF_UNDEFINED=1;

    /**
     * Always query the server and take it's values for Content-length and
     * Content-type.
     */
    public static int QUERY_ALWAYS=2;

    private int m_queryBehavior;

    private int m_transContext;

    public METSLikeDODeserializer()
            throws FactoryConfigurationError, ParserConfigurationException,
            SAXException, UnsupportedEncodingException {
        this("UTF-8", false, QUERY_NEVER);
    }

    /**
     * Initializes by setting up a parser that doesn't validate and never
     * queries the server for values of DSSize and DSMIME.
     */
    public METSLikeDODeserializer(String characterEncoding)
            throws FactoryConfigurationError, ParserConfigurationException,
            SAXException, UnsupportedEncodingException {
        this(characterEncoding, false, QUERY_NEVER);
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
    public METSLikeDODeserializer(String characterEncoding, boolean validate,
            int queryBehavior)
            throws FactoryConfigurationError, ParserConfigurationException,
            SAXException, UnsupportedEncodingException {
        m_queryBehavior=queryBehavior;
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
        m_parser=spf.newSAXParser();
    }

    public DODeserializer getInstance()
            throws RepositoryConfigurationException {
        try {
            return (DODeserializer) new METSLikeDODeserializer("UTF-8", false, QUERY_NEVER);
        } catch (Exception e) {
            throw new RepositoryConfigurationException("Error trying to get a "
                    + "new METSLikeDODeserializer instance: " + e.getClass().getName()
                    + ": " + e.getMessage());
        }
    }

    public void deserialize(InputStream in, DigitalObject obj, String encoding, int transContext)
            throws ObjectIntegrityException, StreamIOException, UnsupportedEncodingException {
		System.out.println("Deserializing using METSLike...");
        m_obj=obj;
		m_transContext=transContext;
		initialize();       
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
        
        // Deal with METS way of having datastreams point to other metadata about them
        // (via ADMID attribute).  We want to figure out which of these IDREFs point
        // to audit records about changes to datastreams.       
        Iterator dsIdIter=obj.datastreamIdIterator();
        while (dsIdIter.hasNext()) {
            List datastreams=obj.datastreams((String) dsIdIter.next());
            for (int i=0; i<datastreams.size(); i++) {
                Datastream ds=(Datastream) datastreams.get(i);
                List admIdList=(List) m_dsAdmIds.get(ds.DSVersionID);
                if (admIdList!=null) {
                    // it's got admids..cool.  decide where to put em based
                    // on the types of their targets.
                    Iterator admIdIter=admIdList.iterator();
                    while (admIdIter.hasNext()) {
                       String admId=(String) admIdIter.next();
                       List targetDatastreamSequence=obj.datastreams(admId);
                       if (targetDatastreamSequence.size()>0) {
                           // the admId is a datastream in the object, meaning
                           // it's a datastream that is metadata
                           // for another datastream... so put the admId in the 
                           // list of metadata about the current datastream.
                           try {
                               DatastreamContent dsc=(DatastreamContent) ds;
                               dsc.metadataIdList().add(admId);
                           } catch (ClassCastException cce) {
                               throw new ObjectIntegrityException(
                                       "Metadata datastream can't use ADMID to"
                                       + " point to non-AuditRecord "
                                       + "datastream(s).");
                           }
                       } else {
                           	// the admId is an audit record for the current datastream,
                           	// so put it in the audit records list on the datastream
                           	ds.auditRecordIdList().add(admId);
                           	// also capture the correlation of admId adn dsid.
                           	// In v2.0 we can use this during migration from
                           	// METS to FOXML, specifically we can obtain component ids
                           	// for the audit records by grabbing the ADMIDs off
                           	// of datastreams (METS file element).
							m_auditIdToComponentId.put(admId, ds.DSVersionID);
                       }
                    }
                }
            }
        }
        
		// update audit records to include the id of the component (datastream)
		// that the audit record is about.  METS awkwardness about having files
		// back-reference things that describe them makes this inversion necessary.
		// We want self-standing audit records that identify the datastreams
		// they apply to. 
		Iterator iter=((ArrayList) m_obj.getAuditRecords()).iterator();
		while (iter.hasNext()) {
			AuditRecord au=(AuditRecord) iter.next();
			au.componentID=(String)m_auditIdToComponentId.get(au.id);
		}
		
        // put dissems in obj
        Iterator dissemIter=m_dissems.values().iterator();
        while (dissemIter.hasNext()) {
            Disseminator diss=(Disseminator) dissemIter.next();
            obj.disseminators(diss.dissID).add(diss);
        }
		// FIXME: this should somehow be gathered from the serialization, ideally.
		obj.setOwnerId("fedoraAdmin");
    }

    public void startPrefixMapping(String prefix, String uri) {
        // save a forward and backward hash of namespace prefix-to-uri
        // mapping ... for the entire object.
        m_prefixes.put(uri, prefix);
        m_prefixUris.put(prefix, uri);
        // if we're looking at inline metadata, be sure to save the prefix
        // so we know it's used in that datastream

        if (m_inXMLMetadata) {
            if (!m_dsPrefixes.contains(prefix)) {
                    m_dsPrefixes.add(prefix);
            }
        }
    }

    public void startElement(String uri, String localName, String qName,
            Attributes a) throws SAXException {
        if (uri.equals(M) && !m_inXMLMetadata) {
            // a new mets element is starting
            if (localName.equals("mets")) {
                m_rootElementFound=true;
                m_obj.setPid(grab(a, M, "OBJID"));
                m_obj.setLabel(grab(a, M, "LABEL"));
                m_obj.setContentModelId(grab(a, M, "PROFILE"));
                String objType=grab(a, M, "TYPE");
                if (objType==null || objType.equals("")) { objType="FedoraObject"; }
                if (objType.equalsIgnoreCase("FedoraBDefObject")) {
                    m_obj.setFedoraObjectType(DigitalObject.FEDORA_BDEF_OBJECT);
                } else if (objType.equalsIgnoreCase("FedoraBMechObject")) {
                    m_obj.setFedoraObjectType(DigitalObject.FEDORA_BMECH_OBJECT);
                } else {
                    m_obj.setFedoraObjectType(DigitalObject.FEDORA_OBJECT);
                }
            } else if (localName.equals("metsHdr")) {
                m_obj.setCreateDate(DateUtility.convertStringToDate(
                        grab(a, M, "CREATEDATE")));
                m_obj.setLastModDate(DateUtility.convertStringToDate(
                        grab(a, M, "LASTMODDATE")));
                m_obj.setState(grab(a, M, "RECORDSTATUS"));
            } else if (localName.equals("amdSec")) {
                m_dsId=grab(a, M, "ID");
                m_dsState=grab(a, M, "STATUS");
            } else if (localName.equals("dmdSecFedora")) {
                m_dsId=grab(a, M, "ID");
                m_dsState=grab(a, M, "STATUS");
            } else if (localName.equals("techMD") || localName.equals("descMD")
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
                if (localName.equals("descMD")) {
                    m_dsMDClass=DatastreamXMLMetadata.DESCRIPTIVE;
                }
                String dateString=grab(a, M, "CREATED");
                if (dateString!=null && !dateString.equals("")){
					m_dsCreateDate=
						DateUtility.convertStringToDate(dateString);
                }
            } else if (localName.equals("mdWrap")) {
                m_dsInfoType=grab(a, M, "MDTYPE");
				m_dsOtherInfoType=grab(a, M, "OTHERMDTYPE");
                m_dsLabel=grab(a, M, "LABEL");
                m_dsMimeType=grab(a, M, "MIMETYPE");
            } else if (localName.equals("xmlData")) {
                m_dsXMLBuffer=new StringBuffer();
                m_dsFirstElementBuffer=new StringBuffer();
                m_dsPrefixes=new ArrayList();
                m_xmlDataLevel=0;
                m_inXMLMetadata=true;
                m_firstInlineXMLElement=true;
            } else if (localName.equals("fileGrp")) {
                m_dsId=grab(a, M, "ID");
                // reset the values for the next file
                m_dsVersId="";
                m_dsCreateDate=null;
                m_dsMimeType="";
                m_dsControlGrp="";
                m_dsDmdIds=null;
                m_dsState=grab(a,M,"STATUS");
                m_dsSize=-1;
            } else if (localName.equals("file")) {
                m_dsVersId=grab(a, M, "ID");
				String dateString=grab(a, M, "CREATED");
				if (dateString!=null && !dateString.equals("")){
					m_dsCreateDate=
						DateUtility.convertStringToDate(dateString);
				}
                m_dsMimeType=grab(a,M,"MIMETYPE");
                m_dsControlGrp=grab(a,M,"OWNERID");
                String ADMID=grab(a,M,"ADMID");
                if ((ADMID!=null) && (!"".equals(ADMID))) {
                    // remember admids for when we're finished...
                    // we can't reliably determine yet whether they are
                    // metadata refs or audit record refs, since we can't
                    // rely on having gone through all audit records yet.
                    ArrayList al=new ArrayList();
                    if (ADMID.indexOf(" ")!=-1) {
                        String[] admIds=ADMID.split(" ");
                        for (int idi=0; idi<admIds.length; idi++) {
                            al.add(admIds[idi]);
                        }
                    } else {
                        al.add(ADMID);
                    }
                    m_dsAdmIds.put(m_dsVersId, al);
                }
                String DMDID=grab(a,M,"DMDID");
                if ((DMDID!=null) && (!"".equals(DMDID))) {
                    // saved till we're makin' the datastream object
                    if (DMDID.indexOf(" ")!=-1) {
                        m_dsDmdIds=DMDID.split(" ");
                    } else {
                        m_dsDmdIds=new String[] {DMDID};
                    }
                }
                String sizeString=grab(a,M,"SIZE");
                if (sizeString!=null && !sizeString.equals("")) {
                    try {
                        m_dsSize=Long.parseLong(sizeString);
                    } catch (NumberFormatException nfe) {
                        throw new SAXException("If specified, a datastream's "
                                + "SIZE attribute must be an xsd:long.");
                    }
                }
                // inside a "file" element, it's either going to be
                // FLocat (a reference) or FContent (inline)
            } else if (localName.equals("FLocat")) {
                m_dsLabel=grab(a,XLINK_NAMESPACE,"title");
                String dsLocation=grab(a,XLINK_NAMESPACE,"href");
                if (dsLocation==null || dsLocation.equals("")) {
                    throw new SAXException("xlink:href must be specified in FLocat element");
                }

                if (m_dsControlGrp.equalsIgnoreCase("E") ||
                    m_dsControlGrp.equalsIgnoreCase("R") ) {
                    	
				  // URL FORMAT VALIDATION for dsLocation:
				  // make sure we have a properly formed URL (must have protocol)
                  try {
                  	  m_dsLocationURL=new URL(dsLocation);
				  } catch (MalformedURLException murle) {
					 // or else it must be a Fedora relative repository URL
					  if (!dsLocation.startsWith(
							DOTranslationUtility.s_relativeGetPattern.pattern()) &&
						  !dsLocation.startsWith(
							DOTranslationUtility.s_relativeSearchPattern.pattern())) {
						  throw new SAXException("Datastream's xlink:href is malformed URL."
							  + " Missing protocol or invalid relative URL."
							  + " Relative repository URLs must start with 'fedora/get'"
							  + " or 'fedora/search' with NO leading slash.\n " 
							  + " URL is: " + dsLocation);
					  }
				  }
				  // system will set dsLocationType for E and R datastreams...
				  m_dsLocationType="URL";
				  m_dsInfoType="DATA";
				  m_dsLocation=dsLocation;
				  instantiateDatastream(new DatastreamReferencedContent());
                } else if (m_dsControlGrp.equalsIgnoreCase("M")) {
				  // URL FORMAT VALIDATION for dsLocation:
				  // For Managed Content the URL is only checked when we are parsing a
				  // a NEW ingest file because the URL is replaced with an internal identifier
				  // once the repository has sucked in the content for storage.
                  if (m_obj.isNew())
                  {
                      try {
						  // it must have a protocol
                      	  m_dsLocationURL=new URL(dsLocation);
					  } catch (MalformedURLException murle) {
						 // or else it must be a Fedora relative repository URL
						  if (!dsLocation.startsWith(
								DOTranslationUtility.s_relativeGetPattern.pattern()) &&
							  !dsLocation.startsWith(
								DOTranslationUtility.s_relativeSearchPattern.pattern())) {
							  throw new SAXException("Datastream's xlink:href is malformed URL."
								+ " Missing protocol or invalid relative URL."
								+ " Relative repository URLs must start with 'fedora/get'"
								+ " or 'fedora/search' with NO leading slash.\n " 
								+ " URL is: " + dsLocation);
						  }
					  }
                  }
				  m_dsLocationType="INTERNAL_ID";
				  m_dsInfoType="DATA";
				  m_dsLocation=dsLocation;
				  instantiateDatastream(new DatastreamManagedContent());
                }
            } else if (localName.equals("FContent")) {
                // signal that we want to suck it in
                m_readingContent=true;
            } else if (localName.equals("structMap")) {
                // this is a component of a disseminator.  here we assume the rest
                // of the disseminator's information will be seen later, so we
                // construct a new Disseminator object to hold the structMap...
                // and later, the other info
                //
                // Building up a global map of Disseminators, m_dissems,
                // keyed by bindingmap ID.
                //
                if (grab(a,M,"TYPE").equals("fedora:dsBindingMap")) {
                    String bmId=grab(a,M,"ID");
                    if ( (bmId==null) || (bmId.equals("")) ) {
                        throw new SAXException("structMap with TYPE fedora:dsBindingMap must specify a non-empty ID attribute.");
                    } else {
                        Disseminator diss=new Disseminator();
                        diss.dsBindMapID=bmId;
                        m_dissems.put(bmId,diss);
                        m_diss=diss;
                        m_diss.dsBindMap=new DSBindingMap();
                        m_diss.dsBindMap.dsBindMapID=bmId;
                        m_indiv=false; // flag that we're not looking at inner part yet
                    }
                } else {
                    throw new SAXException("StructMap must have TYPE fedora:dsBindingMap");
                }
            } else if (localName.equals("div")) {
                if (m_indiv) {
                    // inner part of structmap
                    DSBinding binding=new DSBinding();
                    if (m_diss.dsBindMap.dsBindings==null) {
                        // none yet.. create array of size one
                        DSBinding[] bindings=new DSBinding[1];
                        m_diss.dsBindMap.dsBindings=bindings;
                        m_diss.dsBindMap.dsBindings[0]=binding;
                    } else {
                        // need to expand the array size by one,
                        // and do an array copy.
                        int curSize=m_diss.dsBindMap.dsBindings.length;
                        DSBinding[] oldArray=m_diss.dsBindMap.dsBindings;
                        DSBinding[] newArray=new DSBinding[curSize+1];
                        for (int i=0; i<curSize; i++) {
                            newArray[i]=oldArray[i];
                        }
                        newArray[curSize]=binding;
                        m_diss.dsBindMap.dsBindings=newArray;
                    }
                    // now populate 'binding' values...we'll have
                    // everything at this point except datastreamID...
                    // that comes as a child: <fptr FILEID="DS2"/>
                    binding.bindKeyName=grab(a,M,"TYPE");
                    binding.bindLabel=grab(a,M,"LABEL");
                    binding.seqNo=grab(a,M,"ORDER");
                } else {
                    m_indiv=true;
                    // first (outer div) part of structmap
                    // TYPE="test:2" LABEL="DS Binding Map for UVA Standard Image mechanism"
                    m_diss.dsBindMap.dsBindMechanismPID=grab(a,M,"TYPE");
                    m_diss.dsBindMap.dsBindMapLabel=grab(a,M,"LABEL");
                }
            } else if (localName.equals("fptr")) {
                // assume we're inside the inner div... that's the
                // only place the fptr element is valid.
                DSBinding binding=m_diss.dsBindMap.dsBindings[
                        m_diss.dsBindMap.dsBindings.length-1];
                binding.datastreamID=grab(a,M,"FILEID");
            } else if (localName.equals("behaviorSec")) {
                // looks like we're in a disseminator... it should be in the
                // hash by now because we've already gone through structmaps
                // ...keyed by structmap id... remember the id (group id)
                // so we can put it in when parsing serviceBinding
                m_dissemId=grab(a,M,"ID");
                m_dissemState=grab(a,M,"STATUS");
            } else if (localName.equals("serviceBinding")) {
                // remember the structId so we can grab the right dissem
                // when parsing children
                m_structId=grab(a,M,"STRUCTID");
                // grab the disseminator associated with the provided structId
                Disseminator dissem=(Disseminator) m_dissems.get(m_structId);
                // plug known items in..
                dissem.dissID=m_dissemId;
                dissem.dissState=m_dissemState;
                // then grab the new stuff for the dissem for this element, and
                // put it in.
                dissem.dissVersionID=grab(a,M,"ID");
                dissem.bDefID=grab(a,M,"BTYPE");
                dissem.dissCreateDT=DateUtility.convertStringToDate(grab(a,M,"CREATED"));
                dissem.dissLabel=grab(a,M,"LABEL");
            } else if (localName.equals("interfaceMD")) {
                // interfaceDef LABEL="UVA Std Image Behavior Definition"
                // LOCTYPE="URN" xlink:href="test:1"/>
                Disseminator dissem=(Disseminator) m_dissems.get(m_structId);
                // already have the id from containing element, just need label
                dissem.bDefLabel=grab(a,M,"LABEL");
            } else if (localName.equals("serviceBindMD")) {
                Disseminator dissem=(Disseminator) m_dissems.get(m_structId);
                dissem.bMechLabel=grab(a,M,"LABEL");
                dissem.bMechID=grab(a,XLINK_NAMESPACE,"href");
            }
        } else {
            if (m_inXMLMetadata) {
                // must be in xmlData... just output it, remembering the number
                // of METS:xmlData elements we see
                String prefix=(String) m_prefixes.get(uri);
                if (m_firstInlineXMLElement) {
                // deal with root element... buffer it separately so we can 
                // add namespace stuff after it's known
                    m_firstInlineXMLElement=false;
                    m_dsFirstElementBuffer.append('<');
                    if (prefix!=null && !prefix.equals("")) {
                        if (!m_dsPrefixes.contains(prefix)) {
                            m_dsPrefixes.add(prefix);
                        }
                        m_dsFirstElementBuffer.append(prefix);
                        m_dsFirstElementBuffer.append(':');
                    }
                    m_dsFirstElementBuffer.append(localName);
                } else {
                // deal with non-root elements
                    m_dsXMLBuffer.append('<');
                    if (prefix!=null && !prefix.equals("")) {
                        if (!m_dsPrefixes.contains(prefix)) {
                            if (!"".equals(prefix)) {
                                m_dsPrefixes.add(prefix);
                            }
                        }
                        m_dsXMLBuffer.append(prefix);
                        m_dsXMLBuffer.append(':');
                    }
                    m_dsXMLBuffer.append(localName);
                }
                // deal with attributes
                for (int i=0; i<a.getLength(); i++) {
                    m_dsXMLBuffer.append(' ');
                    String aPrefix=(String) m_prefixes.get(a.getURI(i));
                    if (aPrefix!=null && !aPrefix.equals("")) {
                        if (!m_dsPrefixes.contains(aPrefix)) {
                            if (!"".equals(aPrefix)) {
                                m_dsPrefixes.add(aPrefix);
                            }
                        }
                        m_dsXMLBuffer.append(aPrefix);
                        m_dsXMLBuffer.append(':');
                    }
                    m_dsXMLBuffer.append(a.getLocalName(i));
                    m_dsXMLBuffer.append("=\"");
                    // re-encode decoded standard entities (&, <, >, ", ')
                    m_dsXMLBuffer.append(StreamUtility.enc(a.getValue(i)));
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
							|| (localName.equals("componentID"))
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
            if (m_auditBuffer!=null) {
                m_auditBuffer.append(ch, start, length);
            } else {
                // since this data is encoded straight back to xml,
                // we need to make sure special characters &, <, >, ", and '
                // are re-converted to the xml-acceptable equivalents.
                StreamUtility.enc(ch, start, length, m_dsXMLBuffer);
            }
        } else if (m_readingContent) {
            // append it to something...
        }
    }

    public void endElement(String uri, String localName, String qName) {
        m_readingContent=false;
        if (m_inXMLMetadata) {
            if (uri.equals(M) && localName.equals("xmlData")
                    && m_xmlDataLevel==0) {
                // finished all xml metadata for this datastream
                if (m_dsId.equals("FEDORA-AUDITTRAIL")) {
                    // we've been looking at an audit trail... set audit record
                    AuditRecord a=new AuditRecord();
                    // In METS each audit record is in its own <digiprovMD>
                    // element within an <amdSec>.  So, pick up the XML ID
                    // of the <digiprovMD> element for the audit record id.
                    // This amdSec is treated like a datastream, and each 
                    // digiprovMD is a version, so id was parsed into dsVersId.
                    a.id=m_dsVersId; 
                    a.processType=m_auditProcessType;
                    a.action=m_auditAction;
                    a.componentID=m_auditComponentID;
                    a.responsibility=m_auditResponsibility;
                    a.date=DateUtility.convertStringToDate(m_auditDate);
                    a.justification=m_auditJustification;
                    System.out.println("Adding Audit records to object in METS deserializer...");
                    System.out.println("Audit id = " + a.id);
                    m_obj.getAuditRecords().add(a);
					m_inXMLMetadata=false; // other stuff is re-initted upon
										   // startElement for next xml metadata
										   // element
                } else {
                    // create the right kind of datastream and add it to m_obj
                    for (int i=0; i<m_dsPrefixes.size(); i++) {
                        // now finish writing to m_dsFirstElementBuffer, a series of strings like
                        // ' xmlns:PREFIX="URI"'
                        String pfx=(String) m_dsPrefixes.get(i);
                        String pfxUri=(String) m_prefixUris.get(pfx);
                        if (!pfx.equals("")) {
                            m_dsFirstElementBuffer.append(" xmlns:");
                            m_dsFirstElementBuffer.append(pfx);
                        } else {
                            m_dsFirstElementBuffer.append(" xmlns");
                        }
                        m_dsFirstElementBuffer.append("=\"");
                        m_dsFirstElementBuffer.append(pfxUri);
                        m_dsFirstElementBuffer.append("\"");
                    }
					DatastreamXMLMetadata ds=new DatastreamXMLMetadata();
					instantiateXMLDatastream(ds);
					m_inXMLMetadata=false; // other stuff is re-initted upon
										   // startElement for next xml metadata
										   // element
                }
            } else {
                // finished an element in xml metadata... print end tag,
                // subtracting the level of METS:xmlData elements we're at
                // if needed
                m_dsXMLBuffer.append("</");
                String prefix=(String) m_prefixes.get(uri);
                if (prefix!=null && !prefix.equals("")) {
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
					} else if (localName.equals("componentID")) {
						m_auditComponentID=m_auditBuffer.toString();
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
    
	private void instantiateDatastream(Datastream ds) throws SAXException {

		// set datastream variables with values grabbed from the SAX parse     	  	
		ds.DatastreamID=m_dsId;
		ds.DatastreamURI=""; // FOXML only. In METS it's null;
		ds.DSVersionable=""; // FOXML only. In METS it's null;
		ds.DSFormatURI="";   // FOXML only. In METS it's null;
		ds.DSVersionID=m_dsVersId;
		ds.DSLabel=m_dsLabel;
		ds.DSCreateDT=m_dsCreateDate;
		ds.DSMIME=m_dsMimeType;
		ds.DSControlGrp=m_dsControlGrp;
		ds.DSState=m_dsState;
		ds.DSLocation=m_dsLocation;
		ds.DSLocationType=m_dsLocationType;
		ds.DSInfoType=m_dsInfoType;
		
		// Normalize the dsLocation for the deserialization context
		ds.DSLocation=
			(DOTranslationUtility.normalizeDSLocationURLs(
				m_obj.getPid(), ds, m_transContext)).DSLocation;
		
		
		// LOOK! if the query behavior this deserializer instance was activated 
		// in the constructor,  then we will obtain the datastreams's
		// content stream to obtain its size and set the ds size attribute
		// (done within the ds.getContentStream method implementation).
		if (m_queryBehavior!=QUERY_NEVER) {
			if ((m_queryBehavior==QUERY_ALWAYS)	
				|| (m_dsMimeType==null)
				|| (m_dsMimeType.equals(""))
				|| (m_dsSize==-1)) {
				try {
					InputStream in=ds.getContentStream();
				} catch (StreamIOException e) {
					throw new SAXException("Error getting datastream content"
						+ " for setting ds size (ds.getContentStream)"
						+ " during SAX parse.");
				}
		  }
		}
		
		// LOOK! METS SPECIFIC: 
		// For E or R datastreams, collect the list of other 
		// datastream ids that are descriptive metadata about this datastream.  
		// This is supported in METS via an attribute on METS
		// file element that allows IDREFS for "about" relationships
		// on the file element.
		if (m_dsControlGrp.equalsIgnoreCase("E") ||
			m_dsControlGrp.equalsIgnoreCase("R") )
		{
			if (m_dsDmdIds!=null) {
			  for (int idi=0; idi<m_dsDmdIds.length; idi++) {
				((DatastreamReferencedContent)ds).metadataIdList().add(m_dsDmdIds[idi]);
			  }
			}
		}
		
		// FINALLY! add the datastream to the digital object instantiation
		m_obj.datastreams(m_dsId).add(ds);	
	}

	private void instantiateXMLDatastream(DatastreamXMLMetadata ds) {
		
		// set the attrs common to all datastream versions
		ds.DatastreamID=m_dsId;
		ds.DatastreamURI=""; // FOXML only. In METS it's null;
		ds.DSVersionable=""; // FOXML only. In METS it's null;
		ds.DSFormatURI="";   // FOXML only. In METS it's null;
		ds.DSVersionID=m_dsVersId;
		ds.DSLabel=m_dsLabel;
		ds.DSCreateDT=m_dsCreateDate;
		if (m_dsMimeType==null || m_dsMimeType.equals("")) {
			ds.DSMIME="text/xml";
		} else {
			ds.DSMIME=m_dsMimeType;
		}
		// set the attrs specific to datastream version
		ds.DSControlGrp="X";
		ds.DSState=m_dsState;
		ds.DSLocation=m_obj.getPid() + "+" + m_dsId + "+" + m_dsVersId;
		ds.DSLocationType=m_dsLocationType;
		ds.DSInfoType=m_dsInfoType; // METS only
		ds.DSMDClass=m_dsMDClass;   // METS only
		
		// now set the xml content stream itself...
		try {
			String combined=m_dsFirstElementBuffer.toString() + m_dsXMLBuffer.toString();
		
			// Relative Repository URL processing... 
			// For selected inline XML datastreams look for relative repository URLs
			// and make them absolute.
			if ( m_obj.getFedoraObjectType()==DigitalObject.FEDORA_BMECH_OBJECT &&
				 (m_dsId.equals("SERVICE-PROFILE") || m_dsId.equals("WSDL")) ) {
					ds.xmlContent=
						(DOTranslationUtility.normalizeInlineXML(
							combined, m_transContext))
							.getBytes(m_characterEncoding);
			} else {
				ds.xmlContent=combined.getBytes(m_characterEncoding);
			}
			//LOOK! this sets bytes, not characters.  Do we want to set this?
			ds.DSSize=ds.xmlContent.length;
		} catch (Exception uee) {
			System.out.println("Error processing inline xml content in SAX parse: " 
				+ uee.getMessage());
		}				
		// FINALLY! add the xml datastream to the digitalObject
		m_obj.datastreams(m_dsId).add(ds);  	
	}
	
    private static String grab(Attributes a, String namespace,
            String elementName) {
        String ret=a.getValue(namespace, elementName);
        if (ret==null) {
            ret=a.getValue(elementName);
        }
		// set null attribute value to empty string since it's
		// generally helpful in the code to avoid null pointer exception
		// when operations are performed on attributes values.
		if (ret==null) {
			ret="";
		}
        return ret;
    }

	private void initialize(){		
		//NOTE: variables that are commented out exist in FOXML but not METS
		
		// temporary variables and state variables
		m_rootElementFound=false;
		//m_objPropertyName=null;
		//m_readingBinaryContent=false; // indicates reading base64-encoded content
		m_firstInlineXMLElement=false;
		m_inXMLMetadata=false;

		// temporary variables for processing datastreams		
		m_dsId="";
		//m_dsURI="";
		//m_dsVersionable="";
		m_dsVersId="";
		m_dsCreateDate=null;
		m_dsState="";
		//m_dsFormatURI="";
		m_dsSize=-1;
		m_dsLocationType="";
		m_dsLocationURL=null;
		m_dsLocation="";
		m_dsMimeType="";
		m_dsControlGrp="";
		m_dsInfoType="";
		m_dsOtherInfoType="";
		m_dsMDClass=0;
		m_dsLabel="";
		m_dsXMLBuffer=null;
		m_prefixes=new HashMap();
		m_prefixUris=new HashMap();
		m_dsAdmIds=new HashMap();
		m_dsDmdIds=null;
		
		// temporary variables for processing disseminators
		m_diss=null;
		m_dissems=new HashMap();
	
		// temporary variables for processing audit records
		m_auditBuffer=null;
		m_auditComponentID="";
		m_auditProcessType="";
		m_auditAction="";
		m_auditResponsibility="";
		m_auditDate="";
		m_auditJustification="";
		m_auditIdToComponentId=new HashMap();
	}
/*   
	private void initialize(){		
		//NOTE: variables that are commented out exist in FOXML but not METS
		
		// temporary variables and state variables
		m_rootElementFound=false;
		//m_objPropertyName=null;
		//m_readingBinaryContent=false; // indicates reading base64-encoded content
		m_firstInlineXMLElement=false;
		m_inXMLMetadata=false;

		// temporary variables for processing datastreams		
		m_dsId=null;
		//m_dsURI=null;
		//m_dsVersionable=null;
		m_dsVersId=null;
		m_dsCreateDate=null;
		m_dsState=null;
		//m_dsFormatURI=null;
		m_dsSize=-1;
		m_dsLocationType=null;
		m_dsLocationURL=null;
		m_dsLocation=null;
		m_dsMimeType=null;
		m_dsControlGrp=null;
		m_dsInfoType=null;
		m_dsOtherInfoType=null;
		m_dsMDClass=0;
		m_dsLabel=null;
		m_dsXMLBuffer=null;
		m_prefixes=new HashMap();
		m_prefixUris=new HashMap();
		m_dsAdmIds=new HashMap();
		m_dsDmdIds=null;
		
		// temporary variables for processing disseminators
		m_diss=null;
		m_dissems=new HashMap();
	
		// temporary variables for processing audit records
		m_auditBuffer=null;
		m_auditComponentID=null;
		m_auditProcessType=null;
		m_auditAction=null;
		m_auditResponsibility=null;
		m_auditDate=null;
		m_auditJustification=null;
		m_auditIdToComponentId=new HashMap();
	}
	*/
}
