// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SerializerWriterBase.java

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.GeneratorUtil;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import java.io.IOException;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.generator.writer:
//            SerializerWriter

public abstract class SerializerWriterBase
    implements SerializerWriter {

    AbstractType type;

    public SerializerWriterBase(AbstractType type) {
        this.type = type;
    }

    public void registerSerializer(IndentingWriter p, boolean encodeTypes, boolean multiRefEncoding, String typeMapping) throws IOException {
        StringBuffer typeName = new StringBuffer(40);
        typeName.append("type");
        createSerializer(p, typeName, "serializer", encodeTypes, multiRefEncoding, typeMapping);
        writeRegisterFactories(p, typeName.toString(), "serializer", typeMapping);
    }

    public void initializeSerializer(IndentingWriter p, String typeName, String registry) throws IOException {
        p.pln(serializerMemberName() + " = (CombinedSerializer)registry.getSerializer(" + getEncodingStyleString() + ", " + type.getJavaType().getName() + ".class, " + typeName + ");");
    }

    public String serializerName() {
        return GeneratorConstants.BASE_SERIALIZER_NAME;
    }

    public String deserializerName() {
        return serializerName();
    }

    protected String getEncodingStyleString() {
        return "SOAPConstants.NS_SOAP_ENCODING";
    }

    protected void declareType(IndentingWriter p, StringBuffer member, QName type, boolean isStatic, boolean isFinal) throws IOException {
        String qnameConstant = GeneratorUtil.getQNameConstant(type);
        if(qnameConstant != null) {
            member.delete(0, member.length());
            member.append(qnameConstant);
        } else {
            String modifier = getModifier(isStatic, isFinal);
            p.p(modifier + "QName " + member + " = ");
            GeneratorUtil.writeNewQName(p, type);
            p.pln(";");
        }
    }

    protected void writeRegisterFactories(IndentingWriter p, String typeName, String memberName, String mapping) throws IOException {
        p.pln("registerSerializer(" + mapping + "," + type.getJavaType().getName() + ".class, " + typeName + ", " + memberName + ");");
    }

    protected String getModifier(boolean isStatic, boolean isFinal) {
        String modifier = "";
        if(isStatic)
            modifier = modifier + "static ";
        if(isFinal)
            modifier = modifier + "final ";
        return modifier;
    }

    public abstract String deserializerMemberName();

    public abstract String serializerMemberName();

    public abstract void declareSerializer(IndentingWriter indentingwriter, boolean flag, boolean flag1) throws IOException;

    public abstract void createSerializer(IndentingWriter indentingwriter, StringBuffer stringbuffer, String s, boolean flag, boolean flag1, String s1) throws IOException;
}
