package fedora.server.storage.types;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A basic implementation of DigitalObject that stores things in memory.
 *
 * @author cwilper@cs.cornell.edu
 */
public class BasicDigitalObject
        implements DigitalObject {

    private String m_pid;
    private String m_state;
    private String m_label;
    private Date m_createDate;
    private Date m_lastModDate;
    private AuditRecord[] m_auditRecords;
    private HashMap m_datastreams;
    private HashMap m_disseminators;
    private static String[] s_emptyStringArray=new String[0];

    public BasicDigitalObject() { 
        m_auditRecords=new AuditRecord[0];
        m_datastreams=new HashMap();
        m_disseminators=new HashMap();
    }

    public String getPid() {
        return m_pid;
    }
    
    public void setPid(String pid) {
        m_pid=pid;
    }
    
    public String getState() {
        return m_state;
    }
    
    public void setState(String state) {
        m_state=state;
    }
    
    public String getLabel() {
        return m_label;
    }
    
    public void setLabel(String label) {
        m_label=label;
    }
    
    public Date getCreateDate() {
        return m_createDate;
    }
    
    public void setCreateDate(Date createDate) {
        m_createDate=createDate;
    }
    
    public Date getLastModDate() {
        return m_lastModDate;
    }
    
    public void setLastModDate(Date lastModDate) {
        m_lastModDate=lastModDate;
    }
    
    public AuditRecord[] getAuditRecords() {
        return m_auditRecords;
    }
    
    public void setAuditRecords(AuditRecord[] auditRecords) {
        m_auditRecords=auditRecords;
    }
    
    public String[] getDatastreamIds() {
        int num=m_datastreams.size();
        if (num==0) {
            return s_emptyStringArray;
        }
        String[] ids=new String[num];
        Iterator iter=m_datastreams.keySet().iterator();
        int i=0;
        while (iter.hasNext()) {
            ids[i]=(String) iter.next();
        }
        return ids;
    }
    
    public Datastream[] getDatastreams(String id) {
        return null;
    }
    
    public void setDatastreams(String id, Datastream[] datastreams) {
    }

    public String[] getDisseminatorIds() {
        return null;
    }
    
    public Disseminator[] getDisseminators(String id) {
        return null;
    }
    
    public void setDisseminators(String id, Disseminator[] disseminators) {
        
    }

}