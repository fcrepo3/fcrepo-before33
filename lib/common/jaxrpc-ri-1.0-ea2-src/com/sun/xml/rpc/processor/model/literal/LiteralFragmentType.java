// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralFragmentType.java

package com.sun.xml.rpc.processor.model.literal;


// Referenced classes of package com.sun.xml.rpc.processor.model.literal:
//            LiteralType, LiteralTypeVisitor

public class LiteralFragmentType extends LiteralType {

    public LiteralFragmentType() {
    }

    public void accept(LiteralTypeVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
