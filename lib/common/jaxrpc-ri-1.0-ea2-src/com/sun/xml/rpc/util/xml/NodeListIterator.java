// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   NodeListIterator.java

package com.sun.xml.rpc.util.xml;

import java.util.Iterator;
import org.w3c.dom.NodeList;

public class NodeListIterator
    implements Iterator {

    protected NodeList _list;
    protected int _index;

    public NodeListIterator(NodeList list) {
        _list = list;
        _index = 0;
    }

    public boolean hasNext() {
        if(_list == null)
            return false;
        else
            return _index < _list.getLength();
    }

    public Object next() {
        Object obj = _list.item(_index);
        if(obj != null)
            _index++;
        return obj;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
