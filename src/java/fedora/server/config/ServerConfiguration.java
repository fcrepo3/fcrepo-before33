package fedora.server.config;

import java.io.*;
import java.util.*;

public class ServerConfiguration 
        extends Configuration {

    private List m_moduleConfigurations;
    private List m_datastoreConfigurations;

    public ServerConfiguration(List parameters,
                               List moduleConfigurations,
                               List datastoreConfigurations) {
        super(parameters);
        m_moduleConfigurations = moduleConfigurations;
        m_datastoreConfigurations = datastoreConfigurations;
    }

    public void serialize(OutputStream xmlStream) throws IOException {
    }

    public List getModuleConfigurations() {
        return m_moduleConfigurations;
    }

    public List getDatastoreConfigurations() {
        return m_datastoreConfigurations;
    }

}
