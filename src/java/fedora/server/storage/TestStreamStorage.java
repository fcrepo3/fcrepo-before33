package fedora.server.storage;

import java.io.InputStream;
import java.io.IOException;

public interface TestStreamStorage {

    public void add(String id, InputStream in) 
            throws IOException;

    public void replace(String id, InputStream in) 
            throws IOException;

    public InputStream retrieve(String id)
            throws IOException;

    public boolean delete(String id)
            throws IOException;

}
