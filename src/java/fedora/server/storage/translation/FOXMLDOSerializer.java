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

	public static final String FOXML_NS="http://www.fedora.info/definitions/foxml";
    public static final String FEDORA_AUDIT_NS="http://www.fedora.info/definitions/audit";
	public static final String FEDORA_SYSMETA_NS="http://www.fedora.info/definitions/sysmeta";
	public static final String FEDORA_RELSOUT_NS="http://www.fedora.info/definitions/relation/outer";
    public static final String FOXML_PREFIX="foxml";

    public static final String FOXML_XSD_LOCATION="http://www.fedora.info/definitions/1/0/foxml.xsd";
    public static final String XSI_NS="http://www.w3.org/2001/XMLSchema-instance";

    private String m_fedoraAuditPrefix="audit";
	private String m_fedoraSysmetaPrefix="sysmeta";
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
        appendAudit(obj, buf);
        appendDatastreams(obj, buf);
        appendDisseminators(obj, buf);
        appendRootElementEnd(buf);
        writeToStream(buf, out, encoding, true);
    }

    private void appendXMLDeclaration(DigitalObject obj, String encoding,
            StringBuffer buf) {
        buf.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\" ?>\n");
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
        buf.append(indent + "PID=\"" + obj.getPid());
		buf.append(indent + "TYPE=\"" + getTypeAttribute(obj));
		buf.append(indent + "CREATEDATE=\"" + m_formatter.format(obj.getCreateDate()));
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
                } else if (URI.equals(FEDORA_SYSMETA_NS)) {
						m_fedoraSysmetaPrefix=prefix;
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
		buf.append(prepend + "xmlns:" + m_fedoraSysmetaPrefix + "=\""
				+ FEDORA_SYSMETA_NS + "\"\n");
		buf.append(prepend + "xmlns:" + m_fedoraRelsoutPrefix + "=\""
				+ FEDORA_RELSOUT_NS + "\"\n");
    }
	private void appendDatastreams(DigitalObject obj, StringBuffer buf)
			throws ObjectIntegrityException, UnsupportedEncodingException, 
			StreamIOException {
		Iterator iter=obj.datastreamIdIterator();
		//boolean didDatastreams=false;
		while (iter.hasNext()) {
			// Given a datastream ID, get all the version entities.
			// Then, get the first version in the List and pick up attributes
			// shared by all versions.
			List dsList = obj.datastreams((String) iter.next());
			for (int i=0; i<dsList.size(); i++) {
				Datastream ds = (Datastream) dsList.get(i);
				// AUDIT datastream is rebuilt from latest values in Digital Object
				if (ds.DatastreamID.equalsIgnoreCase("AUDIT")) {
					appendAudit(obj, buf);
				} else {
					validateDatastream(ds);
					// insert the ds group-level element
					if (i==0) {
						buf.append("  <" + FOXML_PREFIX 
							+ ":datastream ID=\"" + ds.DatastreamID + "\"" 
							+ "URI=\"" + ds.DatastreamURI + "\""
							+ "STATE=\"" + ds.DSState + "\""
							+ "MIMETYPE=\"" + ds.DSMIME + "\""
							+ "FORMAT_URI=\"" + ds.DSFormatURI + "\""
							+ "CONTROL_GROUP=\"" + ds.DSControlGrp + "\""
							+ "VERSIONABLE=\"" + ds.DSVersionable + "\">\n");
					}
					// insert the ds version-level element(s)
					buf.append("  <" + FOXML_PREFIX 
						+ ":datastreamVersion ID=\"" + ds.DSVersionID + "\"" 
						+ "LABEL=\"" + StreamUtility.enc(ds.DSLabel) + "\""
						+ "CREATED=\"" + m_formatter.format(ds.DSCreateDT) + "\""
						+ "SIZE=\"" + ds.DSSize + "\""
						+ "CURRENT=\"" + ds.DSCurrent +  "\">\n");
				
					// if E or R insert content location as URL
					if (ds.DSControlGrp.equalsIgnoreCase("E") ||
						ds.DSControlGrp.equalsIgnoreCase("R") ) {
							buf.append("  <" + FOXML_PREFIX 
								+ ":contentLocation TYPE=\"" + "URL\""
								+ "REF=\"" + StreamUtility.enc(normalizeDSLocation(ds.DSLocation)) 
								+ "\"/>\n");	
					// if M insert content location as internal identifier				
					} else if (ds.DSControlGrp.equalsIgnoreCase("M")) {
						buf.append("  <" + FOXML_PREFIX 
							+ ":contentLocation TYPE=\"" + "INTERNAL_ID\""
							+ "REF=\"" + StreamUtility.enc(normalizeDSLocation(ds.DSLocation)) 
							+ "\"/>\n");	
					// if X insert inline XML
					} else if (ds.DSControlGrp.equalsIgnoreCase("X")) {
						appendInlineXML(ds, buf);
					}					
					// FUTURE: Add digest of datastream content 
					//(to be calculated in DefaultManagement).
					buf.append("    <" + FOXML_PREFIX + ":contentDigest TYPE=\"MD5\">"
						+ "future: hash of content goes here" 
						+ "</" + FOXML_PREFIX + ":contentDigest>\n");
					buf.append("  </" + FOXML_PREFIX + ":datastreamVersion>\n");				
				}
				buf.append("</" + FOXML_PREFIX + ":datastream>\n");
				}

		}
	}

	private void appendAudit(DigitalObject obj, StringBuffer buf) 
			throws ObjectIntegrityException {
		if (obj.getAuditRecords().size()>0) {
			for (int i=0; i<obj.getAuditRecords().size(); i++) {
				AuditRecord audit=(AuditRecord) obj.getAuditRecords().get(i);
				validateAudit(audit);
				buf.append("  <" + m_fedoraAuditPrefix + ":auditTrail" + ">\n");
				buf.append("    <" + m_fedoraAuditPrefix + ":record>\n");
				buf.append("      <" + m_fedoraAuditPrefix + ":process type=\""
						+ StreamUtility.enc(audit.processType) + "\"/>\n");
				buf.append("      <" + m_fedoraAuditPrefix + ":action>"
						+ StreamUtility.enc(audit.action)
						+ "</" + m_fedoraAuditPrefix + ":action>\n");
				buf.append("      <" + m_fedoraAuditPrefix + ":componentID>"
						+ StreamUtility.enc(audit.componentID)
						+ "</" + m_fedoraAuditPrefix + ":componentID>\n");
				buf.append("      <" + m_fedoraAuditPrefix + ":responsibility>"
						+ StreamUtility.enc(audit.responsibility)
						+ "</" + m_fedoraAuditPrefix + ":responsibility>\n");
				buf.append("      <" + m_fedoraAuditPrefix + ":date>"
						+ m_formatter.format(audit.date)
						+ "</" + m_fedoraAuditPrefix + ":date>\n");
				buf.append("      <" + m_fedoraAuditPrefix + ":justification>"
						+ StreamUtility.enc(audit.justification)
						+ "</" + m_fedoraAuditPrefix + ":justification>\n");
				buf.append("  </" + m_fedoraAuditPrefix + ":record>\n");
			}
			buf.append("/>" + m_fedoraSysmetaPrefix + ":auditTrail" + ">\n");
		}
	}

	//private void appendInlineXML(DigitalObject obj, String outerName,
	//         String innerName, List XMLMetadata, StringBuffer buf, String encoding)
	//         throws ObjectIntegrityException, UnsupportedEncodingException,
	//         StreamIOException {
	private void appendInlineXML(Datastream ds, StringBuffer buf)
		throws ObjectIntegrityException, UnsupportedEncodingException,
		StreamIOException {
		buf.append("  <" + FOXML_PREFIX + ":xmlContent>\n");
		// do not use the xml for the AUDIT datastream.  It will be re-created
		// from the audit information in the digital object.
		if (!ds.DatastreamID.equalsIgnoreCase("AUDIT")) {
			// do any global changes required for the stream (e.g., normalize host:port)
		
			// append the stream
		}

		
		
		/*
        buf.append("  <" + FOXML_PREFIX + ":" + outerName + " ID=\""
                + first.DatastreamID + "\" STATUS=\"" + first.DSState 
                + "\">\n");
        for (int i=0; i<XMLMetadata.size(); i++) {
            DatastreamXMLMetadata ds=(DatastreamXMLMetadata) XMLMetadata.get(i);
            if (ds.DSVersionID==null) {
                throw new ObjectIntegrityException("Datastream must have a version id.");
            }
            if (ds.DSCreateDT==null) {
                throw new ObjectIntegrityException("Datastream must have a creation date.");
            }
            buf.append("    <" + FOXML_PREFIX + ":" + innerName + " ID=\""
                    + ds.DSVersionID + "\" CREATED=\"" + m_formatter.format(
                    ds.DSCreateDT) + "\">\n");
            if (ds.DSMIME==null) {
                ds.DSMIME="text/html";
            }
            if (ds.DSInfoType==null || ds.DSInfoType.equals("")
                    || ds.DSInfoType.equalsIgnoreCase("OTHER") ) {
                ds.DSInfoType="UNSPECIFIED";
            }
            String mdType=ds.DSInfoType;
            String otherString="";
            if ( !mdType.equals("MARC") && !mdType.equals("EAD")
                    && !mdType.equals("DC") && !mdType.equals("NISOIMG")
                    && !mdType.equals("LC-AV") && !mdType.equals("VRA")
                    && !mdType.equals("TEIHDR") && !mdType.equals("DDI")
                    && !mdType.equals("FGDC") ) {
                mdType="OTHER";
                otherString=" OTHERMDTYPE=\"" + StreamUtility.enc(ds.DSInfoType)
                        + "\" ";
            }
            String labelString="";
            if ( ds.DSLabel!=null && !ds.DSLabel.equals("") ) {
                labelString=" LABEL=\"" + StreamUtility.enc(ds.DSLabel) + "\"";
            }
            buf.append("      <" + FOXML_PREFIX + ":mdWrap MIMETYPE=\"" + ds.DSMIME
                    + "\" MDTYPE=\"" + mdType + "\"" + otherString
                    + labelString + ">\n");
            buf.append("        <" + FOXML_PREFIX + ":xmlData>\n");
            if (obj.getFedoraObjectType()==DigitalObject.FEDORA_BMECH_OBJECT
                    && (ds.DatastreamID.equals("SERVICE-PROFILE")) || (ds.DatastreamID.equals("WSDL")) ) {
                // If it's the WSDL or SERVICE-PROFILE datastream in a bMech and it contains a
                // service URL that's local, replace it with a machine-neutral
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
            } else {
                appendStream(ds.getContentStream(), buf, encoding);
            }
            buf.append("        </" + FOXML_PREFIX + ":xmlData>");
            buf.append("      </" + FOXML_PREFIX + ":mdWrap>\n");
            buf.append("    </" + FOXML_PREFIX + ":" + innerName + ">\n");
        }
        buf.append("  </" + FOXML_PREFIX + ":" + outerName + ">\n");
        */
    }

    private void appendStream(InputStream in, StringBuffer buf, String encoding)
            throws ObjectIntegrityException, UnsupportedEncodingException,
            StreamIOException {
        if (in==null) {
            throw new ObjectIntegrityException("Object's inline descriptive "
                    + "metadata stream cannot be null.");
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
            throw new StreamIOException("Error reading from inline datastream.");
        } finally {
            try {
                in.close();
            } catch (IOException closeProb) {
                throw new StreamIOException("Error closing read stream.");
            }
        }
    }

    // append the admin md, while replacing occurances of
    // s_localServerUrlStartWithPort and s_localServerUrlStartWithoutPort and
    // s_localhostUrlStartWithPort and s_localhostUrlStartWithoutPort
    // with "http://local.fedora.server/" in the SERVICE-PROFILE and WSDL id'd admin datastreams
    // bMech objects.
    private void appendOtherAdminMD(DigitalObject obj, StringBuffer buf,
            String encoding)
            throws ObjectIntegrityException, UnsupportedEncodingException,
            StreamIOException {
         /*
        Iterator iter=obj.datastreamIdIterator();
        while (iter.hasNext()) {
            String id=(String) iter.next();
            Datastream firstDS=(Datastream) obj.datastreams(id).get(0);
            if ((firstDS.DSControlGrp.equals("X"))
                    && (((DatastreamXMLMetadata) firstDS).DSMDClass!=
                    DatastreamXMLMetadata.DESCRIPTIVE)) {
                DatastreamXMLMetadata md=(DatastreamXMLMetadata) firstDS;
                String mdClass=null;
                if (md.DSMDClass==DatastreamXMLMetadata.TECHNICAL) {
                    mdClass="techMD";
                } else if (md.DSMDClass==DatastreamXMLMetadata.SOURCE) {
                    mdClass="sourceMD";
                } else if (md.DSMDClass==DatastreamXMLMetadata.RIGHTS) {
                    mdClass="rightsMD";
                } else if (md.DSMDClass==DatastreamXMLMetadata.DIGIPROV) {
                    mdClass="digiprovMD";
                } else {
                    throw new ObjectIntegrityException(
					"Object's inline XML datastream must have a class (md.DSMDClass=" 
					+ md.DSMDClass + ").");
                }
                appendMDSec(obj, "amdSec", mdClass, obj.datastreams(id),
                        buf, encoding);
            }
        }
        */
    }

    private String getIdString(DigitalObject obj, DatastreamContent content,
            boolean adm)
            throws ObjectIntegrityException {
        ArrayList ret;
        if (adm) {
            ret=new ArrayList(content.auditRecordIdList());
        } else {
            ret=new ArrayList();
        }
        Iterator mdIdIter=content.metadataIdList().iterator();
        while (mdIdIter.hasNext()) {
            String mdId=(String) mdIdIter.next();
            List datastreams=obj.datastreams(mdId);
            if (datastreams.size()==0) {
                throw new ObjectIntegrityException("Object's content datastream"
                        + " points to an invalid inline metadata datastream id.");
            }
            Datastream ds=(Datastream) datastreams.get(0);
            if (ds.DSControlGrp.equalsIgnoreCase("X")) {
                DatastreamXMLMetadata mds=(DatastreamXMLMetadata) ds;
                if (mds.DSMDClass == DatastreamXMLMetadata.DESCRIPTIVE) {
                    if (!adm) ret.add(mdId);
                }
                else {
                    if (adm) ret.add(mdId);
                }
            }
        }
        StringBuffer out=new StringBuffer();
        for (int i=0; i<ret.size(); i++) {
            if (i>0) {
                out.append(' ');
            } else {
                if (adm) {
                    out.append(" ADMID=\"");
                } else {
                    out.append(" DMDID=\"");
                }
            }
            out.append((String) ret.get(i));
            if (i==ret.size()-1) {
                out.append("\"");
            }
        }
        return out.toString();
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
        buf.append("</" + FOXML_PREFIX + ":mets>");
    }

	private void validateDatastream(Datastream ds) throws ObjectIntegrityException {
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
    
	private void appendSysmeta(DigitalObject obj, StringBuffer buf) 
			throws ObjectIntegrityException {
		buf.append("  <" + m_fedoraSysmetaPrefix + ":objectProperties" + ">\n");
		String uri = obj.getURI();
		if (uri!=null){
			buf.append("    <" + m_fedoraSysmetaPrefix + ":URI" + ">" 
			+ uri + "</" + m_fedoraSysmetaPrefix + ":URI" + ">\n");
		}
		String state = obj.getState();
		if (state!=null){
			buf.append("    <" + m_fedoraSysmetaPrefix + ":objectState" + ">" 
			+ state + "</" + m_fedoraSysmetaPrefix + ":objectState" + ">\n");
		}
		String label = obj.getLabel();
		if (label!=null){
			buf.append("    <" + m_fedoraSysmetaPrefix + ":label" + ">" 
			+ label + "</" + m_fedoraSysmetaPrefix + ":label" + ">\n");
		}
		String cModelID = obj.getContentModelId();
		if (cModelID!=null){
			buf.append("    <" + m_fedoraSysmetaPrefix + ":contentModelID" + ">" 
			+ cModelID + "</" + m_fedoraSysmetaPrefix + ":contentModelID" + ">\n");
		}
		Date mDate=obj.getLastModDate();
		if (mDate!=null) {
			buf.append("    <" + m_fedoraSysmetaPrefix + ":modifiedDate" + ">" 
			+ m_formatter.format(mDate) + "</" + m_fedoraSysmetaPrefix + ":modifiedDate" + ">\n");
		}
		// append extensible object properties
		Map propMap = obj.getProperties();
		Iterator iter=propMap.keySet().iterator();
		while (iter.hasNext()) {
			String propName=(String) iter.next();
			String propValue =(String) propMap.get(propName);
			if (propValue!=null){
				buf.append("    <" + m_fedoraSysmetaPrefix + 
				":ext" + " propertyName=" + "\"" + propName + "\">" +
				propValue + "</" + m_fedoraSysmetaPrefix + ":ext" + ">\n");
			}
		}
		/*
		String fedoraType = getTypeAttribute(obj);
		if (fedoraType!=null){
			buf.append("    <" + m_fedoraSysmetaPrefix + ":objectType" + ">" 
			+ fedoraType + "</" + m_fedoraSysmetaPrefix + ":objectType" + ">\n");
		}
		Date cDate=obj.getCreateDate();
		if (cDate!=null) {
			buf.append("    <" + m_fedoraSysmetaPrefix + ":createdDate" + ">" 
			+ m_formatter.format(cDate) + "</" + m_fedoraSysmetaPrefix + ":createdDate" + ">\n");
		}
		*/
		buf.append("/>" + m_fedoraSysmetaPrefix + ":objectProperties" + ">\n");
	}
}
