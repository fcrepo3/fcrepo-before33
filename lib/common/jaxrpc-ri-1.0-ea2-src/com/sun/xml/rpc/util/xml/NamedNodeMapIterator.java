// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   NamedNodeMapIterator.java

package com.sun.xml.rpc.util.xml;

import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;

public class NamedNodeMapIterator
    implements Iterator {

    protected NamedNodeMap _map;
    protected int _index;

    public NamedNodeMapIterator(NamedNodeMap map) {
        _map = map;
        _index = 0;
    }

    public boolean hasNext() {
        if(_map == null)
            return false;
        else
            return _index < _map.getLength();
    }

    public Object next() {
        Object obj = _map.item(_index);
        if(obj != null)
            _index++;
        return obj;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
