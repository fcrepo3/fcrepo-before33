// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Names.java

package com.sun.xml.rpc.processor.generator;

import com.sun.xml.rpc.processor.model.*;
import com.sun.xml.rpc.processor.model.java.*;
import com.sun.xml.rpc.processor.model.literal.LiteralFragmentType;
import com.sun.xml.rpc.processor.model.literal.LiteralType;
import com.sun.xml.rpc.processor.model.soap.SOAPCustomType;
import com.sun.xml.rpc.processor.model.soap.SOAPType;
import com.sun.xml.rpc.processor.util.*;
import com.sun.xml.rpc.streaming.PrefixFactory;
import com.sun.xml.rpc.streaming.PrefixFactoryImpl;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.rpc.namespace.QName;
import sun.tools.java.Identifier;

// Referenced classes of package com.sun.xml.rpc.processor.generator:
//            GeneratorConstants

public class Names
    implements GeneratorConstants {

    private static String serializerNameInfix = null;
    private static PrefixFactory prefixFactory = new PrefixFactoryImpl("ns");
    private static Map reservedWords;
    private static Map holderClassNames;

    public Names() {
    }

    public static final String stubFor(String name) {
        return name + "_Stub";
    }

    public static final String skeletonFor(String name) {
        return name + "_Skeleton";
    }

    public static final String tieFor(String name) {
        return name + "_Tie";
    }

    public static final String mangleClass(String classNameStr) {
        Identifier className = Identifier.lookup(classNameStr);
        className = className.getName();
        if(!className.isInner())
            return className.toString();
        Identifier mangled = Identifier.lookup(className.getFlatName().toString().replace('.', '$'));
        if(mangled.isInner())
            throw new Error("failed to mangle inner class name");
        else
            return Identifier.lookup(className.getQualifier(), mangled).toString();
    }

    public static final String stripQualifier(Class classObj) {
        String name = classObj.getName();
        return stripQualifier(name);
    }

    public static final String stripQualifier(String name) {
        int idx = name.lastIndexOf('.');
        if(idx >= 0)
            name = name.substring(idx + 1);
        return name;
    }

    public static File sourceFileForClass(String className, String outputClassName, File destDir, BatchEnvironment env) {
        return sourceFileForClass(Identifier.lookup(className), Identifier.lookup(outputClassName), destDir, env);
    }

    private static File sourceFileForClass(Identifier className, Identifier outputClassName, File destDir, BatchEnvironment env) {
        File packageDir = DirectoryUtil.getOutputDirectoryFor(className, destDir, env);
        String outputName = mangleClass(outputClassName.toString());
        String outputFileName = outputName + ".java";
        return new File(packageDir, outputFileName);
    }

    public static String typeClassName(SOAPType type) {
        return typeClassName(type.getJavaType());
    }

    public static String typeClassName(JavaType type) {
        String typeName = type.getName();
        return typeName;
    }

    public static String typeObjectSerializerClassName(SOAPType type) {
        return typeObjectSerializerClassName(type.getJavaType(), "_SOAPSerializer");
    }

    public static String typeObjectArraySerializerClassName(SOAPType type) {
        return typeObjectArraySerializerClassName(type.getJavaType(), "Array_SOAPSerializer");
    }

    public static String typeObjectSerializerClassName(LiteralType type) {
        return typeObjectSerializerClassName(type.getJavaType(), "_LiteralSerializer");
    }

    public static String typeObjectArraySerializerClassName(LiteralType type) {
        return typeObjectArraySerializerClassName(type.getJavaType(), "Array_LiteralSerializer");
    }

    public static String typeObjectSerializerClassName(JavaType type, String suffix) {
        String typeName = type.getName();
        return serializerClassName(typeName, suffix);
    }

    private static String serializerClassName(String className, String suffix) {
        if(serializerNameInfix != null)
            className = className + serializerNameInfix;
        return className + suffix;
    }

    public static String typeObjectArraySerializerClassName(JavaType type, String suffix) {
        String typeName = type.getName();
        int idx = typeName.indexOf("[]");
        if(idx > 0)
            typeName = typeName.substring(0, idx);
        return serializerClassName(typeName, suffix);
    }

    public static String typeObjectBuilderClassName(SOAPType type) {
        return typeObjectBuilderClassName(type.getJavaType());
    }

    public static String typeObjectBuilderClassName(JavaType type) {
        return type.getName() + "_SOAPBuilder";
    }

    public static String faultBuilderClassName(Port port, Operation operation) {
        String typeName = port.getJavaInterface().getName() + "_" + operation.getUniqueName() + "_SOAPBuilder";
        return typeName;
    }

    public static String faultSerializerClassName(Port port, Operation operation) {
        String name = port.getJavaInterface().getName() + "_" + operation.getUniqueName();
        return serializerClassName(name, "_Fault_SOAPSerializer");
    }

    public static String customJavaTypeClassName(JavaInterface intf) {
        String intName = intf.getName();
        return intName;
    }

    public static String customJavaTypeClassName(AbstractType type) {
        String typeName = type.getJavaType().getName();
        return typeName;
    }

    private static String customJavaTypeClassName(String typeName) {
        return typeName;
    }

    public static String customExceptionClassName(Fault fault) {
        String typeName = fault.getJavaException().getName();
        return typeName;
    }

    public static String interfaceImplClassName(JavaInterface intf) {
        String intName = intf.getName() + "_Impl";
        return intName;
    }

    public static String serializerRegistryClassName(JavaInterface intf) {
        String intName = intf.getName() + "_SerializerRegistry";
        return intName;
    }

    public static String holderClassName(Port port, SOAPType type) {
        return holderClassName(port, type.getJavaType());
    }

    public static String holderClassName(Port port, JavaType type) {
        return holderClassName(port, type.getName());
    }

    private static String holderClassName(Port port, String typeName) {
        String holderTypeName = (String)holderClassNames.get(typeName);
        Identifier className = Identifier.lookup(port.getJavaInterface().getName());
        if(holderTypeName == null) {
            String packageName = "";
            if(typeName.startsWith("java.") || typeName.startsWith("javax."))
                if(className.isQualified())
                    packageName = className.getQualifier().toString() + ".holders.";
                else
                    packageName = "holders.";
            for(int idx = typeName.indexOf("[]"); idx > 0; idx = typeName.indexOf("[]"))
                typeName = typeName.substring(0, idx) + "Array" + typeName.substring(idx + 2);

            holderTypeName = packageName + typeName + "_Holder";
        }
        return holderTypeName;
    }

    public static String memberName(String name) {
        return "my" + name;
    }

    public static String getClassMemberName(String className) {
        int idx = className.lastIndexOf('.');
        if(idx > 0)
            className = className.substring(idx + 1);
        return memberName(className);
    }

    public static String getClassMemberName(String className, AbstractType type, String suffix) {
        int idx = className.lastIndexOf('.');
        if(idx > 0)
            className = className.substring(idx + 1);
        String additionalClassName = type.getJavaType().getName().replace('.', '_');
        idx = additionalClassName.indexOf('[');
        if(idx > 0)
            additionalClassName = additionalClassName.substring(0, idx);
        return memberName(getPrefix(type.getName()) + "_" + validJavaName(type.getName().getLocalPart()) + "__" + additionalClassName + "_" + className + suffix);
    }

    public static String getClassMemberName(String className, AbstractType type) {
        int idx = className.lastIndexOf('.');
        if(idx > 0)
            className = className.substring(idx + 1);
        return getClassMemberName(getPrefix(type.getName()) + "_" + validJavaName(type.getName().getLocalPart()) + "__" + className);
    }

    public static String getTypeMemberName(SOAPType type) {
        return getTypeMemberName(type.getJavaType());
    }

    public static String getTypeMemberName(JavaType javaType) {
        String typeName = javaType.getName();
        return getTypeMemberName(typeName);
    }

    public static String getTypeMemberName(String typeName) {
        int idx = typeName.lastIndexOf('.');
        if(idx > 0)
            typeName = typeName.substring(idx + 1);
        if(typeName.endsWith("[]"))
            typeName = typeName.substring(0, typeName.length() - 2) + "Array";
        return memberName(typeName);
    }

    public static String getCustomTypeSerializerMemberName(SOAPCustomType type) {
        return getTypeQName(type.getName()) + "_Serializer";
    }

    public static String getCustomTypeDeserializerMemberName(SOAPCustomType type) {
        return getTypeQName(type.getName()) + "_Deserializer";
    }

    public static String getLiteralFragmentTypeSerializerMemberName(LiteralFragmentType type) {
        return getTypeQName(type.getName()) + "_Serializer";
    }

    public static String getOPCodeName(String name) {
        String qname = name + "_OPCODE";
        qname = qname.replace('-', '_');
        return qname.replace('.', '_');
    }

    public static String getQNameName(QName name) {
        String qname = getPrefix(name) + "_" + name.getLocalPart() + "_QNAME";
        qname = qname.replace('-', '_');
        return qname.replace('.', '_');
    }

    public static String getBlockQNameName(Operation operation, Block block) {
        QName blockName = block.getName();
        String qname = getPrefix(blockName);
        if(operation != null)
            qname = qname + "_" + operation.getUniqueName();
        qname = qname + "_" + blockName.getLocalPart() + "_QNAME";
        qname = qname.replace('-', '_');
        return qname.replace('.', '_');
    }

    public static String getBlockUniqueName(Operation operation, Block block) {
        QName blockName = block.getName();
        String qname = getPrefix(blockName);
        if(operation != null)
            qname = qname + "_" + operation.getUniqueName();
        qname = qname + "_" + blockName.getLocalPart();
        qname = qname.replace('-', '_');
        return qname.replace('.', '_');
    }

    public static String getTypeQName(QName name) {
        String qname = getPrefix(name) + "_" + name.getLocalPart() + "_TYPE_QNAME";
        qname = qname.replace('-', '_');
        return qname.replace('.', '_');
    }

    public static String validJavaClassName(String name) {
        return StringUtils.capitalize(validJavaName(name));
    }

    public static String validJavaMemberName(String name) {
        return StringUtils.decapitalize(validJavaName(name));
    }

    public static String validJavaName(String name) {
        name = wordBreakString(name);
        name = removeWhiteSpace(name);
        String tmp = (String)reservedWords.get(name);
        if(tmp != null)
            name = tmp;
        return name;
    }

    public static boolean isJavaReservedWord(String name) {
        return reservedWords.get(name) != null;
    }

    public static String getJavaMemberReadMethod(JavaStructureMember member) {
        return "get" + StringUtils.capitalize(member.getName());
    }

    public static String getJavaMemberWriteMethod(JavaStructureMember member) {
        return "set" + StringUtils.capitalize(member.getName());
    }

    public static String getResponseName(String messageName) {
        return messageName + "Response";
    }

    public static String removeWhiteSpace(String str) {
        String tmp = removeCharacter(32, str);
        return tmp;
    }

    public static String wordBreakString(String str) {
        StringBuffer buf = new StringBuffer(str);
        for(int i = 0; i < buf.length(); i++) {
            char ch = buf.charAt(i);
            if(Character.isDigit(ch)) {
                if(i + 1 < buf.length() && !Character.isDigit(buf.charAt(i + 1)))
                    buf.insert(1 + i++, ' ');
            } else
            if(!Character.isSpaceChar(ch))
                if(!Character.isJavaIdentifierPart(ch))
                    buf.setCharAt(i, ' ');
                else
                if(!Character.isLetter(ch))
                    buf.setCharAt(i, ' ');
        }

        return buf.toString();
    }

    public static String removePunctuation(String str) {
        str = str.replace('-', ' ');
        str = str.replace('.', ' ');
        str = str.replace(':', ' ');
        str = str.replace(';', ' ');
        str = str.replace('_', ' ');
        str = str.replace('\267', ' ');
        str = str.replace('\u0387', ' ');
        str = str.replace('\u06DD', ' ');
        str = str.replace('\u06DE', ' ');
        return str;
    }

    public static String removeCharacter(int ch, String str) {
        for(int idx = str.indexOf(ch); idx >= 0; idx = str.indexOf(' '))
            str = str.substring(0, idx) + StringUtils.capitalize(str.substring(idx + 1).trim());

        return str;
    }

    public static String getPrefix(QName name) {
        return getPrefix(name.getNamespaceURI());
    }

    public static String getPrefix(String uri) {
        return prefixFactory.getPrefix(uri);
    }

    public static void resetPrefixFactory() {
        prefixFactory = new PrefixFactoryImpl("ns");
    }

    public static void setSerializerNameInfix(String serNameInfix) {
        if(serNameInfix != null && serNameInfix.length() > 0)
            serializerNameInfix = "_" + serNameInfix;
    }

    static  {
        reservedWords = new HashMap();
        reservedWords.put("abstract", "_abstract");
        reservedWords.put("boolean", "_boolean");
        reservedWords.put("break", "_break");
        reservedWords.put("byte", "_byte");
        reservedWords.put("case", "_case");
        reservedWords.put("catch", "_catch");
        reservedWords.put("char", "_char");
        reservedWords.put("class", "_class");
        reservedWords.put("const", "_const");
        reservedWords.put("continue", "_continue");
        reservedWords.put("default", "_default");
        reservedWords.put("do", "_do");
        reservedWords.put("double", "_double");
        reservedWords.put("else", "_else");
        reservedWords.put("extends", "_extends");
        reservedWords.put("false", "_false");
        reservedWords.put("final", "_final");
        reservedWords.put("finally", "_finally");
        reservedWords.put("float", "_float");
        reservedWords.put("for", "_for");
        reservedWords.put("goto", "_goto");
        reservedWords.put("if", "_if");
        reservedWords.put("implements", "_implements");
        reservedWords.put("import", "_import");
        reservedWords.put("instanceof", "_instanceof");
        reservedWords.put("int", "_int");
        reservedWords.put("interface", "_interface");
        reservedWords.put("long", "_long");
        reservedWords.put("native", "_native");
        reservedWords.put("new", "_new");
        reservedWords.put("null", "_null");
        reservedWords.put("package", "_package");
        reservedWords.put("private", "_private");
        reservedWords.put("protected", "_protected");
        reservedWords.put("public", "_public");
        reservedWords.put("return", "_return");
        reservedWords.put("short", "_short");
        reservedWords.put("static", "_static");
        reservedWords.put("strictfp", "_strictfp");
        reservedWords.put("super", "_super");
        reservedWords.put("switch", "_switch");
        reservedWords.put("synchronized", "_synchronized");
        reservedWords.put("this", "_this");
        reservedWords.put("throw", "_throw");
        reservedWords.put("throws", "_throws");
        reservedWords.put("transient", "_transient");
        reservedWords.put("true", "_true");
        reservedWords.put("try", "_try");
        reservedWords.put("void", "_void");
        reservedWords.put("volatile", "_volatile");
        reservedWords.put("while", "_while");
        holderClassNames = new HashMap();
        holderClassNames.put("int", "javax.xml.rpc.holders.IntHolder");
        holderClassNames.put("long", "javax.xml.rpc.holders.LongHolder");
        holderClassNames.put("short", "javax.xml.rpc.holders.ShortHolder");
        holderClassNames.put("float", "javax.xml.rpc.holders.FloatHolder");
        holderClassNames.put("double", "javax.xml.rpc.holders.DoubleHolder");
        holderClassNames.put("boolean", "javax.xml.rpc.holders.BooleanHolder");
        holderClassNames.put("byte", "javax.xml.rpc.holders.ByteHolder");
        holderClassNames.put("java.lang.Integer", "javax.xml.rpc.holders.IntegerWrapperHolder");
        holderClassNames.put("java.lang.Long", "javax.xml.rpc.holders.LongWrapperHolder");
        holderClassNames.put("java.lang.Short", "javax.xml.rpc.holders.ShortWrapperHolder");
        holderClassNames.put("java.lang.Float", "javax.xml.rpc.holders.FloatWrapperHolder");
        holderClassNames.put("java.lang.Double", "javax.xml.rpc.holders.DoubleWrapperHolder");
        holderClassNames.put("java.lang.Boolean", "javax.xml.rpc.holders.BooleanWrapperHolder");
        holderClassNames.put("java.lang.Byte", "javax.xml.rpc.holders.ByteWrapperHolder");
        holderClassNames.put("java.lang.String", "javax.xml.rpc.holders.StringHolder");
        holderClassNames.put("java.math.BigDecimal", "javax.xml.rpc.holders.BigDecimalHolder");
        holderClassNames.put("java.math.BigInteger", "javax.xml.rpc.holders.BigIntegerHolder");
        holderClassNames.put("java.util.Calendar", "javax.xml.rpc.holders.CalendarHolder");
        holderClassNames.put("javax.xml.rpc.namespace.QName", "javax.xml.rpc.holders.QNameHolder");
        holderClassNames.put("byte[]", "javax.xml.rpc.holders.ByteArrayHolder");
    }
}
