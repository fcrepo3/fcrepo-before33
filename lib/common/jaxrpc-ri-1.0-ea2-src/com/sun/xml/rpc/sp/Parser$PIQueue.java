// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Parser.java

package com.sun.xml.rpc.sp;


// Referenced classes of package com.sun.xml.rpc.sp:
//            Parser

final class Parser$PIQueue {

    private String pi[];
    private int size;
    private int index;
    private final Parser this$0; /* synthetic field */

    public Parser$PIQueue(Parser this$0, int initialCapacity) {
        this.this$0 = this$0;
        size = 0;
        index = 0;
        pi = new String[2 * initialCapacity];
    }

    public boolean empty() {
        return size == index;
    }

    public void clear() {
        size = 0;
    }

    public void in(String target, String content) {
        ensureCapacity();
        pi[size++] = target;
        pi[size++] = content;
    }

    public String getNextTarget() {
        String result = null;
        if(index < size) {
            result = pi[index];
            pi[index++] = null;
        }
        return result;
    }

    public String getNextContent() {
        String result = null;
        if(index < size) {
            result = pi[index];
            pi[index++] = null;
        }
        return result;
    }

    private void ensureCapacity() {
        if(pi.length == size) {
            String oldPi[] = pi;
            pi = new String[2 * pi.length + 2];
            System.arraycopy(oldPi, 0, pi, 0, size);
        }
    }
}
