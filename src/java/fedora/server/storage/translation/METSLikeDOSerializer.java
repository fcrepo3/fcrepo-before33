package fedora.server.storage.translation;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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

    public METSLikeDOSerializer() {
    }

    public DOSerializer getInstance() {
        return new METSLikeDOSerializer();
    }

    public void serialize(DigitalObject obj, OutputStream out, String encoding) {
    }
/*
    public void serialize(DigitalObject obj, OutputStream out, String encoding)
            throws ObjectIntegrityException, StreamIOException, 
            UnsupportedEncodingException {
        "testEncoding".getBytes(encoding); // throws UnsuppEnc if fails
        m_encoding=encoding;
        StringBuffer buf=new StringBuffer();
        buf.append("<?xml version=\"1.0\" encoding=\"" + encoding + "\" ?>\n");
        buf.append("<" + METS_PREFIX + ":mets xmlns:" + METS_PREFIX + "=\"" 
                + METS_NS + "\"\n");
        appendNamespaceDeclarations("           ",obj.getNamespaceMapping(),buf);
        String PID=obj.getPid();
        if (PID==null) {
            throw new ObjectIntegrityException("Object PID cannot be null.");
        }
        buf.append("           OBJID=\"" + PID + "\" TYPE=\"" 
                + getTypeAttribute(obj) + "\"\n");
         
        buf.append("</" + METS_PREFIX + ":mets>");
    }
    
    private void appendNamespaceDeclarations(String prepend, Map URIToPrefix, 
            StringBuffer out) {
        for (int i=0; i<URIToPrefix.keySet().size(); i++) {
            String URI=(String) URIToPrefix.keySet().get(i);
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
        if (obj.getFedoraObjectType()==DigitalObject.FEDORA_BDEF_OBJECT) {
        }
    }
    
*/

}