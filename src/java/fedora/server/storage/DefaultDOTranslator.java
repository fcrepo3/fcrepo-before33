package fedora.server.storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
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
        
    private HashMap m_serializerClassNameMap;
    private HashMap m_deserializerClassNameMap;
    private String m_defaultFormat;
    
    /** FIXME: not thread safe, but little impact. */
    private Exception m_instex;
        
    public DefaultDOTranslator(Map moduleParameters, Server server, String role)
            throws ModuleInitializationException {
        super(moduleParameters, server, role);
    }
    
    public void initModule() 
            throws ModuleInitializationException {
        // get serializer_ deserializer_ formatname->classname mapping
        // and make sure we can get an instance of each for later
        m_serializerClassNameMap=new HashMap();
        m_deserializerClassNameMap=new HashMap();
        Iterator nameIter=parameterNames();
        while (nameIter.hasNext()) {
            String paramName=(String) nameIter.next();
            if (paramName.startsWith("serializer_")) {
                if (newSerializerInstance(getParameter(paramName))==null) {
                    throw new ModuleInitializationException(
                            "Can't instantiate class for format=" + paramName 
                            + " : " + m_instex.getMessage(), getRole());
                }
                m_serializerClassNameMap.put(paramName.substring(
                        paramName.indexOf("_")+1), getParameter(paramName));
            } else if (paramName.startsWith("deserializer")) {
                if (newDeserializerInstance(getParameter(paramName))==null) {
                    throw new ModuleInitializationException(
                            "Can't instantiate class for format=" + paramName 
                            + " : " + m_instex.getMessage(), getRole());
                }
                m_deserializerClassNameMap.put(paramName.substring(
                        paramName.indexOf("_")+1), getParameter(paramName));
            }
        }
        // now get default format, and ensure a serializer/deserializer pair
        // exists for that format
        m_defaultFormat=getParameter("defaultFormat");
        if (m_defaultFormat==null) {
            throw new ModuleInitializationException("defaultFormat"
                    + " must be specified.", getRole());
        }
        if (! (m_serializerClassNameMap.get(m_defaultFormat)!=null)
                & (m_deserializerClassNameMap.get(m_defaultFormat)!=null) ) {
            throw new ModuleInitializationException("implementing serializer"
                    + " *and* deserializer classes must be specified for "
                    + "defaultFormat '" + m_defaultFormat + "'.", getRole());
        }
    }
    
    public String getDefaultFormat() {
        return m_defaultFormat;
    }
    
    private DOSerializer newSerializerInstance(String className) {
        try {
            return (DOSerializer) Class.forName(className).newInstance();
        } catch (IllegalAccessException iae) {
            m_instex=iae;
        } catch (InstantiationException ie) {
            m_instex=ie;
        } catch (ClassNotFoundException cnfe) {
            m_instex=cnfe;
        } catch (ClassCastException cce) {
            m_instex=cce;
        }
        return null;
    }

    private DODeserializer newDeserializerInstance(String className) {
        try {
            return (DODeserializer) Class.forName(className).newInstance();
        } catch (IllegalAccessException iae) {
            m_instex=iae;
        } catch (InstantiationException ie) {
            m_instex=ie;
        } catch (ClassNotFoundException cnfe) {
            m_instex=cnfe;
        } catch (ClassCastException cce) {
            m_instex=cce;
        }
        return null;
    }

    public void deserialize(InputStream in, DigitalObject out, 
            String format, String encoding)
            throws ObjectIntegrityException, StreamIOException, 
            UnsupportedTranslationException {
        String className=(String) m_deserializerClassNameMap.get(format);
        if (className==null) {
            throw new UnsupportedTranslationException("No deserializer "
                    + "registered for format " + format);
        }
        DODeserializer d=newDeserializerInstance(className);
        if (d==null) {
            throw new UnsupportedTranslationException("Problem getting "
                    + "deserializer for format " + format + " (" + className 
                    + ") : " + m_instex.getClass().getName() + " : "
                    + m_instex.getMessage());
        }
        try {
            d.deserialize(in, out, encoding);
        } catch (UnsupportedEncodingException uee) {
            throw new UnsupportedTranslationException("Encoding, "
                    + encoding + " is not understood/supported by the JVM.");
        }
    }
    
    public void serialize(DigitalObject in, OutputStream out, 
            String format, String encoding)
            throws ObjectIntegrityException, StreamIOException, 
            UnsupportedTranslationException {
        String className=(String) m_serializerClassNameMap.get(format);
        if (className==null) {
            throw new UnsupportedTranslationException("No serializer "
                    + "registered for format " + format);
        }
        DOSerializer s=newSerializerInstance(className);
        if (s==null) {
            throw new UnsupportedTranslationException("Problem getting "
                    + "serializer for format " + format + " (" + className 
                    + ") : " + m_instex.getClass().getName() + " : "
                    + m_instex.getMessage());
        }
        try {
            s.serialize(in, out, encoding);
        } catch (UnsupportedEncodingException uee) {
            throw new UnsupportedTranslationException("Encoding, "
                    + encoding + " is not understood/supported by the JVM.");
        }
    }

}