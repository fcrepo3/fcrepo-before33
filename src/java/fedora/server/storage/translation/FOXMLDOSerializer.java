package fedora.server.storage.translation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import fedora.server.Server;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamWriteException;
import fedora.server.storage.types.AuditRecord;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamContent;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.DSBinding;
import fedora.server.utilities.StreamUtility;

/**
 *
 * <p><b>Title:</b> FOXMLDOSerializer.java</p>
 * <p><b>Description:</b> </p>
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
public class FOXMLDOSerializer
        implements DOSerializer {

	public static final String FOXML_NS="info:fedora/def:foxml";
    public static final String FEDORA_AUDIT_NS="info:fedora/def:audit";
	public static final String FEDORA_DC_NS="http://www.openarchives.org/OAI/2.0/oai_dc/";
	public static final String FEDORA_RELSOUT_NS="info:fedora/def:relation:outer";
    public static final String FOXML_PREFIX="foxml";

    public static final String FOXML_XSD_LOCATION="http://www.fedora.info/definitions/1/0/foxml.xsd";
    public static final String XSI_NS="http://www.w3.org/2001/XMLSchema-instance";

    private String m_fedoraAuditPrefix="audit";
	private String m_fedoraRelsoutPrefix="fro";
    private SimpleDateFormat m_formatter=
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private static Pattern s_localServerUrlStartWithPort; // "http://actual.hostname:8080/"
    private static Pattern s_localServerUrlStartWithoutPort; // "http://actual.hostname/"
    private static Pattern s_localhostUrlStartWithPort; // "http://localhost:8080/"
    private static Pattern s_localhostUrlStartWithoutPort; // "http://localhost/"

    private boolean m_onPort80=false;

    public FOXMLDOSerializer() {
    }

    public DOSerializer getInstance() {
        return new FOXMLDOSerializer();
    }

    public void serialize(DigitalObject obj, OutputStream out, String encoding)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedEncodingException {
        // get the host info in a static var so search/replaces are quicker later
        if (s_localServerUrlStartWithPort==null) {
            String fedoraHome=System.getProperty("fedora.home");
            String fedoraServerHost=null;
            String fedoraServerPort=null;
            if (fedoraHome==null || fedoraHome.equals("")) {
                // if fedora.home is undefined or empty, assume we're testing,
                // in which case the host and port will be taken from system
                // properties
                fedoraServerHost=System.getProperty("fedoraServerHost");
                fedoraServerPort=System.getProperty("fedoraServerPort");
            } else {
                try {
                    Server s=Server.getInstance(new File(fedoraHome));
                    fedoraServerHost=s.getParameter("fedoraServerHost");
                    fedoraServerPort=s.getParameter("fedoraServerPort");
					if (fedoraServerPort.equals("80")) {
					    m_onPort80=true;
					}
                } catch (InitializationException ie) {
                    // can only possibly happen during failed testing, in which
                    // case it's ok to do a System.exit
                    System.err.println("STARTUP ERROR: " + ie.getMessage());
                    System.exit(1);
                }
            }
            s_localServerUrlStartWithPort=Pattern.compile("http://"
                    + fedoraServerHost + ":" + fedoraServerPort + "/");
            s_localServerUrlStartWithoutPort=Pattern.compile("http://"
                    + fedoraServerHost + "/");
            s_localhostUrlStartWithoutPort=Pattern.compile("http://localhost/");
            s_localhostUrlStartWithPort=Pattern.compile("http://localhost:" + fedoraServerPort + "/");
        }
        // now do serialization stuff
        StringBuffer buf=new StringBuffer();
        appendXMLDeclaration(obj, encoding, buf);
        appendRootElementStart(obj, buf);
        appendProperties(obj, buf, encoding);
        appendAudit(obj, buf, encoding);
        appendDatastreams(obj, buf, encoding);
        appendDisseminators(obj, buf);
        appendRootElementEnd(buf);
        writeToStream(buf, out, encoding, true);
    }

    private void appendXMLDeclaration(DigitalObject obj, String encoding,
            StringBuffer buf) {
        buf.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
    }

    private void appendRootElementStart(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
        buf.append("<" + FOXML_PREFIX + ":digitalObject xmlns:" + FOXML_PREFIX + "=\""
                + StreamUtility.enc(FOXML_NS) + "\"\n");
        String indent="           ";
        // make sure XSI_NS is mapped...
        String xsiPrefix=(String) obj.getNamespaceMapping().get(XSI_NS);
        if (xsiPrefix==null) {
            xsiPrefix="fedoraxsi";
            obj.getNamespaceMapping().put(XSI_NS, "fedoraxsi"); // 99.999999999% chance this is unique
        }
        appendNamespaceDeclarations(indent,obj.getNamespaceMapping(),buf);
        // hardcode xsi:schemaLocation to definitive location for such.
        buf.append(indent + xsiPrefix + ":schemaLocation=\"" + 
        	StreamUtility.enc(FOXML_NS) + " "  + 
        	StreamUtility.enc(FOXML_XSD_LOCATION) + "\"\n");
        if (obj.getPid()==null) {
            throw new ObjectIntegrityException("Object must have a pid.");
        }
        buf.append(indent + "PID=\"" + obj.getPid() + "\" ");
		buf.append(indent + "URI=\"" + obj.getURI()+ "\"");
		/*
		buf.append(indent + "STATE=\"" + obj.getState());
		buf.append(indent + "TYPE=\"" + getTypeAttribute(obj));
		buf.append(indent + "CREATED=\"" + m_formatter.format(obj.getCreateDate()));
		buf.append(indent + "MODIFIED=\"" + m_formatter.format(obj.getLastModDate()));
		*/
        buf.append(">\n");
    }

    private void appendNamespaceDeclarations(String prepend, Map URIToPrefix,
            StringBuffer buf) {
        Iterator iter=URIToPrefix.keySet().iterator();
        while (iter.hasNext()) {
            String URI=(String) iter.next();
            String prefix=(String) URIToPrefix.get(URI);
            if (!prefix.equals("")) {
                if (URI.equals(FEDORA_AUDIT_NS)) {
                    m_fedoraAuditPrefix=prefix;
				} else if (URI.equals(FEDORA_RELSOUT_NS)) {
						m_fedoraRelsoutPrefix=prefix;
                } else if (!URI.equals(FOXML_NS)) {
                    buf.append(prepend + "xmlns:" + prefix + "=\""
                            + StreamUtility.enc(URI) + "\"\n");
                }
            }
        }
        buf.append(prepend + "xmlns:" + m_fedoraAuditPrefix + "=\""
                + FEDORA_AUDIT_NS + "\"\n");
		buf.append(prepend + "xmlns:" + m_fedoraRelsoutPrefix + "=\""
				+ FEDORA_RELSOUT_NS + "\"\n");
    }
    
	private void appendProperties(DigitalObject obj, StringBuffer buf, String encoding) 
			throws ObjectIntegrityException {
		buf.append("  <" + FOXML_PREFIX + ":objectProperties>\n");
		buf.append("    <" + FOXML_PREFIX + ":property NAME=\"" + "info:fedora/def:dobj:type" + "\">" 
			+ obj.getFedoraObjectType() + "</" + FOXML_PREFIX + ":property>\n");
		buf.append("    <" + FOXML_PREFIX + ":property NAME=\"" + "info:fedora/def:dobj:state" + "\">" 
			+ obj.getState() + "</" + FOXML_PREFIX + ":property>\n");
		buf.append("    <" + FOXML_PREFIX + ":property NAME=\"" + "info:fedora/def:dobj:label" + "\">" 
			+ obj.getLabel() + "</" + FOXML_PREFIX + ":property>\n");
		buf.append("    <" + FOXML_PREFIX + ":property NAME=\"" + "info:fedora/def:dobj:created" + "\">" 
			+ m_formatter.format(obj.getCreateDate()) + "</" + FOXML_PREFIX + ":property>\n");
		buf.append("    <" + FOXML_PREFIX  + ":property NAME=\"" + "info:fedora/def:dobj:modified" + "\">" 
		+ m_formatter.format(obj.getLastModDate()) + "</" + FOXML_PREFIX + ":property>\n");
		buf.append("    <" + FOXML_PREFIX + ":property NAME=\"" + "info:fedora/def:dobj:cmodel" + "\">" 
			+ obj.getContentModelId() + "</" + FOXML_PREFIX + ":property>\n");	
		Iterator iter = obj.getExtProperties().keySet().iterator();
		while (iter.hasNext()){
			String name = (String)iter.next();
			buf.append("    <" + FOXML_PREFIX + ":extproperty NAME=\"" + name 
				+ "\">" + obj.getExtProperty(name) + "</" + FOXML_PREFIX + ":extproperty>\n");
		}
		buf.append("  </" + FOXML_PREFIX + ":objectProperties>\n");
	}
	
	private void appendDatastreams(DigitalObject obj, StringBuffer buf, String encoding)
			throws ObjectIntegrityException, UnsupportedEncodingException, 
			StreamIOException {
		Iterator iter=obj.datastreamIdIterator();
		while (iter.hasNext()) {
			String dsid = (String) iter.next();
			// AUDIT datastream is rebuilt from the latest in-memory audit trail
			// which is a separate array list in the DigitalObject class.
			// So, ignore it here.
			if (dsid.equalsIgnoreCase("AUDIT")) {
				continue;
			}
			// Given a datastream ID, get all the datastream versions.
			// Use the first version to pick up the attributes common to all versions.
			List dsList = obj.datastreams(dsid);
			for (int i=0; i<dsList.size(); i++) {
				Datastream vds = validateDatastream((Datastream) dsList.get(i));
				// insert the ds elements common to all versions.
				if (i==0) {
					buf.append("  <" + FOXML_PREFIX 
						+ ":datastream ID=\"" + vds.DatastreamID + "\"" 
						+ " URI=\"" + vds.DatastreamURI + "\""
						+ " STATE=\"" + vds.DSState + "\""
						+ " MIMETYPE=\"" + vds.DSMIME + "\""
						+ " FORMAT_URI=\"" + vds.DSFormatURI + "\""
						+ " CONTROL_GROUP=\"" + vds.DSControlGrp + "\""
						+ " VERSIONABLE=\"" + vds.DSVersionable + "\">\n");
				}
				// insert the ds version-level elements
				buf.append("    <" + FOXML_PREFIX 
					+ ":datastreamVersion ID=\"" + vds.DSVersionID + "\"" 
					+ " LABEL=\"" + StreamUtility.enc(vds.DSLabel) + "\""
					+ " CREATED=\"" + m_formatter.format(vds.DSCreateDT) + "\""
					+ " SIZE=\"" + vds.DSSize +  "\">\n");
			
				// if E or R insert content location as URL
				if (vds.DSControlGrp.equalsIgnoreCase("E") ||
					vds.DSControlGrp.equalsIgnoreCase("R") ) {
						buf.append("      <" + FOXML_PREFIX 
							+ ":contentLocation TYPE=\"" + "URL\""
							+ " REF=\"" + StreamUtility.enc(normalizeDSLocation(vds.DSLocation)) 
							+ "\"/>\n");	
				// if M insert content location as internal identifier				
				} else if (vds.DSControlGrp.equalsIgnoreCase("M")) {
					buf.append("      <" + FOXML_PREFIX 
						+ ":contentLocation TYPE=\"" + "INTERNAL_ID\""
						+ " REF=\"" + StreamUtility.enc(normalizeDSLocation(vds.DSLocation)) 
						+ "\"/>\n");	
				// if X insert inline XML
				} else if (vds.DSControlGrp.equalsIgnoreCase("X")) {
					appendInlineXML(obj.getFedoraObjectType(), 
						(DatastreamXMLMetadata)vds, buf, encoding);
				}					
				// FUTURE: Add digest of datastream content 
				//(to be calculated in DefaultManagement).
				buf.append("      <" + FOXML_PREFIX + ":contentDigest TYPE=\"MD5\">"
					+ "future: hash of content goes here" 
					+ "</" + FOXML_PREFIX + ":contentDigest>\n");
				buf.append("    </" + FOXML_PREFIX + ":datastreamVersion>\n");
				// if it's the last version, wrap-up with closing datastream element.	
				if (i==(dsList.size() - 1)) {
					buf.append("  </" + FOXML_PREFIX + ":datastream>\n");
				}			
			}
		}
	}

	private void appendAudit(DigitalObject obj, StringBuffer buf, String encoding) 
			throws ObjectIntegrityException {
		if (obj.getAuditRecords().size()>0) {
			// Audit trail datastream re-created from audit records.
			// There is only ONE version of the audit trail datastream!
			buf.append("  <" + FOXML_PREFIX 
				+ ":datastream ID=\"" + "AUDIT" + "\"" 
				+ " URI=\"" + "info:fedora/" + obj.getPid() + "AUDIT" + "\""
				+ " STATE=\"" + "A" + "\""
				+ " MIMETYPE=\"" + "text/xml" + "\""
				+ " FORMAT_URI=\"" + "info:fedora/format:xml:audit" + "\""
				+ " CONTROL_GROUP=\"" + "X" + "\""
				+ " VERSIONABLE=\"" + "NO" + "\">\n");
			// insert the ds version-level elements
			buf.append("    <" + FOXML_PREFIX 
				+ ":datastreamVersion ID=\"" + "AUDIT.0" + "\"" 
				+ " LABEL=\"" + "Fedora Object Audit Trail" + "\""
				+ " CREATED=\"" + m_formatter.format(obj.getCreateDate()) +  "\">\n");
			buf.append("      <" + m_fedoraAuditPrefix + ":auditTrail" + ">\n");
			for (int i=0; i<obj.getAuditRecords().size(); i++) {
				AuditRecord audit=(AuditRecord) obj.getAuditRecords().get(i);
				validateAudit(audit);
				buf.append("        <" + m_fedoraAuditPrefix + ":record>\n");
				buf.append("          <" + m_fedoraAuditPrefix + ":recordID>"
						+ StreamUtility.enc(audit.id)
						+ "</" + m_fedoraAuditPrefix + ":recordID>\n");
				buf.append("          <" + m_fedoraAuditPrefix + ":process type=\""
						+ StreamUtility.enc(audit.processType) + "\"/>\n");
				buf.append("          <" + m_fedoraAuditPrefix + ":action>"
						+ StreamUtility.enc(audit.action)
						+ "</" + m_fedoraAuditPrefix + ":action>\n");
				buf.append("          <" + m_fedoraAuditPrefix + ":componentID>"
						+ StreamUtility.enc(audit.componentID)
						+ "</" + m_fedoraAuditPrefix + ":componentID>\n");
				buf.append("          <" + m_fedoraAuditPrefix + ":responsibility>"
						+ StreamUtility.enc(audit.responsibility)
						+ "</" + m_fedoraAuditPrefix + ":responsibility>\n");
				buf.append("          <" + m_fedoraAuditPrefix + ":date>"
						+ m_formatter.format(audit.date)
						+ "</" + m_fedoraAuditPrefix + ":date>\n");
				buf.append("          <" + m_fedoraAuditPrefix + ":justification>"
						+ StreamUtility.enc(audit.justification)
						+ "</" + m_fedoraAuditPrefix + ":justification>\n");
				buf.append("        </" + m_fedoraAuditPrefix + ":record>\n");
			}
			buf.append("      </" + m_fedoraAuditPrefix + ":auditTrail" + ">\n");
			// FUTURE: Add digest of datastream content (calc in DefaultManagement).
			buf.append("      <" + FOXML_PREFIX + ":contentDigest TYPE=\"MD5\">"
				+ "future: hash of content goes here" 
				+ "</" + FOXML_PREFIX + ":contentDigest>\n");
			buf.append("    </" + FOXML_PREFIX + ":datastreamVersion>\n");				
			buf.append("  </" + FOXML_PREFIX + ":datastream>\n");
		}
	}

	private void appendInlineXML(int fedoraObjectType, DatastreamXMLMetadata ds, 
		StringBuffer buf, String encoding)
		throws ObjectIntegrityException, UnsupportedEncodingException, StreamIOException {
			
		buf.append("        <" + FOXML_PREFIX + ":xmlContent>\n");
        if (fedoraObjectType==DigitalObject.FEDORA_BMECH_OBJECT) {
        	if (ds.DatastreamID.equals("SERVICE-PROFILE") || 
				ds.DatastreamID.equals("WSDL")) {
	            // If WSDL or SERVICE-PROFILE datastream (in BMech) 
	            // and it contains a service URL that's local, 
	            // then modify the URL with a machine-neutral
	            // host identifier.
	            try {
	                String xml=new String(ds.xmlContent, "UTF-8");
	                xml=s_localServerUrlStartWithPort.matcher(xml).replaceAll(
	                        "http://local.fedora.server/");
	                xml=s_localhostUrlStartWithPort.matcher(xml).replaceAll(
	                        "http://local.fedora.server/");
					if (m_onPort80) {
	                    xml=s_localServerUrlStartWithoutPort.matcher(xml).replaceAll(
	                            "http://local.fedora.server/");
	                    xml=s_localhostUrlStartWithoutPort.matcher(xml).replaceAll(
	                            "http://local.fedora.server/");
				    }
	                buf.append(xml);
	            } catch (UnsupportedEncodingException uee) {
	                // wont happen, java always supports UTF-8
	            }
			}
        } else {
            appendXMLStream(ds.getContentStream(), buf, encoding);
        }
        buf.append("        </" + FOXML_PREFIX + ":xmlContent>\n");
    }

    private void appendXMLStream(InputStream in, StringBuffer buf, String encoding)
            throws ObjectIntegrityException, UnsupportedEncodingException,
            StreamIOException {
        if (in==null) {
            throw new ObjectIntegrityException("Object's inline xml "
                    + "stream cannot be null.");
        }
        try {
            byte[] byteBuf = new byte[4096];
            int len;
            while ( ( len = in.read( byteBuf ) ) != -1 ) {
                buf.append(new String(byteBuf, 0, len, encoding));
            }
        } catch (UnsupportedEncodingException uee) {
            throw uee;
        } catch (IOException ioe) {
            throw new StreamIOException("Error reading from inline xml datastream.");
        } finally {
            try {
                in.close();
            } catch (IOException closeProb) {
                throw new StreamIOException("Error closing read stream.");
            }
        }
    }

    private void appendStructMaps(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
        /*
        Iterator dissIdIter=obj.disseminatorIdIterator();
        while (dissIdIter.hasNext()) {
            String did=(String) dissIdIter.next();
            Iterator dissIter=obj.disseminators(did).iterator();
            while (dissIter.hasNext()) {
                Disseminator diss=(Disseminator) dissIter.next();
                if (diss.dsBindMapID==null) {
                    throw new ObjectIntegrityException("Object's disseminator must have a binding map id.");
                }
                if (diss.bMechID==null) {
                    throw new ObjectIntegrityException("Object's disseminator must have a bmech id.");
                }
                if (diss.dsBindMap==null) {
                    throw new ObjectIntegrityException("Object's disseminator must have a binding map.");
                }
                String labelString="";
                if ( diss.dsBindMap.dsBindMapLabel!=null
                        && !diss.dsBindMap.dsBindMapLabel.equals("") ) {
                    labelString=" LABEL=\"" + StreamUtility.enc(diss.dsBindMap.dsBindMapLabel) + "\"";
                }
                buf.append("  <" + FOXML_PREFIX + ":structMap ID=\""
                        + diss.dsBindMapID + "\" TYPE=\"fedora:dsBindingMap\">\n");
                buf.append("    <" + FOXML_PREFIX + ":div TYPE=\"" + diss.bMechID
                        + "\"" + labelString + ">\n");
                DSBinding[] bindings=diss.dsBindMap.dsBindings;
                for (int i=0; i<bindings.length; i++) {
                    if (bindings[i].bindKeyName==null
                            || bindings[i].bindKeyName.equals("")) {
                        throw new ObjectIntegrityException("Object's disseminator binding map binding must have a binding key name.");
                    }
                    buf.append("      <" + FOXML_PREFIX + ":div TYPE=\"");
                    buf.append(bindings[i].bindKeyName);
                    if (bindings[i].bindLabel!=null
                            && !bindings[i].bindLabel.equals("")) {
                        buf.append("\" LABEL=\"");
                        buf.append(StreamUtility.enc(bindings[i].bindLabel));
                    }
                    if (bindings[i].seqNo!=null) {
                        buf.append("\" ORDER=\"");
                        buf.append(bindings[i].seqNo);
                    }
                    if (bindings[i].datastreamID==null
                            || bindings[i].datastreamID.equals("")) {
                        throw new ObjectIntegrityException("Object's disseminator binding map binding must point to a datastream.");
                    }
                    buf.append("\">\n        <" + FOXML_PREFIX + ":fptr FILEID=\""
                            + bindings[i].datastreamID + "\"/>\n" + "      </"
                            + FOXML_PREFIX + ":div>\n");
                }
                buf.append("    </" + FOXML_PREFIX + ":div>\n");
                buf.append("  </" + FOXML_PREFIX + ":structMap>\n");
            }
        }
        */
    }

    private void appendDisseminators(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
         /*
        Iterator dissIdIter=obj.disseminatorIdIterator();
        while (dissIdIter.hasNext()) {
            String did=(String) dissIdIter.next();
            Iterator dissIter=obj.disseminators(did).iterator();
            Disseminator diss=(Disseminator) obj.disseminators(did).get(0);
            if (diss.dissState==null || diss.dissState.equals("")) {
                throw new ObjectIntegrityException("Object's disseminator must have a state.");
            }
            buf.append("  <" + FOXML_PREFIX + ":behaviorSec ID=\"" + did
                    + "\" STATUS=\"" + diss.dissState + "\">\n");
            for (int i=0; i<obj.disseminators(did).size(); i++) {
                diss=(Disseminator) obj.disseminators(did).get(i);
                if (diss.dissVersionID==null || diss.dissVersionID.equals("")) {
                    throw new ObjectIntegrityException("Object's disseminator must have a version id.");
                }
                if (diss.bDefID==null || diss.bDefID.equals("")) {
                    throw new ObjectIntegrityException("Object's disseminator must have a bdef id.");
                }
                if (diss.dissCreateDT==null) {
                    throw new ObjectIntegrityException("Object's disseminator must have a create date.");
                }
                if (diss.dissState==null || diss.dissState.equals("")) {
                    throw new ObjectIntegrityException("Object's disseminator must have a state.");
                }
                String dissLabelString="";
                if (diss.dissLabel!=null && !diss.dissLabel.equals("")) {
                    dissLabelString=" LABEL=\"" + StreamUtility.enc(diss.dissLabel) + "\"";
                }
                String bDefLabelString="";
                if (diss.bDefLabel!=null && !diss.bDefLabel.equals("")) {
                    bDefLabelString=" LABEL=\"" + StreamUtility.enc(diss.bDefLabel) + "\"";
                }
                String bMechLabelString="";
                if (diss.bMechLabel!=null && !diss.bMechLabel.equals("")) {
                    bMechLabelString=" LABEL=\"" + StreamUtility.enc(diss.bMechLabel) + "\"";
                }
                buf.append("    <" + FOXML_PREFIX + ":serviceBinding ID=\""
                        + diss.dissVersionID + "\" STRUCTID=\"" + diss.dsBindMapID
                        + "\" BTYPE=\"" + diss.bDefID + "\" CREATED=\""
                        + m_formatter.format(diss.dissCreateDT) + "\""
                        + dissLabelString + ">\n");
                buf.append("      <" + FOXML_PREFIX + ":interfaceMD" + bDefLabelString
                        + " LOCTYPE=\"URN\" " + m_XLinkPrefix + ":href=\""
                        + diss.bDefID + "\"/>\n");
                buf.append("      <" + FOXML_PREFIX + ":serviceBindMD" + bMechLabelString
                        + " LOCTYPE=\"URN\" " + m_XLinkPrefix + ":href=\""
                        + diss.bMechID + "\"/>\n");

                buf.append("    </" + FOXML_PREFIX + ":serviceBinding>\n");
            }
            buf.append("  </" + FOXML_PREFIX + ":behaviorSec>\n");
        }
        */
    }

    private void appendRootElementEnd(StringBuffer buf) {
        buf.append("</" + FOXML_PREFIX + ":digitalObject>");
    }

	private Datastream validateDatastream(Datastream ds) throws ObjectIntegrityException {
		// check on some essentials
		if (ds.DSVersionID==null || ds.DSVersionID.equals("")) {
			throw new ObjectIntegrityException("Datastream must have a version id.");
		}
		if (!ds.DSControlGrp.equalsIgnoreCase("E") &&
			!ds.DSControlGrp.equalsIgnoreCase("R") &&
			!ds.DSControlGrp.equalsIgnoreCase("M") &&
			!ds.DSControlGrp.equalsIgnoreCase("X")) {
			throw new ObjectIntegrityException("Datastream control group must be E,R,M or X.");
		}
		if (ds.DSCreateDT==null) {
			throw new ObjectIntegrityException("Datastream must have a create date.");
		}
		if (!ds.DSControlGrp.equalsIgnoreCase("X") && 
			(ds.DSLocation==null || ds.DSLocation.equals(""))) {
			throw new ObjectIntegrityException("Content datastream must have a location.");
		}
		if (ds.DSMIME==null && ds.DSControlGrp.equalsIgnoreCase("X")) {
			ds.DSMIME="text/xml";
		}
		if (ds.DSInfoType==null || ds.DSInfoType.equals("")
				|| ds.DSInfoType.equalsIgnoreCase("OTHER") ) {
			ds.DSInfoType="UNSPECIFIED";
		}
		if ( ds.DSLabel==null && ds.DSLabel.equals("") ) {
			ds.DSLabel = "Datastream known as: " + ds.DatastreamURI;
		}
		// For METS backward compatibility:
		// If we have a METS MDClass value, preserve MDClass and MDType in a format URI
		if (ds.DSControlGrp.equalsIgnoreCase("X")) {
			if ( ((DatastreamXMLMetadata)ds).DSMDClass !=0 ) {
				String mdClassName = "";
				String mdType=ds.DSInfoType;
				String otherType="";
				if (((DatastreamXMLMetadata)ds).DSMDClass==1) {mdClassName = "techMD";
				} else if (((DatastreamXMLMetadata)ds).DSMDClass==2) {mdClassName = "sourceMD";
				} else if (((DatastreamXMLMetadata)ds).DSMDClass==3) {mdClassName = "rightsMD";
				} else if (((DatastreamXMLMetadata)ds).DSMDClass==4) {mdClassName = "digiprovMD";
				} else if (((DatastreamXMLMetadata)ds).DSMDClass==5) {mdClassName = "descMD";}			
				if ( !mdType.equals("MARC") && !mdType.equals("EAD")
						&& !mdType.equals("DC") && !mdType.equals("NISOIMG")
						&& !mdType.equals("LC-AV") && !mdType.equals("VRA")
						&& !mdType.equals("TEIHDR") && !mdType.equals("DDI")
						&& !mdType.equals("FGDC") ) {
					mdType="OTHER";
					otherType=ds.DSInfoType;
				}
				ds.DSFormatURI = 
					"info:fedora/format:xml:mets:" 
					+ mdClassName + ":" + mdType + ":" + otherType;
			}
		}
		return ds;
	}
	
	private void validateAudit(AuditRecord audit) throws ObjectIntegrityException {
		if (audit.id==null) {
			throw new ObjectIntegrityException("Audit record must have id.");
		}
		if (audit.date==null) {
			throw new ObjectIntegrityException("Audit record must have date.");
		}
		if (audit.processType==null) {
			throw new ObjectIntegrityException("Audit record must have processType.");
		}
		if (audit.action==null) {
			throw new ObjectIntegrityException("Audit record must have action.");
		}
		if (audit.componentID==null) {
			audit.componentID = ""; // for backwards compatibility, no error on null
			// throw new ObjectIntegrityException("Audit record must have componentID.");
		}
		if (audit.responsibility==null) {
			throw new ObjectIntegrityException("Audit record must have responsibility.");
		}
		if (audit.justification==null) {
			throw new ObjectIntegrityException("Audit record must have justification.");
		}
	}
	private String normalizeDSLocation(String dsLocation) {
		// When datastream location makes reference to the LOCAL machine and port
		// (i.e., the one that the repository is running on), then we want to put 
		// placeholders in the ds location.  This is to prevent breakage if the 
		// repository host:port is reconfigured.
		String newLoc = dsLocation;
		newLoc=s_localServerUrlStartWithPort.matcher(dsLocation).replaceAll("http://local.fedora.server/");
		newLoc=s_localhostUrlStartWithPort.matcher(dsLocation).replaceAll("http://local.fedora.server/");
	  	if (m_onPort80) {
			newLoc=s_localServerUrlStartWithoutPort.matcher(dsLocation).replaceAll("http://local.fedora.server/");
			newLoc=s_localhostUrlStartWithoutPort.matcher(dsLocation).replaceAll("http://local.fedora.server/");
	  	}
	  	return newLoc;
	}
	
	private String getTypeAttribute(DigitalObject obj)
			throws ObjectIntegrityException {
		int t=obj.getFedoraObjectType();
		if (t==DigitalObject.FEDORA_BDEF_OBJECT) {
			return "FedoraBDefObject";
		} else if (t==DigitalObject.FEDORA_BMECH_OBJECT) {
			return "FedoraBMechObject";
		} else if (t==DigitalObject.FEDORA_OBJECT) {
			return "FedoraObject";
		} else {
			throw new ObjectIntegrityException("Object must have a FedoraObjectType.");
		}
	}
	
    private void writeToStream(StringBuffer buf, OutputStream out,
            String encoding, boolean closeWhenFinished)
            throws StreamIOException, UnsupportedEncodingException {
        try {
            out.write(buf.toString().getBytes(encoding));
            out.flush();
        } catch (IOException ioe) {
            throw new StreamWriteException("Problem serializing to FOXML: "
                    + ioe.getMessage());
        } finally {
            if (closeWhenFinished) {
                try {
                    out.close();
                } catch (IOException ioe2) {
                    throw new StreamWriteException("Problem closing stream after "
                            + " serializing to FOXML: " + ioe2.getMessage());
                }
            }
        }
    }
 }