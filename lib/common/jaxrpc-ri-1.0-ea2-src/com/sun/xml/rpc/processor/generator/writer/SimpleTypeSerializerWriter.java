// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SimpleTypeSerializerWriter.java

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.encoding.AttachmentConstants;
import com.sun.xml.rpc.processor.generator.*;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.modeler.ModelerConstants;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.wsdl.document.schema.BuiltInTypes;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import java.io.IOException;
import java.util.*;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.generator.writer:
//            SerializerWriterBase

public class SimpleTypeSerializerWriter extends SerializerWriterBase
    implements GeneratorConstants {

    private String encoder;
    private String serializerMemberName;
    private static Map encoderMap;
    private static Set attachmentTypes;

    public SimpleTypeSerializerWriter(SOAPType type) {
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
        SOAPType type = (SOAPType)super.type;
        String nillable = type.isNillable() ? "NULLABLE" : "NOT_NULLABLE";
        String referenceable = type.isReferenceable() ? "REFERENCEABLE" : "NOT_REFERENCEABLE";
        String multiRef = !multiRefEncoding || !type.isReferenceable() ? "DONT_SERIALIZE_AS_REF" : "SERIALIZE_AS_REF";
        String encodeType = encodeTypes ? "ENCODE_TYPE" : "DONT_ENCODE_TYPE";
        declareType(p, typeName, type.getName(), false, false);
        QName typeQName = type.getName();
        if(attachmentTypes.contains(typeQName)) {
            boolean serAsAttachment = !typeQName.equals(SOAPConstants.QNAME_TYPE_STRING) && !typeQName.equals(BuiltInTypes.STRING);
            p.plnI(serializerName() + " " + serName + " = new " + GeneratorConstants.ATTACHMENT_SERIALIZER_NAME + "(" + typeName + ",");
            p.pln(encodeType + ", " + nillable + ", SOAPConstants.NS_SOAP_ENCODING, " + serAsAttachment + ", " + encoder + ".getInstance());");
            multiRef = "DONT_SERIALIZE_AS_REF";
        } else
        if(typeQName.equals(BuiltInTypes.BASE64_BINARY) || typeQName.equals(SOAPConstants.QNAME_TYPE_BASE64_BINARY) || typeQName.equals(SOAPConstants.QNAME_TYPE_BASE64)) {
            p.plnI(serializerName() + " " + serName + " = new " + GeneratorConstants.SIMPLE_MULTI_TYPE_SERIALIZER_NAME + "(" + typeName + ",");
            p.pln(encodeType + ", " + nillable + ", SOAPConstants.NS_SOAP_ENCODING, " + encoder + ".getInstance(),");
            p.plnI("new QName[] {");
            GeneratorUtil.writeNewQName(p, BuiltInTypes.BASE64_BINARY);
            p.pln(",");
            GeneratorUtil.writeNewQName(p, SOAPConstants.QNAME_TYPE_BASE64_BINARY);
            p.pln(",");
            GeneratorUtil.writeNewQName(p, SOAPConstants.QNAME_TYPE_BASE64);
            p.pOln("});");
        } else {
            p.plnI(serializerName() + " " + serName + " = new " + GeneratorConstants.SIMPLE_TYPE_SERIALIZER_NAME + "(" + typeName + ",");
            p.pln(encodeType + ", " + nillable + ", SOAPConstants.NS_SOAP_ENCODING, " + encoder + ".getInstance());");
        }
        p.pO();
        if(type.isReferenceable()) {
            p.plnI(serName + " = new " + GeneratorConstants.REFERENCEABLE_SERIALIZER_NAME + "(" + multiRef + ", " + serName + ");");
            p.pO();
        }
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
            if((name.equals(BuiltInTypes.HEX_BINARY) || name.equals(SOAPConstants.QNAME_TYPE_HEX_BINARY)) && javaName.equals(ModelerConstants.BYTE_ARRAY_CLASSNAME))
                encoder = GeneratorConstants.XSD_HEX_BINARY_ENCODER_NAME;
        }
        return encoder;
    }

    protected String getEncoder() {
        return getTypeEncoder(super.type);
    }

    static  {
        encoderMap = null;
        attachmentTypes = null;
        attachmentTypes = new HashSet();
        attachmentTypes.add(AttachmentConstants.QNAME_TYPE_IMAGE);
        attachmentTypes.add(AttachmentConstants.QNAME_TYPE_MIME_MULTIPART);
        attachmentTypes.add(AttachmentConstants.QNAME_TYPE_SOURCE);
        attachmentTypes.add(AttachmentConstants.QNAME_TYPE_DATA_HANDLER);
        attachmentTypes.add(BuiltInTypes.STRING);
        attachmentTypes.add(SOAPConstants.QNAME_TYPE_STRING);
        encoderMap = new HashMap();
        encoderMap.put(AttachmentConstants.QNAME_TYPE_IMAGE, GeneratorConstants.IMAGE_ENCODER_NAME);
        encoderMap.put(AttachmentConstants.QNAME_TYPE_MIME_MULTIPART, GeneratorConstants.MIME_MULTIPART_ENCODER_NAME);
        encoderMap.put(AttachmentConstants.QNAME_TYPE_SOURCE, GeneratorConstants.SOURCE_ENCODER_NAME);
        encoderMap.put(AttachmentConstants.QNAME_TYPE_DATA_HANDLER, GeneratorConstants.DATA_HANDLER_ENCODER_NAME);
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
