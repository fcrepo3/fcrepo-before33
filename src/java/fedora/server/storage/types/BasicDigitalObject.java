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
 * @version $Id$
 */
public class BasicDigitalObject
        implements DigitalObject {

    private boolean m_isNew;
    private int m_fedoraObjectType;
    private String m_pid;
    private String m_state;
    private String m_ownerId;
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
		setNew(false);
    }

    public boolean isNew() {
	    return m_isNew;
	}

    public void setNew(boolean isNew) {
	    m_isNew=isNew;
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

    public String getOwnerId() {
        return m_ownerId;
    }

    public void setOwnerId(String user) {
        m_ownerId=user;
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

    public String newDatastreamID() {
        return newID(datastreamIdIterator(), "DS");
    }

    public String newDatastreamID(String id) {
        ArrayList versionIDs=new ArrayList();
        Iterator iter=((ArrayList) m_datastreams.get(id)).iterator();
        while (iter.hasNext()) {
            Datastream ds=(Datastream) iter.next();
            versionIDs.add(ds.DSVersionID);
        }
        return newID(versionIDs.iterator(), id + ".");
    }

    public String newDisseminatorID() {
        return newID(disseminatorIdIterator(), "DISS");
    }

    public String newDisseminatorID(String id) {
        ArrayList versionIDs=new ArrayList();
        Iterator iter=((ArrayList) m_disseminators.get(id)).iterator();
        while (iter.hasNext()) {
            Disseminator diss=(Disseminator) iter.next();
            versionIDs.add(diss.dissVersionID);
        }
        return newID(versionIDs.iterator(), id + ".");
    }

    public String newDatastreamBindingMapID() {
        ArrayList mapIDs=new ArrayList(); // the list we'll put
                                          // allbinding map ids in
        Iterator dissIter=m_disseminators.keySet().iterator();
        // for every List of disseminators...
        while (dissIter.hasNext()) {
            // get the dissID
            String id=(String) dissIter.next();
            Iterator iter=((ArrayList) m_disseminators.get(id)).iterator();
            // then for every version with that id...
            while (iter.hasNext()) {
                Disseminator diss=(Disseminator) iter.next();
                // add its dsBindMapID to the mapIDs list
                mapIDs.add(diss.dsBindMapID);
            }
        }
        // get a new, unique binding map id, starting with "S" given the complete list
        return newID(mapIDs.iterator(), "S");
    }

    public String newDatastreamBindingMapID(String id) {
        ArrayList versionIDs=new ArrayList();
        Iterator iter=((ArrayList) m_disseminators.get(id)).iterator();
        while (iter.hasNext()) {
            Disseminator diss=(Disseminator) iter.next();
            versionIDs.add(diss.dsBindMapID);
        }
        return newID(versionIDs.iterator(), "S");
    }

    public String newAuditRecordID() {
        ArrayList auditIDs=new ArrayList();
        Iterator iter=m_auditRecords.iterator();
        while (iter.hasNext()) {
            AuditRecord record=(AuditRecord) iter.next();
            auditIDs.add(record.id);
        }
        return newID(auditIDs.iterator(), "AUDIT");
    }

    /**
     * Given an iterator of existing ids, return a new id that
     * starts with <code>start</code> and is guaranteed to be
     * unique.  This algorithm adds one to the highest existing
     * id that starts with <code>start</code>.  If no such existing
     * id exists, it will return <i>start</i> + "1".
     */
    private String newID(Iterator iter, String start) {
        int highest=0;
        while (iter.hasNext()) {
            String id=(String) iter.next();
            if (id.startsWith(start) && id.length()>start.length()) {
                try {
                    int num=Integer.parseInt(id.substring(start.length()));
                    if (num>highest) highest=num;
                } catch (NumberFormatException ignored) { }
            }
        }
        int newNum=highest+1;
        return start + newNum;
    }

}
