// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralSequenceSerializerWriter.java

package com.sun.xml.rpc.processor.generator.writer;

import com.sun.xml.rpc.processor.generator.GeneratorConstants;
import com.sun.xml.rpc.processor.generator.Names;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import java.io.IOException;

// Referenced classes of package com.sun.xml.rpc.processor.generator.writer:
//            LiteralSerializerWriterBase, SerializerWriterBase

public class LiteralSequenceSerializerWriter extends LiteralSerializerWriterBase
    implements GeneratorConstants {

    private String serializerName;
    private String serializerMemberName;

    public LiteralSequenceSerializerWriter(LiteralType type) {
        super(type);
        serializerName = Names.typeObjectSerializerClassName(type);
        serializerMemberName = Names.getClassMemberName(serializerName);
    }

    public void createSerializer(IndentingWriter p, StringBuffer typeName, String serName, boolean encodeTypes, boolean multiRefEncoding, String typeMapping) throws IOException {
        LiteralType type = (LiteralType)super.type;
        declareType(p, typeName, type.getName(), false, false);
        p.plnI(GeneratorConstants.BASE_SERIALIZER_NAME + " " + serName + " = new " + serializerName + "(" + typeName + ", \"\");");
        p.pO();
    }

    public void declareSerializer(IndentingWriter p, boolean isStatic, boolean isFinal) throws IOException {
        String modifier = getPrivateModifier(isStatic, isFinal);
        p.pln(modifier + GeneratorConstants.BASE_SERIALIZER_NAME + " " + serializerMemberName + ";");
    }

    public String serializerName() {
        return serializerName;
    }

    public String serializerMemberName() {
        return serializerMemberName;
    }

    public String deserializerName() {
        return serializerName;
    }

    public String deserializerMemberName() {
        return serializerMemberName;
    }

    protected String getPrivateModifier(boolean isStatic, boolean isFinal) {
        return "private " + super.getModifier(isStatic, isFinal);
    }
}
