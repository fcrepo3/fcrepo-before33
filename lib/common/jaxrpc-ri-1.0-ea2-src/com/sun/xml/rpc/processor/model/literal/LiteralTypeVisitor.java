// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LiteralTypeVisitor.java

package com.sun.xml.rpc.processor.model.literal;


// Referenced classes of package com.sun.xml.rpc.processor.model.literal:
//            LiteralSimpleType, LiteralSequenceType, LiteralArrayType, LiteralAllType, 
//            LiteralFragmentType

public interface LiteralTypeVisitor {

    public abstract void visit(LiteralSimpleType literalsimpletype) throws Exception;

    public abstract void visit(LiteralSequenceType literalsequencetype) throws Exception;

    public abstract void visit(LiteralArrayType literalarraytype) throws Exception;

    public abstract void visit(LiteralAllType literalalltype) throws Exception;

    public abstract void visit(LiteralFragmentType literalfragmenttype) throws Exception;
}
