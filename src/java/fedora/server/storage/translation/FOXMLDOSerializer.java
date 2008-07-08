/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.storage.translation;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import fedora.common.Constants;
import fedora.common.rdf.RDFName;
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
 * Serializes objects in the constructor-provided version of FOXML.
 * 
 * @author Sandy Payette
 * @author Chris Wilper
 */
@SuppressWarnings("deprecation")
public class FOXMLDOSerializer
        implements DOSerializer, Constants {

    /**
     * The format this serializer will write if unspecified at construction.
     * This defaults to the latest FOXML format.
     */
    public static final XMLFormat DEFAULT_FORMAT = FOXML1_1;

    /** Logger for this class. */
    private static final Logger LOG = Logger.getLogger(FOXMLDOSerializer.class);

    /** The format this serializer writes. */
    private final XMLFormat m_format;

    /** The current translation context. */
    private int m_transContext;

    /**
     * Creates a serializer that writes the default FOXML format.
     */
    public FOXMLDOSerializer() {
        m_format = DEFAULT_FORMAT;
    }

    /**
     * Creates a serializer that writes the given FOXML format.
     * 
     * @param format
     *        the version-specific FOXML format.
     * @throws IllegalArgumentException
     *         if format is not a known FOXML format.
     */
    public FOXMLDOSerializer(XMLFormat format) {
        if (format.equals(FOXML1_0) || format.equals(FOXML1_1)) {
            m_format = format;
        } else {
            throw new IllegalArgumentException("Not a FOXML format: "
                    + format.uri);
        }
    }

    //---
    // DOSerializer implementation
    //---

    /**
     * {@inheritDoc}
     */
    public DOSerializer getInstance() {
        return new FOXMLDOSerializer(m_format);
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
        appendProperties(obj, buf, encoding);
        appendAudit(obj, buf, encoding);
        appendDatastreams(obj, buf, encoding);
        if (m_format.equals(FOXML1_0)) {
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
        buf.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n");
    }

    private void appendRootElementStart(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
        buf.append("<" + FOXML.DIGITAL_OBJECT.qName);
        if (m_format.equals(FOXML1_1)) {
            buf.append(" " + FOXML.VERSION.localName + "=\"1.1\"");
        }
        buf.append(" " + FOXML.PID.localName + "=\"" + obj.getPid() + "\"");
        if (m_transContext == DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC) {
            buf.append(" " + FOXML.FEDORA_URI.localName + "=\"info:fedora/"
                    + obj.getPid() + "\"");
        }
        buf.append("\n");
        buf
                .append("        xmlns:" + FOXML.prefix + "=\"" + FOXML.uri
                        + "\"\n");
        buf.append("        xmlns:" + XSI.prefix + "=\"" + XSI.uri + "\"\n");
        buf.append("        " + XSI.SCHEMA_LOCATION.qName + "=\"" + FOXML.uri
                + " " + m_format.xsdLocation + "\">\n");
    }

    private void appendProperties(DigitalObject obj,
                                  StringBuffer buf,
                                  String encoding)
            throws ObjectIntegrityException {


        String state = DOTranslationUtility.getStateAttribute(obj);
        String ownerId = obj.getOwnerId();
        String label = obj.getLabel();
        Date cdate = obj.getCreateDate();
        Date mdate = obj.getLastModDate();

        buf.append("    <" + FOXML.prefix + ":objectProperties>\n");

        /*
         * fType is eliminated in foxml 1.1+, so choose the best reasonable
         * value for 1.0 serializations
         */
        if (m_format.equals(FOXML1_0)) {
            RDFName ftype = DOTranslationUtility.getTypeAttribute(obj);
            if (ftype != null && !ftype.equals("")) {
                buf.append("        <" + FOXML.prefix + ":property NAME=\""
                        + RDF.TYPE.uri + "\"" + " VALUE=\"" + ftype.uri
                        + "\"/>\n");
            }
        }

        if (state != null && !state.equals("")) {
            buf.append("        <" + FOXML.prefix + ":property NAME=\""
                    + MODEL.STATE.uri + "\"" + " VALUE=\"" + state + "\"/>\n");
        }
        if (label != null && !label.equals("")) {
            buf.append("        <" + FOXML.prefix + ":property NAME=\""
                    + MODEL.LABEL.uri + "\"" + " VALUE=\""
                    + StreamUtility.enc(label) + "\"/>\n");
        }
        if (ownerId != null && !ownerId.equals("")) {
            buf.append("        <" + FOXML.prefix + ":property NAME=\""
                    + MODEL.OWNER.uri + "\"" + " VALUE=\""
                    + StreamUtility.enc(ownerId) + "\"/>\n");
        }
        if (cdate != null) {
            buf.append("        <" + FOXML.prefix + ":property NAME=\""
                    + MODEL.CREATED_DATE.uri + "\"" + " VALUE=\""
                    + DateUtility.convertDateToString(cdate) + "\"/>\n");
        }
        if (mdate != null) {
            buf.append("        <" + FOXML.prefix + ":property NAME=\""
                    + VIEW.LAST_MODIFIED_DATE.uri + "\"" + " VALUE=\""
                    + DateUtility.convertDateToString(mdate) + "\"/>\n");
        }

        /*
         * FIXME: Content model properties in foxml 1.0 are now lossy - they
         * will be simply be ignored by serialization/deserialization. Is that
         * OK, or should we do something special with them?
         */

        Iterator<String> iter = obj.getExtProperties().keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            buf.append("        <" + FOXML.prefix + ":extproperty NAME=\""
                    + name + "\"" + " VALUE=\"" + obj.getExtProperty(name)
                    + "\"/>\n");
        }
        buf.append("    </" + FOXML.prefix + ":objectProperties>\n");
    }

    private void appendDatastreams(DigitalObject obj,
                                   StringBuffer buf,
                                   String encoding)
            throws ObjectIntegrityException, UnsupportedEncodingException,
            StreamIOException {
        Iterator<String> iter = obj.datastreamIdIterator();
        while (iter.hasNext()) {
            String dsid = (String) iter.next();
            boolean haveWrittenCommonAttributes = false;

            // AUDIT datastream is rebuilt from the latest in-memory audit trail
            // which is a separate array list in the DigitalObject class.
            // So, ignore it here.
            if (dsid.equals("AUDIT") || dsid.equals("FEDORA-AUDITTRAIL")) {
                continue;
            }
            // Given a datastream ID, get all the datastream versions.
            // Use the first version to pick up the attributes common to all versions.

            for (Datastream v : obj.datastreams(dsid)) {
                Datastream vds = DOTranslationUtility.setDatastreamDefaults(v);

                // insert the ds attributes common to all versions, when necessary
                if (!haveWrittenCommonAttributes) {
                    String dsURIAttr = "";
                    if (m_transContext == DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC) {
                        dsURIAttr =
                                " FEDORA_URI=\"" + "info:fedora/"
                                        + obj.getPid() + "/" + vds.DatastreamID
                                        + "\"";
                    }
                    buf.append("    <" + FOXML.prefix + ":datastream ID=\""
                            + vds.DatastreamID + "\"" + dsURIAttr + " STATE=\""
                            + vds.DSState + "\"" + " CONTROL_GROUP=\""
                            + vds.DSControlGrp + "\"" + " VERSIONABLE=\""
                            + vds.DSVersionable + "\">\n");
                    haveWrittenCommonAttributes = true;
                }

                // insert the ds version elements
                String altIdsAttr = "";
                String altIds =
                        DOTranslationUtility.oneString(vds.DatastreamAltIDs);
                if (altIds != null && !altIds.equals("")) {
                    altIdsAttr =
                            " ALT_IDS=\"" + StreamUtility.enc(altIds) + "\"";
                }
                String formatURIAttr = "";
                if (vds.DSFormatURI != null && !vds.DSFormatURI.equals("")) {
                    formatURIAttr =
                            " FORMAT_URI=\""
                                    + StreamUtility.enc(vds.DSFormatURI) + "\"";
                }
                String dateAttr = "";
                if (vds.DSCreateDT != null) {
                    dateAttr =
                            " CREATED=\""
                                    + DateUtility
                                            .convertDateToString(vds.DSCreateDT)
                                    + "\"";
                }
                buf.append("        <" + FOXML.prefix
                        + ":datastreamVersion ID=\"" + vds.DSVersionID + "\""
                        + " LABEL=\"" + StreamUtility.enc(vds.DSLabel) + "\""
                        + dateAttr + altIdsAttr + " MIMETYPE=\""
                        + StreamUtility.enc(vds.DSMIME) + "\"" + formatURIAttr
                        + " SIZE=\"" + vds.DSSize + "\">\n");

                // include checksum if it has a value
                String csType = vds.getChecksumType();
                if (csType != null && csType.length() > 0
                        && !csType.equals("none")) {
                    buf.append("            <" + FOXML.prefix
                            + ":contentDigest TYPE=\"" + csType + "\""
                            + " DIGEST=\"" + vds.getChecksum() + "\"/>\n");
                }

                // if E or R insert ds content location as URL
                if (vds.DSControlGrp.equalsIgnoreCase("E")
                        || vds.DSControlGrp.equalsIgnoreCase("R")) {
                    buf
                            .append("            <"
                                    + FOXML.prefix
                                    + ":contentLocation TYPE=\""
                                    + "URL\""
                                    + " REF=\""
                                    + StreamUtility
                                            .enc(DOTranslationUtility
                                                    .normalizeDSLocationURLs(obj
                                                                                     .getPid(),
                                                                             vds,
                                                                             m_transContext).DSLocation)
                                    + "\"/>\n");
                    // if M insert ds content location as an internal identifier				
                } else if (vds.DSControlGrp.equalsIgnoreCase("M")) {
                    if (m_transContext == DOTranslationUtility.SERIALIZE_EXPORT_ARCHIVE) {
                        buf
                                .append("            <"
                                        + FOXML.prefix
                                        + ":binaryContent> \n"
                                        + StringUtility
                                                .splitAndIndent(StreamUtility
                                                                        .encodeBase64(vds
                                                                                .getContentStream()),
                                                                14,
                                                                80)
                                        + "            </" + FOXML.prefix
                                        + ":binaryContent> \n");
                    } else {
                        buf
                                .append("            <"
                                        + FOXML.prefix
                                        + ":contentLocation TYPE=\""
                                        + "INTERNAL_ID\""
                                        + " REF=\""
                                        + StreamUtility
                                                .enc(DOTranslationUtility
                                                        .normalizeDSLocationURLs(obj
                                                                                         .getPid(),
                                                                                 vds,
                                                                                 m_transContext).DSLocation)
                                        + "\"/>\n");
                    }
                    // if X insert inline XML
                } else if (vds.DSControlGrp.equalsIgnoreCase("X")) {
                    appendInlineXML(obj,
                                    (DatastreamXMLMetadata) vds,
                                    buf,
                                    encoding);
                }

                buf.append("        </" + FOXML.prefix
                        + ":datastreamVersion>\n");
            }
            buf.append("    </" + FOXML.prefix + ":datastream>\n");
        }
    }

    private void appendAudit(DigitalObject obj,
                             StringBuffer buf,
                             String encoding) throws ObjectIntegrityException {

        if (obj.getAuditRecords().size() > 0) {
            // Audit trail datastream re-created from audit records.
            // There is only ONE version of the audit trail datastream!
            String dsURIAttr = "";
            if (m_transContext == DOTranslationUtility.SERIALIZE_EXPORT_PUBLIC) {
                dsURIAttr =
                        " FEDORA_URI=\"" + "info:fedora/" + obj.getPid()
                                + "/AUDIT" + "\"";
            }
            buf.append("    <" + FOXML.prefix + ":datastream ID=\"" + "AUDIT"
                    + "\"" + dsURIAttr + " STATE=\"" + "A" + "\""
                    + " CONTROL_GROUP=\"" + "X" + "\"" + " VERSIONABLE=\""
                    + "false" + "\">\n");
            // insert the ds version-level elements
            buf.append("        <" + FOXML.prefix + ":datastreamVersion ID=\""
                    + "AUDIT.0" + "\"" + " LABEL=\""
                    + "Fedora Object Audit Trail" + "\"" + " CREATED=\""
                    + DateUtility.convertDateToString(obj.getCreateDate())
                    + "\"" + " MIMETYPE=\"" + "text/xml" + "\""
                    + " FORMAT_URI=\"" + AUDIT1_0.uri + "\">\n");
            buf.append("            <" + FOXML.prefix + ":xmlContent>\n");
            buf.append(DOTranslationUtility.getAuditTrail(obj));
            buf.append("            </" + FOXML.prefix + ":xmlContent>\n");
            buf.append("        </" + FOXML.prefix + ":datastreamVersion>\n");
            buf.append("    </" + FOXML.prefix + ":datastream>\n");
        }
    }

    private void appendInlineXML(DigitalObject obj,
                                 DatastreamXMLMetadata ds,
                                 StringBuffer buf,
                                 String encoding)
            throws ObjectIntegrityException, UnsupportedEncodingException,
            StreamIOException {

        buf.append("            <" + FOXML.prefix + ":xmlContent>\n");

        // Relative Repository URLs: If it's a WSDL or SERVICE-PROFILE datastream 
        // in a SDep object search for any embedded URLs that are relative to
        // the local repository (like internal service URLs) and make sure they
        // are converted appropriately for the translation context.
        if (obj.hasRelationship(MODEL.HAS_MODEL, SERVICE_DEPLOYMENT_3_0)
                && (ds.DatastreamID.equals("SERVICE-PROFILE") || ds.DatastreamID
                        .equals("WSDL"))) {
            // FIXME! We need a more efficient way than to search
            // the whole block of inline XML. We really only want to 
            // look at service URLs in the XML.
            buf.append(DOTranslationUtility
                    .normalizeInlineXML(new String(ds.xmlContent, "UTF-8")
                            .trim(), m_transContext));
        } else {
            DOTranslationUtility.appendXMLStream(ds.getContentStream(),
                                                 buf,
                                                 encoding);
        }
        buf.append("\n            </" + FOXML.prefix + ":xmlContent>\n");
    }

    private void appendDisseminators(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {

        Iterator<String> dissIdIter = obj.disseminatorIdIterator();
        while (dissIdIter.hasNext()) {
            String did = (String) dissIdIter.next();
            List<Disseminator> dissList = obj.disseminators(did);

            for (int i = 0; i < dissList.size(); i++) {
                Disseminator vdiss =
                        DOTranslationUtility
                                .setDisseminatorDefaults((Disseminator) obj
                                        .disseminators(did).get(i));
                // insert the disseminator elements common to all versions.
                if (i == 0) {
                    buf.append("    <" + FOXML.prefix + ":disseminator ID=\""
                            + did + "\" BDEF_CONTRACT_PID=\"" + vdiss.bDefID
                            + "\" STATE=\"" + vdiss.dissState
                            + "\" VERSIONABLE=\"" + vdiss.dissVersionable
                            + "\">\n");
                }
                // insert the disseminator version-level elements
                String dissLabelAttr = "";
                if (vdiss.dissLabel != null && !vdiss.dissLabel.equals("")) {
                    dissLabelAttr =
                            " LABEL=\"" + StreamUtility.enc(vdiss.dissLabel)
                                    + "\"";
                }
                String dateAttr = "";
                if (vdiss.dissCreateDT != null) {
                    dateAttr =
                            " CREATED=\""
                                    + DateUtility
                                            .convertDateToString(vdiss.dissCreateDT)
                                    + "\"";
                }
                buf.append("        <" + FOXML.prefix
                        + ":disseminatorVersion ID=\"" + vdiss.dissVersionID
                        + "\"" + dissLabelAttr + " BMECH_SERVICE_PID=\""
                        + vdiss.sDepID + "\"" + dateAttr + ">\n");

                // datastream bindings...	
                DSBinding[] bindings = vdiss.dsBindMap.dsBindings;
                buf.append("            <" + FOXML.prefix
                        + ":serviceInputMap>\n");
                for (int j = 0; j < bindings.length; j++) {
                    if (bindings[j].seqNo == null) {
                        bindings[j].seqNo = "";
                    }
                    String labelAttr = "";
                    if (bindings[j].bindLabel != null
                            && !bindings[j].bindLabel.equals("")) {
                        labelAttr =
                                " LABEL=\""
                                        + StreamUtility
                                                .enc(bindings[j].bindLabel)
                                        + "\"";
                    }
                    String orderAttr = "";
                    if (bindings[j].seqNo != null
                            && !bindings[j].seqNo.equals("")) {
                        orderAttr = " ORDER=\"" + bindings[j].seqNo + "\"";
                    }
                    buf.append("                <" + FOXML.prefix
                            + ":datastreamBinding KEY=\""
                            + bindings[j].bindKeyName + "\""
                            + " DATASTREAM_ID=\"" + bindings[j].datastreamID
                            + "\"" + labelAttr + orderAttr + "/>\n");
                }
                buf.append("            </" + FOXML.prefix
                        + ":serviceInputMap>\n");
                buf.append("        </" + FOXML.prefix
                        + ":disseminatorVersion>\n");
            }
            buf.append("    </" + FOXML.prefix + ":disseminator>\n");
        }
    }

    private void appendRootElementEnd(StringBuffer buf) {
        buf.append("</" + FOXML.prefix + ":digitalObject>");
    }
}
