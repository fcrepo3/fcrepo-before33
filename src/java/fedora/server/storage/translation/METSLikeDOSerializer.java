package fedora.server.storage.translation;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamWriteException;
import fedora.server.storage.types.DigitalObject;

public class METSLikeDOSerializer 
        implements DOSerializer {

    public static final String FEDORA_AUDIT_NS="http://fedora.comm.nsdlib.org/audit";
    public static final String METS_PREFIX="METS";
    public static final String METS_NS="http://www.loc.gov/METS/";
    public static final String METS_XLINK_NS="http://www.w3.org/TR/xlink";
    public static final String REAL_XLINK_NS="http://www.w3.org/1999/xlink";

    private String m_encoding;
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
        "testEncoding".getBytes(encoding); // throws UnsuppEnc if fails
        m_encoding=encoding;
        StringBuffer buf=new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\" ?>\n");
        // begin mets element
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
        buf.append("/>\n");
        appendHdr(obj, buf);
        // end mets element
        buf.append("</" + METS_PREFIX + ":mets>");
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
    
}