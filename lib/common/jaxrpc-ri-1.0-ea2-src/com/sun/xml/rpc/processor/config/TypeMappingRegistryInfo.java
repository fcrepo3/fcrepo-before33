// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   TypeMappingRegistryInfo.java

package com.sun.xml.rpc.processor.config;

import java.util.HashMap;
import java.util.Map;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.config:
//            TypeMappingInfo

public class TypeMappingRegistryInfo {

    private TypeMappingRegistryInfo parent;
    private Map xmlTypeMap;
    private Map javaTypeMap;

    public TypeMappingRegistryInfo() {
        xmlTypeMap = new HashMap();
        javaTypeMap = new HashMap();
    }

    public TypeMappingRegistryInfo getParent() {
        return parent;
    }

    public void setParent(TypeMappingRegistryInfo i) {
        parent = i;
    }

    public void addMapping(TypeMappingInfo i) {
        xmlTypeMap.put(getKeyFor(i.getEncodingStyle(), i.getXMLType()), i);
        javaTypeMap.put(getKeyFor(i.getEncodingStyle(), i.getJavaTypeName()), i);
    }

    public TypeMappingInfo getTypeMappingInfo(String encodingStyle, QName xmlType) {
        TypeMappingInfo i = (TypeMappingInfo)xmlTypeMap.get(getKeyFor(encodingStyle, xmlType));
        if(i == null && parent != null)
            i = parent.getTypeMappingInfo(encodingStyle, xmlType);
        return i;
    }

    public TypeMappingInfo getTypeMappingInfo(String encodingStyle, String javaTypeName) {
        TypeMappingInfo i = (TypeMappingInfo)javaTypeMap.get(getKeyFor(encodingStyle, javaTypeName));
        if(i == null && parent != null)
            i = parent.getTypeMappingInfo(encodingStyle, javaTypeName);
        return i;
    }

    private String getKeyFor(String s, QName q) {
        return getKeyFor(s, q.toString());
    }

    private String getKeyFor(String s, String t) {
        return s + "***" + t;
    }
}
