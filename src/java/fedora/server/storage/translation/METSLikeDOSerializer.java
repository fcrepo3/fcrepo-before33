package fedora.server.storage.translation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamWriteException;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;

public class METSLikeDOSerializer 
        implements DOSerializer {

    public static final String FEDORA_AUDIT_NS="http://fedora.comm.nsdlib.org/audit";
    public static final String METS_PREFIX="METS";
    public static final String METS_NS="http://www.loc.gov/METS/";
    public static final String METS_XLINK_NS="http://www.w3.org/TR/xlink";
    public static final String REAL_XLINK_NS="http://www.w3.org/1999/xlink";

    private String m_XLinkPrefix="xlink";
    private String m_fedoraAuditPrefix="fedora-auditing";
    private SimpleDateFormat m_formatter=
            new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

    public METSLikeDOSerializer() {
    }

    public DOSerializer getInstance() {
        return new METSLikeDOSerializer();
    }

    public void serialize(DigitalObject obj, OutputStream out, String encoding)
            throws ObjectIntegrityException, StreamIOException, 
            UnsupportedEncodingException {
        StringBuffer buf=new StringBuffer();
        appendXMLDeclaration(obj, encoding, buf);
        appendRootElementStart(obj, buf);
        appendHdr(obj, buf);
        appendDescriptiveMD(obj, buf);
        appendAuditRecordAdminMD(obj, buf);
        appendOtherAdminMD(obj, buf);
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
                + METS_NS + "\"\n");
        appendNamespaceDeclarations("           ",obj.getNamespaceMapping(),buf);
        String PID=obj.getPid();
        if (PID==null) {
            throw new ObjectIntegrityException("Object must have a pid.");
        }
        buf.append("           OBJID=\"" + PID + "\" TYPE=\"" 
                + getTypeAttribute(obj) + "\"");
        String label=obj.getLabel();
        if (label!=null) {
            buf.append("\n           LABEL=\"" + label + "\"");
        }
        String profile=obj.getContentModelId();
        if (profile!=null) {
            buf.append("\n           PROFILE=\"" + profile + "\"");
        }
        buf.append(">\n");
    }
    
    private void appendNamespaceDeclarations(String prepend, Map URIToPrefix, 
            StringBuffer buf) {
        Iterator iter=URIToPrefix.keySet().iterator();
        while (iter.hasNext()) {
            String URI=(String) iter.next();
            String prefix=(String) URIToPrefix.get(URI);
            if ( (URI.equals(METS_XLINK_NS)) || (URI.equals(REAL_XLINK_NS)) ) {
                m_XLinkPrefix=prefix;
            } else if (URI.equals(FEDORA_AUDIT_NS)) {
                m_fedoraAuditPrefix=prefix;
            } else if (!URI.equals(METS_NS)) {
                buf.append(prepend + "xmlns:" + prefix + "=\"" + URI + "\"\n");
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
    
    private void appendHdr(DigitalObject obj, StringBuffer buf) 
            throws ObjectIntegrityException {
        Date cDate=obj.getCreateDate();
        if (cDate==null) {
            throw new ObjectIntegrityException("Object must have a create date.");
        }
        buf.append(METS_PREFIX + ":metsHdr CREATEDATE=\"" 
                + m_formatter.format(cDate) + "\" LASTMODDATE=\"");
        Date mDate=obj.getLastModDate();
        if (mDate==null) {
            throw new ObjectIntegrityException("Object must have a last modified date.");
        }
        buf.append(m_formatter.format(mDate) + "\" RECORDSTATUS=\"");
        String state=obj.getState();
        if (state==null) {
            throw new ObjectIntegrityException("Object must have a state.");
        }
        buf.append(state + "\" />\n");
    }

    private void appendDescriptiveMD(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
        Iterator iter=obj.datastreamIdIterator();
        while (iter.hasNext()) {
            String id=(String) iter.next();
            Datastream firstDS=(Datastream) obj.datastreams(id).get(0);
            if ((firstDS.DSControlGrp.equals("X")) 
                    && (((DatastreamXMLMetadata) firstDS).DSMDClass==
                    DatastreamXMLMetadata.DESCRIPTIVE)) {
                appendMDSec(obj, "dmdSecFedora", "descMD", obj.datastreams(id),
                        buf);
            }
        }
    }
    
    private void appendMDSec(DigitalObject obj, String outerName, 
            String innerName, List XMLMetadata, StringBuffer buf) 
            throws ObjectIntegrityException {
        DatastreamXMLMetadata first=(DatastreamXMLMetadata) XMLMetadata.get(0);
        if (first.DatastreamID==null) {
            throw new ObjectIntegrityException("Datastream must have an id.");
        }
        if (first.DSState==null) {
            throw new ObjectIntegrityException("Datastream must have a state.");
        }
        buf.append("<" + METS_PREFIX + ":" + outerName + " ID=\"" 
                + first.DatastreamID + "\" STATUS=\"" + first.DSState + "\">\n");
        for (int i=0; i<XMLMetadata.size(); i++) {
            DatastreamXMLMetadata ds=(DatastreamXMLMetadata) XMLMetadata.get(i);
            if (ds.DSVersionID==null) {
                throw new ObjectIntegrityException("Datastream must have a version id.");
            }
            if (ds.DSCreateDT==null) {
                throw new ObjectIntegrityException("Datastream must have a creation date.");
            }
            buf.append("<" + METS_PREFIX + ":" + innerName + " ID=\"" 
                    + ds.DSVersionID + "\" CREATED=\"" + m_formatter.format(
                    ds.DSCreateDT) + "\">\n");
            if (ds.DSMIME==null) {
                ds.DSMIME="text/html";
            }
            buf.append("<" + METS_PREFIX + ":mdWrap MIMETYPE=\"");
            buf.append("</" + METS_PREFIX + ":" + innerName + ">\n");
        }
        buf.append("</" + METS_PREFIX + ":" + outerName + ">\n");
    }
    
    private void appendAuditRecordAdminMD(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
    }
    
    private void appendOtherAdminMD(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
    }
    
    private void appendFileSecs(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
    }
    
    private void appendStructMaps(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
    }
    
    private void appendDisseminators(DigitalObject obj, StringBuffer buf)
            throws ObjectIntegrityException {
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