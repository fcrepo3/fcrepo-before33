// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   LongStack.java

package com.sun.xml.rpc.util;


public final class LongStack {

    private long values[];
    private int topOfStack;

    public LongStack() {
        this(32);
    }

    public LongStack(int size) {
        values = null;
        topOfStack = 0;
        values = new long[size];
    }

    public void push(long newValue) {
        resize();
        values[topOfStack] = newValue;
        topOfStack++;
    }

    public long pop() {
        topOfStack--;
        return values[topOfStack];
    }

    public long peek() {
        return values[topOfStack - 1];
    }

    private void resize() {
        if(topOfStack >= values.length) {
            long newValues[] = new long[values.length * 2];
            System.arraycopy(values, 0, newValues, 0, values.length);
            values = newValues;
        }
    }
}
