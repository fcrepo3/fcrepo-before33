// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   SimpleHashtable.java

package com.sun.xml.rpc.sp;

import java.util.Iterator;

final class SimpleHashtable
    implements Iterator {

    private SimpleHashtable$Entry table[];
    private SimpleHashtable$Entry current;
    private int currentBucket;
    private int count;
    private int threshold;
    private static final float loadFactor = 0.75F;

    public SimpleHashtable(int initialCapacity) {
        current = null;
        currentBucket = 0;
        if(initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        if(initialCapacity == 0)
            initialCapacity = 1;
        table = new SimpleHashtable$Entry[initialCapacity];
        threshold = (int)((float)initialCapacity * 0.75F);
    }

    public SimpleHashtable() {
        this(11);
    }

    public void clear() {
        count = 0;
        currentBucket = 0;
        current = null;
        for(int i = 0; i < table.length; i++)
            table[i] = null;

    }

    public int size() {
        return count;
    }

    public Iterator keys() {
        currentBucket = 0;
        current = null;
        return this;
    }

    public boolean hasNext() {
        if(current != null)
            return true;
        while(currentBucket < table.length)  {
            current = table[currentBucket++];
            if(current != null)
                return true;
        }
        return false;
    }

    public Object next() {
        if(current == null) {
            throw new IllegalStateException();
        } else {
            Object retval = current.key;
            current = current.next;
            return retval;
        }
    }

    public void remove() {
        if(current == null)
            throw new IllegalStateException();
        else
            throw new UnsupportedOperationException();
    }

    public Object get(String key) {
        SimpleHashtable$Entry tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7fffffff) % tab.length;
        for(SimpleHashtable$Entry e = tab[index]; e != null; e = e.next)
            if(e.hash == hash && e.key == key)
                return e.value;

        return null;
    }

    public Object getNonInterned(String key) {
        SimpleHashtable$Entry tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7fffffff) % tab.length;
        for(SimpleHashtable$Entry e = tab[index]; e != null; e = e.next)
            if(e.hash == hash && e.key.equals(key))
                return e.value;

        return null;
    }

    private void rehash() {
        int oldCapacity = table.length;
        SimpleHashtable$Entry oldMap[] = table;
        int newCapacity = oldCapacity * 2 + 1;
        SimpleHashtable$Entry newMap[] = new SimpleHashtable$Entry[newCapacity];
        threshold = (int)((float)newCapacity * 0.75F);
        table = newMap;
        for(int i = oldCapacity; i-- > 0;) {
            for(SimpleHashtable$Entry old = oldMap[i]; old != null;) {
                SimpleHashtable$Entry e = old;
                old = old.next;
                int index = (e.hash & 0x7fffffff) % newCapacity;
                e.next = newMap[index];
                newMap[index] = e;
            }

        }

    }

    public Object put(Object key, Object value) {
        if(value == null)
            throw new NullPointerException();
        SimpleHashtable$Entry tab[] = table;
        int hash = key.hashCode();
        int index = (hash & 0x7fffffff) % tab.length;
        for(SimpleHashtable$Entry e = tab[index]; e != null; e = e.next)
            if(e.hash == hash && e.key == key) {
                Object old = e.value;
                e.value = value;
                return old;
            }

        if(count >= threshold) {
            rehash();
            tab = table;
            index = (hash & 0x7fffffff) % tab.length;
        }
        SimpleHashtable$Entry e = new SimpleHashtable$Entry(hash, key, value, tab[index]);
        tab[index] = e;
        count++;
        return null;
    }
}
