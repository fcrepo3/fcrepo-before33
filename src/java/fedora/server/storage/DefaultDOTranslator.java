package fedora.server.storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import fedora.server.Module;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.types.DigitalObject;

public class DefaultDOTranslator 
        extends Module implements DOTranslator {
        
    public DefaultDOTranslator(Map moduleParameters, Server server, String role)
            throws ModuleInitializationException {
        super(moduleParameters, server, role);
    }
    
    public void initModule() {
    }

    public void deserialize(InputStream in, DigitalObject out, 
            String format, String encoding)
//            throws ObjectIntegrityException, StreamIOException, 
//           UnsupportedTranslationException 
            {
    }
    
    public void serialize(DigitalObject in, OutputStream out, 
            String format, String encoding)
//            throws ObjectIntegrityException, StreamIOException, 
//            UnsupportedTranslationException 
            {
    }

}