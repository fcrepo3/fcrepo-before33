package fedora.server;

import fedora.server.errors.ServerInitializationException;
import fedora.server.errors.ServerShutdownException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;
import fedora.server.storage.DOManager;

import edu.cornell.dlrg.logging.DatingFileHandler;
import edu.cornell.dlrg.logging.SimpleXMLFormatter;
import java.io.IOException;
import java.io.File;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class BasicServer 
        extends Server {
        
    private File logDir;
        
    public BasicServer(Element rootElement, File fedoraHomeDir) 
            throws ServerInitializationException,
                   ModuleInitializationException {
        super(rootElement, fedoraHomeDir);
    }
    
    public void initServer() 
            throws ServerInitializationException {
        Logger logger=Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.FINEST);
        logDir=new File(getHomeDir(), Server.LOG_DIR);
        int maxSize=1024*1024;
        int maxFiles=5;
        int maxDays=1;
        String ext=".log";
        DatingFileHandler fh=null;
        try {
            fh=new DatingFileHandler(logDir, maxSize, maxDays, maxFiles, ext, 
                    new SimpleXMLFormatter(true, "UTF-8"));
            fh.setLevel(Level.FINEST);
        } catch (IOException ioe) {
            throw new ServerInitializationException("IO Problem initializing loghandler: " + ioe.getMessage());
        }
        logger.addHandler(fh);
        setLogger(logger);
        logInfo("Here's my first log message");
    }
    
    public void shutdownServer() 
            throws ServerShutdownException {
        closeLogger();
    }

    /**
     * Gets the names of the roles that are required to be fulfilled by
     * modules specified in this server's configuration file.
     *
     * @returns String[] The roles.
     */
    public String[] getRequiredModuleRoles() {
        return new String[] {Server.DOMANAGER_CLASS};
    }
    
    public DOManager getManager(String name) {
        return null;
    }
    
}
