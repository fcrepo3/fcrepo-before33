package fedora.server.test;

import java.util.Date;
import java.util.HashMap;
import junit.framework.TestCase;

import fedora.server.storage.types.AuditRecord;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;
import fedora.server.storage.types.Disseminator;
import fedora.server.storage.types.Datastream;

/**
 * Tests the implementation of the DigitalObject interface, BasicDigitalObject.
 *
 * @author cwilper@cs.cornell.edu
 */
public class DigitalObjectTest 
        extends TestCase {
        
    private DigitalObject m_obj;
    private DigitalObject m_bdef;
    private DigitalObject m_bmech;
    private Date m_startTime;
    private String m_namespacePrefix;
    private String m_namespaceURI;
    private Datastream m_ds1_0;
    private Datastream m_ds1_1;
    private Datastream m_ds2_0;
    private Disseminator m_diss1_0;
    private Disseminator m_diss1_1;
    private Disseminator m_diss2_0;
    private AuditRecord m_audit1;
    private AuditRecord m_audit2;
    private AuditRecord m_audit3;
    private AuditRecord m_audit4;
    private AuditRecord m_audit5;

    public DigitalObjectTest(String label) {
        super(label);
    }
    
    public void setUp() {
        // init common values
        m_startTime=new Date();
        m_namespacePrefix="prefix";
        m_namespaceURI="http://www.namespaceuri.com/path/";
        HashMap nsMap=new HashMap();
        nsMap.put(m_namespacePrefix, m_namespaceURI);
        // init regular object
        m_obj=new BasicDigitalObject();
        m_obj.setContentModelId("cModel1");
        m_obj.setCreateDate(m_startTime);
        m_obj.setFedoraObjectType(DigitalObject.FEDORA_OBJECT);
        m_obj.setLabel("Test Object");
        m_obj.setLastModDate(m_startTime);
        m_obj.setLockingUser("userId1");
        m_obj.setNamespaceMapping(nsMap);
        m_obj.setPid("test:1");
        m_obj.setState("A");
        // add some datastreams
        m_ds1_0=new Datastream();
        m_ds1_0.DatastreamID="DS1.0";
        m_ds1_0.DSVersionID="DS1";
        m_ds1_1=new Datastream();
        m_ds1_1.DatastreamID="DS1.1";
        m_ds1_1.DSVersionID="DS1";
        m_ds2_0=new Datastream();
        m_ds2_0.DatastreamID="DS2.0";
        m_ds2_0.DSVersionID="DS2";
        // ... and some disseminators
        m_diss1_0=new Disseminator();
        m_diss1_1=new Disseminator();
        m_diss2_0=new Disseminator();
        // ... and some audit records
        m_audit1=new AuditRecord();
        m_audit2=new AuditRecord();
        m_audit3=new AuditRecord();
        m_audit4=new AuditRecord();
        m_audit5=new AuditRecord();
        // init bdef
        m_bdef=new BasicDigitalObject();
        m_bdef.setContentModelId("cModel2");
        m_bdef.setCreateDate(m_startTime);
        m_bdef.setFedoraObjectType(DigitalObject.FEDORA_BDEF_OBJECT);
        m_bdef.setLabel("Test Behavior Definition Object");
        m_bdef.setLastModDate(m_startTime);
        m_bdef.setLockingUser("userId2");
        m_bdef.setNamespaceMapping(nsMap);
        m_bdef.setPid("test:2");
        m_bdef.setState("W");
        // init bmech
        m_bmech=new BasicDigitalObject();
        m_bmech.setContentModelId("cModel3");
        m_bmech.setCreateDate(m_startTime);
        m_bmech.setFedoraObjectType(DigitalObject.FEDORA_BMECH_OBJECT);
        m_bmech.setLabel("Test Behavior Mechanism Object");
        m_bmech.setLastModDate(m_startTime);
        m_bmech.setLockingUser("userId3");
        m_bmech.setNamespaceMapping(nsMap);
        m_bmech.setPid("test:3");
        m_bmech.setState("D");
    }
    
    public void testSimpleParts() {
        assertEquals(m_obj.getContentModelId(), "cModel1");
        assertEquals(m_bdef.getContentModelId(), "cModel2");
        assertEquals(m_bmech.getContentModelId(), "cModel3");
        assertEquals(m_obj.getCreateDate(), m_startTime);
        assertEquals(m_bdef.getCreateDate(), m_startTime);
        assertEquals(m_bmech.getCreateDate(), m_startTime);
        assertEquals(m_obj.getFedoraObjectType(), DigitalObject.FEDORA_OBJECT);
        assertEquals(m_bdef.getFedoraObjectType(), DigitalObject.FEDORA_BDEF_OBJECT);
        assertEquals(m_bmech.getFedoraObjectType(), DigitalObject.FEDORA_BMECH_OBJECT);
        assertEquals(m_obj.getLabel(), "Test Object");
        assertEquals(m_bdef.getLabel(), "Test Behavior Definition Object");
        assertEquals(m_bmech.getLabel(), "Test Behavior Mechanism Object");
        assertEquals(m_obj.getLastModDate(), m_startTime);
        assertEquals(m_bdef.getLastModDate(), m_startTime);
        assertEquals(m_bmech.getLastModDate(), m_startTime);
        assertEquals(m_obj.getLockingUser(), "userId1");
        assertEquals(m_bdef.getLockingUser(), "userId2");
        assertEquals(m_bmech.getLockingUser(), "userId3");
        assertEquals((String) m_obj.getNamespaceMapping().get(m_namespacePrefix), m_namespaceURI);
        assertEquals((String) m_bdef.getNamespaceMapping().get(m_namespacePrefix), m_namespaceURI);
        assertEquals((String) m_bmech.getNamespaceMapping().get(m_namespacePrefix), m_namespaceURI);
        assertEquals(m_obj.getPid(), "test:1");
        assertEquals(m_bdef.getPid(), "test:2");
        assertEquals(m_bmech.getPid(), "test:3");
        assertEquals(m_obj.getState(), "A");
        assertEquals(m_bdef.getState(), "W");
        assertEquals(m_bmech.getState(), "D");
    }
    
    public void testDatastreamComposition() {
        m_obj.datastreams("DS1").add(m_ds1_0);
        m_obj.datastreams("DS1").add(m_ds1_1);
        m_obj.datastreams("DS2").add(m_ds2_0);
    }

    public void testDisseminatorComposition() {
    }

    public void testAuditRecordComposition() {
    }

}