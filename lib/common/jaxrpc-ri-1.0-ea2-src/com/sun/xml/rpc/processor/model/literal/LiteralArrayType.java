// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralArrayType.java

package com.sun.xml.rpc.processor.model.literal;


// Referenced classes of package com.sun.xml.rpc.processor.model.literal:
//            LiteralType, LiteralTypeVisitor

public class LiteralArrayType extends LiteralType {

    private LiteralType elementType;

    public LiteralArrayType() {
    }

    public LiteralType getElementType() {
        return elementType;
    }

    public void setElementType(LiteralType type) {
        elementType = type;
    }

    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
