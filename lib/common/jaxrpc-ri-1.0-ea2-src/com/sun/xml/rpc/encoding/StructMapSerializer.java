// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy

// Source File Name:   StructMapSerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import com.sun.xml.rpc.util.StructMap;
import java.util.Collection;
import java.util.Iterator;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            ObjectSerializerBase, JAXRPCSerializer, JAXRPCDeserializer, SOAPDeserializationState,
//            Initializable, SerializerBase, InternalTypeMappingRegistry, SOAPSerializationContext,
//            SOAPDeserializationContext

public class StructMapSerializer extends ObjectSerializerBase
    implements Initializable {

    protected InternalTypeMappingRegistry registry;

    public StructMapSerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle) {
        super(type, encodeType, isNullable, encodingStyle);
    }

    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        this.registry = registry;
    }

    protected void doSerializeInstance(Object instance, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        StructMap struct = (StructMap)instance;
        Iterator eachKey = struct.keys().iterator();
        Iterator eachValue = struct.values().iterator();
        while(eachKey.hasNext())  {
            Object value = eachValue.next();
            QName key = (QName)eachKey.next();
            if(value != null) {
                JAXRPCSerializer serializer = (JAXRPCSerializer)registry.getSerializer(super.encodingStyle, value.getClass());
                serializer.serialize(value, key, null, writer, context);
            } else {
                serializeNull(key, writer, context);
            }
        }
    }

    protected Object doDeserialize(SOAPDeserializationState state, XMLReader reader, SOAPDeserializationContext context) throws Exception {
        StructMap instance = new StructMap();
        StructMapSerializer$StructMapBuilder builder = null;
        boolean isComplete = true;
        int memberIndex = 0;
        while(reader.getState() != 2)  {
            reader.nextElementContent();
            QName key = reader.getName();
            if(!SerializerBase.getNullStatus(reader)) {
                JAXRPCDeserializer deserializer = (JAXRPCDeserializer)registry.getDeserializer(super.encodingStyle, SerializerBase.getType(reader));
                Object member = deserializer.deserialize(key, reader, context);
                if(member instanceof SOAPDeserializationState) {
                    if(builder == null)
                        builder = new StructMapSerializer$StructMapBuilder(this, instance);
                    state = ObjectSerializerBase.registerWithMemberState(instance, state, member, memberIndex, builder);
                    isComplete = false;
                }
                instance.put(key, member);
            } else {
                instance.put(key, null);
            }
        }
        if (isComplete)
            return instance;
        else
            return state;

    }
}
