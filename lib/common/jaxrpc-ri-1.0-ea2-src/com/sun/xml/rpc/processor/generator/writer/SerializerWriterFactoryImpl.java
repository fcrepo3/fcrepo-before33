// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SerializerWriterFactoryImpl.java

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.processor.generator.GeneratorException;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.*;
import com.sun.xml.rpc.processor.model.soap.*;
import java.util.HashMap;
import java.util.Map;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.generator.writer:
//            DynamicSerializerWriter, SimpleTypeSerializerWriter, SOAPObjectSerializerWriter, ArraySerializerWriter, 
//            EnumerationSerializerWriter, CustomSerializerWriter, LiteralFragmentSerializerWriter, LiteralSimpleSerializerWriter, 
//            LiteralSequenceSerializerWriter, SerializerWriter, SerializerWriterFactory

public class SerializerWriterFactoryImpl
    implements SerializerWriterFactory {

    private Map writerMap;

    public SerializerWriterFactoryImpl() {
        writerMap = new HashMap();
    }

    public SerializerWriter createWriter(AbstractType type) {
        SerializerWriter writer = getTypeSerializerWriter(type);
        if(writer == null) {
            if(type instanceof SOAPAnyType)
                writer = new DynamicSerializerWriter((SOAPType)type);
            else
            if(type instanceof SOAPSimpleType)
                writer = new SimpleTypeSerializerWriter((SOAPType)type);
            else
            if(type instanceof SOAPStructureType)
                writer = new SOAPObjectSerializerWriter((SOAPType)type);
            else
            if(type instanceof SOAPArrayType)
                writer = new ArraySerializerWriter((SOAPType)type);
            else
            if(type instanceof SOAPEnumerationType)
                writer = new EnumerationSerializerWriter((SOAPType)type);
            else
            if(type instanceof SOAPCustomType)
                writer = new CustomSerializerWriter((SOAPType)type);
            else
            if(type instanceof LiteralFragmentType)
                writer = new LiteralFragmentSerializerWriter((LiteralFragmentType)type);
            else
            if(type instanceof LiteralSimpleType)
                writer = new LiteralSimpleSerializerWriter((LiteralSimpleType)type);
            else
            if(type instanceof LiteralSequenceType)
                writer = new LiteralSequenceSerializerWriter((LiteralSequenceType)type);
            else
            if(type instanceof LiteralAllType)
                writer = new LiteralSequenceSerializerWriter((LiteralAllType)type);
            if(writer == null)
                throw new GeneratorException("generator.unsupported.type.encountered", new Object[] {
                    type.getName().getLocalPart(), type.getName().getNamespaceURI()
                });
            setTypeSerializerWriter(type, writer);
        }
        return writer;
    }

    private SerializerWriter getTypeSerializerWriter(AbstractType type) {
        String key = genKey(type.getName(), type.getJavaType().getName());
        SerializerWriter writer = (SerializerWriter)writerMap.get(key);
        return writer;
    }

    private void setTypeSerializerWriter(AbstractType type, SerializerWriter writer) {
        String key = genKey(type.getName(), type.getJavaType().getName());
        writerMap.put(key, writer);
    }

    protected static String genKey(QName schemaType, String javaType) {
        return schemaType + ";" + javaType;
    }
}
