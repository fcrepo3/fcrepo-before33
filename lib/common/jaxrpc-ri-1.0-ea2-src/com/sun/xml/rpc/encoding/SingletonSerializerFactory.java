// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SingletonSerializerFactory.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.SingleElementIterator;
import java.util.Iterator;
import javax.xml.rpc.encoding.Serializer;
import javax.xml.rpc.encoding.SerializerFactory;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            TypeMappingException

public class SingletonSerializerFactory
    implements SerializerFactory {

    protected Serializer serializer;

    public SingletonSerializerFactory(Serializer serializer) {
        this.serializer = serializer;
    }

    public Serializer getSerializerAs(String mechanismType) {
        if(!"http://java.sun.com/jax-rpc-ri/1.0/streaming/".equals(mechanismType))
            throw new TypeMappingException("typemapping.mechanism.unsupported", mechanismType);
        else
            return serializer;
    }

    public Iterator getSupportedMechanismTypes() {
        return new SingleElementIterator("http://java.sun.com/jax-rpc-ri/1.0/streaming/");
    }

    public Iterator getSerializers() {
        return new SingleElementIterator(serializer);
    }
}
