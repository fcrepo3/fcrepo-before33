// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralAllType.java

package com.sun.xml.rpc.processor.model.literal;

import com.sun.xml.rpc.processor.model.java.JavaStructureType;
import javax.xml.rpc.namespace.QName;

// Referenced classes of package com.sun.xml.rpc.processor.model.literal:
//            LiteralStructuredType, LiteralTypeVisitor

public class LiteralAllType extends LiteralStructuredType {

    public LiteralAllType(QName name) {
        this(name, null);
    }

    public LiteralAllType(QName name, JavaStructureType javaType) {
        super(name, javaType);
    }

    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
