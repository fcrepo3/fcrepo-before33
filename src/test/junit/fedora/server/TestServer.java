package fedora.server;

import java.io.File;

import org.w3c.dom.Element;

import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ServerInitializationException;

/**
 * @author eddie
 */
public class TestServer extends Server {
    
    public static String CONFIG_FILE="test.fcfg";
    
    /**
     * @param rootConfigElement
     * @param homeDir
     * @throws ServerInitializationException
     * @throws ModuleInitializationException
     */
    protected TestServer(Element rootConfigElement, File homeDir) throws ServerInitializationException, ModuleInitializationException {
        super(rootConfigElement, homeDir);
    }

}
