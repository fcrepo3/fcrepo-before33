// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Parser.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            Parser

final class Parser$FastStack {

    private Parser$StackElement first;
    private final Parser this$0; /* synthetic field */

    public Parser$FastStack(Parser this$0, int initialCapacity) {
        this.this$0 = this$0;
    }

    public boolean empty() {
        return first == null;
    }

    public void push(Parser$StackElement e) {
        if(first == null) {
            first = e;
        } else {
            e.next = first;
            first = e;
        }
    }

    public Parser$StackElement pop() {
        Parser$StackElement result = first;
        first = first.next;
        result.next = null;
        return result;
    }

    public void clear() {
        first = null;
    }
}
