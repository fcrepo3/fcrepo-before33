// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SingletonDeserializerFactory.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.SingleElementIterator;
import java.util.Iterator;
import javax.xml.rpc.encoding.Deserializer;
import javax.xml.rpc.encoding.DeserializerFactory;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            TypeMappingException

public class SingletonDeserializerFactory
    implements DeserializerFactory {

    protected Deserializer deserializer;

    public SingletonDeserializerFactory(Deserializer deserializer) {
        this.deserializer = deserializer;
    }

    public Deserializer getDeserializerAs(String mechanismType) {
        if(!"http://java.sun.com/jax-rpc-ri/1.0/streaming/".equals(mechanismType))
            throw new TypeMappingException("typemapping.mechanism.unsupported", mechanismType);
        else
            return deserializer;
    }

    public Iterator getSupportedMechanismTypes() {
        return new SingleElementIterator("http://java.sun.com/jax-rpc-ri/1.0/streaming/");
    }

    public Iterator getDeserializers() {
        return new SingleElementIterator(deserializer);
    }
}
