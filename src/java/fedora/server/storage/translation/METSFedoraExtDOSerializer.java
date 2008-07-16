/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.translation;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import fedora.common.Constants;
import fedora.common.xml.format.XMLFormat;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.storage.types.DSBinding;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Disseminator;
import fedora.server.utilities.DateUtility;
import fedora.server.utilities.StreamUtility;
import fedora.server.utilities.StringUtility;

import static fedora.common.Models.SERVICE_DEPLOYMENT_3_0;

/**
 * Serializes objects in the constructor-provider version of the METS Fedora
 * Extension format.
 * 
 * @author Sandy Payette
 * @author Chris Wilper
 */
@SuppressWarnings("deprecation")
public class METSFedoraExtDOSerializer
        implements Constants, DOSerializer {

    /**
     * The format this serializer will write if unspecified at construction.
     * This defaults to the latest METS Fedora Extension format.
     */
    public static final XMLFormat DEFAULT_FORMAT = METS_EXT1_1;

    /** Logger for this class. */
    private static final Logger LOG =
            Logger.getLogger(METSFedoraExtDOSerializer.class);

    /** The format this serializer writes. */
    private final XMLFormat m_format;

    /** The current translation context. */
    private int m_transContext;

    /**
     * Creates a serializer that writes the default METS Fedora Extension
     * format.
     */
    public METSFedoraExtDOSerializer() {
        m_format = DEFAULT_FORMAT;
    }

    /**
     * Creates a serializer that writes the given METS Fedora Extension format.
     * 
     * @param format
     *        the version-specific METS Fedora Extension format.
     * @throws IllegalArgumentException
     *         if format is not a known METS Fedora extension format.
     */
    public METSFedoraExtDOSerializer(XMLFormat format) {
        if (format.equals(METS_EXT1_0) || format.equals(METS_EXT1_1)) {
            m_format = format;
        } else {
            throw new IllegalArgumentException("Not a METS Fedora Extension "
                    + "format: " + format.uri);
        }
    }

    //---
    // DOSerializer implementation
    //---

    /**
     * {@inheritDoc}
     */
    public DOSerializer getInstance() {
        return new METSFedoraExtDOSerializer(m_format);
    }

    /**
     * {@inheritDoc}
     */
    public void serialize(DigitalObject obj,
                          OutputStream out,
                          String encoding,
                          int transContext) throws ObjectIntegrityException,
            StreamIOException, UnsupportedEncodingException {
        LOG.debug("Serializing " + m_format.uri + " for transContext: "
                + transContext);
        m_transContext = transContext;
        StringBuffer buf = new StringBuffer();
        appendXMLDeclaration(obj, encoding, buf);
        appendRootElementStart(obj, buf);
        appendHdr(obj, buf);
        appendDescriptiveMD(obj, buf, encoding);
        appendAuditRecordAdminMD(obj, buf);
        appendOtherAdminMD(obj, buf, encoding);
        appendFileSecs(obj, buf);
        if (m_format.equals(METS_EXT1_0)) {
            appendStructMaps(obj, buf);
            appendDisseminators(obj, buf);
        }
        appendRootElementEnd(buf);
        DOTranslationUtility.writeToStream(buf, out, encoding, true);
    }

    //---
    // Instance helpers
    //---

    private void appendXMLDeclaration(DigitalObject obj,
                                      String encoding,
                                      StringBuffer buf) {
        buf.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\" ?>\n");
    }

    private void appendRootElementStart(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
        buf.append("<" + METS.METS.qName);
        if (m_format.equals(METS_EXT1_1)) {
            buf.append(" " + METS_EXT.EXT_VERSION.localName + "=\"1.1\"");
        }
        buf.append(" " + METS.OBJID.localName + "=\"" + obj.getPid() + "\"");

        if (m_format.equals(METS_EXT1_0)
                && DOTranslationUtility.getTypeAttribute(obj) != null) {
            buf.append(" " + METS.TYPE.localName + "=\""
                    + DOTranslationUtility.getTypeAttribute(obj).localName
                    + "\"");
        }

        buf.append("\n");
        String label = obj.getLabel();
        if (label != null && label.length() > 0) {
            buf.append("        " + METS.LABEL.localName + "=\""
                    + StreamUtility.enc(label) + "\"\n");
        }
        buf.append("        xmlns:" + METS.prefix + "=\"" + METS.uri + "\"\n");
        if (m_format.equals(METS_EXT1_0)) {
            buf.append("        xmlns:" + XLINK.prefix + "=\"" + OLD_XLINK.uri
                    + "\"\n");
        } else {
            buf.append("        xmlns:" + XLINK.prefix + "=\"" + XLINK.uri
                    + "\"\n");
        }
        buf.append("        xmlns:" + XSI.prefix + "=\"" + XSI.uri + "\"\n");
        buf.append("        " + XSI.SCHEMA_LOCATION.qName + "=\"" + METS.uri
                + " " + m_format.xsdLocation + "\">\n");
    }

    private void appendHdr(DigitalObject obj, StringBuffer buf) {
        buf.append("  <" + METS.prefix + ":metsHdr");
        Date cDate = obj.getCreateDate();
        if (cDate != null) {
            buf.append(" CREATEDATE=\"");
            buf.append(DateUtility.convertDateToString(cDate));
            buf.append("\"");
        }
        Date mDate = obj.getLastModDate();
        if (mDate != null) {
            buf.append(" LASTMODDATE=\"");
            buf.append(DateUtility.convertDateToString(mDate) + "\"");
        }
        String state = obj.getState();
        if (state != null && !state.equals("")) {
            buf.append(" RECORDSTATUS=\"");
            buf.append(state + "\"");
        }
        buf.append(">\n");
        // use agent to identify the owner of the digital object
        String ownerId = obj.getOwnerId();
        if (ownerId != null && !ownerId.equals("")) {
            buf.append("  <" + METS.prefix + ":agent");
            buf.append(" ROLE=\"IPOWNER\">\n");
            buf.append("    <" + METS.prefix + ":name>" + ownerId + "</"
                    + METS.prefix + ":name>\n");
            buf.append("  </" + METS.prefix + ":agent>\n");
        }
        buf.append("  </" + METS.prefix + ":metsHdr>\n");
    }

    private void appendDescriptiveMD(DigitalObject obj,
                                     StringBuffer buf,
                                     String encoding)
            throws ObjectIntegrityException, UnsupportedEncodingException,
            StreamIOException {
        Iterator<String> iter = obj.datastreamIdIterator();
        while (iter.hasNext()) {
            String id = iter.next();
            Datastream firstDS = obj.datastreams(id).iterator().next();
            if (firstDS.DSControlGrp.equals("X")
                    && ((DatastreamXMLMetadata) firstDS).DSMDClass == DatastreamXMLMetadata.DESCRIPTIVE) {
                appendMDSec(obj,
                            "dmdSecFedora",
                            "descMD",
                            obj.datastreams(id),
                            buf,
                            encoding);
            }
        }
    }

    private void appendMDSec(DigitalObject obj,
                             String outerName,
                             String innerName,
                             Iterable<Datastream> XMLMetadata,
                             StringBuffer buf,
                             String encoding) throws ObjectIntegrityException,
            UnsupportedEncodingException, StreamIOException {
        DatastreamXMLMetadata first =
                (DatastreamXMLMetadata) DOTranslationUtility
                        .setDatastreamDefaults((DatastreamXMLMetadata) XMLMetadata
                                .iterator().next());
        buf.append("  <" + METS.prefix + ":" + outerName + " ID=\""
                + first.DatastreamID + "\" STATUS=\"" + first.DSState
                + "\" VERSIONABLE=\"" + first.DSVersionable + "\">\n");
        for (Datastream d : XMLMetadata) {
            DatastreamXMLMetadata ds =
                    (DatastreamXMLMetadata) DOTranslationUtility
                            .setDatastreamDefaults((DatastreamXMLMetadata) d);
            String dateAttr = "";
            if (ds.DSCreateDT != null) {
                dateAttr =
                        " CREATED=\""
                                + DateUtility
                                        .convertDateToString(ds.DSCreateDT)
                                + "\"";
            }
            buf.append("    <" + METS.prefix + ":" + innerName + " ID=\""
                    + ds.DSVersionID + "\"" + dateAttr + ">\n");
            String mdType = ds.DSInfoType;
            String otherAttr = "";
            if (!mdType.equals("MARC") && !mdType.equals("EAD")
                    && !mdType.equals("DC") && !mdType.equals("NISOIMG")
                    && !mdType.equals("LC-AV") && !mdType.equals("VRA")
                    && !mdType.equals("TEIHDR") && !mdType.equals("DDI")
                    && !mdType.equals("FGDC")) {
                mdType = "OTHER";
                otherAttr =
                        " OTHERMDTYPE=\"" + StreamUtility.enc(ds.DSInfoType)
                                + "\" ";
            }
            String labelAttr = "";
            if (ds.DSLabel != null && !ds.DSLabel.equals("")) {
                labelAttr = " LABEL=\"" + StreamUtility.enc(ds.DSLabel) + "\"";
            }
            // FORMAT_URI attribute is optional so check if non-empty
            String formatURIAttr = "";
            if (ds.DSFormatURI != null && !ds.DSFormatURI.equals("")) {
                formatURIAttr =
                        " FORMAT_URI=\"" + StreamUtility.enc(ds.DSFormatURI)
                                + "\"";
            }
            // ALT_IDS attribute is optional so check if non-empty
            String altIdsAttr = "";
            String altIds = DOTranslationUtility.oneString(ds.DatastreamAltIDs);
            if (altIds != null && !altIds.equals("")) {
                altIdsAttr = " ALT_IDS=\"" + StreamUtility.enc(altIds) + "\"";
            }
            // CHECKSUM and CHECKSUMTYPE are also optional
            String checksumTypeAttr = "";
            String checksumAttr = "";
            String csType = ds.DSChecksumType;
            if (csType != null
                    && csType.length() > 0 
                    && !csType.equals(Datastream.CHECKSUMTYPE_DISABLED)) {
                checksumTypeAttr =
                        " CHECKSUMTYPE=\"" + StreamUtility.enc(csType) + "\"";
                checksumAttr =
                        " CHECKSUM=\"" + StreamUtility.enc(ds.DSChecksum)
                                + "\"";
            }
            buf.append("      <" + METS.prefix + ":mdWrap MIMETYPE=\""
                    + StreamUtility.enc(ds.DSMIME) + "\"" + " MDTYPE=\""
                    + mdType + "\"" + otherAttr + labelAttr + formatURIAttr
                    + altIdsAttr + checksumAttr + checksumTypeAttr + ">\n");
            buf.append("        <" + METS.prefix + ":xmlData>\n");

            // If WSDL or SERVICE-PROFILE datastream (in BMech) 
            // make sure that any embedded URLs are encoded 
            // appropriately for either EXPORT or STORE.
            if (obj.hasRelationship(MODEL.HAS_MODEL, SERVICE_DEPLOYMENT_3_0)
                    && ds.DatastreamID.equals("SERVICE-PROFILE")
                    || ds.DatastreamID.equals("WSDL")) {
                buf.append(DOTranslationUtility
                        .normalizeInlineXML(new String(ds.xmlContent, "UTF-8")
                                .trim(), m_transContext));
            } else {
                DOTranslationUtility.appendXMLStream(ds.getContentStream(),
                                                     buf,
                                                     encoding);
            }
            buf.append("\n        </" + METS.prefix + ":xmlData>");
            buf.append("      </" + METS.prefix + ":mdWrap>\n");
            buf.append("    </" + METS.prefix + ":" + innerName + ">\n");
        }
        buf.append("  </" + METS.prefix + ":" + outerName + ">\n");
    }

    private void appendAuditRecordAdminMD(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
        if (obj.getAuditRecords().size() > 0) {
            buf.append("  <" + METS.prefix + ":amdSec ID=\"AUDIT\""
                    + " STATUS=\"A\" VERSIONABLE=\"false\">\n");
            buf.append("    <" + METS.prefix + ":digiprovMD ID=\""
                    + "AUDIT.0\" CREATED=\""
                    + DateUtility.convertDateToString(obj.getCreateDate())
                    + "\">\n");
            buf.append("      <" + METS.prefix
                    + ":mdWrap MIMETYPE=\"text/xml\" "
                    + "MDTYPE=\"OTHER\" OTHERMDTYPE=\"FEDORA-AUDIT\""
                    + " LABEL=\"Audit Trail for this object\""
                    + " FORMAT_URI=\"" + AUDIT1_0.uri + "\">\n");
            buf.append("        <" + METS.prefix + ":xmlData>\n");
            buf.append(DOTranslationUtility.getAuditTrail(obj));
            buf.append("        </" + METS.prefix + ":xmlData>\n");
            buf.append("      </" + METS.prefix + ":mdWrap>\n");
            buf.append("    </" + METS.prefix + ":digiprovMD>\n");
            buf.append("  </" + METS.prefix + ":amdSec>\n");
        }
    }

    private void appendOtherAdminMD(DigitalObject obj,
                                    StringBuffer buf,
                                    String encoding)
            throws ObjectIntegrityException, UnsupportedEncodingException,
            StreamIOException {
        Iterator<String> iter = obj.datastreamIdIterator();
        while (iter.hasNext()) {
            String id = iter.next();
            Datastream firstDS =
                    (Datastream) obj.datastreams(id).iterator().next();
            // First, work with the first version to get the mdClass set to
            // a proper value required in the METS XML Schema.
            if (firstDS.DSControlGrp.equals("X")
                    && ((DatastreamXMLMetadata) firstDS).DSMDClass != DatastreamXMLMetadata.DESCRIPTIVE) {
                DatastreamXMLMetadata md = (DatastreamXMLMetadata) firstDS;
                // Default mdClass to techMD when a valid one does not appear
                // (say because the object was born as FOXML)
                String mdClass = "techMD";
                if (md.DSMDClass == DatastreamXMLMetadata.TECHNICAL) {
                    mdClass = "techMD";
                } else if (md.DSMDClass == DatastreamXMLMetadata.SOURCE) {
                    mdClass = "sourceMD";
                } else if (md.DSMDClass == DatastreamXMLMetadata.RIGHTS) {
                    mdClass = "rightsMD";
                } else if (md.DSMDClass == DatastreamXMLMetadata.DIGIPROV) {
                    mdClass = "digiprovMD";
                }
                // Then, pass everything along to do the actual serialization
                appendMDSec(obj,
                            "amdSec",
                            mdClass,
                            obj.datastreams(id),
                            buf,
                            encoding);
            }
        }
    }

    private void appendFileSecs(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException, StreamIOException {
        Iterator<String> iter = obj.datastreamIdIterator();
        boolean didFileSec = false;
        while (iter.hasNext()) {
            Datastream ds =
                    DOTranslationUtility.setDatastreamDefaults((Datastream) obj
                            .datastreams(iter.next()).iterator().next());
            if (!ds.DSControlGrp.equals("X")) {
                if (!didFileSec) {
                    didFileSec = true;
                    buf.append("  <" + METS.prefix + ":fileSec>\n");
                    buf.append("    <" + METS.prefix
                            + ":fileGrp ID=\"DATASTREAMS\">\n");
                }
                buf.append("      <" + METS.prefix + ":fileGrp ID=\""
                        + ds.DatastreamID + "\" STATUS=\"" + ds.DSState
                        + "\" VERSIONABLE=\"" + ds.DSVersionable + "\">\n");
                Iterator<Datastream> contentIter =
                        obj.datastreams(ds.DatastreamID).iterator();
                while (contentIter.hasNext()) {
                    Datastream dsc =
                            DOTranslationUtility
                                    .setDatastreamDefaults(contentIter.next());
                    String labelAttr = "";
                    if (dsc.DSLabel != null && !dsc.DSLabel.equals("")) {
                        labelAttr =
                                " " + XLINK.prefix + ":title=\""
                                        + StreamUtility.enc(dsc.DSLabel) + "\"";
                    }
                    String dateAttr = "";
                    if (dsc.DSCreateDT != null) {
                        dateAttr =
                                " CREATED=\""
                                        + DateUtility
                                                .convertDateToString(dsc.DSCreateDT)
                                        + "\"";
                    }
                    // SIZE attribute is optional so check if non-zero
                    String sizeAttr = "";
                    if (dsc.DSSize != 0) {
                        sizeAttr = " SIZE=\"" + dsc.DSSize + "\"";
                    }
                    // FORMAT_URI attribute is optional so check if non-empty
                    String formatURIAttr = "";
                    if (dsc.DSFormatURI != null && !dsc.DSFormatURI.equals("")) {
                        formatURIAttr =
                                " FORMAT_URI=\""
                                        + StreamUtility.enc(dsc.DSFormatURI)
                                        + "\"";
                    }
                    // ALT_IDS attribute is optional so check if non-empty
                    String altIdsAttr = "";
                    String altIds =
                            DOTranslationUtility
                                    .oneString(dsc.DatastreamAltIDs);
                    if (altIds != null && !altIds.equals("")) {
                        altIdsAttr =
                                " ALT_IDS=\"" + StreamUtility.enc(altIds)
                                        + "\"";
                    }
                    // CHECKSUM and CHECKSUMTYPE are also optional
                    String checksumTypeAttr = "";
                    String checksumAttr = "";
                    String csType = ds.DSChecksumType;
                    if (csType != null 
                            && csType.length() > 0
                            && !csType.equals(Datastream.CHECKSUMTYPE_DISABLED)) {
                        checksumTypeAttr =
                                " CHECKSUMTYPE=\"" + StreamUtility.enc(csType)
                                        + "\"";
                        checksumAttr =
                                " CHECKSUM=\""
                                        + StreamUtility.enc(ds.DSChecksum)
                                        + "\"";
                    }
                    buf.append("        <" + METS.prefix + ":file ID=\""
                            + dsc.DSVersionID + "\"" + dateAttr
                            + " MIMETYPE=\"" + StreamUtility.enc(dsc.DSMIME)
                            + "\"" + sizeAttr + formatURIAttr + altIdsAttr
                            + checksumAttr + checksumTypeAttr + " OWNERID=\""
                            + dsc.DSControlGrp + "\">\n");
                    if (m_transContext == DOTranslationUtility.SERIALIZE_EXPORT_ARCHIVE
                            && dsc.DSControlGrp.equalsIgnoreCase("M")) {
                        buf
                                .append("          <"
                                        + METS.prefix
                                        + ":FContent> \n"
                                        + StringUtility
                                                .splitAndIndent(StreamUtility
                                                                        .encodeBase64(dsc
                                                                                .getContentStream()),
                                                                14,
                                                                80)
                                        + "          </" + METS.prefix
                                        + ":FContent> \n");
                    } else {
                        buf
                                .append("          <"
                                        + METS.prefix
                                        + ":FLocat"
                                        + labelAttr
                                        + " LOCTYPE=\"URL\" "
                                        + XLINK.prefix
                                        + ":href=\""
                                        + StreamUtility
                                                .enc(DOTranslationUtility
                                                        .normalizeDSLocationURLs(obj
                                                                                         .getPid(),
                                                                                 dsc,
                                                                                 m_transContext).DSLocation)
                                        + "\"/>\n");
                    }
                    buf.append("        </" + METS.prefix + ":file>\n");
                }
                buf.append("      </" + METS.prefix + ":fileGrp>\n");
            }
        }
        if (didFileSec) {
            buf.append("    </" + METS.prefix + ":fileGrp>\n");
            buf.append("  </" + METS.prefix + ":fileSec>\n");
        }
    }

    private void appendStructMaps(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
        Iterator<String> dissIdIter = obj.disseminatorIdIterator();
        while (dissIdIter.hasNext()) {
            String did = dissIdIter.next();
            Iterator<Disseminator> dissIter = obj.disseminators(did).iterator();
            while (dissIter.hasNext()) {
                Disseminator diss =
                        DOTranslationUtility.setDisseminatorDefaults(dissIter
                                .next());
                String labelAttr = "";
                if (diss.dsBindMap.dsBindMapLabel != null
                        && !diss.dsBindMap.dsBindMapLabel.equals("")) {
                    labelAttr =
                            " LABEL=\""
                                    + StreamUtility
                                            .enc(diss.dsBindMap.dsBindMapLabel)
                                    + "\"";
                }
                buf.append("  <" + METS.prefix + ":structMap ID=\""
                        + diss.dsBindMapID
                        + "\" TYPE=\"fedora:dsBindingMap\">\n");
                buf.append("    <" + METS.prefix + ":div TYPE=\""
                        + diss.sDepID + "\"" + labelAttr + ">\n");
                DSBinding[] bindings = diss.dsBindMap.dsBindings;
                for (int i = 0; i < bindings.length; i++) {
                    if (bindings[i].bindKeyName == null
                            || bindings[i].bindKeyName.equals("")) {
                        throw new ObjectIntegrityException("Object's disseminator"
                                + " binding map binding must have a binding key name.");
                    }
                    buf.append("      <" + METS.prefix + ":div TYPE=\"");
                    buf.append(bindings[i].bindKeyName);
                    if (bindings[i].bindLabel != null
                            && !bindings[i].bindLabel.equals("")) {
                        buf.append("\" LABEL=\"");
                        buf.append(StreamUtility.enc(bindings[i].bindLabel));
                    }
                    if (bindings[i].seqNo != null
                            && !bindings[i].seqNo.equals("")) {
                        buf.append("\" ORDER=\"");
                        buf.append(bindings[i].seqNo);
                    }
                    if (bindings[i].datastreamID == null
                            || bindings[i].datastreamID.equals("")) {
                        throw new ObjectIntegrityException("Object's disseminator"
                                + " binding map binding must point to a datastream.");
                    }
                    buf.append("\">\n        <" + METS.prefix
                            + ":fptr FILEID=\"" + bindings[i].datastreamID
                            + "\"/>\n" + "      </" + METS.prefix + ":div>\n");
                }
                buf.append("    </" + METS.prefix + ":div>\n");
                buf.append("  </" + METS.prefix + ":structMap>\n");
            }
        }
    }

    private void appendDisseminators(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
        Iterator<String> dissIdIter = obj.disseminatorIdIterator();
        while (dissIdIter.hasNext()) {
            String did = dissIdIter.next();
            Disseminator diss =
                    DOTranslationUtility.setDisseminatorDefaults(obj
                            .disseminators(did).get(0));
            buf.append("  <" + METS.prefix + ":behaviorSec ID=\"" + did
                    + "\" STATUS=\"" + diss.dissState + "\">\n");
            for (int i = 0; i < obj.disseminators(did).size(); i++) {
                diss =
                        DOTranslationUtility
                                .setDisseminatorDefaults((Disseminator) obj
                                        .disseminators(did).get(i));
                String dissLabelAttr = "";
                if (diss.dissLabel != null && !diss.dissLabel.equals("")) {
                    dissLabelAttr =
                            " LABEL=\"" + StreamUtility.enc(diss.dissLabel)
                                    + "\"";
                }
                buf.append("    <" + METS.prefix + ":serviceBinding ID=\""
                        + diss.dissVersionID + "\" STRUCTID=\""
                        + diss.dsBindMapID + "\" BTYPE=\"" + diss.bDefID
                        + "\" CREATED=\""
                        + DateUtility.convertDateToString(diss.dissCreateDT)
                        + "\"" + dissLabelAttr + ">\n");
                buf.append("      <" + METS.prefix + ":interfaceMD"
                        + " LOCTYPE=\"URN\" " + XLINK.prefix + ":href=\""
                        + diss.bDefID + "\"/>\n");
                buf.append("      <" + METS.prefix + ":serviceBindMD"
                        + " LOCTYPE=\"URN\" " + XLINK.prefix + ":href=\""
                        + diss.sDepID + "\"/>\n");

                buf.append("    </" + METS.prefix + ":serviceBinding>\n");
            }
            buf.append("  </" + METS.prefix + ":behaviorSec>\n");
        }
    }

    private void appendRootElementEnd(StringBuffer buf) {
        buf.append("</" + METS.prefix + ":mets>");
    }

}
