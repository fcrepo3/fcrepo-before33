package fedora.server;

import fedora.server.errors.ServerInitializationException;
import fedora.server.errors.ServerShutdownException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ModuleShutdownException;

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
        initLogger();
    }
    
    private int getLoggerIntParam(String paramName) 
            throws ServerInitializationException {
        String s=getParameter(paramName);
        int ret;
        if (s==null) {
            ret=0;
            logConfig(paramName + " not specified, defaulting to 0 (infinite)");
        } else {
            try {
                ret=Integer.parseInt(s);
                if (ret<0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException nfe) {
                throw new ServerInitializationException(paramName 
                        + " must be an integer from 0 to " + Integer.MAX_VALUE);
            }
            String retString;
            if (ret==0) {
                retString="0 (infinite)";
            } else {
                retString="" + ret;
            }
            logConfig(paramName + " specified = " + retString + ", ok.");
        }
        return ret;
    }
    
    private void initLogger() 
            throws ServerInitializationException {
        Logger logger=Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        int maxSize=getLoggerIntParam("log_max_size");
        int maxFiles=getLoggerIntParam("log_max_files");
        int maxDays=getLoggerIntParam("log_max_days");
        int flushThreshold=getLoggerIntParam("log_flush_threshold");
        String levelParam=getParameter("log_level");
        Level logLevel;
        if (levelParam==null) {
            logConfig("log_level not specified, defaulting to config.");
            logLevel=Level.CONFIG;
        } else {
            if (levelParam.equalsIgnoreCase("severe")) {
                logLevel=Level.SEVERE;
            } else if (levelParam.equalsIgnoreCase("warning")) {
                logLevel=Level.WARNING;
            } else if (levelParam.equalsIgnoreCase("info")) {
                logLevel=Level.INFO;
            } else if (levelParam.equalsIgnoreCase("config")) {
                logLevel=Level.CONFIG;
            } else if (levelParam.equalsIgnoreCase("fine")) {
                logLevel=Level.FINE;
            } else if (levelParam.equalsIgnoreCase("finer")) {
                logLevel=Level.FINER;
            } else if (levelParam.equalsIgnoreCase("finest")) {
                logLevel=Level.FINEST;
            } else {
                throw new ServerInitializationException("log_level was "
                        + "specified as " + levelParam + ".  If specified, "
                        + "log_level must be severe, warning, info, config, "
                        + "fine, finer, or finest.");
            }
            logConfig("log_level specified = " + levelParam + ", ok.");
        }
        logger.setLevel(logLevel);
        
        String dirParam=getParameter("log_dir");
        File logDir;
        if (dirParam==null) {
            logDir=new File(getHomeDir(), Server.LOG_DIR);
            logConfig("log_dir not specified, defaulting to " + logDir);
        } else {
            if (dirParam.startsWith(File.separator)) {
                logDir=new File(dirParam);
            } else {
                logDir=new File(getHomeDir(), dirParam);
            }
            logConfig("log_dir specified = " + dirParam + ", "
                    + "using " + logDir);
        }
        if (!(logDir.exists() && logDir.isDirectory())) {
            throw new ServerInitializationException("log_dir is not an "
                    + "existing directory.");
        }
        String ext=".log";
        DatingFileHandler fh=null;
        try {
            fh=new DatingFileHandler(logDir, maxSize, maxDays, maxFiles, ext, 
                    new SimpleXMLFormatter(true, "UTF-8"), flushThreshold);
            fh.setLevel(Level.FINEST);
        } catch (IOException ioe) {
            throw new ServerInitializationException("Could not initialize "
                    + "logger due to I/O error: " + ioe.getMessage());
        }
        logger.addHandler(fh);
        logFinest("Logger initialized. Switching to file-based log...");
        setLogger(logger);
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
        return new String[] {"fedora.server.storage.DOManager"};
    }

    
}
