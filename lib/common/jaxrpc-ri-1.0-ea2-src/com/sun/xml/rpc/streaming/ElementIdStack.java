// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   ElementIdStack.java

package com.sun.xml.rpc.streaming;


public final class ElementIdStack {

    private int _values[];
    private int _tos;
    private int _nextElementId;
    private static final int INITIAL_SIZE = 32;

    public ElementIdStack() {
        this(32);
    }

    public ElementIdStack(int size) {
        _values = new int[size];
        _tos = 0;
        _nextElementId = 1;
    }

    public int getCurrent() {
        return _values[_tos - 1];
    }

    public int pushNext() {
        ensureCapacity();
        _values[_tos++] = _nextElementId;
        return _nextElementId++;
    }

    public int pop() {
        _tos--;
        return _values[_tos];
    }

    private void ensureCapacity() {
        if(_tos >= _values.length) {
            int newValues[] = new int[_values.length * 2];
            System.arraycopy(_values, 0, newValues, 0, _values.length);
            _values = newValues;
        }
    }
}
