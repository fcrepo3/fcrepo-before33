// NMI's Java Code Viewer 5.1 © 1997-2001 B. Lemaire
// http://njcv.htmlplanet.com - info@njcv.htmlplanet.com

// Copy registered to Evaluation Copy                                   

// Source File Name:   JavaType.java

package com.sun.xml.rpc.processor.model.java;


public abstract class JavaType {

    private String name;
    private boolean present;
    private boolean holder;
    private String initString;

    public JavaType(String name, boolean present, String initString) {
        this.name = name;
        this.present = present;
        holder = false;
        this.initString = initString;
    }

    public String getName() {
        return name;
    }

    public boolean isPresent() {
        return present;
    }

    public boolean isHolder() {
        return holder;
    }

    public void setHolder(boolean holder) {
        this.holder = holder;
    }

    public String getInitString() {
        return initString;
    }
}
