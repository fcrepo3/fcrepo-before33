// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralSimpleType.java

package com.sun.xml.rpc.processor.model.literal;

import com.sun.xml.rpc.processor.model.java.JavaSimpleType;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.literal:
//            LiteralType, LiteralTypeVisitor

public class LiteralSimpleType extends LiteralType {

    public LiteralSimpleType(QName name) {
        this(name, null);
    }

    public LiteralSimpleType(QName name, JavaSimpleType javaType) {
        super(name, javaType);
    }

    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
