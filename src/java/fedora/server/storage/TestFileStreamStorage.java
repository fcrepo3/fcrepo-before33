package fedora.server.storage;

import fedora.server.errors.ObjectExistsException;
import fedora.server.errors.ObjectNotFoundException;
import fedora.server.errors.StorageDeviceException;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.io.ByteArrayInputStream;

/**
 *
 * <p><b>Title:</b> TestFileStreamStorage.java</p>
 * <p><b>Description:</b> </p>
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
public class TestFileStreamStorage
        implements TestStreamStorage {

    private File m_baseDir;

    private int m_bufSize;

    /** Creates if dir doesn't exist, one level only. */
    public TestFileStreamStorage(File baseDir, int bufSize)
            throws StorageDeviceException {
        m_baseDir=baseDir;
        m_bufSize=bufSize;
        if (!baseDir.exists()) {
            if (!baseDir.mkdir()) {
                throw new StorageDeviceException("Could not create directory '"
                        + baseDir + "'.");
            }
        }
    }

    public void add(String id, InputStream in)
            throws ObjectExistsException, StorageDeviceException {
        try {
            pipeStream(in, new FileOutputStream(getNewFile(id)));
        } catch (IOException ioe) {
            throw new StorageDeviceException("Could not create "
                    + "FileOutputStream for '" + id + "'.");
        }
    }

    public void replace(String id, InputStream in)
            throws ObjectNotFoundException, StorageDeviceException {
        try {
            pipeStream(in, new FileOutputStream(getExistingFile(id)));
        } catch (IOException ioe) {
            throw new StorageDeviceException("Could not create "
                    + "FileOutputStream for '" + id + "'.");
        }
    }

    public InputStream retrieve(String id)
            throws ObjectNotFoundException, StorageDeviceException {
        try {
            return new FileInputStream(getExistingFile(id));
        } catch (IOException ioe) {
            throw new StorageDeviceException("Could get a "
                    + "FileInputStream for '" + id + "'.");
        }
    }

    public void delete(String id)
            throws ObjectNotFoundException, StorageDeviceException {
        if (!getExistingFile(id).delete()) {
            throw new StorageDeviceException("Unable to delete object '" + id + "'.");
        }
    }

    private File getExistingFile(String id)
            throws ObjectNotFoundException {
        File f=new File(m_baseDir, id.replace(':', '_'));
        if (!f.exists()) {
            throw new ObjectNotFoundException("Object '" + id + "' was not found.");
        }
        return f;
    }

    private File getNewFile(String id)
            throws ObjectExistsException {
        File f=new File(m_baseDir, id.replace(':', '_'));
        if (f.exists()) {
            throw new ObjectExistsException("Object '" + id + "' already exists.");
        }
        return f;
    }

    private void pipeStream(InputStream in, OutputStream out)
            throws StorageDeviceException {
        try {
            byte[] buf = new byte[m_bufSize];
            int len;
            while ( ( len = in.read( buf ) ) != -1 ) {
                out.write( buf, 0, len );
            }
        } catch (IOException ioe) {
            // a better impl would clean up after itself if it fails to write
            throw new StorageDeviceException("Error writing to stream");
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException closeProb) {
              // ignore problems while closing
            }
        }
    }

    public static void main(String[] args) {
        try {
            TestFileStreamStorage storage=new TestFileStreamStorage(new File("."), 4096);
            storage.add("myns:0", new ByteArrayInputStream(new String("one").getBytes()));
            storage.add("myns:1", new ByteArrayInputStream(new String("two").getBytes()));
            storage.add("myns:2", new ByteArrayInputStream(new String("three").getBytes()));
            storage.delete("myns:0");
            InputStream in=storage.retrieve("myns:1");
            StringBuffer out=new StringBuffer();
            byte[] buf = new byte[4096];
            int len;
            while ( ( len = in.read( buf ) ) != -1 ) {
                out.append(new String(buf, 0, len));
            }
            in.close();
            System.out.println("myns:1 contained '" + out.toString() + "'");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
