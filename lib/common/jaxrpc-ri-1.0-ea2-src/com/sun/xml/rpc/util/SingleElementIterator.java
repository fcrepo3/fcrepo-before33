// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SingleElementIterator.java

package com.sun.xml.rpc.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingleElementIterator
    implements Iterator {

    protected boolean hasNext;
    protected Object element;

    public SingleElementIterator() {
        hasNext = false;
    }

    public SingleElementIterator(Object element) {
        hasNext = false;
        this.element = element;
        hasNext = true;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public Object next() throws NoSuchElementException {
        if(!hasNext) {
            throw new NoSuchElementException("No elements left in SingleElementIterator next()");
        } else {
            hasNext = false;
            return element;
        }
    }

    public void remove() throws UnsupportedOperationException, IllegalStateException {
        throw new UnsupportedOperationException("SingleElementIterator does not support remove()");
    }
}
