// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ValueObjectSerializer.java

package com.sun.xml.rpc.encoding;

import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.beans.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.encoding:
//            GenericObjectSerializer, DeserializationException

public class ValueObjectSerializer extends GenericObjectSerializer {

    protected String memberNamespace;

    protected ValueObjectSerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle) {
        super(type == null ? new QName("") : type, encodeType, isNullable, encodingStyle);
        memberNamespace = null;
    }

    public ValueObjectSerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle, Class targetClass) {
        this(type, encodeType, isNullable, encodingStyle);
        super.setTargetClass(targetClass);
    }

    public ValueObjectSerializer(QName type, boolean encodeType, boolean isNullable, String encodingStyle, Class targetClass, String memberNamespace) {
        this(type, encodeType, isNullable, encodingStyle, targetClass);
        this.memberNamespace = memberNamespace;
    }

    protected void doSetTargetClass(Class targetClass) {
        try {
            introspectTargetClass(targetClass);
            reflectTargetClass(targetClass);
        }
        catch(Exception e) {
            throw new DeserializationException("nestedSerializationError", new LocalizableExceptionAdapter(e));
        }
    }

    protected void introspectTargetClass(Class targetClass) throws Exception {
        BeanInfo beanInfoForTarget = Introspector.getBeanInfo(targetClass);
        PropertyDescriptor targetProperties[] = beanInfoForTarget.getPropertyDescriptors();
        for(int i = 0; i < targetProperties.length; i++) {
            java.lang.reflect.Method getterMethod = targetProperties[i].getReadMethod();
            java.lang.reflect.Method setterMethod = targetProperties[i].getWriteMethod();
            if(getterMethod != null && setterMethod != null) {
                GenericObjectSerializer$MemberInfo member = new GenericObjectSerializer$MemberInfo();
                member.name = new QName(memberNamespace, targetProperties[i].getName());
                member.javaType = getBoxedClassFor(targetProperties[i].getPropertyType());
                member.getter = new ValueObjectSerializer$1(this, getterMethod);
                member.setter = new ValueObjectSerializer$2(this, setterMethod);
                super.addMember(member);
            }
        }

    }

    protected void reflectTargetClass(Class targetClass) throws Exception {
        Field targetFields[] = targetClass.getFields();
        for(int i = 0; i < targetFields.length; i++) {
            Field currentField = targetFields[i];
            int fieldModifiers = currentField.getModifiers();
            if(Modifier.isPublic(fieldModifiers) && !Modifier.isTransient(fieldModifiers) && !Modifier.isFinal(fieldModifiers)) {
                GenericObjectSerializer$MemberInfo member = new GenericObjectSerializer$MemberInfo();
                member.name = new QName(memberNamespace, currentField.getName());
                member.javaType = getBoxedClassFor(targetFields[i].getType());
                member.getter = new ValueObjectSerializer$3(this, currentField);
                member.setter = new ValueObjectSerializer$4(this, currentField);
                super.addMember(member);
            }
        }

    }

    private static Class getBoxedClassFor(Class possiblePrimitiveType) {
        if(!possiblePrimitiveType.isPrimitive())
            return possiblePrimitiveType;
        if(possiblePrimitiveType == Boolean.TYPE)
            return java.lang.Boolean.class;
        if(possiblePrimitiveType == Byte.TYPE)
            return java.lang.Byte.class;
        if(possiblePrimitiveType == Short.TYPE)
            return java.lang.Short.class;
        if(possiblePrimitiveType == Integer.TYPE)
            return java.lang.Integer.class;
        if(possiblePrimitiveType == Long.TYPE)
            return java.lang.Long.class;
        if(possiblePrimitiveType == Character.TYPE)
            return java.lang.Character.class;
        if(possiblePrimitiveType == Float.TYPE)
            return java.lang.Float.class;
        if(possiblePrimitiveType == Double.TYPE)
            return java.lang.Double.class;
        else
            return possiblePrimitiveType;
    }
}
