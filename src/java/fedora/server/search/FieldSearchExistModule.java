package fedora.server.search;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.xmldb.api.base.XMLDBException;

import fedora.server.Logging;
import fedora.server.Module;
import fedora.server.Server;
import fedora.server.StdoutLogging;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;

public class FieldSearchExistModule
        extends Module
        implements FieldSearch {
        
    private FieldSearchExistImpl m_wrappedFieldSearch;

    public FieldSearchExistModule(Map params, Server server, String role) 
            throws ModuleInitializationException {
        super(params, server, role);
    }
    
    public void initModule() 
            throws ModuleInitializationException {
        try {
            String existHome=new File(getServer().getHomeDir(), "exist09").getPath();
            m_wrappedFieldSearch=new FieldSearchExistImpl(existHome, this);
        } catch (XMLDBException xmldbe) {
            throw new ModuleInitializationException(
                    "Couldn't initialize embedded eXist (xml database) "
                    + "instance: org.xmldb.api.base.XMLDBException: "
                    + xmldbe.getMessage(), getRole());
        }
    }
    
    public void shutdownModule()
            throws ModuleShutdownException {
        try {
            m_wrappedFieldSearch.shutdown();
        } catch (Throwable th) {
            throw new ModuleShutdownException("Error shutting down eXist: " + th.getClass().getName() + ": " + th.getMessage(), getRole());
        }
    }
    
    public void update(DOReader reader) 
            throws ServerException {
        m_wrappedFieldSearch.update(reader);
    }
    
    public boolean delete(String pid) 
            throws ServerException {
        return m_wrappedFieldSearch.delete(pid);
    }

    public List search(String[] resultFields, String terms) 
            throws ServerException {
        return m_wrappedFieldSearch.search(resultFields, terms);
    }
    
    public List search(String[] resultFields, List conditions) 
            throws ServerException {
        return m_wrappedFieldSearch.search(resultFields, conditions);
    }
    
}