package fedora.server.storage;

import java.io.InputStream;
import java.io.OutputStream;
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
                    throw new ModuleInitializationException("Can't instantiate class for format=" + paramName + " : " + m_instex.getMessage(), getRole());
                }
                m_serializerClassNameMap.put(paramName.substring(
                        paramName.indexOf("_")+1), getParameter(paramName));
            } else if (paramName.startsWith("deserializer")) {
                if (newDeserializerInstance(getParameter(paramName))==null) {
                    throw new ModuleInitializationException("Can't instantiate class for format=" + paramName + " : " + m_instex.getMessage(), getRole());
                }
                m_deserializerClassNameMap.put(paramName.substring(
                        paramName.indexOf("_")+1), getParameter(paramName));
            }
        }
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