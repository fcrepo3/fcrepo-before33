// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   StructMap.java

package com.sun.xml.rpc.util;

import java.util.*;

public class StructMap
    implements Map {

    protected HashMap map;
    protected ArrayList keys;
    protected ArrayList values;

    public StructMap() {
        map = new HashMap();
        keys = new ArrayList();
        values = new ArrayList();
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    public Object get(Object key) {
        return map.get(key);
    }

    public Object put(Object key, Object value) {
        keys.add(key);
        values.add(value);
        return map.put(key, value);
    }

    public Object remove(Object key) {
        Object value = map.get(key);
        keys.remove(key);
        values.remove(value);
        return map.remove(key);
    }

    public void putAll(Map t) {
        if(!(t instanceof StructMap))
            throw new IllegalArgumentException("Cannot putAll members of anything other than a StructMap");
        StructMap that = (StructMap)t;
        for(int i = 0; i < that.keys.size(); i++)
            put(that.keys.get(i), that.values.get(i));

    }

    public void clear() {
        keys.clear();
        values.clear();
        map.clear();
    }

    public Set keySet() {
        return map.keySet();
    }

    public Collection values() {
        return Collections.unmodifiableList(values);
    }

    public Set entrySet() {
        return map.entrySet();
    }

    public boolean equals(Object o) {
        return map.equals(o);
    }

    public int hashCode() {
        return map.hashCode() ^ keys.hashCode() ^ values.hashCode();
    }

    public Collection keys() {
        return Collections.unmodifiableList(keys);
    }

    public void set(int index, Object key, Object value) {
        keys.set(index, key);
        values.set(index, value);
        map.put(key, value);
    }

    public void set(int index, Object value) {
        Object key = keys.get(index);
        values.set(index, value);
        map.put(key, value);
    }
}
