// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Parser2.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            Parser2, ElementDecl, EntityDecl, InputEntity

final class Parser2$StackElement {

    int origState;
    int curState;
    ElementDecl elt;
    EntityDecl entity;
    InputEntity in;
    Parser2$StackElement next;
    private final Parser2 this$0; /* synthetic field */

    public Parser2$StackElement(Parser2 this$0, int origState, int curState, ElementDecl elt, EntityDecl entity, InputEntity in) {
        this.this$0 = this$0;
        this.origState = origState;
        this.curState = curState;
        this.elt = elt;
        this.entity = entity;
        this.in = in;
    }
}
