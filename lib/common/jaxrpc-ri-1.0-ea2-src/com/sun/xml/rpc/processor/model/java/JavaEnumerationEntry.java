// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JavaEnumerationEntry.java

package com.sun.xml.rpc.processor.model.java;


public class JavaEnumerationEntry {

    private String name;
    private Object value;
    private String literalValue;

    public JavaEnumerationEntry(String name, Object value, String literalValue) {
        this.name = name;
        this.value = value;
        this.literalValue = literalValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public Object getValue() {
        return value;
    }

    public String getLiteralValue() {
        return literalValue;
    }
}
