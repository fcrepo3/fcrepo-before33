package fedora.server.validation;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.RepositoryConfigurationException;
import fedora.server.errors.StreamIOException;
import fedora.common.Constants;

import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * <p><b>Title:</b> RelsExtValidator.java</p>
 * <p><b>Description: This class will validate relationship metadata that
 * may exist in a digital object.  The validator will SAX parse the content 
 * of the RELS-EXT datastream which must be an RDF stream that asserts 
 * object-to-object relationships for a digital object.  The validator will
 * enforce the following restrictions on the RDF stream:
 * 
 *   1. The RDF must follow a prescribed RDF/XML authoring style where 
 *      there is ONE subject encoded as an RDF <Description> with an
 *      RDF 'about' attribute containing a digital object URI.
 *      The sub-elements are the relationship properties of the subject.
 *      Each relationship refers to another digital object URI via an 
 *      RDF 'resource' attribute.  Relationship assertions can be from
 *      the default Fedora relationship ontology, or from other namespaces.
 *      For example: 
 *         <rdf:Description about="info:fedora/demo:5">
 *             <fedora:isMemberOfCollection resource="info:fedora/demo:100">
 *             <nsdl:isAugmentedBy resource="info:fedora/demo:333">
 *         </rdf:Description>
 * 
 *   2.  There must be only ONE RDF <Description> in the RELS-EXT datastream.
 * 
 *   3.  There must be NO nesting of assertions.  In terms of XML depth, 
 *       the RDF root element is considered depth of 0.  Then, the RDF
 *       <Description> must be at depth of 1, and the relationship properties 
 *       must exist at depth of 2.  That's it.
 * 
 *   4.  The RDF 'about' attribute of the RDF <Description> must be the URI
 *       of the digital object in which the RELS-EXT datastream resides.  This
 *       means that all relationships are FROM "this" object to other objects.
 * 
 *   5.  The RDF 'resource' attribute of a relationship assertion
 *       must be the URI of another Fedora digital object.  This is because 
 *       the RELS-EXT datastream is for object-to-object relationships.
 * 
 *   6.  The RDF 'resource' attribute of a relationship assertion must NOT
 *       be the URI of the digital object that is the subject of the
 *       relationships.  In other words, NO SELF-REFERENTIAL relationships.
 * 
 *   7.  There must NOT be any assertion of properties from the DC namespace or
 *       from the Fedora object properties namespaces (model and view).  This is
 *       because these assertions exist elsewhere in a Fedora digital object and
 *       we do not want duplication.  The RELS-EXT datasream is reserved for
 *       relationship metadata.</b> </p>
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
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class RelsExtValidator
        extends DefaultHandler {

	// Namespace URIs
    private final static String F="info:fedora/fedora-system:def/foxml#";
	private final static String OAIDC="http://www.openarchives.org/OAI/2.0/oai_dc/";
	private final static String FMODEL=fedora.common.Constants.MODEL.uri;
	private final static String FVIEW=fedora.common.Constants.VIEW.uri;
	private final static String RDF=fedora.common.Constants.RDF.uri;
	private final static String DC=fedora.common.Constants.DC.uri;

	// state variables
	private String m_characterEncoding;
	private String m_doURI;
	private boolean m_rootRDFFound;
	private boolean m_descriptionFound;
	private int m_depth;
    
	// SAX parser
	private SAXParser m_parser;


    public RelsExtValidator(String characterEncoding, boolean validate)
            throws FactoryConfigurationError, ParserConfigurationException,
            SAXException, UnsupportedEncodingException {
            	
        m_characterEncoding=characterEncoding;
        StringBuffer buf=new StringBuffer();
        buf.append("test");
        byte[] temp=buf.toString().getBytes(m_characterEncoding);
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setValidating(validate);
        spf.setNamespaceAware(true);
        m_parser=spf.newSAXParser();
    }

    public RelsExtValidator getInstance()
            throws RepositoryConfigurationException {
        try {
            return new RelsExtValidator("UTF-8", true);
        } catch (Exception e) {
            throw new RepositoryConfigurationException("RelsExtValidator:"
            	+ "Error instantiating RELS-EXT datastream validator:"
                + e.getClass().getName() + " : " + e.getMessage());
        }
    }

    public void deserialize(InputStream relsDS, String doURI)
            throws ObjectIntegrityException, StreamIOException, SAXException {
            	
        if (fedora.server.Debug.DEBUG) System.out.println("Deserializing RELS-EXT...");
		m_rootRDFFound=false;
		m_descriptionFound=false;
		m_depth=0;
        m_doURI = doURI;
        try {
            m_parser.parse(relsDS, this);
        } catch (IOException ioe) {
            throw new StreamIOException("RelsExtValidator:"
            	+ " low-level stream IO problem occurred"
            	+ " while SAX parsing RELS-EXT datastream.");
        } catch (SAXException se) {
			throw new SAXException(se.getMessage());
			//throw new ObjectIntegrityException(se.getMessage());
        }
        if (fedora.server.Debug.DEBUG) System.out.println("Just finished parse.");      
    }

    public void startElement(String nsURI, String localName, String qName,
            Attributes a) throws SAXException {

        if (nsURI.equals(RDF) && localName.equalsIgnoreCase("RDF")) {
            m_rootRDFFound=true;
        } else if (m_rootRDFFound) {
        	if (nsURI.equals(RDF) && localName.equalsIgnoreCase("Description")) {
        		if (!m_descriptionFound) {
					m_descriptionFound=true;
					m_depth++;
					checkDepth(m_depth, qName);
					checkAboutURI(grab(a, RDF, "about"));
        		} else {
					throw new SAXException("RelsExtValidator:"
						+ " Only ONE RDF <Description> element is allowed"
						+ " in the RELS-EXT datastream.");
        		}
        	} else if (m_descriptionFound) {
					m_depth++;
					checkDepth(m_depth, qName);
					checkBadAssertion(nsURI, localName, qName);
					checkResourceURI(grab(a, RDF, "resource"), qName);       		
        	} else {
				throw new SAXException("RelsExtValidator:"
					+ " Invalid element " + localName 
					+ " found in the RELS-EXT datastream.\n"
					+ " Relationship assertions must be built"
					+ " upon an RDF <Description> element.");
        	}
        } else {
			throw new SAXException("RelsExtValidator:"
				+ " The 'RDF' root element was not found " 
				+ " in the RELS-EXT datastream.\n"
				+ " Relationship metadata must be encoded using RDF/XML.");
        }
	}

	public void endElement(String nsURI, String localName, String qName) {
    	
		if (m_rootRDFFound && m_descriptionFound) {
			m_depth--;
		}
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
	
	/**
	 * checkDepth: checks that there is NO nesting of relationship assertions.  
	 * In terms of XML depth, the RDF root element is considered depth of 0.
	 * Then, the RDF <Description> must be at depth of 1, and the relationship
	 * properties must exist at depth of 2.  That's it.
	 * @param depth - the depth of the XML element being evaluated
	 * @param qName - the name of the relationship property being evaluated
	 * @throws SAXException
	 */
	private void checkDepth(int depth, String qName) throws SAXException {

		if (depth > 2) {
			throw new SAXException("RelsExtValidator:" 
				+ " The RELS-EXT datastream has improper"
				+ " nesting in its relationship assertions.\n"
				+ " (The XML depth is " + depth
				+ " which must not exceed a depth of 2.\n"
				+ " The root <RDF> element should be level 0,"
				+ " the <Description> element should be level 1,"
				+ " and relationship elements should be level 2.)");
		}
	}
	
	/**
	 * checkBadAssertion: checks that there are NOT be any assertions of 
	 * properties from the DC or Fedora properties namespaces.  This is 
	 * because these assertions exist elsewhere in a Fedora digital object 
	 * and we do not want duplication.
	 * @param nsURI - the namespace URI of the property being evaluated
	 * @param localName - the local name of the property being evaluated
	 * @param qName - the qualified name of the property being evaluated
	 * @throws SAXException
	 */
	private void checkBadAssertion(String nsURI, String localName, String qName) 
		throws SAXException {

		if (nsURI.equals(DC) || nsURI.equals(OAIDC)) {
			throw new SAXException("RelsExtValidator:"
				+ " The RELS-EXT datastream has improper"
				+ " relationship assertion: " + qName + ".\n"
				+ " No Dublin Core assertions allowed"
				+ " in Fedora relationship metadata.");
		} else if (nsURI.equals(FMODEL) || nsURI.equals(FVIEW)) {
			throw new SAXException("RelsExtValidator:"
				+ " The RELS-EXT datastream has improper"
				+ " relationship assertion: " + qName + ".\n"
				+ " Relationship metadata cannot contain"
				+ " assertions from the Fedora object properties" 
				+ " namespaces.");
		}
	}

	/**
	 * checkAboutURI: ensure that the RDF <Description> is about the 
	 * digital object that contains the RELS-EXT datastream, since 
	 * the REL-EXT datastream is only supposed to capture relationships 
	 * about "this" digital object.
	 * @param aboutURI - the URI value of the RDF 'about' attribute
	 * @throws SAXException
	 */	
	private void checkAboutURI(String aboutURI) throws SAXException {

		if (!m_doURI.equals(aboutURI)) {
			throw new SAXException("RelsExtValidator:" 
				+ " The RELS-EXT datastream refers to"
				+ " an improper URI in the 'about' attribute of the"
				+ " RDF <Description> element.\n"
				+ " The URI must be that of the digital object"
				+ " in which the RELS-EXT datastream resides"
				+ " (" + m_doURI + ").");
		}		
	}

	/**
	 * checkResourceURI: ensure that relationship assertions within 
	 * the RDF <Description> refer to proper Fedora object URIs of
	 * OTHER Fedora digital objects.
	 * @param resourceURI - the URI value of the RDF 'resource' attribute
	 * @param relName - the name of the relationship property being evaluated
	 * @throws SAXException
	 */		
	private void checkResourceURI(String resourceURI, String relName) throws SAXException {

		if (resourceURI==null || resourceURI.equals("")){
			throw new SAXException("RelsExtValidator:"
				+ " Error in relationship '" + relName + "'"
				+ " within the RELS-EXT datastream.\n"
				+ " All properties (sub-elements) of RDF <Description>"
				+ " must be valid relationship assertions.\n"
				+ " These relationships MUST have an RDF 'resource'"
				+ " attribute whose value is the URI of a related"
				+ " digital object.");
		}

		if (resourceURI.equals(m_doURI)) {
			throw new SAXException("RelsExtValidator:"
				+ " Error in relationship '" + relName + "'."
				+ " The RELS-EXT datastream asserts a self-referential"
				+ " relationship.\n"
				+ " The RDF 'resource' attribute cannot contain the"
				+ " URI of the digital object that the relationships"
				+ " are about.\n"
				+ " Relationships within one object must point"
				+ " to the URIs of OTHER digital objects.");	
		} 
				
		String[] partsOfURI = resourceURI.split("/");
		if (partsOfURI.length!=2) {
			throw new SAXException("RelsExtValidator:"
				+ " Error in relationship '" + relName + "'."
				+ " The RELS-EXT datastream has an improper URI specified"
				+ " in the RDF 'resource' attribute.\n"
				+ " The URI must contain a digital object URI that begins"
				+ " with 'info:fedora', followed by a forward slash ('/'),\n"
				+ " followed by a valid Fedora PID." 
				+ " (e.g., info:fedora/demo:100).");	
		} 
		
		if (!partsOfURI[0].equals("info:fedora")) {
			throw new SAXException("RelsExtValidator:"
				+ " Error in relationship '" + relName + "'."
				+ " The RELS-EXT datastream has an improper URI specified"
				+ " in the RDF 'resource' attribute.\n"
				+ " The URI must use the 'info:' URI scheme for Fedora"
				+ " (e.g., info:fedora/demo:100).");	
		} 
		
		if (partsOfURI[1].split(":").length!=2) {
			throw new SAXException("RelsExtValidator:"
				+ " Error in relationship '" + relName + "'."
				+ " The RELS-EXT datastream has an improper URI specified"
				+ " in the RDF 'resource' attribute.\n"
				+ " The URI must contain a valid Fedora PID with ONE"
				+ " colon (':') separating the Fedora PID namespace from"
				+ " the identifier string." 
				+ " (e.g., info:fedora/demo:100).");	
		} 
	}
}
