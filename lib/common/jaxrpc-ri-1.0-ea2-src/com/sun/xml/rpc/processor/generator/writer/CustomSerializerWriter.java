// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   CustomSerializerWriter.java

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.processor.config.TypeMappingInfo;
import com.sun.xml.rpc.processor.generator.*;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaCustomType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.soap.SOAPArrayType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.util.exception.LocalizableExceptionAdapter;
import java.io.IOException;
import javax.naming.OperationNotSupportedException;

// Referenced classes of package com.sun.xml.rpc.processor.generator.writer:
//            SerializerWriterBase

public class CustomSerializerWriter extends SerializerWriterBase
    implements GeneratorConstants {

    private String serializerName;
    private String serializerMemberName;
    private String deserializerName;
    private String deserializerMemberName;

    public CustomSerializerWriter(SOAPType type) {
        super(type);
        serializerName = Names.getTypeQName(type.getName()) + "_Serializer";
        serializerMemberName = Names.getClassMemberName(serializerName);
        deserializerName = Names.getTypeQName(type.getName()) + "_Deserializer";
        deserializerMemberName = Names.getClassMemberName(deserializerName);
    }

    public void createSerializer(IndentingWriter p, StringBuffer typeName, String varName, boolean encodeTypes, boolean multiRefEncoding, String typeMapping) throws IOException {
        throw new GeneratorException("generator.nestedGeneratorError", new LocalizableExceptionAdapter(new OperationNotSupportedException()));
    }

    public void registerSerializer(IndentingWriter p, boolean encodeTypes, boolean multiRefEncoding, String typeMapping) throws IOException {
        TypeMappingInfo mappingInfo = ((JavaCustomType)super.type.getJavaType()).getTypeMappingInfo();
        if(!mappingInfo.getEncodingStyle().equals("http://schemas.xmlsoap.org/soap/encoding/")) {
            throw new GeneratorException("generator.unsupported.encoding.encountered", mappingInfo.getEncodingStyle().toString());
        } else {
            String serFac = mappingInfo.getSerializerFactoryName();
            String deserFac = mappingInfo.getDeserializerFactoryName();
            StringBuffer typeName = new StringBuffer("type");
            declareType(p, typeName, super.type.getName(), false, false);
            p.pln(typeMapping + ".register(" + super.type.getJavaType().getName() + ".class, " + typeName.toString() + ", " + "new " + serFac + "(), " + "new " + deserFac + "());");
            return;
        }
    }

    public void declareSerializer(IndentingWriter p, boolean isStatic, boolean isFinal) throws IOException {
        String modifier = getPrivateModifier(isStatic, isFinal);
        p.pln("private static JAXRPCSerializer " + serializerMemberName + ";");
        p.pln("private static JAXRPCDeserializer " + deserializerMemberName + ";");
    }

    public void initializeSerializer(IndentingWriter p, String typeName, String registry) throws IOException {
        p.pln(serializerMemberName + " = (JAXRPCSerializer)registry.getSerializer(SOAPConstants.NS_SOAP_ENCODING, " + super.type.getJavaType().getName() + ".class, " + typeName + ");");
        p.pln(deserializerMemberName + " = (JAXRPCDeserializer)registry.getDeserializer(SOAPConstants.NS_SOAP_ENCODING, " + super.type.getJavaType().getName() + ".class, " + typeName + ");");
    }

    public String serializerName() {
        return serializerName;
    }

    public String serializerMemberName() {
        return serializerMemberName;
    }

    public String deserializerName() {
        return deserializerName;
    }

    public String deserializerMemberName() {
        return deserializerMemberName;
    }

    protected String getPrivateModifier(boolean isStatic, boolean isFinal) {
        return "private " + super.getModifier(isStatic, isFinal);
    }

    public AbstractType getElementType() {
        SOAPType elemType;
        for(elemType = ((SOAPArrayType)super.type).getElementType(); elemType instanceof SOAPArrayType; elemType = ((SOAPArrayType)elemType).getElementType());
        return elemType;
    }
}
