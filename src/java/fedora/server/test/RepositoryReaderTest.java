package fedora.server.test;

import java.io.File;
import java.util.HashMap;
import junit.framework.TestCase;

import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;
import fedora.server.storage.DirectoryBasedRepositoryReader;
import fedora.server.storage.DOReader;
import fedora.server.storage.translation.DOTranslatorImpl;
import fedora.server.storage.translation.METSDODeserializer;
import fedora.server.storage.translation.METSDOSerializer;

/**
 * Tests the implementation of the RepositoryReader interface, 
 * DirectoryBasedRepositoryReader.
 *
 * @author cwilper@cs.cornell.edu
 */
public class RepositoryReaderTest 
        extends TestCase {
        
    private File m_repoDir;
    private DirectoryBasedRepositoryReader m_repoReader;
        
    public RepositoryReaderTest(String fedoraHome, String label) {
        super(label);
        m_repoDir=new File(new File(fedoraHome), "demo");
    }
    
    public void setUp() {
        try {
            String mets="mets11fedora1";
            HashMap sers=new HashMap();
            sers.put(mets, new METSDOSerializer());
            HashMap desers=new HashMap();
            desers.put(mets, new METSDODeserializer());
            DOTranslatorImpl translator=new DOTranslatorImpl(sers, desers, null);
            m_repoReader=new DirectoryBasedRepositoryReader(m_repoDir, translator,
                    mets, mets, mets, "UTF-8", null);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    public void testList() {
        try {
            String[] pids=m_repoReader.listObjectPIDs(null);
            System.out.println("Repository has " + pids.length + " objects.");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    public void testGetReader() {
        try {
            String[] pids=m_repoReader.listObjectPIDs(null);
            for (int i=0; i<pids.length; i++) {
                DOReader r=m_repoReader.getReader(null, pids[i]);
                System.out.println(r.GetObjectPID() + " found via DOReader.");
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    public void testGetBDefReader() {
        try {
            String[] pids=m_repoReader.listObjectPIDs(null);
            for (int i=0; i<pids.length; i++) {
                DOReader r=m_repoReader.getReader(null, pids[i]);
                if (r.getFedoraObjectType().equals("D")) {
                    BDefReader dr=m_repoReader.getBDefReader(null, pids[i]);
                    System.out.println(dr.GetObjectPID() + " found via getBDefReader.");
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    public void testGetBMechReader() {
        try {
            String[] pids=m_repoReader.listObjectPIDs(null);
            for (int i=0; i<pids.length; i++) {
                DOReader r=m_repoReader.getReader(null, pids[i]);
                if (r.getFedoraObjectType().equals("M")) {
                    BMechReader mr=m_repoReader.getBMechReader(null, pids[i]);
                    System.out.println(mr.GetObjectPID() + " found via getBMechReader.");
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        RepositoryReaderTest test=new RepositoryReaderTest(System.getProperty("fedora.home"), "Testing DirectoryBasedRepositoryReader");
        test.setUp();
        test.testList();
        test.testGetReader();
        test.testGetBDefReader();
        test.testGetBMechReader();
    }

}