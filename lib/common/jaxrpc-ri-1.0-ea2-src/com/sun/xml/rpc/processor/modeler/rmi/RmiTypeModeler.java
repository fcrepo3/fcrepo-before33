// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   RmiTypeModeler.java

package com.sun.xml.rpc.processor.modeler.rmi;

import com.sun.xml.rpc.processor.config.TypeMappingInfo;
import com.sun.xml.rpc.processor.config.TypeMappingRegistryInfo;
import com.sun.xml.rpc.processor.model.AbstractType;
import com.sun.xml.rpc.processor.model.java.*;
import com.sun.xml.rpc.processor.model.soap.*;
import com.sun.xml.rpc.processor.modeler.ModelerConstants;
import com.sun.xml.rpc.processor.modeler.ModelerException;
import com.sun.xml.rpc.processor.util.BatchEnvironment;
import java.io.PrintStream;
import java.util.*;
import javax.xml.rpc.namespace.QName;
import sun.tools.java.*;

// Referenced classes of package com.sun.xml.rpc.processor.modeler.rmi:
//            MemberInfo, RmiConstants, RmiStructure, JavaBean

public class RmiTypeModeler
    implements RmiConstants, Constants {

    private static Map soapTypeMap;

    public RmiTypeModeler() {
    }

    public static SOAPType modelTypeSOAP(BatchEnvironment env, TypeMappingRegistryInfo typeMappingRegistry, String typeUri, Type type) {
        SOAPType typeNode = getMappedSoapType(type);
        if(typeNode != null)
            return typeNode;
        if(typeMappingRegistry != null) {
            TypeMappingInfo typeMapping = typeMappingRegistry.getTypeMappingInfo("http://schemas.xmlsoap.org/soap/encoding/", type.toString());
            if(typeMapping != null)
                return processTypeMapping(env, typeMapping);
        }
        int typeCode = type.getTypeCode();
        switch(typeCode) {
        case 11: // '\013'
            return null;

        case 9: // '\t'
            QName arrName = new QName(typeUri, "tmp");
            JavaArrayType javaArType = new JavaArrayType(type.toString());
            javaArType.setElementName("item");
            SOAPArrayType arrType = new SOAPArrayType(arrName);
            arrType.setJavaType(javaArType);
            arrType.setElementName(new QName(null, javaArType.getName()));
            soapTypeMap.put(type.toString(), arrType);
            SOAPType elemType = modelTypeSOAP(env, typeMappingRegistry, typeUri, type.getElementType());
            String tmp = elemType.getName().getLocalPart();
            arrName = new QName(typeUri, "ArrayOf" + tmp);
            arrType.setName(arrName);
            log(env, "creating soaparray: " + arrName.getLocalPart());
            arrType.setElementType(elemType);
            arrType.setElementName(new QName(null, elemType.getName().getLocalPart()));
            if(elemType instanceof SOAPArrayType)
                arrType.setRank(((SOAPArrayType)elemType).getRank() + 1);
            else
                arrType.setRank(1);
            javaArType.setElementType(elemType.getJavaType());
            return arrType;

        case 10: // '\n'
            log(env, "creating soapstructure: " + type.toString());
            SOAPStructureType struct = new SOAPOrderedStructureType(new QName(typeUri, type.typeString("", true, true)));
            JavaStructureType javaStruct = new JavaStructureType(type.toString(), true);
            struct.setJavaType(javaStruct);
            soapTypeMap.put(type.toString(), struct);
            Map members = RmiStructure.modelTypeSOAP(env, type);
            Map members2 = JavaBean.modelTypeSOAP(env, type);
            if(members.size() != 0 && members2.size() != 0) {
                for(Iterator keys = members.keySet().iterator(); keys.hasNext();) {
                    String key = (String)keys.next();
                    if(members2.containsKey(key))
                        throw new ModelerException("rmimodeler.javabean.property.has.public.member", new Object[] {
                            type.toString(), key
                        });
                }

            }
            members.putAll(members2);
            if(members.size() == 0) {
                throw new ModelerException("rmimodeler.invalid.rmi.type", type.toString());
            } else {
                fillInStructure(env, typeMappingRegistry, typeUri, struct, javaStruct, members);
                return struct;
            }
        }
        throw new ModelerException("rmimodeler.unexpected.type", type.toString());
    }

    private static SOAPType processTypeMapping(BatchEnvironment env, TypeMappingInfo typeMapping) {
        log(env, "creating custom type for: " + typeMapping.getJavaTypeName());
        SOAPCustomType soapType = new SOAPCustomType(typeMapping.getXMLType());
        JavaCustomType javaType = new JavaCustomType(typeMapping.getJavaTypeName(), typeMapping);
        soapType.setJavaType(javaType);
        soapTypeMap.put(javaType.getName(), soapType);
        return soapType;
    }

    private static void fillInStructure(BatchEnvironment env, TypeMappingRegistryInfo typeMappingRegistry, String typeUri, SOAPStructureType struct, JavaStructureType javaStruct, Map members) {
        SOAPStructureMember member;
        for(Iterator iter = members.entrySet().iterator(); iter.hasNext(); struct.add(member)) {
            MemberInfo memInfo = (MemberInfo)((java.util.Map$Entry)iter.next()).getValue();
            log(env, "creating soap struct member: " + memInfo.getName());
            member = new SOAPStructureMember(new QName(null, memInfo.getName()), modelTypeSOAP(env, typeMappingRegistry, typeUri, memInfo.getType()));
            JavaStructureMember javaMember = new JavaStructureMember(memInfo.getName(), member.getType().getJavaType(), member, memInfo.isPublic());
            javaMember.setReadMethod(memInfo.getReadMethod());
            javaMember.setWriteMethod(memInfo.getWriteMethod());
            javaStruct.add(javaMember);
        }

    }

    private static SOAPType getMappedSoapType(Type type) {
        String name = type.toString();
        return (SOAPType)soapTypeMap.get(name);
    }

    private static void log(BatchEnvironment env, String msg) {
        if(env.verbose())
            System.out.println("[RmiTypeModeler: " + msg + "]");
    }

    public static Collection getPrimitiveTypes() {
        HashMap map = new HashMap();
        initializeTypeMap(map);
        return map.values();
    }

    public static void initializeTypeMap(Map typeMap) {
        typeMap.put(ModelerConstants.BOXED_BOOLEAN_CLASSNAME, RmiConstants.SOAP_BOXED_BOOLEAN_SOAPTYPE);
        typeMap.put(ModelerConstants.BOOLEAN_CLASSNAME, RmiConstants.XSD_BOOLEAN_SOAPTYPE);
        typeMap.put(ModelerConstants.BOXED_BYTE_CLASSNAME, RmiConstants.SOAP_BOXED_BYTE_SOAPTYPE);
        typeMap.put(ModelerConstants.BYTE_CLASSNAME, RmiConstants.XSD_BYTE_SOAPTYPE);
        typeMap.put(ModelerConstants.BYTE_ARRAY_CLASSNAME, RmiConstants.XSD_BYTE_ARRAY_SOAPTYPE);
        typeMap.put(ModelerConstants.BOXED_DOUBLE_CLASSNAME, RmiConstants.SOAP_BOXED_DOUBLE_SOAPTYPE);
        typeMap.put(ModelerConstants.DOUBLE_CLASSNAME, RmiConstants.XSD_DOUBLE_SOAPTYPE);
        typeMap.put(ModelerConstants.BOXED_FLOAT_CLASSNAME, RmiConstants.SOAP_BOXED_FLOAT_SOAPTYPE);
        typeMap.put(ModelerConstants.FLOAT_CLASSNAME, RmiConstants.XSD_FLOAT_SOAPTYPE);
        typeMap.put(ModelerConstants.BOXED_INTEGER_CLASSNAME, RmiConstants.SOAP_BOXED_INTEGER_SOAPTYPE);
        typeMap.put(ModelerConstants.INT_CLASSNAME, RmiConstants.XSD_INT_SOAPTYPE);
        typeMap.put(ModelerConstants.BOXED_LONG_CLASSNAME, RmiConstants.SOAP_BOXED_LONG_SOAPTYPE);
        typeMap.put(ModelerConstants.LONG_CLASSNAME, RmiConstants.XSD_LONG_SOAPTYPE);
        typeMap.put(ModelerConstants.BOXED_SHORT_CLASSNAME, RmiConstants.SOAP_BOXED_SHORT_SOAPTYPE);
        typeMap.put(ModelerConstants.SHORT_CLASSNAME, RmiConstants.XSD_SHORT_SOAPTYPE);
        typeMap.put(ModelerConstants.STRING_CLASSNAME, RmiConstants.XSD_STRING_SOAPTYPE);
        typeMap.put(ModelerConstants.BIGDECIMAL_CLASSNAME, RmiConstants.XSD_DECIMAL_SOAPTYPE);
        typeMap.put(ModelerConstants.BIGINTEGER_CLASSNAME, RmiConstants.XSD_INTEGER_SOAPTYPE);
        typeMap.put(ModelerConstants.DATE_CLASSNAME, RmiConstants.XSD_DATE_TIME_SOAPTYPE);
        typeMap.put(ModelerConstants.CALENDAR_CLASSNAME, RmiConstants.XSD_DATE_TIME_CALENDAR_SOAPTYPE);
        typeMap.put(ModelerConstants.QNAME_CLASSNAME, RmiConstants.XSD_QNAME_SOAPTYPE);
        typeMap.put(ModelerConstants.VOID_CLASSNAME, RmiConstants.XSD_VOID_SOAPTYPE);
        typeMap.put(ModelerConstants.OBJECT_CLASSNAME, RmiConstants.XSD_ANYTYPE_SOAPTYPE);
        typeMap.put(ModelerConstants.IMAGE_CLASSNAME, RmiConstants.IMAGE_SOAPTYPE);
        typeMap.put(ModelerConstants.MIME_MULTIPART_CLASSNAME, RmiConstants.MIME_MULTIPART_SOAPTYPE);
        typeMap.put(ModelerConstants.SOURCE_CLASSNAME, RmiConstants.SOURCE_SOAPTYPE);
        typeMap.put(ModelerConstants.DATA_HANDLER_CLASSNAME, RmiConstants.DATA_HANDLER_SOAPTYPE);
    }

    static  {
        soapTypeMap = new HashMap();
        initializeTypeMap(soapTypeMap);
    }
}
