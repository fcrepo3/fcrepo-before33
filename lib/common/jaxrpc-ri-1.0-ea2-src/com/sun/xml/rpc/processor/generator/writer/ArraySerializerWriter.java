// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ArraySerializerWriter.java

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.processor.generator.*;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

// Referenced classes of package com.sun.xml.rpc.processor.generator.writer:
//            SerializerWriterBase, SerializerWriterFactoryImpl, SerializerWriterFactory, SerializerWriter

public class ArraySerializerWriter extends SerializerWriterBase
    implements GeneratorConstants {

    private String serializerMemberName;
    private AbstractType baseElemType;
    private SerializerWriterFactory writerFactory;
    private static Set boxedSet;

    public ArraySerializerWriter(SOAPType type) {
        super(type);
        String serializerName = Names.typeObjectArraySerializerClassName(type);
        serializerMemberName = Names.getClassMemberName(serializerName, type) + ((SOAPArrayType)type).getRank();
        baseElemType = getBaseElementType();
        writerFactory = new SerializerWriterFactoryImpl();
    }

    public void createSerializer(IndentingWriter p, StringBuffer typeName, String serName, boolean encodeTypes, boolean multiRefEncoding, String typeMapping) throws IOException {
        SOAPArrayType type = (SOAPArrayType)super.type;
        String nillable = type.isNillable() ? "NULLABLE" : "NOT_NULLABLE";
        String referenceable = type.isReferenceable() ? "REFERENCEABLE" : "NOT_REFERENCEABLE";
        String multiRef = !multiRefEncoding || !type.isReferenceable() ? "DONT_SERIALIZE_AS_REF" : "SERIALIZE_AS_REF";
        String encodeType = encodeTypes ? "ENCODE_TYPE" : "DONT_ENCODE_TYPE";
        declareType(p, typeName, type.getName(), false, false);
        StringBuffer elemName = new StringBuffer("elemName");
        if(type.getElementName() != null)
            declareType(p, elemName, type.getElementName(), false, false);
        else
            elemName = new StringBuffer("null");
        StringBuffer elemType = new StringBuffer("elemType");
        declareType(p, elemType, baseElemType.getName(), false, false);
        if(isSimpleType(baseElemType.getJavaType().getName()) && !((SOAPType)baseElemType).isReferenceable()) {
            SerializerWriter writer = writerFactory.createWriter(baseElemType);
            StringBuffer serNameElemType = new StringBuffer(serName + elemType);
            writer.createSerializer(p, serNameElemType, serName + "elemSerializer", encodeTypes, multiRefEncoding, typeMapping);
            p.plnI(serializerName() + " " + serName + " = new SimpleTypeArraySerializer(" + typeName + ",");
            p.pln(encodeType + ", " + nillable + ", SOAPConstants.NS_SOAP_ENCODING, ");
            p.pln(elemName + ", " + elemType + ", " + baseElemType.getJavaType().getName() + ".class, " + type.getRank() + ", " + type.getSize() + ", (SimpleTypeSerializer)" + serName + "elemSerializer);");
            p.pO();
        } else {
            p.plnI(serializerName() + " " + serName + " = new ObjectArraySerializer(" + typeName + ",");
            p.pln(encodeType + ", " + nillable + ", SOAPConstants.NS_SOAP_ENCODING, ");
            p.pln(elemName + ", " + elemType + ", " + baseElemType.getJavaType().getName() + ".class, " + type.getRank() + ", " + type.getSize() + ");");
            p.pO();
        }
        if(type.isReferenceable())
            p.pln(serName + " = new " + GeneratorConstants.REFERENCEABLE_SERIALIZER_NAME + "(" + multiRef + ", " + serName + ");");
    }

    public void declareSerializer(IndentingWriter p, boolean isStatic, boolean isFinal) throws IOException {
        String modifier = getPrivateModifier(isStatic, isFinal);
        p.pln(modifier + serializerName() + " " + serializerMemberName + ";");
    }

    public String serializerMemberName() {
        return serializerMemberName;
    }

    public String deserializerMemberName() {
        return serializerMemberName;
    }

    protected String getPrivateModifier(boolean isStatic, boolean isFinal) {
        return "private " + super.getModifier(isStatic, isFinal);
    }

    public AbstractType getBaseElementType() {
        SOAPType elemType;
        for(elemType = ((SOAPArrayType)super.type).getElementType(); elemType instanceof SOAPArrayType; elemType = ((SOAPArrayType)elemType).getElementType());
        return elemType;
    }

    private boolean isSimpleType(String javaName) {
        return SimpleToBoxedUtil.isPrimitive(javaName) || boxedSet.contains(javaName);
    }

    static  {
        boxedSet = null;
        boxedSet = new HashSet();
        boxedSet.add("java.lang.Boolean");
        boxedSet.add("java.lang.Byte");
        boxedSet.add("java.lang.Double");
        boxedSet.add("java.lang.Float");
        boxedSet.add("java.lang.Int");
        boxedSet.add("java.lang.Long");
        boxedSet.add("java.lang.Short");
        boxedSet.add("java.lang.String");
        boxedSet.add("java.lang.QName");
        boxedSet.add("java.lang.BigDecimal");
        boxedSet.add("java.lang.BigInteger");
        boxedSet.add("java.util.Calendar");
        boxedSet.add("java.util.Date");
    }
}
