package fedora.server.storage.translation;

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

import fedora.server.errors.ObjectIntegrityException;
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
 * <p><b>Title:</b> METSLikeDOSerializer.java</p>
 * <p><b>Description: Creates an XML serialization of a Fedora digital object 
 *       in accordance with the Fedora extension of the METS XML Schema 
 *       defined at: http://www.fedora.info/definitions/1/0/mets-fedora-ext.xsd.
 * 
 *       The serializer uses the currently instantiated digital object
 *       as input (see fedora.server.storage.types.DigitalObject). 
 * 
 *       The serializer will adapt its output to a specific translation contexts.
 *       See the static definitions of different translation contexts in 
 *       fedora.server.storage.translation.DOTranslationUtility. </b></p>
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
public class METSLikeDOSerializer
        implements DOSerializer {

    public static final String FEDORA_AUDIT_NS="info:fedora/def:audit/";
    public static final String METS_PREFIX="METS";
    public static final String METS_NS="http://www.loc.gov/METS/";
    public static final String METS_XSD_LOCATION="http://www.fedora.info/definitions/1/0/mets-fedora-ext.xsd";
    public static final String METS_XLINK_NS="http://www.w3.org/TR/xlink";
    public static final String REAL_XLINK_NS="http://www.w3.org/TR/xlink";
    public static final String XSI_NS="http://www.w3.org/2001/XMLSchema-instance";

    private String m_XLinkPrefix="xlink";
    private String m_fedoraAuditPrefix="audit";
    private SimpleDateFormat m_formatter=
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    private int m_transContext;

    public METSLikeDOSerializer() {
    }

    public DOSerializer getInstance() {
        return new METSLikeDOSerializer();
    }

    public void serialize(DigitalObject obj, OutputStream out, String encoding, int transContext)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedEncodingException {
		System.out.println("Serializing using METSLike...");
		m_transContext=transContext;
        StringBuffer buf=new StringBuffer();
        appendXMLDeclaration(obj, encoding, buf);
        appendRootElementStart(obj, buf);
        appendHdr(obj, buf);
        appendDescriptiveMD(obj, buf, encoding);
        appendAuditRecordAdminMD(obj, buf);
        appendOtherAdminMD(obj, buf, encoding);
        appendFileSecs(obj, buf);
        appendStructMaps(obj, buf);
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
        buf.append("<" + METS_PREFIX + ":mets xmlns:" + METS_PREFIX + "=\""
                + StreamUtility.enc(METS_NS) + "\"\n");
        String indent="           ";
        // make sure XSI_NS is mapped...
        String xsiPrefix=(String) obj.getNamespaceMapping().get(XSI_NS);
        if (xsiPrefix==null) {
            xsiPrefix="fedoraxsi";
            obj.getNamespaceMapping().put(XSI_NS, "fedoraxsi"); // 99.999999999% chance this is unique
        }
        appendNamespaceDeclarations(indent,obj.getNamespaceMapping(),buf);
        // hardcode xsi:schemaLocation to definitive location for such.
        buf.append(indent + xsiPrefix + ":schemaLocation=\"" + StreamUtility.enc(METS_NS) + " http://www.fedora.info/definitions/1/0/mets-fedora-ext.xsd\"\n");
        if (obj.getPid()==null || obj.getPid().equals("")) {
            throw new ObjectIntegrityException("Object must have a pid.");
        }
        buf.append(indent + "OBJID=\"" + obj.getPid() + "\" TYPE=\""
                + getTypeAttribute(obj) + "\"");
        if (obj.getLabel()!=null && !obj.getLabel().equals("")) {
            buf.append("\n" + indent + "LABEL=\"" + StreamUtility.enc(
                    obj.getLabel()) + "\"");
        }
        if (obj.getContentModelId()!=null && !obj.getContentModelId().equals("")) {
            buf.append("\n" + indent + "PROFILE=\"" + StreamUtility.enc(
                    obj.getContentModelId()) + "\"");
        }
        buf.append(">\n");
    }

    private void appendNamespaceDeclarations(String prepend, Map URIToPrefix,
            StringBuffer buf) {
        Iterator iter=URIToPrefix.keySet().iterator();
        while (iter.hasNext()) {
            String URI=(String) iter.next();
            String prefix=(String) URIToPrefix.get(URI);
            if (!prefix.equals("")) {
                if ( (URI.equals(METS_XLINK_NS)) || (URI.equals(REAL_XLINK_NS)) ) {
                    m_XLinkPrefix=prefix;
                } else if (URI.equals(FEDORA_AUDIT_NS)) {
                    m_fedoraAuditPrefix=prefix;
                } else if (!URI.equals(METS_NS)) {
                    buf.append(prepend + "xmlns:" + prefix + "=\""
                            + StreamUtility.enc(URI) + "\"\n");
                }
            }
        }
        buf.append(prepend + "xmlns:" + m_XLinkPrefix + "=\""
                + REAL_XLINK_NS + "\"\n");
        buf.append(prepend + "xmlns:" + m_fedoraAuditPrefix + "=\""
                + FEDORA_AUDIT_NS + "\"\n");
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

    private void appendHdr(DigitalObject obj, StringBuffer buf) {
        buf.append("  <" + METS_PREFIX + ":metsHdr");
        Date cDate=obj.getCreateDate();
        if (cDate!=null) {
            buf.append(" CREATEDATE=\"");
            buf.append(m_formatter.format(cDate));
            buf.append("\"");
        }
        Date mDate=obj.getLastModDate();
        if (mDate!=null) {
            buf.append(" LASTMODDATE=\"");
            buf.append(m_formatter.format(mDate) + "\"");
        }
        String state=obj.getState();
        if (state!=null && !state.equals("")) {
            buf.append(" RECORDSTATUS=\"");
            buf.append(state + "\"");
        }
        buf.append("/>\n");
    }

    private void appendDescriptiveMD(DigitalObject obj, StringBuffer buf,
            String encoding)
            throws ObjectIntegrityException, UnsupportedEncodingException,
            StreamIOException {
        Iterator iter=obj.datastreamIdIterator();
        while (iter.hasNext()) {
            String id=(String) iter.next();
            Datastream firstDS=(Datastream) obj.datastreams(id).get(0);
            if ((firstDS.DSControlGrp.equals("X"))
                    && (((DatastreamXMLMetadata) firstDS).DSMDClass==
                    DatastreamXMLMetadata.DESCRIPTIVE)) {
                appendMDSec(obj, "dmdSecFedora", "descMD", obj.datastreams(id),
                        buf, encoding);
            }
        }
    }

    private void appendMDSec(DigitalObject obj, String outerName,
            String innerName, List XMLMetadata, StringBuffer buf, String encoding)
            throws ObjectIntegrityException, UnsupportedEncodingException,
            StreamIOException {
        DatastreamXMLMetadata first=
        	(DatastreamXMLMetadata)DOTranslationUtility.setDatastreamDefaults(
        		(DatastreamXMLMetadata) XMLMetadata.get(0));
        buf.append("  <" + METS_PREFIX + ":" + outerName + " ID=\""
                + first.DatastreamID + "\" STATUS=\"" + first.DSState 
                + "\">\n");
        for (int i=0; i<XMLMetadata.size(); i++) {
			DatastreamXMLMetadata ds=
				(DatastreamXMLMetadata)DOTranslationUtility.setDatastreamDefaults(
					(DatastreamXMLMetadata)XMLMetadata.get(i));
			String dateAttr="";
			if (ds.DSCreateDT!=null) {
				dateAttr=" CREATED=\"" + m_formatter.format(ds.DSCreateDT) + "\"";
			}
			buf.append("    <" + METS_PREFIX + ":" + innerName 
				+ " ID=\""	+ ds.DSVersionID + "\""
				+ dateAttr 
				+ ">\n");
            String mdType=ds.DSInfoType;
            String otherAttr="";
            if ( !mdType.equals("MARC") && !mdType.equals("EAD")
                    && !mdType.equals("DC") && !mdType.equals("NISOIMG")
                    && !mdType.equals("LC-AV") && !mdType.equals("VRA")
                    && !mdType.equals("TEIHDR") && !mdType.equals("DDI")
                    && !mdType.equals("FGDC") ) {
                mdType="OTHER";
                otherAttr=" OTHERMDTYPE=\"" + StreamUtility.enc(ds.DSInfoType)
                        + "\" ";
            }
            String labelAttr="";
            if ( ds.DSLabel!=null && !ds.DSLabel.equals("") ) {
                labelAttr=" LABEL=\"" + StreamUtility.enc(ds.DSLabel) + "\"";
            }
            buf.append("      <" + METS_PREFIX + ":mdWrap MIMETYPE=\"" + ds.DSMIME + "\""
                    + " MDTYPE=\"" + mdType + "\"" 
                    + otherAttr
                    + labelAttr 
                    + ">\n");
            buf.append("        <" + METS_PREFIX + ":xmlData>\n");
            
			// If WSDL or SERVICE-PROFILE datastream (in BMech) 
			// make sure that any embedded URLs are encoded 
			// appropriately for either EXPORT or STORE.
            if (obj.getFedoraObjectType()==DigitalObject.FEDORA_BMECH_OBJECT
                    && (ds.DatastreamID.equals("SERVICE-PROFILE")) 
                    || (ds.DatastreamID.equals("WSDL")) ) {
				buf.append(DOTranslationUtility.normalizeInlineXML(
					new String(ds.xmlContent, "UTF-8"), m_transContext));
            } else {
                appendStream(ds.getContentStream(), buf, encoding);
            }
            buf.append("        </" + METS_PREFIX + ":xmlData>");
            buf.append("      </" + METS_PREFIX + ":mdWrap>\n");
            buf.append("    </" + METS_PREFIX + ":" + innerName + ">\n");
        }
        buf.append("  </" + METS_PREFIX + ":" + outerName + ">\n");
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

    private void appendAuditRecordAdminMD(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
        if (obj.getAuditRecords().size()>0) {
            buf.append("  <" + METS_PREFIX + ":amdSec ID=\"FEDORA-AUDITTRAIL\">\n");
            for (int i=0; i<obj.getAuditRecords().size(); i++) {
                AuditRecord audit=(AuditRecord) obj.getAuditRecords().get(i);
                // The audit record is created by the system, so programmatic
                // validation here is o.k.  Normally, validation takes place
                // via XML Schema and Schematron.
                if (audit.id==null || audit.id.equals("")) {
                    throw new ObjectIntegrityException("Audit record must have id.");
                }
                if (audit.date==null || audit.date.equals("")) {
                    throw new ObjectIntegrityException("Audit record must have date.");
                }
                if (audit.processType==null || audit.processType.equals("")) {
                    throw new ObjectIntegrityException("Audit record must have processType.");
                }
                if (audit.action==null || audit.action.equals("")) {
                    throw new ObjectIntegrityException("Audit record must have action.");
                }
				if (audit.componentID==null) {
					audit.componentID = ""; // for backwards compatibility, no error on null
				}
                if (audit.responsibility==null || audit.responsibility.equals("")) {
                    throw new ObjectIntegrityException("Audit record must have responsibility.");
                }
                if (audit.justification==null || audit.justification.equals("")) {
                    throw new ObjectIntegrityException("Audit record must have justification.");
                }
                buf.append("    <" + METS_PREFIX + ":digiprovMD ID=\"" + audit.id
                        + "\" CREATED=\"" + m_formatter.format(audit.date)
                        + "\" STATUS=\"A\">\n");
                buf.append("      <" + METS_PREFIX + ":mdWrap MIMETYPE=\"text/xml\" "
                        + "MDTYPE=\"OTHER\" OTHERMDTYPE=\"FEDORA-AUDITTRAIL\""
                        + " LABEL=\"Audit record for '"
                        + StreamUtility.enc(audit.action) + "' action by "
                        + StreamUtility.enc(audit.responsibility) + " at "
                        + m_formatter.format(audit.date) + "\">\n");
                buf.append("        <" + METS_PREFIX + ":xmlData>\n");
				buf.append("            <" + m_fedoraAuditPrefix + ":record>\n");
                buf.append("            <" + m_fedoraAuditPrefix + ":process type=\""
                        + StreamUtility.enc(audit.processType) + "\"/>\n");
                buf.append("            <" + m_fedoraAuditPrefix + ":action>"
                        + StreamUtility.enc(audit.action)
                        + "</" + m_fedoraAuditPrefix + ":action>\n");
				buf.append("            <" + m_fedoraAuditPrefix + ":componentID>"
										+ StreamUtility.enc(audit.componentID)
										+ "</" + m_fedoraAuditPrefix + ":componentID>\n");
                buf.append("            <" + m_fedoraAuditPrefix + ":responsibility>"
                        + StreamUtility.enc(audit.responsibility)
                        + "</" + m_fedoraAuditPrefix + ":responsibility>\n");
                buf.append("            <" + m_fedoraAuditPrefix + ":date>"
                        + m_formatter.format(audit.date)
                        + "</" + m_fedoraAuditPrefix + ":date>\n");
                buf.append("            <" + m_fedoraAuditPrefix + ":justification>"
                        + StreamUtility.enc(audit.justification)
                        + "</" + m_fedoraAuditPrefix + ":justification>\n");
                buf.append("          </" + m_fedoraAuditPrefix + ":record>\n");
                buf.append("        </" + METS_PREFIX + ":xmlData>\n");
                buf.append("      </" + METS_PREFIX + ":mdWrap>\n");
                buf.append("    </" + METS_PREFIX + ":digiprovMD>\n");
            }
            buf.append("  </" + METS_PREFIX + ":amdSec>\n");
        }
    }

    private void appendOtherAdminMD(DigitalObject obj, StringBuffer buf,
            String encoding)
            throws ObjectIntegrityException, UnsupportedEncodingException,
            StreamIOException {
        Iterator iter=obj.datastreamIdIterator();
        while (iter.hasNext()) {
            String id=(String) iter.next();
            Datastream firstDS=(Datastream) obj.datastreams(id).get(0);
            // First, work with the first version to get the mdClass set to
            // a proper value required in the METS XML Schema.
            if ((firstDS.DSControlGrp.equals("X"))
                    && (((DatastreamXMLMetadata) firstDS).DSMDClass!=
                    DatastreamXMLMetadata.DESCRIPTIVE)) {
                DatastreamXMLMetadata md=(DatastreamXMLMetadata) firstDS;
                // Default mdClass to techMD when a valid one does not appear
                // (say because the object was born as FOXML)
                String mdClass="techMD";
                if (md.DSMDClass==DatastreamXMLMetadata.TECHNICAL) {
                    mdClass="techMD";
                } else if (md.DSMDClass==DatastreamXMLMetadata.SOURCE) {
                    mdClass="sourceMD";
                } else if (md.DSMDClass==DatastreamXMLMetadata.RIGHTS) {
                    mdClass="rightsMD";
                } else if (md.DSMDClass==DatastreamXMLMetadata.DIGIPROV) {
                    mdClass="digiprovMD";
                }
                // Then, pass everything along to do the actual serialization
                appendMDSec(obj, "amdSec", mdClass, obj.datastreams(id),
                        buf, encoding);
            }
        }
    }

    private void appendFileSecs(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
        Iterator iter=obj.datastreamIdIterator();
        boolean didFileSec=false;
        while (iter.hasNext()) {
            Datastream ds=
            	DOTranslationUtility.setDatastreamDefaults(
            		(Datastream)obj.datastreams((String)iter.next()).get(0));
            if (!ds.DSControlGrp.equals("X")) {
                if (!didFileSec) {
                    didFileSec=true;
                    buf.append("  <" + METS_PREFIX + ":fileSec>\n");
                    buf.append("    <" + METS_PREFIX + ":fileGrp ID=\"DATASTREAMS\">\n");
                }
                buf.append("      <" + METS_PREFIX + ":fileGrp ID=\""
                        + ds.DatastreamID 
                        + "\" STATUS=\"" + ds.DSState 
                        + "\">\n");
                Iterator contentIter=obj.datastreams(ds.DatastreamID).iterator();
                while (contentIter.hasNext()) {
					Datastream dsc=DOTranslationUtility.setDatastreamDefaults(
						(Datastream)contentIter.next());                   
                    String labelAttr="";
                    if (dsc.DSLabel!=null && !dsc.DSLabel.equals("")) {
                        labelAttr=" " + m_XLinkPrefix + ":title=\""
                                + StreamUtility.enc(dsc.DSLabel) + "\"";
                    }
					String dateAttr="";
					if (dsc.DSCreateDT!=null) {
						dateAttr=" CREATED=\"" + m_formatter.format(dsc.DSCreateDT) + "\"";
					}
                    String sizeAttr=" SIZE=\"" + dsc.DSSize + "\"";
                    String admIDAttr=getIdString(obj, (DatastreamContent)dsc, true);
                    String dmdIDAttr=getIdString(obj, (DatastreamContent)dsc, false);

                    buf.append("        <" + METS_PREFIX + ":file ID=\"" + dsc.DSVersionID + "\"" 
                    		+ dateAttr
                            + " MIMETYPE=\"" + dsc.DSMIME + "\"" 
                            + sizeAttr
                            + admIDAttr 
                            + dmdIDAttr 
                            + " OWNERID=\"" + dsc.DSControlGrp 
                            + "\">\n");
                    buf.append("          <" + METS_PREFIX + ":FLocat" + labelAttr
                            + " LOCTYPE=\"URL\" " 
                            + m_XLinkPrefix + ":href=\""
							+ StreamUtility.enc(
								DOTranslationUtility.normalizeDSLocationURLs(
									obj.getPid(), dsc, m_transContext).DSLocation)
					        + "\"/>\n");
                    buf.append("        </" + METS_PREFIX + ":file>\n");
                }
                buf.append("      </" + METS_PREFIX + ":fileGrp>\n");
            }
        }
        if (didFileSec) {
            buf.append("    </" + METS_PREFIX + ":fileGrp>\n");
            buf.append("  </" + METS_PREFIX + ":fileSec>\n");
        }
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
        Iterator dissIdIter=obj.disseminatorIdIterator();
        while (dissIdIter.hasNext()) {
            String did=(String) dissIdIter.next();
            Iterator dissIter=obj.disseminators(did).iterator();
            while (dissIter.hasNext()) {
                Disseminator diss=
                	DOTranslationUtility.setDisseminatorDefaults(
                		(Disseminator) dissIter.next());
                String labelAttr="";
                if ( diss.dsBindMap.dsBindMapLabel!=null
                        && !diss.dsBindMap.dsBindMapLabel.equals("") ) {
                    labelAttr=" LABEL=\"" + StreamUtility.enc(diss.dsBindMap.dsBindMapLabel) + "\"";
                }
                buf.append("  <" + METS_PREFIX + ":structMap ID=\""
                        + diss.dsBindMapID + "\" TYPE=\"fedora:dsBindingMap\">\n");
                buf.append("    <" + METS_PREFIX + ":div TYPE=\"" + diss.bMechID
                        + "\"" + labelAttr 
                        + ">\n");
                DSBinding[] bindings=diss.dsBindMap.dsBindings;
                for (int i=0; i<bindings.length; i++) {
                    if (bindings[i].bindKeyName==null
                            || bindings[i].bindKeyName.equals("")) {
                        throw new ObjectIntegrityException("Object's disseminator"
                        	+ " binding map binding must have a binding key name.");
                    }
                    buf.append("      <" + METS_PREFIX + ":div TYPE=\"");
                    buf.append(bindings[i].bindKeyName);
                    if (bindings[i].bindLabel!=null
                            && !bindings[i].bindLabel.equals("")) {
                        buf.append("\" LABEL=\"");
                        buf.append(StreamUtility.enc(bindings[i].bindLabel));
                    }
                    if (bindings[i].seqNo!=null && !bindings[i].seqNo.equals("")) {
                        buf.append("\" ORDER=\"");
                        buf.append(bindings[i].seqNo);
                    }
                    if (bindings[i].datastreamID==null
                            || bindings[i].datastreamID.equals("")) {
                        throw new ObjectIntegrityException("Object's disseminator"
                        	+ " binding map binding must point to a datastream.");
                    }
                    buf.append("\">\n        <" + METS_PREFIX + ":fptr FILEID=\""
                            + bindings[i].datastreamID + "\"/>\n" 
                            + "      </"  + METS_PREFIX + ":div>\n");
                }
                buf.append("    </" + METS_PREFIX + ":div>\n");
                buf.append("  </" + METS_PREFIX + ":structMap>\n");
            }
        }
    }

    private void appendDisseminators(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
        Iterator dissIdIter=obj.disseminatorIdIterator();
        while (dissIdIter.hasNext()) {
            String did=(String) dissIdIter.next();
            Iterator dissIter=obj.disseminators(did).iterator();
            Disseminator diss=
				DOTranslationUtility.setDisseminatorDefaults(
            		(Disseminator) obj.disseminators(did).get(0));
            buf.append("  <" + METS_PREFIX + ":behaviorSec ID=\"" + did
                    + "\" STATUS=\"" + diss.dissState + "\">\n");
            for (int i=0; i<obj.disseminators(did).size(); i++) {
                diss=DOTranslationUtility.setDisseminatorDefaults(
                	(Disseminator) obj.disseminators(did).get(i));
                String dissLabelAttr="";
                if (diss.dissLabel!=null && !diss.dissLabel.equals("")) {
                    dissLabelAttr=" LABEL=\"" + StreamUtility.enc(diss.dissLabel) + "\"";
                }
                String bDefLabelAttr="";
                if (diss.bDefLabel!=null && !diss.bDefLabel.equals("")) {
                    bDefLabelAttr=" LABEL=\"" + StreamUtility.enc(diss.bDefLabel) + "\"";
                }
                String bMechLabelAttr="";
                if (diss.bMechLabel!=null && !diss.bMechLabel.equals("")) {
                    bMechLabelAttr=" LABEL=\"" + StreamUtility.enc(diss.bMechLabel) + "\"";
                }
                buf.append("    <" + METS_PREFIX + ":serviceBinding ID=\""
                        + diss.dissVersionID + "\" STRUCTID=\"" + diss.dsBindMapID
                        + "\" BTYPE=\"" + diss.bDefID + "\" CREATED=\""
                        + m_formatter.format(diss.dissCreateDT) + "\""
                        + dissLabelAttr + ">\n");
                buf.append("      <" + METS_PREFIX + ":interfaceMD" + bDefLabelAttr
                        + " LOCTYPE=\"URN\" " + m_XLinkPrefix + ":href=\""
                        + diss.bDefID + "\"/>\n");
                buf.append("      <" + METS_PREFIX + ":serviceBindMD" + bMechLabelAttr
                        + " LOCTYPE=\"URN\" " + m_XLinkPrefix + ":href=\""
                        + diss.bMechID + "\"/>\n");

                buf.append("    </" + METS_PREFIX + ":serviceBinding>\n");
            }
            buf.append("  </" + METS_PREFIX + ":behaviorSec>\n");
        }
    }

    private void appendRootElementEnd(StringBuffer buf) {
        buf.append("</" + METS_PREFIX + ":mets>");
    }

    private void writeToStream(StringBuffer buf, OutputStream out,
            String encoding, boolean closeWhenFinished)
            throws StreamIOException, UnsupportedEncodingException {
        try {
            out.write(buf.toString().getBytes(encoding));
            out.flush();
        } catch (IOException ioe) {
            throw new StreamWriteException("Problem serializing to METS: "
                    + ioe.getMessage());
        } finally {
            if (closeWhenFinished) {
                try {
                    out.close();
                } catch (IOException ioe2) {
                    throw new StreamWriteException("Problem closing stream after "
                            + " serializing to METS: " + ioe2.getMessage());
                }
            }
        }
    }
}
