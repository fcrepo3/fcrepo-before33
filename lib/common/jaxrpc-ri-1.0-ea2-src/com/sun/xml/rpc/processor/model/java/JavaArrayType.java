// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JavaArrayType.java

package com.sun.xml.rpc.processor.model.java;


// Referenced classes of package com.sun.xml.rpc.processor.model.java:
//            JavaType

public class JavaArrayType extends JavaType {

    private String _elementName;
    private JavaType _elementType;

    public JavaArrayType(String name) {
        super(name, true, "null");
    }

    public String getElementName() {
        return _elementName;
    }

    public void setElementName(String name) {
        _elementName = name;
    }

    public JavaType getElementType() {
        return _elementType;
    }

    public void setElementType(JavaType type) {
        _elementType = type;
    }
}
