// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   GenericObjectSerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.streaming.XMLReader;
import com.sun.xml.rpc.streaming.XMLWriter;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            ObjectSerializerBase, JAXRPCSerializer, JAXRPCDeserializer, EncodingException, 
//            SOAPDeserializationState, Initializable, SerializerBase, InternalTypeMappingRegistry, 
//            SOAPSerializationContext, SOAPDeserializationContext

public class GenericObjectSerializer extends ObjectSerializerBase
    implements Initializable {

    protected Class targetClass;
    protected List members;

    public GenericObjectSerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle) {
        super(type, encodeType, isNullable, encodingStyle);
        targetClass = null;
        members = new ArrayList();
    }

    protected void setTargetClass(Class targetClass) {
        clearMembers();
        doSetTargetClass(targetClass);
        this.targetClass = targetClass;
    }

    protected void doSetTargetClass(Class class1) {
    }

    public void initialize(InternalTypeMappingRegistry registry) throws Exception {
        for(Iterator eachMember = members.iterator(); eachMember.hasNext();) {
            GenericObjectSerializer$MemberInfo currentMember = (GenericObjectSerializer$MemberInfo)eachMember.next();
            currentMember.serializer = (JAXRPCSerializer)registry.getSerializer(super.encodingStyle, currentMember.javaType, currentMember.xmlType);
            currentMember.deserializer = (JAXRPCDeserializer)registry.getDeserializer(super.encodingStyle, currentMember.javaType, currentMember.xmlType);
        }

    }

    public void clearMembers() {
        members.clear();
    }

    public void addMember(GenericObjectSerializer$MemberInfo member) throws Exception {
        for(Iterator eachMember = members.iterator(); eachMember.hasNext();) {
            GenericObjectSerializer$MemberInfo existingMember = (GenericObjectSerializer$MemberInfo)eachMember.next();
            if(existingMember.name.equals(member.name))
                throw new EncodingException("soap.duplicate.data.member", new Object[] {
                    member.name
                });
        }

        members.add(member);
    }

    protected void doSerializeInstance(Object instance, XMLWriter writer, SOAPSerializationContext context) throws Exception {
        for(int i = 0; i < members.size(); i++) {
            GenericObjectSerializer$MemberInfo currentMember = (GenericObjectSerializer$MemberInfo)members.get(i);
            currentMember.serializer.serialize(currentMember.getter.get(instance), currentMember.name, null, writer, context);
        }

    }

    protected Object doDeserialize(SOAPDeserializationState state, XMLReader reader, SOAPDeserializationContext context) throws Exception {
        Object instance = targetClass.newInstance();
        GenericObjectSerializer$SOAPGenericObjectInstanceBuilder builder = null;
        boolean isComplete = true;
        int lastMemberIndex = members.size() - 1;
        for(int memberCount = 0; memberCount <= lastMemberIndex; memberCount++) {
            reader.nextElementContent();
            int memberIndex = memberCount;
            do {
                GenericObjectSerializer$MemberInfo currentMember = (GenericObjectSerializer$MemberInfo)members.get(memberIndex);
                if(reader.getName().equals(currentMember.name)) {
                    Object member = currentMember.deserializer.deserialize(currentMember.name, reader, context);
                    if(member instanceof SOAPDeserializationState) {
                        if(builder == null)
                            builder = new GenericObjectSerializer$SOAPGenericObjectInstanceBuilder(this, instance);
                        state = ObjectSerializerBase.registerWithMemberState(instance, state, member, memberIndex, builder);
                        isComplete = false;
                    } else {
                        currentMember.setter.set(instance, member);
                    }
                    break;
                }
                if(memberIndex == lastMemberIndex)
                    memberIndex = 0;
                else
                    memberIndex++;
            } while(memberIndex != memberCount);
        }

        return isComplete ? instance : state;
    }
}
