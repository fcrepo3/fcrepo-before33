// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   CachedStack.java

package com.sun.xml.rpc.util;

import java.util.ArrayList;
import java.util.List;

public abstract class CachedStack {

    protected List elements;
    protected int topOfStack;

    public CachedStack() {
        elements = new ArrayList();
        topOfStack = -1;
    }

    protected abstract Object createObject() throws Exception;

    public void push() throws Exception {
        topOfStack++;
        if(elements.size() == topOfStack)
            elements.add(topOfStack, createObject());
    }

    public void pop() {
        if(topOfStack < 0) {
            throw new ArrayIndexOutOfBoundsException(topOfStack);
        } else {
            topOfStack--;
            return;
        }
    }

    public Object peek() {
        if(topOfStack == -1)
            return null;
        else
            return elements.get(topOfStack);
    }

    public int depth() {
        return topOfStack + 1;
    }
}
