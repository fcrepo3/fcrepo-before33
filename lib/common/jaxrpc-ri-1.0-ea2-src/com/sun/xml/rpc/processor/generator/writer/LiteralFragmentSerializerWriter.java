// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralFragmentSerializerWriter.java

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import java.io.IOException;

// Referenced classes of package com.sun.xml.rpc.processor.generator.writer:
//            LiteralSerializerWriterBase, SerializerWriterBase

public class LiteralFragmentSerializerWriter extends LiteralSerializerWriterBase
    implements GeneratorConstants {

    private String serializerMemberName;

    public LiteralFragmentSerializerWriter(LiteralFragmentType type) {
        super(type);
        String serializerName = GeneratorConstants.LITERAL_FRAGMENT_SERIALIZER_NAME;
        serializerMemberName = Names.getLiteralFragmentTypeSerializerMemberName(type);
    }

    public void createSerializer(IndentingWriter p, StringBuffer typeName, String serName, boolean encodeTypes, boolean multiRefEncoding, String typeMapping) throws IOException {
        LiteralFragmentType type = (LiteralFragmentType)super.type;
        String nillable = type.isNillable() ? "NULLABLE" : "NOT_NULLABLE";
        String encodeType = encodeTypes ? "ENCODE_TYPE" : "DONT_ENCODE_TYPE";
        declareType(p, typeName, type.getName(), false, false);
        p.pln(serializerName() + " " + serName + " = new " + GeneratorConstants.LITERAL_FRAGMENT_SERIALIZER_NAME + "(" + typeName + ", " + nillable + ", \"\");");
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
