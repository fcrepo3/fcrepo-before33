// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   GeneratorUtil.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.model.Block;
import com.sun.xml.rpc.processor.model.Operation;
import com.sun.xml.rpc.processor.util.IndentingWriter;
import com.sun.xml.rpc.wsdl.document.schema.SchemaConstants;
import com.sun.xml.rpc.wsdl.document.soap.SOAPConstants;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            Names

public class GeneratorUtil {

    private static Map typeMap;

    public GeneratorUtil() {
    }

    public static String getQNameConstant(QName name) {
        return (String)typeMap.get(name);
    }

    public static void writeNewQName(IndentingWriter p, QName name) throws IOException {
        String qnameConstant = getQNameConstant(name);
        if(qnameConstant != null)
            p.p(qnameConstant);
        else
            p.p("new QName(\"" + name.getNamespaceURI() + "\", \"" + name.getLocalPart() + "\")");
    }

    public static void writeBlockQNameDeclaration(IndentingWriter p, Operation operation, Block block) throws IOException {
        String qname = Names.getBlockQNameName(operation, block);
        p.p("private static final QName ");
        p.p(qname + " = ");
        writeNewQName(p, block.getName());
        p.pln(";");
    }

    public static void writeQNameDeclaration(IndentingWriter p, QName name) throws IOException {
        String qname = Names.getQNameName(name);
        p.p("private static final QName ");
        p.p(qname + " = ");
        writeNewQName(p, name);
        p.pln(";");
    }

    public static void writeQNameTypeDeclaration(IndentingWriter p, QName name) throws IOException {
        String qname = Names.getTypeQName(name);
        p.p("private static final QName ");
        p.p(qname + " = ");
        writeNewQName(p, name);
        p.pln(";");
    }

    static  {
        typeMap = new HashMap();
        typeMap.put(SchemaConstants.QNAME_TYPE_STRING, "SchemaConstants.QNAME_TYPE_STRING");
        typeMap.put(SchemaConstants.QNAME_TYPE_NORMALIZED_STRING, "SchemaConstants.QNAME_TYPE_NORMALIZED_STRING");
        typeMap.put(SchemaConstants.QNAME_TYPE_TOKEN, "SchemaConstants.QNAME_TYPE_TOKEN");
        typeMap.put(SchemaConstants.QNAME_TYPE_BYTE, "SchemaConstants.QNAME_TYPE_BYTE");
        typeMap.put(SchemaConstants.QNAME_TYPE_UNSIGNED_BYTE, "SchemaConstants.QNAME_TYPE_UNSIGNED_BYTE");
        typeMap.put(SchemaConstants.QNAME_TYPE_BASE64_BINARY, "SchemaConstants.QNAME_TYPE_BASE64_BINARY");
        typeMap.put(SchemaConstants.QNAME_TYPE_HEX_BINARY, "SchemaConstants.QNAME_TYPE_HEX_BINARY");
        typeMap.put(SchemaConstants.QNAME_TYPE_INTEGER, "SchemaConstants.QNAME_TYPE_INTEGER");
        typeMap.put(SchemaConstants.QNAME_TYPE_POSITIVE_INTEGER, "SchemaConstants.QNAME_TYPE_POSITIVE_INTEGER");
        typeMap.put(SchemaConstants.QNAME_TYPE_NEGATIVE_INTEGER, "SchemaConstants.QNAME_TYPE_NEGATIVE_INTEGER");
        typeMap.put(SchemaConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER, "SchemaConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER");
        typeMap.put(SchemaConstants.QNAME_TYPE_NON_POSITIVE_INTEGER, "SchemaConstants.QNAME_TYPE_NON_POSITIVE_INTEGER");
        typeMap.put(SchemaConstants.QNAME_TYPE_INT, "SchemaConstants.QNAME_TYPE_INT");
        typeMap.put(SchemaConstants.QNAME_TYPE_UNSIGNED_INT, "SchemaConstants.QNAME_TYPE_UNSIGNED_INT");
        typeMap.put(SchemaConstants.QNAME_TYPE_LONG, "SchemaConstants.QNAME_TYPE_LONG");
        typeMap.put(SchemaConstants.QNAME_TYPE_UNSIGNED_LONG, "SchemaConstants.QNAME_TYPE_UNSIGNED_LONG");
        typeMap.put(SchemaConstants.QNAME_TYPE_SHORT, "SchemaConstants.QNAME_TYPE_SHORT");
        typeMap.put(SchemaConstants.QNAME_TYPE_UNSIGNED_SHORT, "SchemaConstants.QNAME_TYPE_UNSIGNED_SHORT");
        typeMap.put(SchemaConstants.QNAME_TYPE_DECIMAL, "SchemaConstants.QNAME_TYPE_DECIMAL");
        typeMap.put(SchemaConstants.QNAME_TYPE_FLOAT, "SchemaConstants.QNAME_TYPE_FLOAT");
        typeMap.put(SchemaConstants.QNAME_TYPE_DOUBLE, "SchemaConstants.QNAME_TYPE_DOUBLE");
        typeMap.put(SchemaConstants.QNAME_TYPE_BOOLEAN, "SchemaConstants.QNAME_TYPE_BOOLEAN");
        typeMap.put(SchemaConstants.QNAME_TYPE_TIME, "SchemaConstants.QNAME_TYPE_TIME");
        typeMap.put(SchemaConstants.QNAME_TYPE_DATE_TIME, "SchemaConstants.QNAME_TYPE_DATE_TIME");
        typeMap.put(SchemaConstants.QNAME_TYPE_DURATION, "SchemaConstants.QNAME_TYPE_DURATION");
        typeMap.put(SchemaConstants.QNAME_TYPE_DATE, "SchemaConstants.QNAME_TYPE_DATE");
        typeMap.put(SchemaConstants.QNAME_TYPE_G_MONTH, "SchemaConstants.QNAME_TYPE_G_MONTH");
        typeMap.put(SchemaConstants.QNAME_TYPE_G_YEAR, "SchemaConstants.QNAME_TYPE_G_YEAR");
        typeMap.put(SchemaConstants.QNAME_TYPE_G_YEAR_MONTH, "SchemaConstants.QNAME_TYPE_G_YEAR_MONTH");
        typeMap.put(SchemaConstants.QNAME_TYPE_G_DAY, "SchemaConstants.QNAME_TYPE_G_DAY");
        typeMap.put(SchemaConstants.QNAME_TYPE_G_MONTH_DAY, "SchemaConstants.QNAME_TYPE_G_MONTH_DAY");
        typeMap.put(SchemaConstants.QNAME_TYPE_NAME, "SchemaConstants.QNAME_TYPE_NAME");
        typeMap.put(SchemaConstants.QNAME_TYPE_QNAME, "SchemaConstants.QNAME_TYPE_QNAME");
        typeMap.put(SchemaConstants.QNAME_TYPE_NCNAME, "SchemaConstants.QNAME_TYPE_NCNAME");
        typeMap.put(SchemaConstants.QNAME_TYPE_ANY_URI, "SchemaConstants.QNAME_TYPE_ANY_URI");
        typeMap.put(SchemaConstants.QNAME_TYPE_ID, "SchemaConstants.QNAME_TYPE_ID");
        typeMap.put(SchemaConstants.QNAME_TYPE_IDREF, "SchemaConstants.QNAME_TYPE_IDREF");
        typeMap.put(SchemaConstants.QNAME_TYPE_IDREFS, "SchemaConstants.QNAME_TYPE_IDREFS");
        typeMap.put(SchemaConstants.QNAME_TYPE_ENTITY, "SchemaConstants.QNAME_TYPE_ENTITY");
        typeMap.put(SchemaConstants.QNAME_TYPE_ENTITIES, "SchemaConstants.QNAME_TYPE_ENTITIES");
        typeMap.put(SchemaConstants.QNAME_TYPE_NOTATION, "SchemaConstants.QNAME_TYPE_NOTATION");
        typeMap.put(SchemaConstants.QNAME_TYPE_NMTOKEN, "SchemaConstants.QNAME_TYPE_NMTOKEN");
        typeMap.put(SchemaConstants.QNAME_TYPE_NMTOKENS, "SchemaConstants.QNAME_TYPE_NMTOKENS");
        typeMap.put(SchemaConstants.QNAME_TYPE_URTYPE, "SchemaConstants.QNAME_TYPE_URTYPE");
        typeMap.put(SchemaConstants.QNAME_TYPE_SIMPLE_URTYPE, "SchemaConstants.QNAME_TYPE_SIMPLE_URTYPE");
        typeMap.put(SOAPConstants.QNAME_TYPE_STRING, "SOAPConstants.QNAME_TYPE_STRING");
        typeMap.put(SOAPConstants.QNAME_TYPE_NORMALIZED_STRING, "SOAPConstants.QNAME_TYPE_NORMALIZED_STRING");
        typeMap.put(SOAPConstants.QNAME_TYPE_TOKEN, "SOAPConstants.QNAME_TYPE_TOKEN");
        typeMap.put(SOAPConstants.QNAME_TYPE_BYTE, "SOAPConstants.QNAME_TYPE_BYTE");
        typeMap.put(SOAPConstants.QNAME_TYPE_UNSIGNED_BYTE, "SOAPConstants.QNAME_TYPE_UNSIGNED_BYTE");
        typeMap.put(SOAPConstants.QNAME_TYPE_BASE64_BINARY, "SOAPConstants.QNAME_TYPE_BASE64_BINARY");
        typeMap.put(SOAPConstants.QNAME_TYPE_BASE64, "SOAPConstants.QNAME_TYPE_BASE64");
        typeMap.put(SOAPConstants.QNAME_TYPE_HEX_BINARY, "SOAPConstants.QNAME_TYPE_HEX_BINARY");
        typeMap.put(SOAPConstants.QNAME_TYPE_INTEGER, "SOAPConstants.QNAME_TYPE_INTEGER");
        typeMap.put(SOAPConstants.QNAME_TYPE_POSITIVE_INTEGER, "SOAPConstants.QNAME_TYPE_POSITIVE_INTEGER");
        typeMap.put(SOAPConstants.QNAME_TYPE_NEGATIVE_INTEGER, "SOAPConstants.QNAME_TYPE_NEGATIVE_INTEGER");
        typeMap.put(SOAPConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER, "SOAPConstants.QNAME_TYPE_NON_NEGATIVE_INTEGER");
        typeMap.put(SOAPConstants.QNAME_TYPE_NON_POSITIVE_INTEGER, "SOAPConstants.QNAME_TYPE_NON_POSITIVE_INTEGER");
        typeMap.put(SOAPConstants.QNAME_TYPE_INT, "SOAPConstants.QNAME_TYPE_INT");
        typeMap.put(SOAPConstants.QNAME_TYPE_UNSIGNED_INT, "SOAPConstants.QNAME_TYPE_UNSIGNED_INT");
        typeMap.put(SOAPConstants.QNAME_TYPE_LONG, "SOAPConstants.QNAME_TYPE_LONG");
        typeMap.put(SOAPConstants.QNAME_TYPE_UNSIGNED_LONG, "SOAPConstants.QNAME_TYPE_UNSIGNED_LONG");
        typeMap.put(SOAPConstants.QNAME_TYPE_SHORT, "SOAPConstants.QNAME_TYPE_SHORT");
        typeMap.put(SOAPConstants.QNAME_TYPE_UNSIGNED_SHORT, "SOAPConstants.QNAME_TYPE_UNSIGNED_SHORT");
        typeMap.put(SOAPConstants.QNAME_TYPE_DECIMAL, "SOAPConstants.QNAME_TYPE_DECIMAL");
        typeMap.put(SOAPConstants.QNAME_TYPE_FLOAT, "SOAPConstants.QNAME_TYPE_FLOAT");
        typeMap.put(SOAPConstants.QNAME_TYPE_DOUBLE, "SOAPConstants.QNAME_TYPE_DOUBLE");
        typeMap.put(SOAPConstants.QNAME_TYPE_BOOLEAN, "SOAPConstants.QNAME_TYPE_BOOLEAN");
        typeMap.put(SOAPConstants.QNAME_TYPE_TIME, "SOAPConstants.QNAME_TYPE_TIME");
        typeMap.put(SOAPConstants.QNAME_TYPE_DATE_TIME, "SOAPConstants.QNAME_TYPE_DATE_TIME");
        typeMap.put(SOAPConstants.QNAME_TYPE_DURATION, "SOAPConstants.QNAME_TYPE_DURATION");
        typeMap.put(SOAPConstants.QNAME_TYPE_DATE, "SOAPConstants.QNAME_TYPE_DATE");
        typeMap.put(SOAPConstants.QNAME_TYPE_G_MONTH, "SOAPConstants.QNAME_TYPE_G_MONTH");
        typeMap.put(SOAPConstants.QNAME_TYPE_G_YEAR, "SOAPConstants.QNAME_TYPE_G_YEAR");
        typeMap.put(SOAPConstants.QNAME_TYPE_G_YEAR_MONTH, "SOAPConstants.QNAME_TYPE_G_YEAR_MONTH");
        typeMap.put(SOAPConstants.QNAME_TYPE_G_DAY, "SOAPConstants.QNAME_TYPE_G_DAY");
        typeMap.put(SOAPConstants.QNAME_TYPE_G_MONTH_DAY, "SOAPConstants.QNAME_TYPE_G_MONTH_DAY");
        typeMap.put(SOAPConstants.QNAME_TYPE_NAME, "SOAPConstants.QNAME_TYPE_NAME");
        typeMap.put(SOAPConstants.QNAME_TYPE_QNAME, "SOAPConstants.QNAME_TYPE_QNAME");
        typeMap.put(SOAPConstants.QNAME_TYPE_NCNAME, "SOAPConstants.QNAME_TYPE_NCNAME");
        typeMap.put(SOAPConstants.QNAME_TYPE_ANY_URI, "SOAPConstants.QNAME_TYPE_ANY_URI");
        typeMap.put(SOAPConstants.QNAME_TYPE_ID, "SOAPConstants.QNAME_TYPE_ID");
        typeMap.put(SOAPConstants.QNAME_TYPE_IDREF, "SOAPConstants.QNAME_TYPE_IDREF");
        typeMap.put(SOAPConstants.QNAME_TYPE_IDREFS, "SOAPConstants.QNAME_TYPE_IDREFS");
        typeMap.put(SOAPConstants.QNAME_TYPE_ENTITY, "SOAPConstants.QNAME_TYPE_ENTITY");
        typeMap.put(SOAPConstants.QNAME_TYPE_ENTITIES, "SOAPConstants.QNAME_TYPE_ENTITIES");
        typeMap.put(SOAPConstants.QNAME_TYPE_NOTATION, "SOAPConstants.QNAME_TYPE_NOTATION");
        typeMap.put(SOAPConstants.QNAME_TYPE_NMTOKEN, "SOAPConstants.QNAME_TYPE_NMTOKEN");
        typeMap.put(SOAPConstants.QNAME_TYPE_NMTOKENS, "SOAPConstants.QNAME_TYPE_NMTOKENS");
    }
}
