// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JavaCustomType.java

package com.sun.xml.rpc.processor.model.java;

import com.sun.xml.rpc.processor.config.TypeMappingInfo;

// Referenced classes of package com.sun.xml.rpc.processor.model.java:
//            JavaType

public class JavaCustomType extends JavaType {

    private TypeMappingInfo typeMappingInfo;

    public JavaCustomType(String name) {
        super(name, true, null);
    }

    public JavaCustomType(String name, TypeMappingInfo typeMappingInfo) {
        super(name, true, null);
        this.typeMappingInfo = typeMappingInfo;
    }

    public TypeMappingInfo getTypeMappingInfo() {
        return typeMappingInfo;
    }

    public void setTypeMappingInfo(TypeMappingInfo typeMappingInfo) {
        this.typeMappingInfo = typeMappingInfo;
    }
}
