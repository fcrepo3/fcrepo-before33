package fedora.server.test;

import java.io.File;
import java.util.HashMap;
import junit.framework.TestCase;

import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;
import fedora.server.storage.DirectoryBasedRepositoryReader;
import fedora.server.storage.DOReader;
import fedora.server.storage.translation.DOTranslatorImpl;
import fedora.server.storage.translation.METSLikeDODeserializer;
import fedora.server.storage.translation.METSLikeDOSerializer;

/**
 *
 * <p><b>Title:</b> RepositoryReaderTest.java</p>
 * <p><b>Description:</b> Tests the implementation of the RepositoryReader
 * interface, DirectoryBasedRepositoryReader.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
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
            sers.put(mets, new METSLikeDOSerializer());
            HashMap desers=new HashMap();
            desers.put(mets, new METSLikeDODeserializer());
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