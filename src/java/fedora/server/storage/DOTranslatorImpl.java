package fedora.server.storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import fedora.server.Logging;
import fedora.server.StdoutLogging;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.StreamIOException;
import fedora.server.errors.UnsupportedTranslationException;
import fedora.server.storage.types.DigitalObject;

public class DOTranslatorImpl 
        extends StdoutLogging
        implements DOTranslator {
        
    private Map m_serializers;
    private Map m_deserializers;

    public DOTranslatorImpl(Map serializers, Map deserializers, Logging logTarget) {
        super(logTarget);
        //FIXME: Should DOSerializer and DODeserializer classes
        // have a newInstance method... one that takes params?  possibly.
        // or separate wrappers around the METS...izer classes are needed,
        // cuz small diferences in functionality are desired for ingest
        // vs. pull-from-disk.
    }
    
    public String getDefaultFormat() {
        return "fixme: remove method from interface";
    }

    public void deserialize(InputStream in, DigitalObject out, 
            String format, String encoding) {
//            throws ObjectIntegrityException, StreamIOException, 
//            UnsupportedTranslationException {
    }
    
    public void serialize(DigitalObject in, OutputStream out, 
            String format, String encoding) {
//            throws ObjectIntegrityException, StreamIOException, 
//            UnsupportedTranslationException {
    }

}