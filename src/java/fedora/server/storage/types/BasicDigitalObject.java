package fedora.server.storage.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * <p><b>Title:</b> BasicDigitalObject.java</p>
 * <p><b>Description:</b> A basic implementation of DigitalObject that stores
 * things in memory.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version 1.0
 */
public class BasicDigitalObject
        implements DigitalObject {

    private int m_fedoraObjectType;
    private String m_pid;
    private String m_state;
    private String m_lockingUser;
    private String m_label;
    private String m_contentModelId;
    private Date m_createDate;
    private Date m_lastModDate;
    private ArrayList m_auditRecords;
    private HashMap m_datastreams;
    private HashMap m_disseminators;
    private Map m_prefixes;

    public BasicDigitalObject() {
        m_auditRecords=new ArrayList();
        m_datastreams=new HashMap();
        m_disseminators=new HashMap();
    }

    public int getFedoraObjectType() {
        return m_fedoraObjectType;
    }

    public void setFedoraObjectType(int t) {
        m_fedoraObjectType=t;
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

    public String getLockingUser() {
        return m_lockingUser;
    }

    public void setLockingUser(String user) {
        m_lockingUser=user;
    }

    public String getLabel() {
        return m_label;
    }

    public void setLabel(String label) {
        m_label=label;
    }

    public String getContentModelId() {
        return m_contentModelId;
    }

    public void setContentModelId(String id) {
        m_contentModelId=id;
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

    public void setNamespaceMapping(Map mapping) {
        m_prefixes=mapping;
    }

    public Map getNamespaceMapping() {
        return m_prefixes;
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