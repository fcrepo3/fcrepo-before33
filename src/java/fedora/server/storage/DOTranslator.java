package fedora.server.storage;

import java.io.InputStream;
import java.io.OutputStream;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.types.DigitalObject;

public interface DOTranslator {

    public abstract void deserialize(InputStream in, DigitalObject out, 
            String format, String encoding)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException;
    
    public abstract void serialize(DigitalObject in, OutputStream out, 
            String format, String encoding)
            throws ObjectIntegrityException, StreamIOException,
            UnsupportedTranslationException;
            
    public abstract String getDefaultFormat();

}