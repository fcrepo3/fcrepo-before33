package fedora.server.storage.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    private ArrayList m_auditRecords;
    private HashMap m_datastreams;
    private HashMap m_disseminators;

    public BasicDigitalObject() { 
        m_auditRecords=new ArrayList();
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
    
    public List getAuditRecords() {
        return m_auditRecords;
    }

    public Iterator datastreamIdIterator() {
        return copyOfKeysForNonEmptyLists(m_datastreams).iterator();
    }
    
    private static Set copyOfKeysForNonEmptyLists(HashMap map) {
        HashSet set=new HashSet();
        Iterator iter=map.keySet().iterator();
        while (iter.hasNext()) {
            String key=(String) iter.next();
            List list=(List) map.get(key);
            if (list.size()>0) {
                set.add(key);
            }
        }
        return set;
    }
    
    public List datastreams(String id) {
        ArrayList ret=(ArrayList) m_datastreams.get(id);
        if (ret==null) {
            ret=new ArrayList();
            m_datastreams.put(id, ret);
        }
        return ret;
    }
    
    public Iterator disseminatorIdIterator() {
        return m_disseminators.keySet().iterator();
    }
    
    public List disseminators(String id) {
        ArrayList ret=(ArrayList) m_disseminators.get(id);
        if (ret==null) {
            ret=new ArrayList();
            m_disseminators.put(id, ret);
        }
        return ret;
    }
    
}