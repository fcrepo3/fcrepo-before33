package fedora.server.config;

import java.io.*;
import java.util.*;

public class ServerConfigurationParser {

    private List m_parameters;
    private List m_moduleConfigurations;
    private List m_datastoreConfigurations;

    public ServerConfigurationParser(InputStream xmlStream) {
        m_parameters = new ArrayList();
        m_moduleConfigurations = new ArrayList();
        m_datastoreConfigurations = new ArrayList();
    }

    public ServerConfiguration parse() {

        return new ServerConfiguration(m_parameters, 
                                       m_moduleConfigurations, 
                                       m_datastoreConfigurations);
    }

}
