package fedora.server.storage.types;

import java.util.Date;

/**
 * A holder of Fedora digital object information.
 * <p></p>
 * A DigitalObject instance may be used by DOReader and DOWriter instances 
 * as temporary storage for an object's attributes and components.
 * <p></p>
 * Implementations of this interface are responsible for temporary 
 * storage of these items, by whatever mechanism they deem fit.  The most 
 * obvious implementation would simply store everything in memory.
 * <p></p>
 * Implementations of this interface are <b>not</b> responsible for any sort of
 * validation on these items, or serialization/deserialization to/from specific
 * formats.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface DigitalObject {

    public String getPid();
    public void setPid(String pid);

    public String getState();
    public void setState(String state);
    
    public String getLabel();
    public void setLabel(String label);
    
    public Date getCreateDate();
    public void setCreateDate(Date createDate);
    
    public Date getLastModDate();
    public void setLastModDate(Date lastModDate);
    
    public AuditRecord[] getAuditRecords();
    public void setAuditRecords(AuditRecord[] auditRecords);
    
    public String[] getDatastreamIds();
    public Datastream[] getDatastreams(String id);
    public void setDatastreams(String id, Datastream[] datastreams);

    public String[] getDisseminatorIds();
    public Disseminator[] getDisseminators(String id);
    public void setDisseminators(String id, Disseminator[] disseminators);

}