// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SOAPEnumerationType.java

package com.sun.xml.rpc.processor.model.soap;

import com.sun.xml.rpc.processor.model.java.JavaType;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.soap:
//            SOAPType, SOAPTypeVisitor

public class SOAPEnumerationType extends SOAPType {

    private SOAPType baseType;

    public SOAPEnumerationType(QName name, SOAPType baseType, JavaType javaType) {
        super(name, javaType);
        this.baseType = baseType;
    }

    public SOAPType getBaseType() {
        return baseType;
    }

    public void accept(SOAPTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
