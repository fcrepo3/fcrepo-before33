// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Parser.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            Parser, ElementDecl, EntityDecl, InputEntity

final class Parser$StackElement {

    int origState;
    int curState;
    ElementDecl elt;
    EntityDecl entity;
    InputEntity in;
    Parser$StackElement next;
    private final Parser this$0; /* synthetic field */

    public Parser$StackElement(Parser this$0, int origState, int curState, ElementDecl elt, EntityDecl entity, InputEntity in) {
        this.this$0 = this$0;
        this.origState = origState;
        this.curState = curState;
        this.elt = elt;
        this.entity = entity;
        this.in = in;
    }
}
