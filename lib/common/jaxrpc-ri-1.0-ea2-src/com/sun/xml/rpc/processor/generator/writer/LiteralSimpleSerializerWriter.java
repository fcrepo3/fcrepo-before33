// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralSimpleSerializerWriter.java

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.processor.generator.*;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.modeler.ModelerConstants;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.wsdl.document.schema.BuiltInTypes;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.generator.writer:
//            LiteralSerializerWriterBase, SerializerWriterBase

public class LiteralSimpleSerializerWriter extends LiteralSerializerWriterBase
    implements GeneratorConstants {

    private String encoder;
    private String serializerMemberName;
    private static Map encoderMap;

    public LiteralSimpleSerializerWriter(LiteralType type) {
        super(type);
        encoder = null;
        encoder = getTypeEncoder(type);
        if(encoder == null) {
            throw new GeneratorException("generator.simpleTypeSerializerWriter.no.encoder.for.type", new Object[] {
                type.getName().toString(), type.getJavaType().getName()
            });
        } else {
            String partialSerializerName = encoder.substring(3, encoder.lastIndexOf("Encoder"));
            String serializerName = partialSerializerName + "_Serializer";
            serializerMemberName = Names.getClassMemberName(partialSerializerName, type, "_Serializer");
            return;
        }
    }

    public void createSerializer(IndentingWriter p, StringBuffer typeName, String serName, boolean encodeTypes, boolean multiRefEncoding, String typeMapping) throws IOException {
        LiteralType type = (LiteralType)super.type;
        declareType(p, typeName, type.getName(), false, false);
        QName typeQName = type.getName();
        p.plnI(serializerName() + " " + serName + " = new " + GeneratorConstants.LITERAL_SIMPLE_TYPE_SERIALIZER_NAME + "(" + typeName + ",");
        p.pln("\"\", " + encoder + ".getInstance());");
        p.pO();
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

    public static String getTypeEncoder(AbstractType type) {
        QName name = type.getName();
        String encoder = (String)encoderMap.get(name);
        if(encoder == null) {
            String javaName = type.getJavaType().getName();
            if(name.equals(BuiltInTypes.DATE_TIME) || name.equals(SOAPConstants.QNAME_TYPE_DATE_TIME)) {
                if(javaName.equals(ModelerConstants.DATE_CLASSNAME))
                    encoder = GeneratorConstants.XSD_DATE_TIME_DATE_ENCODER_NAME;
                else
                if(javaName.equals(ModelerConstants.CALENDAR_CLASSNAME))
                    encoder = GeneratorConstants.XSD_DATE_TIME_CALENDAR_ENCODER_NAME;
            } else
            if(name.equals(BuiltInTypes.BASE64_BINARY) || name.equals(SOAPConstants.QNAME_TYPE_BASE64_BINARY) || name.equals(SOAPConstants.QNAME_TYPE_BASE64)) {
                if(javaName.equals(ModelerConstants.BYTE_ARRAY_CLASSNAME))
                    encoder = GeneratorConstants.XSD_BASE64_BINARY_ENCODER_NAME;
            } else
            if(name.equals(BuiltInTypes.HEX_BINARY) || name.equals(SOAPConstants.QNAME_TYPE_HEX_BINARY)) {
                if(javaName.equals(ModelerConstants.BYTE_ARRAY_CLASSNAME))
                    encoder = GeneratorConstants.XSD_HEX_BINARY_ENCODER_NAME;
            } else
            if(javaName.equals(ModelerConstants.STRING_CLASSNAME))
                encoder = GeneratorConstants.XSD_STRING_ENCODER_NAME;
        }
        return encoder;
    }

    protected String getEncoder() {
        return getTypeEncoder(super.type);
    }

    static  {
        encoderMap = null;
        encoderMap = new HashMap();
        encoderMap.put(BuiltInTypes.BOOLEAN, GeneratorConstants.XSD_BOOLEAN_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.BYTE, GeneratorConstants.XSD_BYTE_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.BASE64_BINARY, null);
        encoderMap.put(BuiltInTypes.HEX_BINARY, null);
        encoderMap.put(BuiltInTypes.DOUBLE, GeneratorConstants.XSD_DOUBLE_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.FLOAT, GeneratorConstants.XSD_FLOAT_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.INT, GeneratorConstants.XSD_INT_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.INTEGER, GeneratorConstants.XSD_INTEGER_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.LONG, GeneratorConstants.XSD_LONG_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.SHORT, GeneratorConstants.XSD_SHORT_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.DECIMAL, GeneratorConstants.XSD_DECIMAL_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.DATE_TIME, null);
        encoderMap.put(BuiltInTypes.STRING, GeneratorConstants.XSD_STRING_ENCODER_NAME);
        encoderMap.put(BuiltInTypes.QNAME, GeneratorConstants.XSD_QNAME_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_BOOLEAN, GeneratorConstants.XSD_BOOLEAN_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_BYTE, GeneratorConstants.XSD_BYTE_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_BASE64_BINARY, null);
        encoderMap.put(SOAPConstants.QNAME_TYPE_DOUBLE, GeneratorConstants.XSD_DOUBLE_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_FLOAT, GeneratorConstants.XSD_FLOAT_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_INT, GeneratorConstants.XSD_INT_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_LONG, GeneratorConstants.XSD_LONG_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_SHORT, GeneratorConstants.XSD_SHORT_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_DECIMAL, GeneratorConstants.XSD_DECIMAL_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_DATE_TIME, null);
        encoderMap.put(SOAPConstants.QNAME_TYPE_STRING, GeneratorConstants.XSD_STRING_ENCODER_NAME);
        encoderMap.put(SOAPConstants.QNAME_TYPE_QNAME, GeneratorConstants.XSD_QNAME_ENCODER_NAME);
    }
}
