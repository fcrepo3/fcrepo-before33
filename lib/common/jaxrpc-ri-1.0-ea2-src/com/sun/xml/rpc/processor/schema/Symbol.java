// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   Symbol.java

package com.sun.xml.rpc.processor.schema;

import java.util.HashMap;
import java.util.Map;

public final class Symbol {

    public static final Symbol DEFAULT = new Symbol("default");
    public static final Symbol FIXED = new Symbol("fixed");
    public static final Symbol EXTENSION = new Symbol("extension");
    public static final Symbol RESTRICTION = new Symbol("restriction");
    public static final Symbol SUBSTITUTION = new Symbol("substitution");
    public static final Symbol SKIP = new Symbol("skip");
    public static final Symbol LAX = new Symbol("lax");
    public static final Symbol STRICT = new Symbol("strict");
    public static final Symbol KEY = new Symbol("key");
    public static final Symbol KEYREF = new Symbol("keyref");
    public static final Symbol UNIQUE = new Symbol("unique");
    public static final Symbol ALL = new Symbol("all");
    public static final Symbol CHOICE = new Symbol("choice");
    public static final Symbol SEQUENCE = new Symbol("sequence");
    public static final Symbol ATOMIC = new Symbol("atomic");
    public static final Symbol LIST = new Symbol("list");
    public static final Symbol UNION = new Symbol("union");
    private static Map _symbolMap = new HashMap();
    private String _name;

    public static Symbol named(String s) {
        return (Symbol)_symbolMap.get(s);
    }

    private Symbol(String s) {
        _name = s;
        _symbolMap.put(s, this);
    }

    public String getName() {
        return _name;
    }

}
