package fedora.server.storage.types;

import fedora.server.errors.StreamIOException;

import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Referenced content or managed content -- can point to metadata datastreams.
 */
public class DatastreamContent
        extends Datastream {

    private ArrayList m_metadataIdList;
 
    public DatastreamContent() {
        m_metadataIdList=new ArrayList();
    }
    
    public List metadataIdList() {
        return m_metadataIdList;
    }
}
