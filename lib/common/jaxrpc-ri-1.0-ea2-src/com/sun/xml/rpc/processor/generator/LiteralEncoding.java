// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralEncoding.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.generator.writer.SerializerWriter;
import com.sun.xml.rpc.processor.generator.writer.SerializerWriterFactory;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.JavaType;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import java.io.IOException;
import java.util.Set;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorConstants, Names, GeneratorUtil

public class LiteralEncoding
    implements GeneratorConstants {

    public LiteralEncoding() {
    }

    public static void writeStaticSerializer(IndentingWriter p, LiteralType type, Set processedTypes, SerializerWriterFactory writerFactory) throws IOException {
        if(processedTypes.contains(type.getName() + ";" + type.getJavaType().getName()))
            return;
        processedTypes.add(type.getName() + ";" + type.getJavaType().getName());
        String qnameMember = Names.getTypeQName(type.getName());
        if(!processedTypes.contains(type.getName() + "TYPE_QNAME")) {
            GeneratorUtil.writeQNameTypeDeclaration(p, type.getName());
            processedTypes.add(type.getName() + "TYPE_QNAME");
        }
        if(type instanceof LiteralFragmentType) {
            SerializerWriter writer = writerFactory.createWriter(type);
            String serializerClassName = writer.serializerName();
            String memberName = writer.serializerMemberName();
            p.pln("private static " + serializerClassName + " " + memberName + ";");
        } else {
            SerializerWriter writer = writerFactory.createWriter(type);
            writer.declareSerializer(p, true, false);
        }
    }
}
