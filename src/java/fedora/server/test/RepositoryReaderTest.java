/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.test;

import java.io.File;

import java.util.HashMap;

import junit.framework.TestCase;

import fedora.common.Constants;

import fedora.server.Server;
import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;
import fedora.server.storage.DOReader;
import fedora.server.storage.DirectoryBasedRepositoryReader;
import fedora.server.storage.translation.DOTranslatorImpl;
import fedora.server.storage.translation.METSFedoraExt1_1DODeserializer;
import fedora.server.storage.translation.METSFedoraExt1_1DOSerializer;
import fedora.server.storage.types.DigitalObject;

/**
 * Tests the implementation of the RepositoryReader interface,
 * DirectoryBasedRepositoryReader.
 * 
 * @author Chris Wilper
 */
public class RepositoryReaderTest
        extends TestCase
        implements Constants {

    private final File m_repoDir;

    private DirectoryBasedRepositoryReader m_repoReader;

    public RepositoryReaderTest(String fedoraHome, String label) {
        super(label);
        m_repoDir = new File(new File(fedoraHome), "demo");
    }

    @Override
    public void setUp() {
        try {
            String mets = METS_EXT1_1.uri;
            HashMap sers = new HashMap();
            sers.put(mets, new METSFedoraExt1_1DOSerializer());
            HashMap desers = new HashMap();
            desers.put(mets, new METSFedoraExt1_1DODeserializer());
            DOTranslatorImpl translator = new DOTranslatorImpl(sers, desers);
            m_repoReader =
                    new DirectoryBasedRepositoryReader(m_repoDir,
                                                       translator,
                                                       mets,
                                                       mets,
                                                       "UTF-8");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    public void testList() {
        try {
            String[] pids = m_repoReader.listObjectPIDs(null);
            System.out.println("Repository has " + pids.length + " objects.");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    public void testGetReader() {
        try {
            String[] pids = m_repoReader.listObjectPIDs(null);
            for (String element : pids) {
                DOReader r =
                        m_repoReader.getReader(Server.USE_DEFINITIVE_STORE,
                                               null,
                                               element);
                System.out.println(r.GetObjectPID() + " found via DOReader.");
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    public void testGetBDefReader() {
        try {
            String[] pids = m_repoReader.listObjectPIDs(null);
            for (String element : pids) {
                DOReader r =
                        m_repoReader.getReader(Server.USE_DEFINITIVE_STORE,
                                               null,
                                               element);
                if (r.isFedoraObjectType(DigitalObject.FEDORA_BDEF_OBJECT)) {
                    BDefReader dr =
                            m_repoReader
                                    .getBDefReader(Server.USE_DEFINITIVE_STORE,
                                                   null,
                                                   element);
                    System.out.println(dr.GetObjectPID()
                            + " found via getBDefReader.");
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    public void testGetBMechReader() {
        try {
            String[] pids = m_repoReader.listObjectPIDs(null);
            for (String element : pids) {
                DOReader r =
                        m_repoReader.getReader(Server.USE_DEFINITIVE_STORE,
                                               null,
                                               element);
                if (r.isFedoraObjectType(DigitalObject.FEDORA_BMECH_OBJECT)) {
                    BMechReader mr =
                            m_repoReader
                                    .getBMechReader(Server.USE_DEFINITIVE_STORE,
                                                    null,
                                                    element);
                    System.out.println(mr.GetObjectPID()
                            + " found via getBMechReader.");
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": "
                    + e.getMessage());
        }
    }

    public static void main(String[] args) {
        RepositoryReaderTest test =
                new RepositoryReaderTest(Constants.FEDORA_HOME,
                                         "Testing DirectoryBasedRepositoryReader");
        test.setUp();
        test.testList();
        test.testGetReader();
        test.testGetBDefReader();
        test.testGetBMechReader();
    }

}