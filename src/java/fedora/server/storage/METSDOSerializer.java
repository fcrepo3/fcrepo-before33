package fedora.server.storage;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.StreamWriteException;
import fedora.server.storage.types.AuditRecord;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.utilities.DateUtility;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

public class METSDOSerializer 
        implements DOSerializer {

    // test object says this.. but it should be http://www.fedora.info/definitions/1/0/auditing/
    private final static String FEDORA_AUDIT_NAMESPACE_URI="http://fedora.comm.nsdlib.org/audit";
    
    private String m_characterEncoding;

    // java always supports UTF-8 and UTF-16 btw.
    public METSDOSerializer(String characterEncoding) 
            throws UnsupportedEncodingException {
        m_characterEncoding=characterEncoding;
        StringBuffer buf=new StringBuffer();
        buf.append("test");
        byte[] temp=buf.toString().getBytes(m_characterEncoding);
    }
    
    public String getEncoding() {
        return m_characterEncoding;
    }

    // subclasses should override this
    public static String getVersion() {
        return "1.0";
    }
    
    public void serialize(DigitalObject obj, OutputStream out) 
            throws ObjectIntegrityException, StreamIOException, 
            StreamWriteException {
        try {
            StringBuffer buf=new StringBuffer();
            buf.append("<?xml version=\"1.0\" ");
            buf.append("encoding=\"");
            buf.append(m_characterEncoding);
            buf.append("\" ?>\n");
            buf.append("<mets xmlns=\"http://www.loc.gov/METS/\" ");
            Iterator nsIter=obj.getNamespaceMapping().keySet().iterator();
            while (nsIter.hasNext()) {
                String uri=(String) nsIter.next();
                String prefix=(String) obj.getNamespaceMapping().get(uri);
                buf.append("xmlns:");
                buf.append(prefix);
                buf.append("=\"");
                buf.append(uri);
                buf.append("\" ");
            }
            buf.append("OBJID=\"");
            buf.append(obj.getPid());
            buf.append("\" LABEL=\"");
            buf.append(obj.getLabel());
            buf.append("\" TYPE=\"");
            buf.append("FedoraObject");
            buf.append("\" PROFILE=\"");
            buf.append(obj.getContentModelId());
            buf.append("\">\n  <metsHdr CREATEDATE=\"");
            buf.append(DateUtility.convertDateToString(obj.getCreateDate()));
            buf.append("\" LASTMODDATE=\"");
            buf.append(DateUtility.convertDateToString(obj.getLastModDate()));
            buf.append("\" RECORDSTATUS=\"");
            buf.append(obj.getState());
            buf.append("\">\n    <!-- This info can't be set via API-M -- if it existed, it was ignored during import -->\n");
            buf.append("  </metsHdr>\n");

            if (obj.getAuditRecords().size()>0) {
                buf.append("  <amdSec ID=\"FEDORA-AUDITTRAIL\">\n");
                String auditPrefix=(String) obj.getNamespaceMapping().get(FEDORA_AUDIT_NAMESPACE_URI);
                Iterator iter=obj.getAuditRecords().iterator();
                while (iter.hasNext()) {
                    AuditRecord audit=(AuditRecord) iter.next();
                    buf.append("    <digiprovMD ID=\"");
                    buf.append(audit.id);
                    buf.append("\" CREATED=\""); 
                    String createDate=DateUtility.convertDateToString(audit.date); 
                    buf.append(createDate);
                    buf.append("\" STATUS=\"A\">\n");  // status is always A 
                    buf.append("      <mdWrap MIMETYPE=\"text/xml\" MDTYPE=\"OTHER\" LABEL=\"Fedora Object Audit Trail Record\">\n");
                    buf.append("        <xmlData>\n");
                    buf.append("          <");
                    buf.append(auditPrefix);
                    buf.append(":record>\n");
                    buf.append("            <");
                    buf.append(auditPrefix);
                    buf.append(":process type=\"");
                    buf.append(audit.processType);
                    buf.append("\"/>\n");
                    
                    buf.append("            <");
                    buf.append(auditPrefix);
                    buf.append(":action>");
                    buf.append(audit.action);
                    buf.append("</");
                    buf.append(auditPrefix);
                    buf.append(":action>\n");
                    
                    buf.append("            <");
                    buf.append(auditPrefix);
                    buf.append(":responsibility>");
                    buf.append(audit.responsibility);
                    buf.append("</");
                    buf.append(auditPrefix);
                    buf.append(":responsibility>\n");
                    
                    buf.append("            <");
                    buf.append(auditPrefix);
                    buf.append(":date>");
                    buf.append(createDate);
                    buf.append("</");
                    buf.append(auditPrefix);
                    buf.append(":date>\n");
                    
                    buf.append("            <");
                    buf.append(auditPrefix);
                    buf.append(":justification>");
                    buf.append(audit.justification);
                    buf.append("</");
                    buf.append(auditPrefix);
                    buf.append(":justfication>\n");
                    
                    buf.append("          </");
                    buf.append(auditPrefix);
                    buf.append(":record>\n");
                    buf.append("        </xmlData>\n");
                    buf.append("      </mdWrap>\n");
                    buf.append("    </digiprovMD>\n");
                }
                buf.append("  </amdSec>\n");
                Iterator idIter=obj.datastreamIdIterator();
                while (idIter.hasNext()) {
                    String id=(String) idIter.next();
                    // from the first one with this id, 
                    // decide if it needs an amdSec or dmdSec
                    Datastream ds=(Datastream) obj.datastreams(id).get(0);
                    String mdClass="N/A";
                    if (ds.DSControlGrp==Datastream.XML_METADATA) {
                        DatastreamXMLMetadata mds=(DatastreamXMLMetadata) ds;
                        mdClass="" + mds.DSMDClass;
                    }
                    // FIXME: this isn't mets-valid... it's just a test to see if we got what's expected
                    buf.append("<datastream class=\"" + mdClass + "\" id=\"" + id + "\" ctrlGroup=\"" + ds.DSControlGrp + "\" infoType=\"" + ds.DSInfoType + "\"/>");
                }
            }
            
            buf.append("</mets>");

            out.write(buf.toString().getBytes(m_characterEncoding));
            out.flush();
        } catch (IOException ioe) {
            // this could be an unsupportedencodingexception, but it won't be 
            // because we already checked for that in the constructor
            throw new StreamWriteException("Problem writing to outputstream while serializing to mets: " + ioe.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException ioe2) { 
                throw new StreamIOException("Problem closing outputstream after attempting to serialize to mets: " + ioe2.getMessage());
            }
        }
        if (1==2) throw new ObjectIntegrityException("bad object");
    }
    
    public boolean equals(Object o) {
        if (this==o) { return true; }
        try {
            return equals((METSDOSerializer) o);
        } catch (ClassCastException cce) {
            return false;
        }
    }
    
    public boolean equals(METSDOSerializer o) {
        return (o.getEncoding().equals(getEncoding())
                && o.getVersion().equals(getVersion()));
    }

}