// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   NullIterator.java

package com.sun.xml.rpc.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class NullIterator
    implements Iterator {

    public NullIterator() {
    }

    public boolean hasNext() {
        return false;
    }

    public Object next() {
        throw new NoSuchElementException();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
