// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Parser2.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            Parser2

final class Parser2$FastStack {

    private Parser2$StackElement first;
    private final Parser2 this$0; /* synthetic field */

    public Parser2$FastStack(Parser2 this$0, int initialCapacity) {
        this.this$0 = this$0;
    }

    public boolean empty() {
        return first == null;
    }

    public void push(Parser2$StackElement e) {
        if(first == null) {
            first = e;
        } else {
            e.next = first;
            first = e;
        }
    }

    public Parser2$StackElement pop() {
        Parser2$StackElement result = first;
        first = first.next;
        result.next = null;
        return result;
    }

    public void clear() {
        first = null;
    }
}
