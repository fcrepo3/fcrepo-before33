/*
 * The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */

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
 * A basic implementation of DigitalObject that stores things in memory.
 * 
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
@SuppressWarnings("deprecation")
public class BasicDigitalObject implements DigitalObject {

    private boolean m_isNew;
    private String m_fedoraObjectType;
    private String m_pid;
    private String m_state;
    private String m_ownerId;
    private String m_label;
    private String m_contentModelId;
    private Date m_createDate;
    private Date m_lastModDate;
    private ArrayList<AuditRecord> m_auditRecords;
    private HashMap<String, List<Datastream>> m_datastreams;
    private HashMap<String, List<Disseminator>> m_disseminators;
    private Map m_prefixes;
    private Map<String, String> m_extProperties;

    public BasicDigitalObject() {
        m_auditRecords = new ArrayList<AuditRecord>();
        m_datastreams = new HashMap<String, List<Datastream>>();
        m_disseminators = new HashMap<String, List<Disseminator>>();
        m_extProperties = new HashMap<String, String>();
        setNew(false);
        setContentModelId("");
        m_fedoraObjectType = "";
    }

    public boolean isNew() {
        return m_isNew;
    }

    public void setNew(boolean isNew) {
        m_isNew = isNew;
    }

    public boolean isFedoraObjectType(int type) {
        return (m_fedoraObjectType.indexOf(type) != -1);
    }

    public String getFedoraObjectTypes() {
        return m_fedoraObjectType;
    }

    public void addFedoraObjectType(int type) {
        if (m_fedoraObjectType.indexOf(type) == -1) {
            m_fedoraObjectType = m_fedoraObjectType + (char) type;
        }
    }

    public void removeFedoraObjectType(int type) {
        if (m_fedoraObjectType.indexOf(type) != -1) {
            m_fedoraObjectType = m_fedoraObjectType.replaceAll(
                    "" + (char) type, "");
        }
    }

    public String getPid() {
        return m_pid;
    }

    public void setPid(String pid) {
        m_pid = pid;
    }

    public String getState() {
        return m_state;
    }

    public void setState(String state) {
        m_state = state;
    }

    public String getOwnerId() {
        return m_ownerId;
    }

    public void setOwnerId(String owner) {
        m_ownerId = owner;
    }

    public String getLabel() {
        return m_label;
    }

    public void setLabel(String label) {
        m_label = label;
    }

    public String getContentModelId() {
        return m_contentModelId;
    }

    public void setContentModelId(String id) {
        m_contentModelId = id;
    }

    public Date getCreateDate() {
        return m_createDate;
    }

    public void setCreateDate(Date createDate) {
        m_createDate = createDate;
    }

    public Date getLastModDate() {
        return m_lastModDate;
    }

    public void setLastModDate(Date lastModDate) {
        m_lastModDate = lastModDate;
    }

    public List<AuditRecord> getAuditRecords() {
        return m_auditRecords;
    }

    public Iterator<String> datastreamIdIterator() {
        return copyOfKeysForNonEmptyLists(m_datastreams).iterator();
    }

    private static Set<String> copyOfKeysForNonEmptyLists(HashMap map) {
        HashSet<String> set = new HashSet<String>();
        Iterator<String> iter = map.keySet().iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            List list = (List) map.get(key);
            if (list.size() > 0) {
                set.add(key);
            }
        }
        return set;
    }

    public List<Datastream> datastreams(String id) {
        ArrayList<Datastream> ret = (ArrayList<Datastream>) m_datastreams
                .get(id);
        if (ret == null) {
            ret = new ArrayList<Datastream>();
            m_datastreams.put(id, ret);
        }
        return ret;
    }

    public void addDatastreamVersion(Datastream ds, boolean addNewVersion) {
        List<Datastream> datastreams = datastreams(ds.DatastreamID);
        if (!addNewVersion) {
            Iterator dsIter = datastreams.iterator();
            Datastream latestCreated = null;
            long latestCreateTime = -1;
            while (dsIter.hasNext()) {
                Datastream ds1 = (Datastream) dsIter.next();
                if (ds1.DSCreateDT.getTime() > latestCreateTime) {
                    latestCreateTime = ds1.DSCreateDT.getTime();
                    latestCreated = ds1;
                }
            }
            datastreams.remove(latestCreated);
        }
        datastreams.add(ds);
    }

    @Deprecated
    public Iterator<String> disseminatorIdIterator() {
        return copyOfKeysForNonEmptyLists(m_disseminators).iterator();
    }

    @Deprecated
    public List<Disseminator> disseminators(String id) {
        ArrayList<Disseminator> ret = (ArrayList<Disseminator>) m_disseminators
                .get(id);
        if (ret == null) {
            ret = new ArrayList<Disseminator>();
            m_disseminators.put(id, ret);
        }
        return ret;
    }

    public String newDatastreamID() {
        return newID(datastreamIdIterator(), "DS");
    }

    public String newDatastreamID(String id) {
        List<String> versionIDs = new ArrayList<String>();
        Iterator iter = ((ArrayList) m_datastreams.get(id)).iterator();
        while (iter.hasNext()) {
            Datastream ds = (Datastream) iter.next();
            versionIDs.add(ds.DSVersionID);
        }
        return newID(versionIDs.iterator(), id + ".");
    }

    public String newAuditRecordID() {
        ArrayList<String> auditIDs = new ArrayList<String>();
        Iterator<AuditRecord> iter = m_auditRecords.iterator();
        while (iter.hasNext()) {
            AuditRecord record = (AuditRecord) iter.next();
            auditIDs.add(record.id);
        }
        return newID(auditIDs.iterator(), "AUDREC");
    }

    /**
     * Sets an extended property on the object.
     * 
     * @param propName
     *            The extende property name, either a string, or URI as string.
     */
    public void setExtProperty(String propName, String propValue) {
        m_extProperties.put(propName, propValue);

    }

    /**
     * Gets an extended property value, given the property name.
     * 
     * @return The property value.
     */
    public String getExtProperty(String propName) {
        return (String) m_extProperties.get(propName);

    }

    /**
     * Gets a Map containing all of the extended properties on the object. Map
     * key is property name.
     * 
     * @return The property Map.
     */
    public Map<String, String> getExtProperties() {
        return m_extProperties;

    }

    /**
     * Given an iterator of existing ids, return a new id that starts with
     * <code>start</code> and is guaranteed to be unique. This algorithm adds
     * one to the highest existing id that starts with <code>start</code>. If
     * no such existing id exists, it will return <i>start</i> + "1".
     */
    private String newID(Iterator iter, String start) {
        int highest = 0;
        while (iter.hasNext()) {
            String id = (String) iter.next();
            if (id.startsWith(start) && id.length() > start.length()) {
                try {
                    int num = Integer.parseInt(id.substring(start.length()));
                    if (num > highest)
                        highest = num;
                } catch (NumberFormatException ignored) {
                }
            }
        }
        int newNum = highest + 1;
        return start + newNum;
    }

}
