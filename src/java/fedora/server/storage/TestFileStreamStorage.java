package fedora.server.storage;

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.io.ByteArrayInputStream;

public class TestFileStreamStorage
        implements TestStreamStorage {

    private File m_baseDir;
    
    private int m_bufSize;

    public TestFileStreamStorage(File baseDir, int bufSize) {
        m_baseDir=baseDir;
        m_bufSize=bufSize;
    }

    public void add(String id, InputStream in) 
            throws IOException {
        pipeStream(in, new FileOutputStream(getFile(id, false)));
    }

    public void replace(String id, InputStream in) 
            throws IOException {
        pipeStream(in, new FileOutputStream(getFile(id, true)));
    }

    public InputStream retrieve(String id)
            throws IOException {
        return new FileInputStream(getFile(id, true));
    }

    public boolean delete(String id)
            throws IOException {
        return getFile(id, true).delete();
    }

    private File getFile(String id, boolean shouldExist) 
            throws IOException {
        File f=new File(m_baseDir, id.replace(':', '_'));
        boolean exists=f.exists();
        if (shouldExist && !exists) {
            throw new IOException(f + " doesn't exist.");
        }
        if (!shouldExist && exists) {
            throw new IOException(f + " already exists.");
        }
        return f;
    }

    private void pipeStream(InputStream in, OutputStream out) 
            throws IOException {
        byte[] buf = new byte[m_bufSize];
        int len;
        while ( ( len = in.read( buf ) ) != -1 ) {
            out.write( buf, 0, len );
        }
        out.close();
        in.close();
    }
    
    public static void main(String[] args) {
        TestFileStreamStorage storage=new TestFileStreamStorage(new File("."), 4096);
        try {
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
            
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

}
