// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   DynamicSerializerWriter.java

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import java.io.IOException;

// Referenced classes of package com.sun.xml.rpc.processor.generator.writer:
//            SerializerWriterBase

public class DynamicSerializerWriter extends SerializerWriterBase
    implements GeneratorConstants {

    private String serializerMemberName;

    public DynamicSerializerWriter(SOAPType type) {
        super(type);
        String serializerName = "DynamicSerializer";
        serializerMemberName = Names.getClassMemberName(serializerName, type);
    }

    public void createSerializer(IndentingWriter p, StringBuffer typeName, String serName, boolean encodeTypes, boolean multiRefEncoding, String typeMapping) throws IOException {
        SOAPType type = (SOAPType)super.type;
        String nillable = type.isNillable() ? "NULLABLE" : "NOT_NULLABLE";
        String referenceable = type.isReferenceable() ? "REFERENCEABLE" : "NOT_REFERENCEABLE";
        String multiRef = "DONT_SERIALIZE_AS_REF";
        String encodeType = encodeTypes ? "ENCODE_TYPE" : "DONT_ENCODE_TYPE";
        declareType(p, typeName, type.getName(), false, false);
        p.plnI(serializerName() + " " + serName + " = new " + GeneratorConstants.DYNAMIC_SERIALIZER_NAME + "(" + typeName + ",");
        p.pln(encodeType + ", " + nillable + ", SOAPConstants.NS_SOAP_ENCODING);");
        p.pO();
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
}
