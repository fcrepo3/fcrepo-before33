// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   CombinedIterator.java

package com.sun.xml.rpc.util;

import java.util.Iterator;

public class CombinedIterator
    implements Iterator {

    protected Iterator currentIterator;
    protected Iterator secondIterator;

    public CombinedIterator(Iterator firstIterator, Iterator secondIterator) {
        currentIterator = firstIterator;
        this.secondIterator = secondIterator;
    }

    public boolean hasNext() {
        if(!currentIterator.hasNext())
            currentIterator = secondIterator;
        return currentIterator.hasNext();
    }

    public Object next() {
        if(!currentIterator.hasNext())
            currentIterator = secondIterator;
        return currentIterator.next();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
