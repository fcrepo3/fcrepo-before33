// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   IntegerArrayList.java

package com.sun.xml.rpc.util;


public final class IntegerArrayList {

    private int values[];
    private int length;

    public IntegerArrayList() {
        this(8);
    }

    public IntegerArrayList(int size) {
        values = null;
        length = 0;
        values = new int[size];
    }

    public boolean add(int value) {
        resize();
        values[length++] = value;
        return true;
    }

    public int get(int index) {
        return values[index];
    }

    public void clear() {
        values = new int[length];
        length = 0;
    }

    public int[] toArray() {
        int array[] = new int[length];
        System.arraycopy(values, 0, array, 0, length);
        return array;
    }

    private void resize() {
        if(length >= values.length) {
            int newValues[] = new int[values.length * 2];
            System.arraycopy(values, 0, newValues, 0, values.length);
            values = newValues;
        }
    }
}
